package al.atis.supermarket.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

import static al.atis.supermarket.managment.AppConstants.SUPPLIER_TABLE_NAME;

@Entity
@Table(name = SUPPLIER_TABLE_NAME)

@FilterDef(name = "like.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "like.name", condition = "lower(name) LIKE :name")

@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String uuid;

    private String name;
}
