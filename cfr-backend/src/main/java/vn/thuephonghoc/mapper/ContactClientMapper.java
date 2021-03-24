package vn.thuephonghoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.thuephonghoc.base.BaseMapper;
import vn.thuephonghoc.dto.ContactClientDto;
import vn.thuephonghoc.entity.ContactClient;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactClientMapper extends BaseMapper<ContactClientDto, ContactClient> {

}
