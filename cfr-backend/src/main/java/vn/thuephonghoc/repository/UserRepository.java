package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import vn.thuephonghoc.entity.User;

import java.util.Date;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * @param username
     * @return /
     */
    User findByUsername(String username);

    /**
     * @param email
     * @return /
     */
    User findByEmail(String email);

    /**
     * @param username 
     * @param pass
     * @param lastPasswordResetTime /
     */
    @Modifying
    @Query(value = "update user set password = ?2 , last_password_reset_time = ?3 where username = ?1",nativeQuery = true)
    void updatePass(String username, String pass, Date lastPasswordResetTime);

    /**
     * @param username
     * @param email
     */
    @Modifying
    @Query(value = "update user set email = ?2 where username = ?1",nativeQuery = true)
    void updateEmail(String username, String email);

}

