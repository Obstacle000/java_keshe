package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HonorRepository extends JpaRepository<Honor,Integer> {
    // person_id是外键
    @Query(value = "from Student where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<Person> findPersonListByNumName(String numName);

    @Query(value = "FROM Honor h WHERE (:personId = 0 OR h.person.personId = :personId)")
    List<Honor> findByPersonPersonId( Integer personId);

}
