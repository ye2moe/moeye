package cn.ye2moe.test.web.service;

import cn.ye2moe.moeye.core.annotation.bean.Service;
import cn.ye2moe.test.web.https.User;
import cn.ye2moe.test.web.https.Https;

@Service
public class HelloServiceImpl implements HelloService {
    public String hello(String username) {
        return Https.get(Https.currentAndDayCourses(new User(username,1)));
    }
}
