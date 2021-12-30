package com.dasd.config.oauth;

import com.dasd.config.auth.PrincipalDetails;
import com.dasd.model.User;
import com.dasd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;
    
    //구글로부터 받은 userRequest 데이터에 대한 후처리를 담당하는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration : "+userRequest.getClientRegistration());
        System.out.println("getAccessToken : "+userRequest.getAccessToken());

        OAuth2User oAuth2User=super.loadUser(userRequest);
        System.out.println("getAttributes : "+oAuth2User.getAttributes());


        String provider=userRequest.getClientRegistration().getClientId();// google
        String providerId= (String) oAuth2User.getAttributes().get("sub");
        String username=provider+"_"+providerId;//username 은 유니크해야하므로 이와 같이 만듬.
        String password=bCryptPasswordEncoder.encode("겟인데어");//Oauth 로그인일 경우, password는 딱히 의미가 없음.
        String email= (String) oAuth2User.getAttributes().get("email");
        String role="ROLE_USER";

        User userEntity=userRepository.findByUsername(username);
        if (userEntity==null){
            //회원가입 진행 절차
            userEntity=User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
    }
}
