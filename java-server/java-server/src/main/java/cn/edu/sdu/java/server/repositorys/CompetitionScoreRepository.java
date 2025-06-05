package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.models.CompetitionScoreRecord;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionScoreRepository extends JpaRepository<CompetitionScoreRecord, Integer> {
    // 根据竞赛 ID 查询所有成绩记录
    List<CompetitionScoreRecord> findByCompetition(Competition competition);

    // 根据学生 ID 查询所有成绩记录
    List<CompetitionScoreRecord> findByStudent(Student student);

    // 根据竞赛 ID 和学生 ID 查询特定的成绩记录
    Optional<CompetitionScoreRecord> findByCompetitionAndStudent(Competition competition, Student student);

    // 根据竞赛 ID 和学生 ID 删除特定的成绩记录
    void deleteByCompetitionAndStudent(Competition competition, Student student);

    CompetitionScoreRecord findByCompetitionCompetitionId(Integer competitionId);


    // 根据竞赛 ID 和学生 ID 更新特定的成绩记录
    // 注意：更新操作通常在服务层进行，这里可以保留
}


