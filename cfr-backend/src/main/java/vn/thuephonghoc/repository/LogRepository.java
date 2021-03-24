package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.thuephonghoc.entity.Log;


@Repository
public interface LogRepository extends JpaRepository<Log, Long>, JpaSpecificationExecutor<Log> {

    /**
     */
    @Query(value = "select count(*) from (select request_ip FROM log where create_time between ?1 and ?2 GROUP BY request_ip) as s",nativeQuery = true)
    Long findIp(String date1, String date2);

    /**
     * @param logType
     */
    @Query(nativeQuery = true,value = "delete from log where log_type = ?1")
    @Modifying
    void deleteByLogType(String logType);

}
