package vn.thuephonghoc.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Used when building front-end routing
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuVo implements Serializable {

    private String name;

    private String path;

    private Boolean hidden;

    private String redirect;

    private String component;

    private Boolean alwaysShow;

    private MenuMetaVo meta;

    private List<MenuVo> children;
}
