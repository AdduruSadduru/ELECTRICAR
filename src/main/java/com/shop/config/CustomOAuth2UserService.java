package com.shop.config;

import com.shop.constant.Role;
import com.shop.dto.SessionUser;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import com.shop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartService cartService;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);


        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();



        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes);

        if (registrationId.equals("kakao")) {
            httpSession.setAttribute("Kakao_User", new SessionUser(member));
        } else if (registrationId.equals("naver")) {
            httpSession.setAttribute("Naver_User", new SessionUser(member));
        } else {
            httpSession.setAttribute("Google_User", new SessionUser(member));
        }

        String MemberRole = "ROLE_" + member.getRole().toString();

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(MemberRole)),
                attributes.getAttributes()
                , attributes.getNameAttributeKet()
        );
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail());
        if (member == null) {
            member = attributes.toEntity();
            member.setRole(Role.USER);
            memberRepository.save(member);
            cartService.saveCart(member);
        } else {
            member.setName(attributes.getName());
        }
        return member;
    }
}





























