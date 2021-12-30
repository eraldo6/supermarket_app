package al.atis.supermarket.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;

@Entity
@Data
public class Attachment {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Id
    private String uuid;

    private String fileName;

    private String mimeType;

    private String azure_url;

    private LocalDateTime creation_date;

    private String creator;

    private String external_type;

    private String external_uuid;

}
