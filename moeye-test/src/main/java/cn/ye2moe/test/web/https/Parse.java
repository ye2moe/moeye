package cn.ye2moe.test.web.https;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
    public static final String NOT_FIND = "not-find";
    String content;
    public Parse(String content){
        this.content = content;
    }

    @Override
    public String toString() {
        return "Parse{" +
                "content='" + content + '\'' +
                '}';
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
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



    public String jsonRegExp(String key){

        // 正则表达式规则  \"page_index\":(.*?)[,}]/g   /"data":([\d-.+]⁺)/g
        String regEx = "\\\""+key+"\\\":(.*?)[,}]";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(content);

        int count = matcher.groupCount();
        //System.out.println("key:"+key+"   count:"+count);
        if(matcher.find()){
            String find = matcher.group(0).replace(",","").replace("}","");

            find = find.split("\":")[1];

            return find.replace("\"","");
        }else{
            return NOT_FIND;
        }
    }



}
