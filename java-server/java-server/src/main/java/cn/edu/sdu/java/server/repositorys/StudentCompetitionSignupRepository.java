package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentCompetitionSignupRepository extends JpaRepository<CompetitionSignup,Integer> {
    // 方式二：使用 @Query 自定义语句
    @Query("SELECT COUNT(s) FROM CompetitionSignup s WHERE s.competition.competitionId = :competitionId")
    long countByCompetitionId(@Param("competitionId") Integer competitionId);

    void deleteByCompetition(Competition Competition);

    CompetitionSignup findByStudent(Student student);

    List<CompetitionSignup> findByCompetitionCompetitionId(Integer CompetitionId);

    CompetitionSignup findByStudentAndCompetition(Student student, Competition competition);

    Optional<CompetitionSignup> findByStudentPersonIdAndCompetitionCompetitionId(Integer personId, Integer competitionId);

    CompetitionSignup findByCompetition(Competition competition);

    CompetitionSignup findByCompetitionAndStudent(Competition competition, Student student);
}
