package com.alvis.exam.configuration.spring.security;

import com.alvis.exam.domain.enums.RoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfigurer {

    private final LoginAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final FormAuthenticationProvider formAuthenticationProvider;
    private final FormDetailsServiceImpl formDetailsService;
    private final FormAuthenticationSuccessHandler formAuthenticationSuccessHandler;
    private final FormAuthenticationFailureHandler formAuthenticationFailureHandler;
    private final FormLogoutSuccessHandler formLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RestLoginAuthenticationFilter authenticationFilter) throws Exception {
        http
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint))
                .authenticationProvider(formAuthenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/admin/**").hasRole(RoleEnum.ADMIN.getName())
                        .requestMatchers("/api/student/**").hasRole(RoleEnum.STUDENT.getName())
                        .requestMatchers("/api/teacher/**").hasRole(RoleEnum.TEACHER.getName())
                        .anyRequest().permitAll())
                .formLogin(formLogin -> formLogin
                        .successHandler(formAuthenticationSuccessHandler)
                        .failureHandler(formAuthenticationFailureHandler))
                .logout(logout -> logout
                        .logoutUrl("/api/user/logout")
                        .logoutSuccessHandler(formLogoutSuccessHandler)
                        .invalidateHttpSession(true))
                .rememberMe(rememberMe -> rememberMe
                        .key(CookieConfig.getName())
                        .tokenValiditySeconds(CookieConfig.getInterval())
                        .userDetailsService(formDetailsService))
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setMaxAge(3600L);
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public RestLoginAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        RestLoginAuthenticationFilter authenticationFilter = new RestLoginAuthenticationFilter();
        authenticationFilter.setAuthenticationSuccessHandler(formAuthenticationSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(formAuthenticationFailureHandler);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        authenticationFilter.setUserDetailsService(userDetailsService);
        return authenticationFilter;
    }
}
