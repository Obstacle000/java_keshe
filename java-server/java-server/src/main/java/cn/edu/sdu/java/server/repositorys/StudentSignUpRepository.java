package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.ActivitySignup;
import cn.edu.sdu.java.server.models.Notice;
import cn.edu.sdu.java.server.models.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentSignUpRepository extends JpaRepository<ActivitySignup,Integer> {
    // 方式二：使用 @Query 自定义语句
    @Query("SELECT COUNT(s) FROM ActivitySignup s WHERE s.activity.activityId = :activityId")
    long countByActivityId(@Param("activityId") Integer activityId);

    void deleteByActivity(Activity activity);

    ActivitySignup findByStudent(Student student);

    ActivitySignup findByActivityActivityId(Integer activityId);
}
