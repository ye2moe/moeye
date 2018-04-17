package cn.ye2moe.moeye.web.core.server;

import cn.ye2moe.moeye.web.annotation.RequestParam;
import cn.ye2moe.moeye.web.annotation.ResponseBody;
import cn.ye2moe.moeye.web.core.server.ioc.InversionHold;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger =  Logger.getLogger(HttpServerHandler.class);


    private final static String LOC = "302";
    private final static String NOT_FOND = "404";
    private final static String BAD_REQUEST = "400";
    private final static String INTERNAL_SERVER_ERROR = "500";
    private static Map<String, HttpResponseStatus> mapStatus = new HashMap<String, HttpResponseStatus>();

    static {
        mapStatus.put(LOC, HttpResponseStatus.FOUND);
        mapStatus.put(NOT_FOND, HttpResponseStatus.NOT_FOUND);
        mapStatus.put(BAD_REQUEST, HttpResponseStatus.BAD_REQUEST);
        mapStatus.put(INTERNAL_SERVER_ERROR, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            boolean keepaLive = HttpUtil.isKeepAlive(request);
            //System.out.println("method " + request.method());
            String uri = request.uri().replace("/", "").trim();
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            if (mapStatus.get(uri) != null) {
                response.setStatus(mapStatus.get(uri));
                response.content().writeBytes(mapStatus.get(uri).toString().getBytes());
            } else {
                doRequest(request,response);
            }
            //重定向处理
            /*
            if (response.status().equals(HttpResponseStatus.FOUND)) {
                response.headers().set(HttpHeaderNames.LOCATION, "https://www.baidu.com/");
            }*/
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            if (keepaLive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(response);
            } else {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private void doRequest(HttpRequest request, FullHttpResponse response) {
        logger.info(String.format("uri:[%s]" , request.uri()));
        Object objs[] = InversionHold.handle(request.uri());

        if (objs == null) {
            logger.warn(request.uri() + ": no mapping");
            return;
        }
        try {
            invoke(objs[0],(Method) objs[1], request, response);
            logger.info(String.format("method:[%s]" , ((Method) objs[1]).getName()));
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage());
        } catch (NoSuchParameter noSuchParameter) {
            logger.error(noSuchParameter.getMessage());
        }
    }

    private static void invoke(Object service, Method execute, HttpRequest request, FullHttpResponse response) throws NoSuchParameter, IllegalAccessException, InvocationTargetException {
        /*
        String method = httpRequest.method().name();
        if (methodCheck(service.getClass(), method)) return;

        if(service.getClass().isAnnotationPresent(PostMapping.class)){
            if(method != "GET"){
                System.out.println(service.getClass().getSimpleName()+"："+ method +" method not support!");
                return;
            }
        }
        */
        Annotation ass[][] = execute.getParameterAnnotations();
        Class parameterTypes[] = execute.getParameterTypes();
        Object[] params = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            //参数是否有注解
            Class pt = parameterTypes[i];
            //System.out.println(pt.getName());
            for (Annotation p : ass[i]) {
                if (p.annotationType().equals(RequestParam.class)) {
                    RequestParam requestParam = (RequestParam) p;
                    //根据 requestParam.value() 获得参数
                    String username = HttpRequestParse.getRequestParam(request, requestParam.value());
                    if (requestParam.require() && "".equals(username))
                        throw new NoSuchParameter(requestParam.value() + " param not contains");
                    params[i] = username;
                }
            }
            if (pt.equals(HttpResponse.class)) {
                params[i] = response;
            } else if (pt.equals(HttpRequest.class)) {
                params[i] = request;
            }
        }

        //throw new IllegalArgumentException("argument type mismatch");


        Object obj = execute.invoke(service, params);

        boolean isJsonBack = false;

        for (Annotation methodAnno : execute.getAnnotations()) {
            if (methodAnno.annotationType().equals(ResponseBody.class)) {
                response.content().writeBytes(obj.toString().getBytes());
                isJsonBack = true;
            }
        }
        if(!isJsonBack){
            response.setStatus(HttpResponseStatus.FOUND);
            response.headers().set(HttpHeaderNames.LOCATION, obj);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println(cause.getMessage());
        //logger.error(cause.getMessage());
        ctx.close();
    }

}