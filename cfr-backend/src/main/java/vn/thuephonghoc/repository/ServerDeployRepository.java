package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import vn.thuephonghoc.entity.ServerDeploy;

public interface ServerDeployRepository extends JpaRepository<ServerDeploy, Long>, JpaSpecificationExecutor<ServerDeploy> {

    /**
     * @param ip /
     * @return /
     */
    ServerDeploy findByIp(String ip);
}
