package vn.thuephonghoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.thuephonghoc.base.BaseMapper;
import vn.thuephonghoc.dto.IntroduceDto;
import vn.thuephonghoc.entity.Introduce;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IntroduceMapper extends BaseMapper<IntroduceDto, Introduce> {

}
