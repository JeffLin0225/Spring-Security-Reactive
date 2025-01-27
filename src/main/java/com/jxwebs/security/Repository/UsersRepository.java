package com.jxwebs.security.Repository;

import com.jxwebs.security.Entity.UsersEntity;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsersRepository extends ReactiveCrudRepository<UsersEntity, Integer> {
    @Query("SELECT * FROM users WHERE username = :username")
    Mono<UsersEntity> findByUsername (String username);
}
