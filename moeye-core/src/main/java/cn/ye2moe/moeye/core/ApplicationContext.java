package cn.ye2moe.moeye.core;

import cn.ye2moe.moeye.core.annotation.bean.Component;
import cn.ye2moe.moeye.core.annotation.bean.Controller;
import cn.ye2moe.moeye.core.annotation.bean.Service;
import cn.ye2moe.moeye.core.exception.NoSuchPropertiesException;
import cn.ye2moe.moeye.core.init.ApplicationContextAware;
import cn.ye2moe.moeye.core.util.ClassUtil;
import cn.ye2moe.moeye.core.util.MoeyeProperties;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ApplicationContext {

    private ApplicationContext() {

    }

    static final String PACKAGE_DOT = ".";
    static final String ROOT_PACKAGE = "";
    static final String CLASS_SUFFIX = ".class";

    static Logger logger = Logger.getLogger(ApplicationContext.class);

    private final static ApplicationContext context = new ApplicationContext();

    List<String> clzNames = new ArrayList<String>();

    private List<ApplicationContextAware> awares = new ArrayList<>();

    Map<Class<? extends Annotation> ,Map<String,Object>> instanceCache = new HashMap<>();


    Map<Class<? extends Annotation>,List<Class<?> >>annotationMap = new HashMap<>();

    MoeyeProperties properties;
    //配置文件信息
    public static void setProperties(String[] resources){
        if(context.properties != null)
            context.properties.addProperties(resources);
        else
            context.properties = new MoeyeProperties(resources);
    }
    public static void auto(){
        context.properties = new MoeyeProperties();
    }

    public static MoeyeProperties getProperties() throws NoSuchPropertiesException {
        if(context.properties == null){
            throw new NoSuchPropertiesException("can't find properties files ，配置文件未找到！");
        }
        return context.properties;
    }

    public static void init(String pkgName) {

        scanPackage(pkgName);

        filterAndInstance();

        aware();
    }

    private static void aware() {
        for (ApplicationContextAware aware : context.awares){
            aware.setApplicationContext();
            aware.afterPropertiesSet();
        }
    }

    public static final Map<String,Object> getBeansWithAnnotation(Class<? extends Annotation> annotationClass){
        if (MapUtils.isEmpty(context.annotationMap)) {
            logger.warn("instance map is empty");
            return Collections.emptyMap();
        }
        if(context.instanceCache.containsKey(annotationClass)){
            return context.instanceCache.get(annotationClass);
        }

        Map<String ,Object> instanceMap = new HashMap<>();

        for(Class clz : context.annotationMap.get(annotationClass)){
            try {
                logger.info("**********************"+clz.getName());
                instanceMap.put(clz.getName(),clz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if(instanceMap.size() > 0){
            context.instanceCache.put(annotationClass,instanceMap);
        }
        return instanceMap;
    }


    /**
     * 递归扫描包路径
     * 取出所有满足条件的 Class.getName()
     *
     * @param pkgName 包名
     */
    static void scanPackage(String pkgName) {

        context.clzNames = ClassUtil.getClassList(pkgName, true);
        /*
        URL url = null;//Thread.currentThread().getContextClassLoader().getResource(replaceDot2Separator(pack));
        if(url == null){

            clzNames = ClassUtil.getClassList("", true);
                //searchClass();
            System.out.println("===============size:" + clzNames.size());
            for(String s : clzNames){
                System.out.println("==============scan:"+s);
            }
            return;
        }
        if (ROOT_PACKAGE.equals(pack))
            logger.info(url.getFile());
        File fs = new File(url.getFile());
        for (File f : fs.listFiles()) {
            if (f.isDirectory()) {
                scanPackage(ROOT_PACKAGE.equals(pack) ? f.getName() : pack + PACKAGE_DOT + f.getName());
            } else {
                if (!f.getName().endsWith(CLASS_SUFFIX) || f.getName().contains("$"))
                    continue;
                //logger.warn(f.getName());
                clzNames.add(pack + PACKAGE_DOT + f.getName().replace(CLASS_SUFFIX, ""));
            }
        }*/
    }

    protected static void filterAndInstance(){
        for (String clzName : context.clzNames) {
            try {

                Class clz = Class.forName(clzName);
                if(clz.isAnnotation())
                    continue;
                Annotation [] ans = clz.getAnnotations();
                if(ans.length < 1 && clz.getInterfaces().length < 1)
                    continue;

                dealInterfaces(clz);

                for(Annotation a : ans){

                    //HashMap<String,Object> ins = context.annotationMap.getOrDefault(a.annotationType(),new HashMap<>());

                    //ins.put(clzName,obj);

                    //logger.debug(ins.get(clzName));
                    List list = context.annotationMap.getOrDefault(a.annotationType(),new ArrayList<>());
                    list.add(clz);
                    context.annotationMap.put(a.annotationType(),list);
                    //logger.debug(annotationMap.get(a.annotationType()));
                    //logger.debug(a.annotationType().getSimpleName() +" put");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private static boolean checkAnnotation(Class clz) {

        if(clz.isAnnotationPresent(Component.class)
                || clz.isAnnotationPresent(Controller.class)
                || clz.isAnnotationPresent(Service.class)
                ){
            return true;
        }
        return false;

    }

    private static void dealInterfaces(Class clz) {
        Class[] ifaces =  clz.getInterfaces();
        for(Class iface : ifaces){
            if(iface.equals(ApplicationContextAware.class)){
                try {
                    context.awares.add((ApplicationContextAware) clz.getDeclaredConstructor().newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String replaceDot2Separator(String path) {
        return path.replace(PACKAGE_DOT, File.separator);
    }

}
