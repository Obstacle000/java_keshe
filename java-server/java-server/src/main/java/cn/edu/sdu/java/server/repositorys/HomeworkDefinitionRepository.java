package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkDefinitionRepository extends JpaRepository<HomeworkDefinition,Integer> {

    //@Query(value = "from Homework where ?1='' or teacherId like %?1% or courseId like %?1% ")
    //List<Homework> findHomeworkListByTeacherCourse(String homeworkTitle);

    Optional<HomeworkDefinition> findByDefinitionId(Integer definitionId);
    Optional<HomeworkDefinition> findByCourseCourseId(Integer courseId);

}
