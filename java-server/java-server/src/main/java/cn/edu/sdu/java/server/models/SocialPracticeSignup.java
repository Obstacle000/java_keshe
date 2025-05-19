package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "social_practice_signup",
        uniqueConstraints = @UniqueConstraint(columnNames = {"practice_id", "student_id"}))
public class SocialPracticeSignup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer signupId;

    @ManyToOne
    @JoinColumn(name = "practice_id", nullable = false)
    private SocialPractice socialPractice;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDateTime signupTime;

    private Boolean status; // 报名状态，0-报名中，1-通过，2-取消
}
