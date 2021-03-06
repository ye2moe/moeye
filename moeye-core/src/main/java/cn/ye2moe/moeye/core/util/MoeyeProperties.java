package cn.ye2moe.moeye.core.util;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 配置中心
 */
public class MoeyeProperties {

    Properties properties = new Properties(); //配置管理map
    String[] sourcesPath;       //配置文件路径


    HashMap<String,Object> readRecordMap = new HashMap<>(); //读取记录

    // 正则匹配如 aa${xxx} , ${yyy}
    static final String pattern = "\\$\\{(.*)\\}";
    static final Pattern p = Pattern.compile(pattern);

    /**
     * 字符串中是否存在 ${xxx}
     * @param value
     * @return
     */
    public static boolean hasProperties(String value){
        return p.matcher(value).find();
    }

    /**
     * 扫描整个resources
     */
    public MoeyeProperties(){
        List<String> fs = scanAll();
        fs.forEach(f->setProperties(f));
    }

    private List<String> scanAll() {
        List<String> fs = new ArrayList<>();

        try {
            String url = System.getProperty("user.dir");//this.getClass().getResource(File.separator).getFile();
            url = URLDecoder.decode(url,"UTF-8");
            System.out.println(url);
            reScan(fs,url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fs;
    }
    private void reScan(List<String> fs , String url) throws UnsupportedEncodingException {

        File ff = new File(url);
        for(File f : ff.listFiles()){
            String utf8Url = URLDecoder.decode(f.getPath(),"UTF-8");
            if(f.isDirectory())
                reScan(fs,utf8Url);
            else{
                if(!f.getName().endsWith(".properties"))
                    continue;
                String file  =utf8Url.replace(URLDecoder.decode(System.getProperty("user.dir")+File.separator,"utf-8"),"");
                fs.add(file);
                System.out.println("***** "+file);
            }
        }
    }

    public MoeyeProperties(String[] sourcesPath) {
        this.sourcesPath = sourcesPath;
        scanProperties(sourcesPath);
    }

    /**
     * 如 *${port}* 将其中的 ${port} 替换为配置文件中 port 的值 假设该值为100 则返回 *100*
     * @param value 带替换的字符串
     * @return 替换结果
     */
    public String getMatcherReplace(String value) {
        Matcher matcher = MoeyeProperties.p.matcher(value);
        if (matcher.find()) {
            String key = matcher.group(1);
            String val = properties.getProperty(key);
            if(val==null)return"";
            value = matcher.replaceFirst(val);
        }
        return value;
    }

    /**
     * 追加配置文件
     * @param files
     */
    public void addProperties(String []files){
        scanProperties(files);
        sourcesPath = Arrays.copyOf(sourcesPath, sourcesPath.length + files.length);// 扩容
        System.arraycopy(files, 0, sourcesPath, sourcesPath.length, files.length);
    }
    /**
     * 扫描配置文件
     */
    private void scanProperties(String []files) {
        for (String p : files) {
            if(setProperties(p)){
                readRecordMap.put(p,null);
            }
        }
    }

    private boolean setProperties(String p) {
        //读取过的文件不会再次读取
        if(readRecordMap.containsKey(p))
            return false;
        p  =  System.getProperty("user.dir") + File.separatorChar +p;
        //System.out.println(File.separatorChar + p);
        //System.out.println(ClassLoader.getSystemResource(File.separatorChar + p));
        String path =p;//ClassLoader.getSystemResource(File.separatorChar + p).getPath();
        try (FileInputStream inputFile = new FileInputStream(path)) {
            Properties properties = new Properties();
            properties.load(inputFile);
            this.properties.putAll(properties);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获得配置对应的值 如 port=1000
     * @param key 属性
     * @return 值
     */
    public String get(String key) {
        return properties.getProperty(key);
    }
}
