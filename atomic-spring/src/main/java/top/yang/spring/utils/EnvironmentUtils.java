package top.yang.spring.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtils implements EnvironmentAware {

  private static Environment environment;
  private static final String PROD = "prod";
  private static final String DEV = "dev";
  private static final String TEST = "test";

  public static boolean isProd() {
    return getEnvironment().acceptsProfiles(Profiles.of(PROD));
  }

  public static boolean isDev() {
    return getEnvironment().acceptsProfiles(Profiles.of(DEV));
  }

  public static boolean isTest() {
    return getEnvironment().acceptsProfiles(Profiles.of(TEST));
  }


  public static String[] getActiveProfiles() {
    return getEnvironment().getActiveProfiles();
  }

  public static String getProperty(String key) {
    return getEnvironment().getProperty(key);
  }

  public static boolean containsProperty(String key) {
    return getEnvironment().containsProperty(key);
  }

  public static String getProperty(String key, String defaultValue) {
    return getEnvironment().getProperty(key, defaultValue);
  }

  public static <T> T getProperty(String key, Class<T> targetType) {
    return getEnvironment().getProperty(key, targetType);
  }

  public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
    return getEnvironment().getProperty(key, targetType, defaultValue);
  }

  public static String getRequiredProperty(String key) {
    return getEnvironment().getRequiredProperty(key);
  }

  public static <T> T getRequiredProperty(String key, Class<T> targetType) {
    return getEnvironment().getRequiredProperty(key, targetType);
  }

  public static Environment getEnvironment() {
    return environment;
  }

  @Override
  public void setEnvironment(Environment environment) {
    EnvironmentUtils.environment = environment;
  }
}
