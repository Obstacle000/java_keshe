package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.ActivitySignup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivitySignupRepository extends JpaRepository<ActivitySignup,Integer> {
    boolean existsByStudentPersonIdAndActivityActivityId(Integer studentId, Integer activityId);

    Optional<ActivitySignup> findByStudentPersonIdAndActivityActivityId(Integer studentId, Integer activityId);

    List<ActivitySignup> findByActivityActivityId(Integer activityId);

}
