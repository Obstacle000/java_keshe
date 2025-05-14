package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.DictionaryInfo;
import cn.edu.sdu.java.server.models.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    @Query(value= " from Material where parent.materialId is null or parent.materialId = 0")
    List<Material> findRootList();

    List<Material> findByParent(Material parent);
}
