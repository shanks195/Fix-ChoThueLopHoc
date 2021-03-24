package vn.thuephonghoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.thuephonghoc.base.BaseMapper;
import vn.thuephonghoc.dto.CaptchaDto;
import vn.thuephonghoc.entity.Captcha;


@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CaptchaMapper extends BaseMapper<CaptchaDto, Captcha> {

}
