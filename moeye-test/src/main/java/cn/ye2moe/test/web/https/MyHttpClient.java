package cn.ye2moe.test.web.https;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author yezwei（zhiwei.ye@ucarinc.com）
 * @version 2018/8/23
 * @date 2018/08/23 10:37
 */
public class MyHttpClient {

    public static String requset(String url)   {
        System.out.println(url);
        StringBuilder sb = new StringBuilder();
        URL reqURL = null; // 创建URL对象
        try {
            reqURL = new URL(url);
            HttpsURLConnection httpsConn = (HttpsURLConnection) reqURL
                    .openConnection();
            //取得该连接的输入流，以读取响应内容
            InputStreamReader isr = new InputStreamReader(httpsConn.getInputStream());

            //创建字符流缓冲区
            BufferedReader bufr = new BufferedReader(isr);//缓冲
            String line;
            while((line = bufr.readLine())!=null){
                sb.append(line);
            }
            isr.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static String get(String url){
        System.out.println(url);
        StringBuilder sb = new StringBuilder();
        URL reqURL = null; //创建URL对象
        try {
            //创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager ()};
            SSLContext sslContext = SSLContext.getInstance("SSL","SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());

            //从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            reqURL = new URL(url);
            HttpsURLConnection httpsConn = (HttpsURLConnection)reqURL.openConnection();

            //取得该连接的输入流，以读取响应内容
            InputStreamReader isr = new InputStreamReader(httpsConn.getInputStream());

            //创建字符流缓冲区
            BufferedReader bufr = new BufferedReader(isr);//缓冲
            String line;
            while((line = bufr.readLine())!=null){
                sb.append(line);
            }
            isr.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
