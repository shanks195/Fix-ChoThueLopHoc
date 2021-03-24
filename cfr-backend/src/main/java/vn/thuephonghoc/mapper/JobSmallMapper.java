package vn.thuephonghoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.thuephonghoc.base.BaseMapper;
import vn.thuephonghoc.dto.JobSmallDto;
import vn.thuephonghoc.entity.Job;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobSmallMapper extends BaseMapper<JobSmallDto, Job> {

}
