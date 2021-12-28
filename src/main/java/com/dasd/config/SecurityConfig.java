package com.dasd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터 체인에 등록이 된다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();//<-사이트 간 위조 요청 방지를 disable한 것.

        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() //<- /user/**이면 인증 필요
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") //<- /manager/**이면 role이 manager 또는 admin 만 접근 가능
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")//<- /admin/** 이면 admin 인 사람만 접근 가능
                .anyRequest().permitAll()//그외의 요청은 다 허용.
                .and()
                .formLogin()
                .loginPage("/login");//인증이 필요한 url은 전부 /login으로 리다이렉트하도록 강제함.
    }
}
