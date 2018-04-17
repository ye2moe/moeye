package cn.ye2moe.moeye.web.core.server.ioc;

import cn.ye2moe.moeye.core.ApplicationContext;
import cn.ye2moe.moeye.core.annotation.bean.Service;
import cn.ye2moe.moeye.web.annotation.Autowired;
import cn.ye2moe.moeye.core.annotation.bean.Controller;
import cn.ye2moe.moeye.web.annotation.RequestMapping;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class InversionHold{
    private static InversionHold ourInstance ;

    private Logger logger = Logger.getLogger(InversionHold.class);

    //uri handler mapping
    private Map<String, Object[]> handlerMap = new HashMap<String, Object[]>();

    //className 对应的 实例
    private Map<String, Object> instanceMap = new HashMap<String, Object>();

    public static InversionHold getInversionHold(){
        if(ourInstance == null){
            throw new NullPointerException("InversionHold context is null!");
        }
        return ourInstance;
    }

    public static void setInversionHold(){
        ourInstance = new InversionHold();
    }

    //路径映射
    public final static Object[] handle(String uri) {
        getInversionHold().logger.info(uri);
        if(uri.contains("?"))
            uri = uri.split("[?]")[0];
        return getInversionHold().handlerMap.get(uri);
    }

    private InversionHold() {

        handleMapping();
        instance();
        ioc();

    }

    private void ioc() {
        logger.info("web ioc");
        for (Object instance : instanceMap.values()){
            try {
                Class clz = instance.getClass();
                for (Field f : clz.getDeclaredFields()) {
                    if (f.isAnnotationPresent(Autowired.class)) {
                        ioc(instance, f);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private void ioc(Object obj, Field f) throws IllegalAccessException {
        String value = f.getAnnotation(Autowired.class).value();
        Object instance = null;
        //logger.error(value + " " + f.getType().getSimpleName());
        if ("".equals(value)) {
            //defalut wired
            instance = instanceMap.get(f.getType().getSimpleName() + "Impl");
        } else {
            //user wired
            instance = instanceMap.get(toUpperFristChar(value));
        }
        if (instance == null) {
            logger.error("autowire fail:" + f.getName());
            return;
        }

        f.setAccessible(true);

        f.set(obj, instance);
    }

    private void instance() {

        Map<String,Object> map = ApplicationContext.getBeansWithAnnotation(Service.class);

        //logger.info("Service size :" + map.size());
        for(Object instance : map.values()){
            instanceMap.put(instance.getClass().getSimpleName(), instance);
        }
        //Object service = clz.getDeclaredConstructor().newInstance();
        map = ApplicationContext.getBeansWithAnnotation(Controller.class);
        for(Object instance : map.values()){
            instanceMap.put(instance.getClass().getSimpleName(), instance);
        }


    }

    private void handleMapping(){
        Map<String,Object> map = ApplicationContext.getBeansWithAnnotation(Controller.class);
        //logger.info("controller size :" + map.size());
        for(Object controller : map.values()){
            Class clz = controller.getClass();
            String cm = "";//class路径
            if (clz.isAnnotationPresent(RequestMapping.class)) {
                cm = ((RequestMapping) clz.getAnnotation(RequestMapping.class)).value();
            }
            Method methods[] = clz.getDeclaredMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(RequestMapping.class)) {
                    String rm = m.getAnnotation(RequestMapping.class).value();
                    handlerMap.put(plusUri(cm, rm), new Object[]{controller, m});
                }
            }
        }

    }

    private String plusUri(String cm, String rm) {
        final String ch = "/";
        if (!"".equals(cm) && !cm.startsWith(ch))
            cm = ch + cm;
        if (!rm.startsWith(ch))
            rm = ch + rm;
        return cm + rm;
    }

    public static String toUpperFristChar(String string) {
        char[] charArray = string.toCharArray();
        if (charArray[0] >= 97 && charArray[0] <= 122)
            charArray[0] -= 32;
        return String.valueOf(charArray);
    }

}
