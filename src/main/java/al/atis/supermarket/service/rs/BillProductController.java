package al.atis.supermarket.service.rs;

import al.atis.api.service.RsRepositoryService;
import al.atis.supermarket.model.BillProduct;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static al.atis.supermarket.managment.AppConstants.BILLS_PRODUCT_PATH;

@RestController
@RequestMapping(BILLS_PRODUCT_PATH)
public class BillProductController extends RsRepositoryService<BillProduct, String> {

    public BillProductController() {
        super(BillProduct.class);
    }

    @Override
    protected String getDefaultOrderBy() {
        return "quantity desc";
    }

    @Override
    public void applyFilters() throws Exception {
        if (notNull("obj.bill_uuid")) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.bill_uuid").setParameter("bill_uuid", getUiValue("obj.bill_uuid"));
        }

        if (notNull("obj.product_uuid")) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.product_uuid").setParameter("product_uuid", getUiValue("obj.product_uuid"));
        }
    }

}


