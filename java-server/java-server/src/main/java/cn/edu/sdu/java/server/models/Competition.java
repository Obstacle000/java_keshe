package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "competition")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer competitionId;

    @Size(max = 100)
    private String title;

    @Size(max = 1000)
    private String description;

    // 关联通知表notice
    @ManyToOne
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    private Date startTime;

    private Date endTime;


}
