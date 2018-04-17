package beanTest;

import cn.ye2moe.moeye.core.ApplicationContext;
import cn.ye2moe.moeye.core.annotation.bean.Component;
import cn.ye2moe.moeye.core.annotation.bean.Service;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ApplicationTest {

    @Test
    public void appTest(){
        Map<String,Object> map = ApplicationContext.getBeansWithAnnotation(Component.class);

        Assert.assertTrue( map .size() == 1);

        map = ApplicationContext.getBeansWithAnnotation(Service.class);

        Assert.assertTrue( map .size() == 1);


    }


}
