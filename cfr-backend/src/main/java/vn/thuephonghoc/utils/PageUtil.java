package vn.thuephonghoc.utils;

import org.springframework.data.domain.Page;
import java.util.*;

/**
 * Paging tool
 */
public class PageUtil extends cn.hutool.core.util.PageUtil {

    /**
     * List pagination
     */
    public static List toPage(int page, int size, List list) {
        int fromIndex = page * size;
        int toIndex = page * size + size;

        if(fromIndex> list.size()){
            return new ArrayList();
        } else if(toIndex >= list.size()) {
            return list.subList(fromIndex,list.size());
        } else {
            return list.subList(fromIndex,toIndex);
        }
    }

    /**
     * Page data processing to prevent redis deserialization errors
     */
    public static Map<String,Object> toPage(Page page) {
        Map<String,Object> map = new LinkedHashMap<>(2);
        map.put("content",page.getContent());
        map.put("totalElements",page.getTotalElements());
        return map;
    }

    /**
     * Custom pagination
     */
    public static Map<String,Object> toPage(Object object, Object totalElements) {
        Map<String,Object> map = new LinkedHashMap<>(2);
        map.put("content",object);
        map.put("totalElements",totalElements);

        return map;
    }

}

