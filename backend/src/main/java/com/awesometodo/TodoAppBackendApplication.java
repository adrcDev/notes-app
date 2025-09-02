package com.awesometodo;

import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.entity.User;
import com.awesometodo.entity.enums.Gender;
import com.awesometodo.repository.PendingSignupUserRepository;
import com.awesometodo.repository.SignupOtpRepository;
import com.awesometodo.repository.UserRepository;
import com.awesometodo.service.JwtService;
import com.awesometodo.util.EnumUtil;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootApplication
public class TodoAppBackendApplication {


	public static void main(String[] args) {
		SpringApplication sa=new SpringApplication(TodoAppBackendApplication.class);
		sa.setLogStartupInfo(false);
		sa.setBannerMode(Banner.Mode.OFF);
		ConfigurableApplicationContext springIOCContainer=sa.run(args);
//		ConfigurableApplicationContext springIOCContainer=SpringApplication.run(TodoAppBackendApplication.class, args);
		UserRepository userRepository=springIOCContainer.getBean(UserRepository.class);
		JwtService jwtService=springIOCContainer.getBean(JwtService.class);
		PendingSignupUserRepository pendingSignupUserRepository=springIOCContainer.getBean(PendingSignupUserRepository.class);
		SignupOtpRepository signupOtpRepository=springIOCContainer.getBean(SignupOtpRepository.class);















	}



	@Bean
	public Argon2PasswordEncoder argon2IdPasswordEncoder() {
		/* Note- parallelism values >1 are ignored as bouncy castle implementation uses 1 thread only */
		Argon2PasswordEncoder argon2IdPasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 47104, 1);
		return argon2IdPasswordEncoder;
	}







}
