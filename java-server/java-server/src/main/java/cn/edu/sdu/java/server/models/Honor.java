package cn.edu.sdu.java.server.models;

import cn.edu.sdu.java.server.repositorys.StudentRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@Entity
@Table(	name = "honor",
        uniqueConstraints = {
        })
public class Honor {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId;//荣誉编号

    @ManyToOne
    @JoinColumn(name = "personId")
    private Person person;//荣誉对应的人


    private String honor;//荣誉名称

}
