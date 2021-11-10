package top.yang.spring.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringUtils implements ApplicationContextAware {

  private final static Logger logger = LoggerFactory.getLogger(SpringUtils.class);
  private static ApplicationContext applicationContext;

  //获取applicationContext
  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * 获取对象
   *
   * @param name
   * @return Object 一个以所给名字注册的bean的实例
   * @throws org.springframework.beans.BeansException
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) throws BeansException {
    return (T) getApplicationContext().getBean(name);
  }

  /**
   * 获取类型为requiredType的对象
   *
   * @param clz
   * @return
   * @throws org.springframework.beans.BeansException
   */
  public static <T> T getBean(Class<T> clz) throws BeansException {
    return (T) getApplicationContext().getBean(clz);
  }

  //通过name,以及Clazz返回指定的Bean
  public static <T> T getBean(String name, Class<T> clazz) {
    return (T) getApplicationContext().getBean(name, clazz);
  }

  /**
   * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
   *
   * @param name
   * @return boolean
   */
  public static boolean containsBean(String name) {
    return getApplicationContext().containsBean(name);
  }

  /**
   * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
   *
   * @param name
   * @return boolean
   * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
   */
  public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return getApplicationContext().isSingleton(name);
  }

  /**
   * @param name
   * @return Class 注册对象的类型
   * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
   */
  public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
    return getApplicationContext().getType(name);
  }

  /**
   * 如果给定的bean名字在bean定义中有别名，则返回这些别名
   *
   * @param name
   * @return
   * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
   */
  public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
    return getApplicationContext().getAliases(name);
  }

  /**
   * 获取aop代理对象
   *
   * @param invoker
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T getAopProxy(T invoker) {
    return (T) AopContext.currentProxy();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (SpringUtils.applicationContext == null) {
      SpringUtils.applicationContext = applicationContext;
    }
    logger.info(
        "========ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext="
            + SpringUtils.applicationContext + "========");
  }
}