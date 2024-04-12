package com.alvis.exam.configuration.spring.security;


import com.alvis.exam.domain.enums.RoleEnum;
import com.alvis.exam.service.AuthenticationService;
import com.alvis.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class FormAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationService authenticationService;

    private final UserService userService;

    @Autowired
    public FormAuthenticationProvider(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        com.alvis.exam.domain.User user = userService.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("잘못된 사용자 이름 또는 비밀번호");
        }

        System.out.println(user.getRealName());
        System.out.println(user.getPassword());
        System.out.println(user.getRole());
        //System.out.println(username);
        //System.out.println(password);
        

        boolean result = authenticationService.authUser(user, username, password);
         if (!result) {
            throw new BadCredentialsException("잘못된 사용자 이름 또는 비밀번호");
        }

        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.fromCode(user.getRole()).getRoleName()));

        AuthUser authUser = new AuthUser(user.getUserName(), user.getPassword(), grantedAuthorities);
        authUser.setUser(user);
        return new UsernamePasswordAuthenticationToken(authUser, authUser.getPassword(), authUser.getAuthorities());
        
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
