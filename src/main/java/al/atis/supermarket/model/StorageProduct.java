package al.atis.supermarket.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import static al.atis.supermarket.managment.AppConstants.STORAGE_PRODUCT_TABLE_NAME;

@Entity
@Table(name = STORAGE_PRODUCT_TABLE_NAME)

@FilterDef(name = "obj.product_uuid", parameters = @ParamDef(name = "product_uuid", type = "string"))
@Filter(name = "obj.product_uuid", condition = "product_uuid = :product_uuid")

@FilterDef(name = "gt.quantity", parameters = @ParamDef(name = "quantity", type = "integer"))
@Filter(name = "gt.quantity", condition = "quantity >:quantity")

@FilterDef(name = "lt.quantity", parameters = @ParamDef(name = "quantity", type = "integer"))
@Filter(name = "lt.quantity", condition = "quantity < :quantity")

@FilterDef(name = "eq.quantity", parameters = @ParamDef(name = "quantity", type = "integer"))
@Filter(name = "eq.quantity", condition = "quantity = :quantity")

@Getter
@Setter
public class StorageProduct {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String uuid;

    private String product_uuid;

    private Integer quantity;

}
