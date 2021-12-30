package al.atis.supermarket.service.rs;

import al.atis.api.service.RsRepositoryService;
import al.atis.supermarket.SupermarketApplication;
import al.atis.supermarket.model.BillProduct;
import al.atis.supermarket.model.Product;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static al.atis.supermarket.managment.AppConstants.PRODUCT_PATH;
import static org.springframework.util.Assert.notNull;

@RestController
@RequestMapping(PRODUCT_PATH)
public class ProductController extends RsRepositoryService<Product, String> {
    public ProductController() {
        super(Product.class);
    }

    @Override
    protected String getDefaultOrderBy() {
        return "name asc";
    }

    @Override
    public void applyFilters() throws Exception {
        if (notNull("gt.price")) {
            getEntityManager().unwrap(Session.class).enableFilter("gt.price").setParameter("price", getUiIntegerValue("gt.price"));
        }

        if (notNull("lt.price")) {
            getEntityManager().unwrap(Session.class).enableFilter("lt.price").setParameter("price", getUiIntegerValue("lt.price"));
        }

        if (notNull("obj.category")) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.category").setParameter("category", getUiValue("obj.category"));
        }

        if (notNull("like.name")) {
            getEntityManager().unwrap(Session.class).enableFilter("like.name").setParameter("name", likeParamToLowerCase("like.name"));
        }

    }
}
