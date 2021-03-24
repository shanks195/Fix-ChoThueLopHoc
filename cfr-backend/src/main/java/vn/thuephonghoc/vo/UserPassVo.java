package vn.thuephonghoc.vo;

import lombok.Data;

/**
 * Vo class to modify password
 */
@Data
public class UserPassVo {

    private String oldPass;

    private String newPass;
}

