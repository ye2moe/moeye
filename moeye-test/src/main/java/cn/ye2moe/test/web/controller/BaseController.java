package cn.ye2moe.test.web.controller;

import cn.ye2moe.moeye.core.annotation.bean.Controller;
import cn.ye2moe.moeye.web.annotation.*;
import cn.ye2moe.test.web.service.HelloService;

@Controller
public class BaseController {

    @Autowired
    HelloService helloService ;

    @RequestMapping("hello")
    @ResponseBody
    public String hello(@IP String ip){
        return ip;
    }



    @RequestMapping("none")
    @ResponseBody
    public String sign(){

        return "qwe";

    }
}
