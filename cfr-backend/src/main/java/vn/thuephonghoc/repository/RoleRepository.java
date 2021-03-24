package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import vn.thuephonghoc.entity.Role;

import java.util.Set;

@SuppressWarnings("all")
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * @param name /
     * @return /
     */
    Role findByName(String name);

    /**
     * @param id
     * @return
     */
    Set<Role> findByUsers_Id(Long id);


    /**
     * @param id
     */
    @Modifying
    @Query(value = "delete from roles_menus where menu_id = ?1",nativeQuery = true)
    void untiedMenu(Long id);


    /**
     * @param permission /
     * @return /
     */
    Role findByPermission(String permission);
}

