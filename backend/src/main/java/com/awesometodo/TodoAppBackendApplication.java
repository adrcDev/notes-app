package com.awesometodo;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.entity.Todo;
import com.awesometodo.entity.User;
import com.awesometodo.entity.enums.Gender;
import com.awesometodo.repository.*;
import com.awesometodo.service.JwtService;
import com.awesometodo.util.EnumUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
/* This is required by spring-retry library */
@EnableRetry
public class TodoAppBackendApplication {
	private static final Logger logger=LoggerFactory.getLogger(TodoAppBackendApplication.class);
	private static final String DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME="http://localhost:8080";
	private static final int JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_DAYS=730;
	private static final long JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MILLIS=
			JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_DAYS*24L*60*60*1000;
	private static final String HMAC_SHA_256_SECRET_KEY=System.getenv("HMAC_SHA_256_SECRET_KEY");
	private static final byte[] hmacSHA256SecretKeyBytes = Base64.getDecoder().decode(HMAC_SHA_256_SECRET_KEY);
	public static String generateJwtAccessTokenWithVeryLongExpiry(int userIdToUseAsSubjectClaimValue) {
		String jwtAccessToken= Jwts.builder().header().type("JWT")
				.and().claims().issuer(DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).subject(String.valueOf(userIdToUseAsSubjectClaimValue)).add("aud",DOMAIN_NAME_ALONG_WITH_HTTP_SCHEME).expiration(new Date(System.currentTimeMillis()+JWT_ACCESS_TOKEN_EXPIRY_TIME_IN_MILLIS)).issuedAt(new Date()).add("token_type","access")
				.and().signWith(Keys.hmacShaKeyFor(hmacSHA256SecretKeyBytes)).compact();
		return jwtAccessToken;
	}


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
		EntityManager em=springIOCContainer.getBean(EntityManager.class);
		TransactionTemplate transactionTemplate=springIOCContainer.getBean(TransactionTemplate.class);
		UserRepository userRepository=springIOCContainer.getBean(UserRepository.class);
		JwtService jwtService=springIOCContainer.getBean(JwtService.class);
		PendingSignupUserRepository pendingSignupUserRepository=springIOCContainer.getBean(PendingSignupUserRepository.class);
		SignupOtpRepository signupOtpRepository=springIOCContainer.getBean(SignupOtpRepository.class);
		PasswordResetTokenRepository passwordResetTokenRepository=springIOCContainer.getBean(PasswordResetTokenRepository.class);
		JwtRefreshTokenRepository jwtRefreshTokenRepository=springIOCContainer.getBean(JwtRefreshTokenRepository.class);
		Argon2PasswordEncoder argon2IdPasswordEncoder=springIOCContainer.getBean(Argon2PasswordEncoder.class);


//		logger.debug("2 year valid jwt access token {}",generateJwtAccessTokenWithVeryLongExpiry(7));
		transactionTemplate.executeWithoutResult((transactionStatus)-> {
			/*Test repository methods or service methods or EntityManager operations here */





		});





	}



	@Bean
	public Argon2PasswordEncoder argon2IdPasswordEncoder() {
		/* Note- parallelism values >1 are ignored as bouncy castle implementation uses 1 thread only */
		Argon2PasswordEncoder argon2IdPasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 47104, 1);
		return argon2IdPasswordEncoder;
	}







}
