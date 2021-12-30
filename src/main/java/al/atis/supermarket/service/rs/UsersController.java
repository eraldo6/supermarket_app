package al.atis.supermarket.service.rs;

import al.atis.api.service.RsRepositoryService;
import al.atis.supermarket.model.Users;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static al.atis.supermarket.managment.AppConstants.USERS_PATH;

@RestController
@RequestMapping(USERS_PATH)
public class UsersController extends RsRepositoryService<Users, String> {
    public UsersController() {
        super(Users.class);
    }


    @Override
    public void applyFilters() throws Exception {
        if (notNull("obj.role")) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.role").setParameter("role", getUiValue("obj.role"));
        }
    }

    @Override
    protected String getDefaultOrderBy() {
        return "role desc";
    }
}
