package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.thuephonghoc.entity.Dept;

import java.util.List;
import java.util.Set;

@SuppressWarnings("all")
public interface DeptRepository extends JpaRepository<Dept, Long>, JpaSpecificationExecutor<Dept> {

    /**
     * @param id pid
     * @return 、
     */
    List<Dept> findByPid(Long id);

    /**
     * @param id ID
     * @return /
     */
    @Query(value = "select name from dept where id = ?1",nativeQuery = true)
    String findNameById(Long id);

    /**
     * @param id
     * @return /
     */
    Set<Dept> findByRoles_Id(Long id);
}
