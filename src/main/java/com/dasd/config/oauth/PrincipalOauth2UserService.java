package com.dasd.config.oauth;

import com.dasd.config.auth.PrincipalDetails;
import com.dasd.config.oauth.provider.FaceBookOAuthUserInfo;
import com.dasd.config.oauth.provider.GoogleUserInfo;
import com.dasd.config.oauth.provider.OAuth2UserInfo;
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
    //이 메서드 실행이 종료되고 나서 @AuthenticationPrincipal 어노테이션이 생성된다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            oAuth2UserInfo = new FaceBookOAuthUserInfo(oAuth2User.getAttributes());
        }

        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();//username 은 유니크해야하므로 이와 같이 만듬.
        String password = bCryptPasswordEncoder.encode("겟인데어");//Oauth 로그인일 경우, password는 딱히 의미가 없음.
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            //회원가입 진행 절차
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(oAuth2UserInfo.getEmail())
                    .role(role)
                    .provider(oAuth2UserInfo.getProvider())
                    .providerId(oAuth2UserInfo.getProviderId())
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
