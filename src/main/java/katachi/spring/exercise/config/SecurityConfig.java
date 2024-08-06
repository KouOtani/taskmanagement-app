package katachi.spring.exercise.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // このアノテーションはこのアプリでは書かなくてよい
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    /*セキュリティの各種設定*/
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

        //ログイン不要ページの設定
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() //直リンクOK
                .requestMatchers(mvc.pattern("/user/signup")).permitAll() //直リンクOK
                .requestMatchers(mvc.pattern("/user/signup/rest")).permitAll() //直リンクOK
                .requestMatchers(mvc.pattern("/admin")).hasAuthority("ROLE_ADMIN") //権限制御
                .anyRequest().authenticated()); //それ以外は直リンクNG

        //ログイン処理
        http.formLogin(login -> login
                .loginProcessingUrl("/login") //ログイン処理のパス
                .loginPage("/login") //ログインページの指定
                .failureUrl("/login?error") //ログイン失敗時の遷移先
                .usernameParameter("userId") //ログインページのユーザーID
                .passwordParameter("password") //ログインページのパスワード
                .defaultSuccessUrl("/todo/list", true) //成功時の遷移先
                .permitAll());

        //ログアウト処理
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll());

        http.headers(headers -> headers
                .frameOptions(FrameOptionsConfig::disable));

        return http.build();
    }

}
