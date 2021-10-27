package top.yang.utils;


import java.util.List;
import top.yang.domain.dto.PageResult;

/**
 * @author Pride_Yang
 */
public class PageUtils {

  public static <T> PageResult<T> buildPage(org.springframework.data.domain.Page<T> page) {
    return new PageResult<T>(page.getTotalPages(), page.getTotalElements(), page.getContent(),
        page.getNumber());
  }

  public static <T> PageResult<T> buildPage(List<T> content, Integer totalPages, Long totalElement,
      Integer currentPage) {
    if (totalElement <= 0) {
      return new PageResult<>();
    }
    return new PageResult<T>(totalPages, totalElement, content, currentPage);
  }

  public static int getStart(Integer page, Integer pageSize) {
    return (page - 1) * pageSize;
  }
}
