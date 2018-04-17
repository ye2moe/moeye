package cn.ye2moe.test.web.controller;

import cn.ye2moe.moeye.core.annotation.bean.Controller;
import cn.ye2moe.moeye.web.annotation.*;
import cn.ye2moe.test.web.https.CurrentCourse;
import cn.ye2moe.test.web.https.User;
import cn.ye2moe.test.web.https.Https;
import cn.ye2moe.test.web.service.HelloService;

@Controller
public class BaseController {

    @Autowired
    HelloService helloService ;

    @RequestMapping("query")
    @ResponseBody
    public String hello(@RequestParam("username")String username){
        return helloService.hello(username);
    }



    @RequestMapping("signin")
    @ResponseBody
    public String sign(
            @RequestParam(value = "username",require = true) String username
            ,@RequestParam(value = "cid",require = true) String cid
            ,@RequestParam(value = "cname",require = true) String cname){

        return Https.get(Https.signCourse(new User(username,1),new CurrentCourse(cid,cname)));

    }
}
