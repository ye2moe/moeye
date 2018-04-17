package cn.ye2moe.test.web;

import cn.ye2moe.moeye.core.MoeyeApplication;
import cn.ye2moe.moeye.core.annotation.AutoConfig;
import cn.ye2moe.moeye.core.annotation.server.WebServer;
import cn.ye2moe.moeye.core.init.ApplicationContextAware;


@WebServer("${web.server.port}")
@AutoConfig
public class WebTest implements ApplicationContextAware{

    public static  void main(String []args) throws Exception{
        MoeyeApplication.run(WebTest.class, args);
    }

    @Override
    public void setApplicationContext() {

        System.out.println("************** Init *****************");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("************** After Init *****************");
    }
}
