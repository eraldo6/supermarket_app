package al.atis.supermarket.model;

import al.atis.supermarket.model.enums.OperationType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.time.LocalDateTime;

import static al.atis.supermarket.managment.AppConstants.STORAGE_OPERATION_TABLE_NAME;

@Entity
@Table(name = STORAGE_OPERATION_TABLE_NAME)

@FilterDef(name = "obj.operation_type", parameters = @ParamDef(name = "operation_type", type = "string"))
@Filter(name = "obj.operation_type", condition = "operation_type = :operation_type")

@FilterDef(name = "obj.bill_uuid", parameters = @ParamDef(name = "bill_uuid", type = "string"))
@Filter(name = "obj.bill_uuid", condition = "bill_uuid = :bill_uuid")

@FilterDef(name = "from.operation_date", parameters = @ParamDef(name = "operation_date", type = "java.time.LocalDateTime"))
@Filter(name = "from.operation_date", condition = "operation_date > :operation_date")

@FilterDef(name = "to.operation_date", parameters = @ParamDef(name = "operation_date", type = "java.time.LocalDateTime"))
@Filter(name = "to.operation_date", condition = "operation_date < :operation_date")

@Getter
@Setter
public class StorageOperation {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @Column(unique = true)
    private String uuid;

    @Enumerated(EnumType.STRING)
    private OperationType operation_type;

    private String bill_uuid;

    private LocalDateTime operation_date;

}
