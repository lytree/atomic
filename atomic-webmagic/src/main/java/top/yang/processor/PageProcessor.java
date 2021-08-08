package top.yang.processor;

import top.yang.Page;
import top.yang.Site;

public interface PageProcessor {

  /**
   * process the page, extract urls to fetch, extract the data and store
   *
   * @param page page
   */
  public void process(Page page);

  /**
   * get the site settings
   *
   * @return site
   * @see Site
   */
  public Site getSite();
}
