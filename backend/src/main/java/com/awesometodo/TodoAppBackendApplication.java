package com.awesometodo;

import com.awesometodo.repository.*;
import com.awesometodo.service.JwtService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootApplication
/* This is required by spring-retry library */
@EnableRetry
public class TodoAppBackendApplication {
	private static final Logger logger=LoggerFactory.getLogger(TodoAppBackendApplication.class);

	public static void main(String[] args) {
		SpringApplication sa=new SpringApplication(TodoAppBackendApplication.class);
		sa.setLogStartupInfo(false);
		sa.setBannerMode(Banner.Mode.OFF);
		ConfigurableApplicationContext springIOCContainer=sa.run(args);
		/*	ConfigurableApplicationContext springIOCContainer=SpringApplication.run(TodoAppBackendApplication.class, args);
 		*/

		/* Print details about logback's state in order see
		whether logback configured itself properly
		*/
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//		StatusPrinter.print(lc);
//		EntityManager em=springIOCContainer.getBean(EntityManager.class);
//		TransactionTemplate transactionTemplate=springIOCContainer.getBean(TransactionTemplate.class);
//		UserRepository userRepository=springIOCContainer.getBean(UserRepository.class);
//		JwtService jwtService=springIOCContainer.getBean(JwtService.class);
//		PendingSignupUserRepository pendingSignupUserRepository=springIOCContainer.getBean(PendingSignupUserRepository.class);
//		SignupOtpRepository signupOtpRepository=springIOCContainer.getBean(SignupOtpRepository.class);
//		PasswordResetTokenRepository passwordResetTokenRepository=springIOCContainer.getBean(PasswordResetTokenRepository.class);
//		JwtRefreshTokenRepository jwtRefreshTokenRepository=springIOCContainer.getBean(JwtRefreshTokenRepository.class);
//		TodoRepository todoRepository=springIOCContainer.getBean(TodoRepository.class);
//		Argon2PasswordEncoder argon2IdPasswordEncoder=springIOCContainer.getBean(Argon2PasswordEncoder.class);
//
//		transactionTemplate.executeWithoutResult((transactionStatus)-> {
//			/*Test repository methods or service methods or EntityManager operations here */
//
//
//		});
	}



	@Bean
	public Argon2PasswordEncoder argon2IdPasswordEncoder() {
		/* Note- parallelism values >1 are ignored as bouncy castle implementation uses 1 thread only */
		Argon2PasswordEncoder argon2IdPasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 47104, 1);
		return argon2IdPasswordEncoder;
	}







}
