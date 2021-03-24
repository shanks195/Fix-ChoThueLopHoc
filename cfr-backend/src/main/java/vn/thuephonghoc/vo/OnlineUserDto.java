package vn.thuephonghoc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineUserDto {

	/**
     * username
     */
    private String userName;

    /**
     * Nickname
     */
    private String nickName;

    /**
     * Position
     */
    private String job;

    /**
     * Browser
     */
    private String browser;

    /**
     * IP
     */
    private String ip;

    /**
     * Address
     */
    private String address;

    /**
     * token
     */
    private String key;

    /**
     * Log in time
     */
    private Date loginTime;


}
