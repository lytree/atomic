package top.yang.web.utils;

import org.springframework.data.domain.Page;
import top.yang.admin.pojo.vo.PageResult;

import java.util.List;

/**
 * @author Pride_Yang
 */
public class PageUtils {
    public static <T> PageResult<T> buildPage(Page<T> page) {
        return new PageResult<T>(page.getTotalElements(), page.getContent(), page.getNumber(), page.getSize());
    }

    public static <T> PageResult<T> buildPage(List<T> content, Integer page, Long count, Integer pageSize) {
        if (count <= 0) {
            return new PageResult<>();
        }
        return new PageResult<T>(count, content, page, pageSize);
    }

    public static int getStart(Integer page, Integer pageSize) {
        return (page - 1) * pageSize;
    }
}
