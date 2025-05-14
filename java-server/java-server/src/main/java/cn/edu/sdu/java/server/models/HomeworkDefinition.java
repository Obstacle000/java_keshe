package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table( name = "homeworkDefinition",
        uniqueConstraints = {
        })

public class HomeworkDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer definitionId;

    @Size(max = 50)
    @NotBlank
    private String homeworkTitle;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Size(max = 200)
    @NotBlank
    private String homeworkContent;

    @ManyToOne
    @JoinColumn(name = "person")
    private Teacher teacher;
}
