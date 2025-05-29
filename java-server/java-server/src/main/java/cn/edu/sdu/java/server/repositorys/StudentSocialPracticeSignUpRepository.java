package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.SocialPractice;
import cn.edu.sdu.java.server.models.SocialPracticeSignup;
import cn.edu.sdu.java.server.models.Notice;
import cn.edu.sdu.java.server.models.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentSocialPracticeSignUpRepository extends JpaRepository<SocialPracticeSignup,Integer> {
    // 方式二：使用 @Query 自定义语句
    @Query("SELECT COUNT(s) FROM SocialPracticeSignup s WHERE s.socialPractice.practiceId = :socialPracticeId")
    long countBySocialPracticeId(@Param("socialPracticeId") Integer socialPracticeId);

    void deleteBySocialPractice(SocialPractice socialPractice);

    SocialPracticeSignup findByStudent(Student student);

    SocialPracticeSignup findBySocialPracticePracticeId(Integer socialPracticeId);

    SocialPracticeSignup findByStudentAndSocialPractice(Student student, SocialPractice socialPractice);
}
