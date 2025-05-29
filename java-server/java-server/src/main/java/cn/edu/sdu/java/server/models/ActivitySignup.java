package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "activity_signup",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"activity_id", "student_id"})})
public class ActivitySignup {
    // 虽然对数据库压力很大,但是用不了redis.委屈数据库了
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer signupId;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;


    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private Date signupTime;

    @Column(name = "status")
    private Boolean status;
}
