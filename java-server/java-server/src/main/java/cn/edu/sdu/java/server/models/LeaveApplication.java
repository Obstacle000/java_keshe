package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "leave_application")
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "person_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "person_id")
    private Teacher teacher;

    @Column(length = 1000)
    private String reason;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime applyTime;

    private Integer status; // 0: 待审批, 1: 通过, 2: 拒绝

    @Column(length = 1000)
    private String reply;
}
