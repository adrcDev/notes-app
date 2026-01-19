package com.awesometodo.springsecurity;

import com.awesometodo.JwtAuthenticationFilter;
import com.awesometodo.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SpringSecurityConfig {
    private JwtService jwtService;

    public SpringSecurityConfig(JwtService jwtService) {
        this.jwtService=jwtService;
    }

    /* Configuring url paths for which the http request message  should not pass through spring security's security filter chain. Basically you can completely deactivate spring security for any url path. */
    @Bean
    WebSecurityCustomizer webSecurityCustomizerBeanDef() {
        return (webSecurity) -> {
//            webSecurity.ignoring().requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/login"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/refresh"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/init"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/verify-otps"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/resend-otps"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/init"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/verify-otps"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/reset-password"),
//             PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/refresh")
//            );

        };
    }

    /* Spring security configuration. This will determine which spring security's security filters are used. */
    /* permitAll() within the HttpSecurity.authorizeHttpRequests() method's lambda argument means that allow the http request message containing these urls to access the respective controller methods without authentication (the SecurityContextHolder does not need to be filled by a SecurityContext object) */
    @Bean
    SecurityFilterChain securityFilterChainBeanDef(HttpSecurity httpSecurity) throws Exception {
        /* HttpSecurity.cors() method is only added for use during development */
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .formLogin(formLoginConfigurer -> formLoginConfigurer.disable())
                .httpBasic(httpBasicConfigurer -> httpBasicConfigurer.disable())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/login"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/refresh"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/init"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/verify-otps"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/signup/resend-otps"),PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/init"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/verify-otps"), PathPatternRequestMatcher.withDefaults().matcher("/auth/v1/forgot-password/reset-password")).permitAll().anyRequest().authenticated();
                })
                .addFilterAfter(new JwtAuthenticationFilter(jwtService), LogoutFilter.class);

        return httpSecurity.build();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:8081");
        corsConfiguration.addAllowedOrigin("http://192.168.1.105:8081");
        corsConfiguration.addAllowedOrigin("https://notes-app-react-frontend.onrender.com");
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource=new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }


}
