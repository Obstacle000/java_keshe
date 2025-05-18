package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.DictionaryInfo;
import cn.edu.sdu.java.server.models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    List<Activity> findByNoticeNoticeId(Integer noticeId);


}
