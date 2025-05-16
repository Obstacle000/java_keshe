package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Integer> {
}
