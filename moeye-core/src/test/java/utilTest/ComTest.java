package utilTest;

import cn.ye2moe.moeye.core.ApplicationContext;
import cn.ye2moe.moeye.core.util.MoeyeProperties;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
public class ComTest {

    @Test
    public void spTest(){
        String content = "127.0.0.1:${port}";

        String pattern = "\\$\\{(.*)\\}";

        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(content);
        if(matcher.find())
        System.out.println(matcher.group(0));

        assertTrue(matcher.find());

        System.out.println(matcher.replaceAll("8080"));

        content = content.replaceAll(pattern,"8080");

        System.out.println(content);
        assertTrue(content.equalsIgnoreCase("127.0.0.1:8080"));
    }

    @Test
    public void test2(){


        MoeyeProperties m = new MoeyeProperties();
    }
}
