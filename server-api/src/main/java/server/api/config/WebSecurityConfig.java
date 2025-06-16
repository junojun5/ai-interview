package server.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import server.api.security.filter.AuthTokenFilter;
import server.api.security.filter.TokenExceptionFilter;
import server.api.security.handler.CustomAccessDeniedHandler;
import server.api.security.handler.CustomAuthenticationEntryPoint;
import server.api.security.service.CustomUserDetailsService;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * UserDetailsService 및 PasswordEncoder를 사용하여 사용자 아이디와 암호를 인증하는 AuthenticationProvider 구현체
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(
            AuthenticationManagerBuilder.class);

        authBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(
                AbstractHttpConfigurer::disable) // csrf 방어 비활성화 -> JWT를 사용하기 때문에 csrf 공격에 취약하지 않음 (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 함)
            .cors(AbstractHttpConfigurer::disable) // cors 비활성화 -> 프론트와 연결 시 따로 설정 필요
            .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
            .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
            .headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin())) // X-Frame-Options 옵션 활성화 -> 도메인과 동일한 출처에 대해서만 iframe 허용 (클릭재킹 공격을 방지하기 위함)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음

            // request 인증, 인가 설정
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/lib/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/error"),
                    new AntPathRequestMatcher("/favicon.ico"),
                    new AntPathRequestMatcher("/index.html")
                ).permitAll()
                .anyRequest().authenticated()
            )

            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            // 토큰 검증 과정에서 발생하는 UnAuthorizedException은 TokenExceptionFilter에서 처리
            .addFilterBefore(new TokenExceptionFilter(), authTokenFilter.getClass())

            .exceptionHandling((exceptions) -> exceptions
                    .authenticationEntryPoint(
                        new CustomAuthenticationEntryPoint()) // 인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 발생하는 예외를 처리
                    .accessDeniedHandler(new CustomAccessDeniedHandler())
                // 인증된 사용자가 보호된 리소스에 접근할 수 없을 때 발생하는 예외를 처리
            );

        return http.build();
    }
}
