package com.jxwebs.security.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @GetMapping("/test")
    public Mono<String> test(){
        return Mono.just("測試成功");
    }

    @GetMapping("/testboss")
    public Mono<String> testboss(){
        return Mono.just("測試 testboss 成功");
    }

}
