package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "competition_signup",
        uniqueConstraints = @UniqueConstraint(columnNames = {"competition_id", "student_id"}))
public class CompetitionSignup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer signupId;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDateTime signupTime;

    private Boolean status; // 报名状态
}
