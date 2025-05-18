package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkSubmissionRepository extends JpaRepository<HomeworkSubmission,Integer>{
    // 查找特定学生对特定作业的提交记录
    Optional<HomeworkSubmission> findByStudentAndHomeworkDefinition(Student student, HomeworkDefinition homeworkDefinition);

    // 查看某作业的所有提交记录（教师用）
    List<HomeworkSubmission> findByHomeworkDefinition(HomeworkDefinition homeworkDefinition);

    Optional<HomeworkSubmission> findByStudentPersonIdAndHomeworkDefinitionDefinitionId(Integer studentId, Integer definitionId);


    @Query("SELECT s.student.personId FROM HomeworkSubmission s WHERE s.homeworkDefinition.definitionId = :definitionId AND s.completed = false")
    List<Integer> findUncompletedStudentIdsByDefinitionId(Integer definitionId);

    // 查看某学生的所有提交记录（逼急了不写了）
    List<HomeworkSubmission> findByStudent(Student student);


    void deleteByHomeworkDefinitionDefinitionId( Integer definitionId);

    void deleteByHomeworkDefinition(HomeworkDefinition homeworkDefinition);
}
