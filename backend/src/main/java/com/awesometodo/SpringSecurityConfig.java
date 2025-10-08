package com.awesometodo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SpringSecurityConfig {

    /* Configuring url paths for which the http request message  should not pass through spring security's security filter chain. Basically you can deactivate spring security for any url path. */
    @Bean
    WebSecurityCustomizer webSecurityCustomizerBeanDef() {
        return (webSecurity) -> {
            webSecurity.ignoring().requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/login"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/refresh"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/init"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/verify-otps"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/resend-otps"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/init"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/verify-otps")
            );

        };
    }

    /* Spring security configuration. This will determine which spring security's security filters are used. */
    @Bean
    SecurityFilterChain securityFilterChainBeanDef(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .formLogin(formLoginConfigurer -> formLoginConfigurer.disable())
                .httpBasic(httpBasicConfigurer -> httpBasicConfigurer.disable())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry.anyRequest().authenticated());


//        httpSecurity.addFilterAfter(new JwtAuthFilter(), LogoutFilter.class);

        return httpSecurity.build();


    }
}
