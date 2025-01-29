package com.jxwebs.security.Controller;

import com.jxwebs.security.Comment.JsonWebTokenUtility;
import com.jxwebs.security.Entity.UsersEntity;
import com.jxwebs.security.Repository.UsersRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UsersController {

    @Autowired
    private JsonWebTokenUtility jwtUtility;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @PostMapping("/adduser")
    public Mono<ResponseEntity<String>> adduser(@RequestBody UsersEntity user) {
        // 檢查用戶名或密碼是否為空
        if (user.getUsername() == null || user.getPassword() == null) {
            System.out.println("沒有輸入帳號密碼");
            return Mono.just(ResponseEntity
                    .badRequest()
                    .body("沒有輸入帳號密碼"));
        }

        return usersRepository.findByUsername(user.getUsername())
                .flatMap(existingUser -> {
                    return Mono.just(ResponseEntity.ok("已經有使用者了"));
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            // 設置新用戶的用戶名和密碼
                            UsersEntity newUser = new UsersEntity();
                            newUser.setUsername(user.getUsername());
                            newUser.setPassword("{noop}"+user.getPassword());
                            newUser.setAuthorities("user");

                            // 保存新用戶
                            return usersRepository.save(newUser)
                                    .flatMap(savedUser -> {
                                        return Mono.just(ResponseEntity
                                                .status(HttpStatus.CREATED)
                                                .body("使用者創建成功"));
                                    });
                        })
                );
    }

    @PostMapping("/api/login")
    public Mono<ResponseEntity<String>> userLogin(@RequestBody UsersEntity user) {
        System.out.println("/api/login="+user.getUsername()  + user.getPassword());
        // 檢查用戶名或密碼是否為空
        if (user.getUsername() == null || user.getPassword() == null) {
            System.out.println("沒有輸入帳號密碼");
            return Mono.just(ResponseEntity
                    .badRequest()
                    .body("沒有輸入帳號密碼"));
        }

        return usersRepository.findByUsername(user.getUsername()) // 檢查用戶是否存在
                .flatMap(bean -> reactiveAuthenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
                        ).flatMap(auth -> {
                            // 認證成功後生成 token
                            String token = jwtUtility.createToken(user.getUsername(), bean.getAuthorities(), null);
                            // 準備返回值
                        System.out.println("token 有給："+token);
                        //     LoginResponseDTO response = new LoginResponseDTO(bean.getId(), token, bean.getUsername(), bean.getAuthorities());
                            JSONObject response = new JSONObject();
                            response.put("id",bean.getId());
                            response.put("token",token);
                            response.put("username",bean.getUsername());
                            response.put("authority",bean.getAuthorities());

                            return Mono.just(ResponseEntity.ok(response.toString())); // 直接返回成功響應
                        })
                        .onErrorResume(e -> {
                            // 如果認證失敗，返回未授權錯誤
                            return Mono.just(ResponseEntity
                                    .status(HttpStatus.UNAUTHORIZED)
                                    .body("認證失敗")); // 直接返回錯誤響應
                        }))
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("沒有這個使用者"))); // 直接返回未找到錯誤
    }


    // LoginResponse 類






}
