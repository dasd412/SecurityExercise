package com.dasd.config.auth;

import com.dasd.model.User;
import com.dasd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//시큐리티 설정에서 loginProcessingUrl("/login") 요청이 오면
//자동으로 UserDetailsService 타입으로 IOC 되어 있는 loadUserByUsername()이 호출된다.

@Service
public class PrincipalDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    //이 메소드의 파라미터와 loginForm.html 의 username 은 username 으로 일치해야 한다.
    //다르게 하려면 securityConfig 에서 설정 변경해야 함.
    //이 메소드의 리턴은 Authentication 타입 객체에 들어간다.
    //그리고 Authentication 타입 객체는 Security Session 에 들어간다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}
