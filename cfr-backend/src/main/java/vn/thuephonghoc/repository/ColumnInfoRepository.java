package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.thuephonghoc.entity.ColumnInfo;

import java.util.List;


public interface ColumnInfoRepository extends JpaRepository<ColumnInfo,Long> {

	/**
     * Query form information
     * @param tableName table name
     * @return table information
     */
    List<ColumnInfo> findByTableNameOrderByIdAsc(String tableName);
}
