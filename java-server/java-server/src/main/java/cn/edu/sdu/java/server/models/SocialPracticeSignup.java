package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "socialPractice_signup",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"socialPractice_id", "student_id"})})
public class SocialPracticeSignup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer signupId;

    @ManyToOne
    @JoinColumn(name = "socialPractice_id")
    private SocialPractice socialPractice;


    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private Date signupTime;

    @Column(name = "status")
    private Boolean status;

    // 提交的社会实践证明材料的文件名
    private String proofMaterialFileName;

    // 提交的社会实践证明材料的文件路径
    private String proofMaterialFilePath;
}
