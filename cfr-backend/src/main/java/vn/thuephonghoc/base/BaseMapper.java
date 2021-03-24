package vn.thuephonghoc.base;

import java.util.List;

public interface BaseMapper<D, E> {

    /**
     * DTO Entity
     */
    E toEntity(D dto);

    /**
     * Entity DTO
     */
    D toDto(E entity);

    /**
     * DTO Entity
     */
    List <E> toEntity(List<D> dtoList);
 
    /**
     * EntityDTO
     */
    List<D> toDto(List<E> entityList);
}
