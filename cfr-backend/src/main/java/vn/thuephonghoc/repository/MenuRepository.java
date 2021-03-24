package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Menu;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("all")
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    /**
     * @param name
     * @return /
     */
    Menu findByName(String name);


    /**
     * @param name
     * @return /
     */
    Menu findByComponentName(String name);

    /**
     * @param pid /
     * @return /
     */
    List<Menu> findByPid(long pid);

    /**
     * @param roleIds roleIDs
     * @param type
     * @return /
     */
    LinkedHashSet<Menu> findByRoles_IdInAndTypeNotOrderBySortAsc(Set<Long> roleIds, int type);
}

