package com.awesometodo;

import com.awesometodo.entity.User;
import com.awesometodo.repository.UserRepository;
import com.awesometodo.service.JwtService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@SpringBootApplication
public class TodoAppBackendApplication {


	public static void main(String[] args) {
//		SpringApplication sa=new SpringApplication(TodoAppBackendApplication.class);
//		sa.setLogStartupInfo(false);
//		sa.setBannerMode(Banner.Mode.OFF);
//		sa.run(args);
		ConfigurableApplicationContext springIOCContainer=SpringApplication.run(TodoAppBackendApplication.class, args);


		UserRepository userRepository=springIOCContainer.getBean(UserRepository.class);
		JwtService jwtService=springIOCContainer.getBean(JwtService.class);

	}

	@Bean
	public Argon2PasswordEncoder argon2IdPasswordEncoder() {
		/* Note- parallelism values >1 are ignored as bouncy castle implementation uses 1 thread only */
		Argon2PasswordEncoder argon2IdPasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 47104, 1);
		return argon2IdPasswordEncoder;
	}

}
