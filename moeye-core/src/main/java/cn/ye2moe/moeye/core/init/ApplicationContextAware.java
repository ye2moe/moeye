package cn.ye2moe.moeye.core.init;

public interface ApplicationContextAware {

    void setApplicationContext();

    void afterPropertiesSet();

}
