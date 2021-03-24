package ${package}.service;

import ${package}.entity.${className};
import ${package}.dto.${className}Dto;
import ${package}.query.${className}QueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface ${className}Service {

    /**
    * @param criteria 
    * @param pageable 
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(${className}QueryCriteria criteria, Pageable pageable);

    /**
    * @param criteria 
    * @return List<${className}Dto>
    */
    List<${className}Dto> queryAll(${className}QueryCriteria criteria);

    /**
    * @param ${pkChangeColName} ID
    * @return ${className}Dto
    */
    ${className}Dto findById(${pkColumnType} ${pkChangeColName});

    /**
    * @param resources /
    * @return ${className}Dto
    */
    ${className}Dto create(${className} resources);

    /**
    * @param resources /
    */
    void update(${className} resources);

    /**
    * @param all
    * @param response /
    * @throws IOException /
    */
    void download(List<${className}Dto> all, HttpServletResponse response) throws IOException;

    /**
    * @param ids /
    */
    void deleteAll(${pkColumnType}[] ids);
}
