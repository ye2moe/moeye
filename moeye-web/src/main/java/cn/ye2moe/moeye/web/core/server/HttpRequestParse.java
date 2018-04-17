package cn.ye2moe.moeye.web.core.server;

import io.netty.handler.codec.http.HttpRequest;

public class HttpRequestParse {
    public static String getRequestParam(HttpRequest request, String param){
        return getRequestParam(request.uri(),param);
    }

    public static String getRequestParam(String url,String key) {
        if(!url.contains("?"))return "";
        String strUrlParam = url.split("[?]")[1];
        if (strUrlParam == null) {
            return "";
        }
        String [] arrSplit;
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                if(key.equals(arrSplitEqual[0])){
                    return  arrSplitEqual[1];
                }
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                }
            }
        }
        return "";
    }

}
