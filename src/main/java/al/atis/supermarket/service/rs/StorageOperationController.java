package al.atis.supermarket.service.rs;

import al.atis.api.service.RsRepositoryService;
import al.atis.supermarket.model.StorageOperation;
import al.atis.supermarket.model.enums.OperationType;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static al.atis.supermarket.managment.AppConstants.STORAGE_OPERATION_PATH;

@RestController
@RequestMapping(STORAGE_OPERATION_PATH)
public class StorageOperationController extends RsRepositoryService<StorageOperation,String> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected StorageOperationController() {
        super(StorageOperation.class);
    }

    @Override
    public void applyFilters() throws Exception {
        if (notNull("obj.operation_type")) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.operation_type").setParameter("operation_type", getUiValue("obj.operation_type"));
        }
        if (notNull("obj.bill_uuid")) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.bill_uuid").setParameter("bill_uuid", getUiValue("obj.bill_uuid"));
        }
        if (notNull("from.operation_date")) {
            getEntityManager().unwrap(Session.class).enableFilter("from.operation_date").setParameter("operation_date", getUiDateTimeValue("from.operation_date"));
        }
        if (notNull("to.operation_date")) {
            getEntityManager().unwrap(Session.class).enableFilter("to.operation_date").setParameter("operation_date", getUiDateTimeValue("to.operation_date"));
        }
    }

    @Override
    protected String getDefaultOrderBy() {
        return "operation_date desc";
    }

    @Override
    protected void prePersist(StorageOperation object) throws Exception {
        super.prePersist(object);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasUserRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_economist"));
        if (object instanceof StorageOperation && hasUserRole){
            logger.info("prepersist: object  is storage operation");
            String operationType = String.valueOf(((StorageOperation) object).getOperation_type());
            if (operationType.equals(OperationType.EXIT.toString())){
                logger.info("Operation is EXIT");
                throw new IllegalArgumentException("Role economist could not create " +
                        "new storage operation with EXIT operation type");
            }

        }
    }
}
