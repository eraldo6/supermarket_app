package al.atis.supermarket.model;

import al.atis.supermarket.model.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

import static al.atis.supermarket.managment.AppConstants.USER_TABLE_NAME;

@Entity
@Table(name = USER_TABLE_NAME)

@FilterDef(name = "obj.role", parameters = @ParamDef(name = "role", type = "string"))
@Filter(name = "obj.role", condition = "role = :role")

@Getter
@Setter
public class Users {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @Column(unique = true)
    private String uuid;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String username;

    private String password;

}
