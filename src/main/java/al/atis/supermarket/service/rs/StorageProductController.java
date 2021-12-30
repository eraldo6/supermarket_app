package al.atis.supermarket.service.rs;

import al.atis.api.service.RsRepositoryService;
import al.atis.supermarket.model.Product;
import al.atis.supermarket.model.StorageProduct;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static al.atis.supermarket.managment.AppConstants.STORAGE_PRODUCT_PATH;

@RestController
@RequestMapping(STORAGE_PRODUCT_PATH)
public class StorageProductController extends RsRepositoryService<StorageProduct, String>  {
    public StorageProductController() {
        super(StorageProduct.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return "quantity desc";
    }

    @Override
    public void applyFilters() throws Exception {
        if (notNull("obj.product_uuid")) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.product_uuid").setParameter("product_uuid", getUiValue("obj.product_uuid"));
        }
        if (notNull("gt.quantity")) {
            getEntityManager().unwrap(Session.class).enableFilter("gt.quantity").setParameter("quantity", getUiIntegerValue("gt.quantity"));
        }
        if (notNull("lt.quantity")) {
            getEntityManager().unwrap(Session.class).enableFilter("lt.quantity").setParameter("quantity", getUiIntegerValue("lt.quantity"));
        }
        if (notNull("eq.quantity")) {
            getEntityManager().unwrap(Session.class).enableFilter("eq.quantity").setParameter("quantity", getUiIntegerValue("eq.quantity"));
        }
    }

}
