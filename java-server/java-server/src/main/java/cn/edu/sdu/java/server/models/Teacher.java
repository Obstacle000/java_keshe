package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(	name = "teacher",
        uniqueConstraints = {
        })
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personId;


    //坑:但由于你使用了 @GeneratedValue(strategy = GenerationType.IDENTITY)，它告诉 JPA：你希望数据库自己生成主键（而不是使用 Person 的 personId）。
    //@MapsId 表示这个实体的主键 来自于关联的外键 Person 的主键，不需要再由数据库生成。
    //因此只要 teacher.setPerson(person)，JPA 会自动把 person.getPersonId() 设置给 teacher.personId。
    // 不然一直是null
    @OneToOne
    @JoinColumn(name="person_id")
    @MapsId
    @JsonIgnore
    private Person person;

    @Size(max = 20)
    private String title;
    @Size(max = 20)
    private String degree;

    @Size(max = 20)
    private String major;


}
