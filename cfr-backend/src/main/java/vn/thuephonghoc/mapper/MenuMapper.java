package vn.thuephonghoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.thuephonghoc.base.BaseMapper;
import vn.thuephonghoc.dto.MenuDto;
import vn.thuephonghoc.entity.Menu;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper extends BaseMapper<MenuDto, Menu> {

}
