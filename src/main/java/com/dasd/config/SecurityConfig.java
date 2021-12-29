package com.dasd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터 체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)// secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean    //<- 해당 메서드의 리턴되는 오브젝트를 빈으로 등록해줌.
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();//<-비밀번호를 암호화해준다.
    }

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
                .loginPage("/loginForm")//인증이 필요한 url은 전부 /login으로 리다이렉트하도록 강제함.
                .loginProcessingUrl("/login")// 로그인 주소가 호출이 되면 시큐리티가 인터셉트해서 대신 로그인 진행해줌. 이렇게 하면 컨트롤러에서 /login 매핑할 필요가 없음.
                .defaultSuccessUrl("/")//로그인 하게 되면 "/" url 로 이동됨. 단, 진입하고자 했던 url이 있었으면 해당 url로 이동함.
                .and()
                .oauth2Login()//oauth 로그인 역시 /loginForm 으로 진행하도록 함.
                .loginPage("/loginForm");
    }
}
