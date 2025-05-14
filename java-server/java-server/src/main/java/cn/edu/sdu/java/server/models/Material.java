package cn.edu.sdu.java.server.models;


import jakarta.persistence.*;
import lombok.Data;


import java.sql.Timestamp;
@Table(name = "material")
@Data
@Entity
public class Material {

    @Id
    @Column(name = "material_id")
    private int materialId;

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Material parent;

    @Column(name = "file_name")
    private String fileName; // 远程文件,非本地文件
    @Column(name = "course_name")
    private String courseName;

    @Column(name = "title")
    private String title;

    @Column(name = "is_leaf")
    private int isLeaf;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;


}
