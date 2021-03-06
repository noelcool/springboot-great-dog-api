package great.dog.api.config;

import great.dog.api.service.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityUserService securityUserService;
    private final WebAccessDeniedHandler webAccessDeniedHandler;

    @Autowired
    public WebSecurityConfig(SecurityUserService securityUserService, WebAccessDeniedHandler webAccessDeniedHandler) {
        this.securityUserService = securityUserService;
        this.webAccessDeniedHandler = webAccessDeniedHandler;
    }

    private final static String[] allowedUrls = {"/", "/login", "/join", "/v1/**", "/token"};

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**", "/api-docs/**", "/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                .authorizeRequests().
                antMatchers(allowedUrls).permitAll().
                antMatchers("/swagger-ui.html", "/swagger-ui/**").access("hasRole('ROLE_VIEW')").
                anyRequest().authenticated()
                .and()
                    .formLogin().loginPage("/login").defaultSuccessUrl("/swagger-ui.html", true)
                    .usernameParameter("username").passwordParameter("password")
                .and().logout().invalidateHttpSession(true).deleteCookies("JSESSIONID")
                .and().exceptionHandling().accessDeniedHandler(webAccessDeniedHandler)
                .and().authenticationProvider(authenticationProvider())
                .csrf() //csrf() ???????????? csrf ????????? ???????????? ?????? antMatchers??? ????????????, ??? ????????? ?????? ?????? ????????? ???????????? GET, HEAD, TRACE, OPTIONS method??? ?????? CORS ????????? ??????????????? requireCsrfProtectionMatcher
                .ignoringAntMatchers(allowedUrls)
                .requireCsrfProtectionMatcher(new CsrfRequireMatcher())
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(securityUserService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // ????????? ????????? ???????????? ????????? ???????????? ?????? ?????????
    // userDetailSErvice??? ????????? ?????? ???????????? ???????????? ????????? ???????????? ??????
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    static class CsrfRequireMatcher implements RequestMatcher {
        private static final Pattern ALLOWED_METHODS = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
        @Override
        public boolean matches(HttpServletRequest request) {
            if (ALLOWED_METHODS.matcher(request.getMethod()).matches())
                return false;
            return true;
        }
    }
}
