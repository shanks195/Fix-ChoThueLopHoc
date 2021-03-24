package vn.thuephonghoc.entity;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;


@Entity
@Data
@Table(name="captcha")
public class Captcha implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "type",nullable = false)
    @NotNull
    private Integer type;

    @Column(name = "font_name")
    private String fontName;

    @Column(name = "font_style")
    private Integer fontStyle;

    @Column(name = "font_size")
    private Integer fontSize;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "len")
    private Integer len;

    public void copy(Captcha source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
