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
    private Integer honorId;

    @ManyToOne
    @JoinColumn(name = "personId")
    private Person person;


    private String honor;

}
