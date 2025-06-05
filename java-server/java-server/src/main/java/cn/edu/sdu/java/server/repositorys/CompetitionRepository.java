package cn.edu.sdu.java.server.repositorys;
import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.models.DictionaryInfo;
import cn.edu.sdu.java.server.models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface CompetitionRepository extends JpaRepository<Competition,Integer>  {
    List<Competition> findByNoticeNoticeId(Integer noticeId);
}
