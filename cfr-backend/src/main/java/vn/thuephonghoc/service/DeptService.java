package vn.thuephonghoc.service;

import javax.servlet.http.HttpServletResponse;

import vn.thuephonghoc.dto.DeptDto;
import vn.thuephonghoc.entity.Dept;
import vn.thuephonghoc.query.DeptQueryCriteria;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface DeptService {

    /**
     * @param criteria
     * @return /
     */
    List<DeptDto> queryAll(DeptQueryCriteria criteria);

    /**
     * @param id /
     * @return /
     */
    DeptDto findById(Long id);

    /**
     * @param resources /
     * @return /
     */
    DeptDto create(Dept resources);

    /**
     * @param resources /
     */
    void update(Dept resources);

    /**
     * @param deptDtos /
     */
    void delete(Set<DeptDto> deptDtos);

    /**
     * @param deptDtos
     * @return /
     */
    Object buildTree(List<DeptDto> deptDtos);

    /**
     * @param pid /
     * @return /
     */
    List<Dept> findByPid(long pid);

    /**
     * @param id /
     * @return /
     */
    Set<Dept> findByRoleIds(Long id);

    /**
     * @param queryAll
     * @param response /
     * @throws IOException /
     */
    void download(List<DeptDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * @param deptList /
     * @param deptDtos /
     * @return /
     */
    Set<DeptDto> getDeleteDepts(List<Dept> deptList, Set<DeptDto> deptDtos);

}
