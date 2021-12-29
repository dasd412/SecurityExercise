package com.dasd.config.auth;

//시큐리티가 /login 주소요청이 오면 낚아채서 로그인을 진행시킴.
//로그인 진행 완료가 되면 시큐리티 세션을 만들어준다. (Security ContextHolder 내에 만들어짐)
//이 Security ContextHolder 내에 들어갈 수 있는 객체는 Authentication 타입 뿐이다.
//그런데 Authentication 안에 User 정보가 있어야 된다.
//그리고 이 User 객체의 타입은 UserDetails 타입이여야 한다.

//Security Session => Authentication => UserDetails

import com.dasd.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private final User user;//합성을 이용

    public PrincipalDetails(User user) {
        this.user = user;
    }

    //해당 User 의 권한을 리턴하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();

        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀 번호 만기 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 활성화 여부.
    //휴면 계정 전환할 때 쓰임.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
