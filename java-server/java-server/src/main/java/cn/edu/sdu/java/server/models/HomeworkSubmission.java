package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table( name = "homeworkSubmission",
        uniqueConstraints = {
        })


public class HomeworkSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer submissionId;

    @ManyToOne
    @JoinColumn(name = "studentId") // 这是personId
    private Student student;

    @ManyToOne
    @JoinColumn(name = "definitionId")
    private HomeworkDefinition homeworkDefinition;

    private Boolean completed = false;

    @Size(max = 1000)  // 学生答案/附件路径
    private String studentAnswer;
}
