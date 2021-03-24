package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.UserAvatar;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long>, JpaSpecificationExecutor<UserAvatar> {

}
