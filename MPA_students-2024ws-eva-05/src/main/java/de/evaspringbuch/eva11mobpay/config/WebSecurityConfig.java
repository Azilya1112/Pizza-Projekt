package de.evaspringbuch.eva11mobpay.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebSecurity//(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig {


	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable());

	    http.authorizeHttpRequests(requests -> requests
	            .requestMatchers("/users/**").permitAll() 
	            .anyRequest().authenticated() 
	    )
	    .httpBasic(withDefaults()); 

	    return http.build();
	}


    @Bean
    PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//		return NoOpPasswordEncoder.getInstance();
	}


}