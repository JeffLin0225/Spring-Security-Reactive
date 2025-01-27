package com.jxwebs.security.Config;

import com.jxwebs.security.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        System.out.println("查找用戶名: " + username);
        // 将查询操作包装在 Mono 中以支持响应式编程
        return usersRepository.findByUsername(username) // 异步查询用户
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("沒有這個使用者喔"))) // 如果没有找到用户，抛出异常
                .map(user -> new User(user.getUsername(), user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(user.getAuthorities()))));
    }
}