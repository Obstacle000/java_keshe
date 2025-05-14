package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSubmissionRepository extends JpaRepository<HomeworkSubmission,Integer>{
    // 查找特定学生对特定作业的提交记录
    Optional<HomeworkSubmission> findByStudentAndHomeworkDefinition(Student student, HomeworkDefinition homeworkDefinition);

    // 查看某作业的所有提交记录（教师用）
    List<HomeworkSubmission> findByHomeworkDefinition(HomeworkDefinition homeworkDefinition);

    // 查看某学生的所有提交记录（逼急了不写了）
    List<HomeworkSubmission> findByStudent(Student student);
}
