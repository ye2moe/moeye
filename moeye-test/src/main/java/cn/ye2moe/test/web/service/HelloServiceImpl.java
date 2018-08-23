package cn.ye2moe.test.web.service;

import cn.ye2moe.moeye.core.annotation.bean.Service;

@Service
public class HelloServiceImpl implements HelloService {
    public String hello(String username) {
        return "hehe";
    }
}
