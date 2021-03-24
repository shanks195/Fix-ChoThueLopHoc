package vn.thuephonghoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.thuephonghoc.base.BaseMapper;
import vn.thuephonghoc.dto.DatabaseDto;
import vn.thuephonghoc.entity.Database;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DatabaseMapper extends BaseMapper<DatabaseDto, Database> {

}
