package br.com.fiap.dunoke.security;

import java.nio.file.AccessDeniedException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SegurancaConfig {
	
	@Bean
	public SecurityFilterChain chain(HttpSecurity http) throws Exception {
		
	http.authorizeHttpRequests( (request) -> request
		.requestMatchers("/usuario/novo").hasAuthority("ADMIN")
		.requestMatchers("/fornecedor/**").hasAuthority("ADMIN")
		.anyRequest().authenticated() )
		.formLogin( (login) -> login.loginPage("/login").defaultSuccessUrl("/index", true)
				.failureUrl("/login?falha=true").permitAll())
		.logout((logout) -> logout.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout=true").permitAll()  )
		.exceptionHandling((exception) -> 
		exception.accessDeniedHandler((request, response, AccessDeniedException) 
		-> {response.sendRedirect("/acesso_negado");}) );
		
		return http.build();
		
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

}
