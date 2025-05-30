package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication,Integer> {
    List<LeaveApplication> findByTeacherPersonId(Integer personId);
    List<LeaveApplication> findByStudentPersonId(Integer personId);
    List<LeaveApplication> findByStudentPersonPersonId(Integer personId);
}
