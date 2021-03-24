package vn.thuephonghoc.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Jwt parameter configuration
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class SecurityProperties {

    /** Request Headers ： Authorization */
    private String header;

    /** Token prefix, leave a space at the end Bearer */
    private String tokenStartWith;

    /** The token must be encoded with a Base64 of at least 88 bits */
    private String base64Secret;

    /** Token expiration time here unit/ms */
    private Long tokenValidityInSeconds;

    /** Online user key, query the data of online users in redis according to the key */
    private String onlineKey;

    /** Verification code key */
    private String codeKey;

    public String getTokenStartWith() {
        return tokenStartWith + " ";
    }
}
