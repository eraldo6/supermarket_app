package al.atis.api.service;


import al.atis.supermarket.model.Attachment;
import al.atis.supermarket.model.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static al.atis.api.management.AppConstants.ORDER_BY_ASC;
import static al.atis.api.management.AppConstants.ORDER_BY_DESC;

public abstract class RsRepositoryService<T, U> extends RsResponseService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Class<T> entityClass;

    @Autowired
    EntityManager entityManager;

    protected RsRepositoryService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected void prePersist(T object) throws Exception {
    }

    protected ResponseEntity handleObjectNotFoundRequest(U id) {
        String errorMessage = String.format("Object [{0}] with id [{1}] not found",
                entityClass.getCanonicalName(), id);
        return jsonMessageResponse(HttpStatus.NOT_FOUND, errorMessage);
    }

    protected ResponseEntity handleObjectNotFoundRequest(U id, String name) {
        String errorMessage = String.format("Object [{0}] with id [{1}] not found",
                name, id);
        return jsonMessageResponse(HttpStatus.NOT_FOUND, errorMessage);
    }

    public T find(U id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<T> persist(@RequestBody T object) {
        logger.info("persist");
        try {
            prePersist(object);
        } catch (Exception e) {
            logger.error("persist", e);
            return jsonMessageResponse(HttpStatus.BAD_REQUEST, e);
        }

        try {
            entityManager.persist(object);
            if (object == null) {
                logger.error("Failed to create resource: " + object);
                return jsonErrorMessageResponse(object);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(object);
            }
        } catch (Exception e) {
            logger.error("persist", e);
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postPersist(object);
            } catch (Exception e) {
                logger.error("persist", e);
            }
        }
    }

    protected void postPersist(T object) throws Exception {
    }

    @GetMapping
    @Transactional
    public ResponseEntity getList(
            @RequestParam(value = "startRow", required = false, defaultValue = "0") Integer startRow,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy) {

        logger.info("getList");
        try {
            applyFilters();

            long listSize = count();
            List<T> list;
            if (listSize == 0) {
                list = new ArrayList<>();
            } else {
                int currentPage = 0;
                if (pageSize != 0) {
                    currentPage = startRow / pageSize;
                } else {
                    pageSize = Long.valueOf(listSize).intValue();
                }
                TypedQuery<T> search = getSearch(orderBy);
                list = search.setFirstResult(startRow)
                        .setMaxResults(pageSize)
                        .getResultList();
            }
            postList(list);

            return ResponseEntity.ok()
                    .header("startRow", String.valueOf(startRow))
                    .header("pageSize", String.valueOf(pageSize))
                    .header("listSize", String.valueOf(listSize))
                    .body(list);


        } catch (Exception e) {
            logger.error("getList", e);
            return jsonErrorMessageResponse(e);
        }
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity fetch(@PathVariable U id) {
        logger.info("fetch: " + id);

        try {
            T t = find(id);
            if (t == null) {
                return handleObjectNotFoundRequest(id);
            } else {
                try {
                    postFetch(t);
                } catch (Exception e) {
                    logger.error("fetch: " + id, e);
                }
                return ResponseEntity.status(HttpStatus.OK).body(t);
            }
        } catch (NoResultException e) {
            logger.error("fetch: " + id, e);
            return jsonMessageResponse(HttpStatus.NOT_FOUND, id);
        } catch (Exception e) {
            logger.error("fetch: " + id, e);
            return jsonErrorMessageResponse(e);
        }
    }

    protected void postFetch(T object) throws Exception {
    }

    protected T preUpdate(T object) throws Exception {
        return object;
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity update(@PathVariable U id, @RequestBody T object) {
        logger.info("update:" + id);

        try {
            object = preUpdate(object);
        } catch (Exception e) {
            logger.error("update:" + id, e);
            return jsonMessageResponse(HttpStatus.BAD_REQUEST.BAD_REQUEST, e);
        }
        try {
            entityManager.merge(object);
            return ResponseEntity.status(HttpStatus.OK).body(object);
        } catch (Exception e) {
            logger.error("update:" + id, e);
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postUpdate(object);
            } catch (Exception e) {
                logger.error("update:" + id, e);
            }
        }
    }

    protected void postUpdate(T object) throws Exception {
    }

    protected void postList(List<T> list) throws Exception {
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity delete(@PathVariable("id") U id) {
        logger.info("delete: " + id);

        try {
            preDelete(id);
        } catch (Exception e) {
            logger.error("delete: " + id, e);
            return jsonMessageResponse(HttpStatus.BAD_REQUEST, e);
        }
        T t;
        try {
            t = find(id);
            if (t == null) {
                return handleObjectNotFoundRequest(id);
            }
        } catch (Exception e) {
            return jsonMessageResponse(HttpStatus.BAD_REQUEST.BAD_REQUEST, e);
        }
        try {
            toDelete(t);
            postDelete(id);
            return jsonMessageResponse(HttpStatus.NO_CONTENT, id);
        } catch (NoResultException e) {
            logger.error("delete: " + id, e);
            return jsonMessageResponse(HttpStatus.NOT_FOUND, id);
        } catch (Exception e) {
            logger.error("delete: " + id, e);
            return jsonErrorMessageResponse(e);
        }
    }

    public void toDelete(T t) {
        getEntityManager().remove(t);
    }

    protected void preDelete(U id) throws Exception {
    }

    protected void postDelete(U id) throws Exception {
    }

    @GetMapping("/list-size")
    @Transactional
    public ResponseEntity getListSize() {
        logger.info("getListSize");
        Map<String, Object> params = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder();
        try {
            long listSize = count();
            return ResponseEntity.ok()
                    .header("Access-Control-Expose-Headers", "listSize")
                    .body(listSize);
        } catch (Exception e) {
            logger.error("getListSize", e);
            return jsonErrorMessageResponse(e);
        }
    }

    @GetMapping("/{id}/exist")
    public ResponseEntity exist(@PathVariable("id") U id) {
        logger.info("exist: " + id);

        try {
            boolean exist = find(id) != null;
            if (!exist) {
                return handleObjectNotFoundRequest(id);
            } else {
                return jsonMessageResponse(HttpStatus.OK, id);
            }
        } catch (Exception e) {
            logger.error("exist: " + id, e);
            return jsonErrorMessageResponse(e);
        }
    }

    @GetMapping("/{uuid}/download")
    @Transactional
    public ResponseEntity download(@PathVariable(value = "uuid") String uuid) throws Exception {
        Attachment attachment = findAttachmentById(uuid);
        if (attachment == null) {
            throw new Exception("Attachment not found");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment;filename=" + attachment.getFileName())
                .header("Content-Type", attachment.getMimeType()).build();
    }

//    @PostMapping("/upload")
//    @Transactional
//    public ResponseEntity upload(@MultipartForm FormData formdata) throws Exception {
//
//        if (formdata.data == null) {
//            throw new Exception(MessageFormat.format("When the field type is [{0}] an attachment should be sent!",
//                    formdata.field_type));
//        }
//        if (formdata.fileName == null || formdata.fileName.isBlank()) {
//            throw new Exception(MessageFormat.format("When the field type is [{0}] file name [{1}] should exist!",
//                    formdata.field_type, formdata.fileName));
//        }
//        String azureUrl = null;
//        byte[] byteArray = null;
//        try {
//            byteArray = formdata.data.readAllBytes();
//            azureUrl = azureClient.upload(formdata.fileName, byteArray);
//        } catch (Exception e) {
//            return jsonErrorMessageResponse(e);
//        }
//        Attachment attachment = new Attachment();
//        attachment.fileName = formdata.fileName;
//        attachment.mimeType = formdata.mimeType;
//        attachment.azure_url = azureUrl;
//        attachment.persist();
//
//        return Response.ok(attachment).build();
//    }

    private long count() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        countCriteriaQuery.select(criteriaBuilder.count(countCriteriaQuery.from(getEntityClass())));

        return getEntityManager().createQuery(countCriteriaQuery).getSingleResult();
    }

    private TypedQuery<T> getSearch(String orderBy) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
        Root<T> root = criteriaQuery.from(getEntityClass());
        criteriaQuery.select(root);

        List<Order> orderList = sort(orderBy, criteriaBuilder, root);
        criteriaQuery.orderBy(orderList);

        TypedQuery<T> search = getEntityManager().createQuery(criteriaQuery);
        return search;
    }

    private List<Order> sort(String orderBy, CriteriaBuilder criteriaBuilder, Root<T> routeRoot) {
        List<Order> orderList = new ArrayList<>();

        List<String> orderByExpresions;
        if (orderBy != null) {
            orderByExpresions = fromValueToList(orderBy);
        } else {
            orderByExpresions = fromValueToList(getDefaultOrderBy());
        }

        for (String orderByExpresion : orderByExpresions) {
            if (orderByExpresion.contains(ORDER_BY_ASC)) {
                String property = orderByExpresion.replace(ORDER_BY_ASC, "").trim();
                orderList.add(criteriaBuilder.asc(routeRoot.get(property)));
            } else if (orderByExpresion.contains(ORDER_BY_DESC)) {
                String property = orderByExpresion.replace(ORDER_BY_DESC, "").trim();
                orderList.add(criteriaBuilder.desc(routeRoot.get(property)));
            }
        }
        return orderList;
    }

    private TypedQuery<Bill> getBills(String orderBy){
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Bill> criteriaQuery = criteriaBuilder.createQuery(Bill.class);
        Root<Bill> root = criteriaQuery.from(Bill.class);
        criteriaQuery.select(root);

        LocalDate date = LocalDate.now();
        LocalDateTime localDateTime = date.atStartOfDay();
        criteriaQuery.select(root).where(criteriaBuilder.greaterThanOrEqualTo(root.get("created_date") , localDateTime));
        TypedQuery<Bill> search = getEntityManager().createQuery(criteriaQuery);

        return search;
    }

    private Attachment findAttachmentById(String uuid){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Attachment> criteriaQuery = criteriaBuilder.createQuery(Attachment.class);
        Root<Attachment> root = criteriaQuery.from(Attachment.class);

        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("uuid"),
                uuid));

        TypedQuery<Attachment> search = entityManager.createQuery(criteriaQuery);

        return search.getSingleResult();
    }

    public abstract void applyFilters() throws Exception;

    protected abstract String getDefaultOrderBy();


}
