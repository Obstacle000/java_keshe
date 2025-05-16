package cn.edu.sdu.java.server.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;

    @Size(max = 100)
    private String title;

    @Size(max = 1000)
    private String description;

    private Date startTime;

    private Date endTime;

    // 一对一关联通知，活动是主控端，通知的外键在通知表中
    @OneToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;
}

