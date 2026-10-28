package pe.upeu.ecorecicla.config;

import pe.upeu.ecorecicla.seguridad.JwtFiltro;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SeguridadConfig {
  @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
  @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
  @Bean SecurityFilterChain seguridad(HttpSecurity http,JwtFiltro filtro,org.springframework.security.web.AuthenticationEntryPoint entry,org.springframework.security.web.access.AccessDeniedHandler denied)throws Exception{
    return http.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
      .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(e->e.authenticationEntryPoint(entry).accessDeniedHandler(denied))
      .authorizeHttpRequests(a->a.requestMatchers(HttpMethod.POST,"/api/v1/auth/login").permitAll().anyRequest().authenticated())
      .addFilterBefore(filtro,UsernamePasswordAuthenticationFilter.class).build();
  }
}
