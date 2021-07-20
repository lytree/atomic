package top.yang.utils;


import java.util.List;
import top.yang.pojo.Page;

/**
 * @author Pride_Yang
 */
public class PageUtils {

  public static <T> Page<T> buildPage(org.springframework.data.domain.Page<T> page) {
    return new Page<T>(page.getTotalPages(), page.getTotalElements(), page.getContent(),
        page.getNumber());
  }

  public static <T> Page<T> buildPage(List<T> content, Integer totalPages, Long totalElement,
      Integer currentPage) {
    if (totalElement <= 0) {
      return new Page<>();
    }
    return new Page<T>(totalPages, totalElement, content, currentPage);
  }

  public static int getStart(Integer page, Integer pageSize) {
    return (page - 1) * pageSize;
  }
}
