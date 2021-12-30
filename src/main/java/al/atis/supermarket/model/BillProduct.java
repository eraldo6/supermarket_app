package al.atis.supermarket.model;

import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import static al.atis.supermarket.managment.AppConstants.BILL_PRODUCT_TABLE_NAME;

@Entity
@Table(name = BILL_PRODUCT_TABLE_NAME)

@FilterDef(name = "obj.bill_uuid", parameters = @ParamDef(name = "bill_uuid", type = "string"))
@Filter(name = "obj.bill_uuid", condition = "bill_uuid = :bill_uuid")

@FilterDef(name = "obj.product_uuid", parameters = @ParamDef(name = "product_uuid", type = "string"))
@Filter(name = "obj.product_uuid", condition = "product_uuid = :product_uuid")

@Getter
@Setter
public class BillProduct {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String uuid;

    private String bill_uuid;

    private String product_uuid;

    private Integer quantity;


}
