package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.SocialPractice;
import cn.edu.sdu.java.server.models.SocialPracticeSignup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialPracticeSignupRepository extends JpaRepository<SocialPracticeSignup,Integer> {
    boolean existsByStudentPersonIdAndSocialPracticePracticeId(Integer studentId, Integer SocialPracticeId);

    Optional<SocialPracticeSignup> findByStudentPersonIdAndSocialPracticePracticeId(Integer studentId, Integer socialPracticeId);

    SocialPracticeSignup findBySocialPracticePracticeId(Integer socialPracticeId);



}