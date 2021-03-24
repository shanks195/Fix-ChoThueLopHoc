package vn.thuephonghoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.thuephonghoc.base.BaseMapper;
import vn.thuephonghoc.dto.HomeDto;
import vn.thuephonghoc.entity.Home;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HomeMapper extends BaseMapper<HomeDto, Home> {

}
