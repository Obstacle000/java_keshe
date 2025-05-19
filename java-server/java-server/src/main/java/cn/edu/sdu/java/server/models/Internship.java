package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "internship")
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer internshipId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher; // 指导老师或审批老师

    @Column(length = 1000)
    private String company; // 实习单位

    @Column(length = 1000)
    private String position; // 实习岗位

    private LocalDateTime applyTime;

    private Integer status; // 申请状态，例：0-待审批，1-通过，2-拒绝

    @Column(length = 1000)
    private String reply; // 老师审批意见
}
