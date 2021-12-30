package al.atis.supermarket.model;

import al.atis.supermarket.model.enums.ProductCategory;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import static al.atis.supermarket.managment.AppConstants.PRODUCT_TABLE_NAME;

@Entity
@Table(name = PRODUCT_TABLE_NAME)

@FilterDef(name = "gt.price", parameters = @ParamDef(name = "price", type = "integer"))
@Filter(name = "gt.price", condition = "price > :price")

@FilterDef(name = "lt.price", parameters = @ParamDef(name = "price", type = "integer"))
@Filter(name = "lt.price", condition = "price < :price")

@FilterDef(name = "obj.category", parameters = @ParamDef(name = "category", type = "string"))
@Filter(name = "obj.category", condition = "category = :category")

@FilterDef(name = "like.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "like.name", condition = "lower(name) LIKE :name")

@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String uuid;

    private String name;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;


}
