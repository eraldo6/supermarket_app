package al.atis.supermarket.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.time.LocalDateTime;

import static al.atis.supermarket.managment.AppConstants.BILL_TABLE_NAME;

@Entity
@Table(name = BILL_TABLE_NAME)

@FilterDef(name = "gt.total_price", parameters = @ParamDef(name = "total_price", type = "java.lang.Double"))
@Filter(name = "gt.total_price", condition = "total_price > :total_price")

@FilterDef(name = "lt.total_price", parameters = @ParamDef(name = "total_price", type = "java.lang.Double"))
@Filter(name = "lt.total_price", condition = "total_price < :total_price")

@FilterDef(name = "from.created_date", parameters = @ParamDef(name = "created_date", type = "java.time.LocalDateTime"))
@Filter(name = "from.created_date", condition = "created_date > :created_date")

@FilterDef(name = "to.created_date", parameters = @ParamDef(name = "created_date", type = "java.time.LocalDateTime"))
@Filter(name = "to.created_date", condition = "created_date < :created_date")

@FilterDef(name = "eq.created_date", parameters = @ParamDef(name = "created_date", type = "java.time.LocalDateTime"))
@Filter(name = "eq.created_date", condition = "created_date = :created_date")

@FilterDef(name = "like.created_by", parameters = @ParamDef(name = "created_by", type = "string"))
@Filter(name = "like.created_by", condition = "lower(created_by) LIKE :created_by")

@FilterDef(name = "obj.created_by", parameters = @ParamDef(name = "created_by", type = "string"))
@Filter(name = "obj.created_by", condition = "created_by = :created_by")

@Getter
@Setter
public class Bill {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    @Id
    private String uuid;

    private Double total_price;

    private LocalDateTime created_date;

    private String created_by;

}


