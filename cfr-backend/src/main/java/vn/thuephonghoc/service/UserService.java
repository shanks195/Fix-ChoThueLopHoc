package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import vn.thuephonghoc.dto.UserDto;
import vn.thuephonghoc.entity.User;
import vn.thuephonghoc.query.UserQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService {

	/**
     * Query by ID
     * @param id ID
     * @return /
     */
    UserDto findById(long id);

    /**
     * New users
     * @param resources /
     * @return /
     */
    UserDto create(User resources);

    /**
     * Edit user
     * @param resources /
     */
    void update(User resources);

    /**
     * delete users
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * Query based on user name
     * @param userName /
     * @return /
     */
    UserDto findByName(String userName);

    /**
     * change Password
     * @param username username
     * @param encryptPassword password
     */
    void updatePass(String username, String encryptPassword);

    /**
     * Modify avatar
     * @param file file
     */
    void updateAvatar(MultipartFile file);

    /**
     * Modify email
     * @param username username
     * @param email
     */
    void updateEmail(String username, String email);

    /**
     * Enquire all
     * @param criteria conditions
     * @param pageable paging parameters
     * @return /
     */
    Object queryAll(UserQueryCriteria criteria, Pageable pageable);

    /**
     * All queries are not paged
     * @param criteria conditions
     * @return /
     */
    List<UserDto> queryAll(UserQueryCriteria criteria);

    /**
     * export data
     *
     * @param queryAll the data to be exported
     * @param response /
     * @throws IOException /
     */
    void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * User modify information
     * @param resources /
     */
    void updateCenter(User resources);
}

