package cn.ye2moe.moeye.core;

import cn.ye2moe.moeye.core.annotation.AutoConfig;
import cn.ye2moe.moeye.core.annotation.Resources;
import cn.ye2moe.moeye.core.annotation.server.RPCServer;
import cn.ye2moe.moeye.core.annotation.server.WebServer;
import cn.ye2moe.moeye.core.exception.NoSuchPropertiesException;
import cn.ye2moe.moeye.core.server.MoeyeServer;
import cn.ye2moe.moeye.core.util.Constant;
import cn.ye2moe.moeye.core.util.MoeyeProperties;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

public class MoeyeApplication {


    static Logger logger = Logger.getLogger(MoeyeApplication.class);

    static boolean isWebStart = false;

    public static void run(Class<?> clz, String[] args) {

        //自动配置
        if (clz.isAnnotationPresent(AutoConfig.class)) {
            ApplicationContext.auto();
        }
        //配置文件
        if (clz.isAnnotationPresent(Resources.class)) {
            Resources reAno = clz.getAnnotation(Resources.class);
            ApplicationContext.setProperties(reAno.value());
        }


        String rootPackage = getRootPckName(clz);
        ApplicationContext.init(rootPackage);

        //开启rpc服务器
        if (clz.isAnnotationPresent(RPCServer.class)) {
            RPCServer serverA = clz.getAnnotation(RPCServer.class);
            rpcServerStart();
        }

        //开启web服务器
        if (clz.isAnnotationPresent(WebServer.class)) {
            WebServer serverA = clz.getAnnotation(WebServer.class);
            WebServerStart(serverA.value());
        }

    }

    private static void rpcServerStart() {
        try {
            Class clz = Class.forName(Constant.RPC_SERVER);

            MoeyeServer server = (MoeyeServer) clz.getDeclaredConstructor().newInstance();

            String info = ApplicationContext.getProperties().get(Constant.ZOOKEEPER_SETTING)
                    +";" +ApplicationContext.getProperties().get(Constant.RPC_SERVICE_SETTING);
            if(info.contains("null")){
                logger.error("rpc server start error，can not find config file !");
                return ;
            }
            server.start(info);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchPropertiesException e) {
            logger.warn("rpc server 启动失败，未找到配置文件!");
        }


    }

    private static void WebServerStart(String value) {
        value = propertiesCheck(value);
        try {
            web(Integer.parseInt(value));
        }catch (NumberFormatException ex){
            logger.error(" web Server 格式錯誤: " +value);
        }
    }

    private static String propertiesCheck(String value) {
        if (MoeyeProperties.hasProperties(value)) {
            try {
                value = ApplicationContext.getProperties().getMatcherReplace(value);
            } catch (NoSuchPropertiesException e) {
                logger.warn(e.getMessage());
            }
        }
        return value;
    }

    public static void web(int port) {
        if (isWebStart) {
            return;
        }
        isWebStart = true;
        try {
            Class clz = Class.forName(Constant.HTTP_SERVER);

            MoeyeServer server = (MoeyeServer) clz.getDeclaredConstructor().newInstance();

            server.start(port);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得项目package根路径
     *
     * @param webTestClass
     * @return
     */
    private static String getRootPckName(Class<?> webTestClass) {
        String packageName = webTestClass.getPackage().getName();
        String rootPackage = packageName;
        if (packageName.contains("."))
            rootPackage = packageName.split("\\.")[0];
        return rootPackage;
    }
}
