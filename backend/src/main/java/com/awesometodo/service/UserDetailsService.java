package com.awesometodo.service;

import com.awesometodo.dto.UserDetailsResponseDTO;
import com.awesometodo.entity.User;
import com.awesometodo.repository.UserRepository;
import com.awesometodo.util.EnumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsService {
    private static Logger logger= LoggerFactory.getLogger(UserDetailsService.class);
    private UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    @Transactional
    public UserDetailsResponseDTO getUserDetails(int userId) {
        Optional<User> optionalUser=userRepository.findById(userId);
        User user=optionalUser.get();
        logger.debug("Obtained details for user with id:{} and username:{}",userId,user.getUserName());
        String gender= EnumUtil.convertToSpaceSeparatedLowerCaseString(user.getGender()).get();
        UserDetailsResponseDTO userDetailsResponseDTO=new UserDetailsResponseDTO(user.getId(),user.getUserName(),user.getDisplayName(),user.getEmail(),user.getDateOfBirth(),gender, user.getPhoneNo(), user.getAccountCreatedAt());
        logger.debug("Mapped retrieved User object to UserDetailsResponseDTO object in order to return a representation of the user as json in the http response message's body, {} -> {}",user,userDetailsResponseDTO);
        logger.info("Authenticated user's details were retrieved");
        return userDetailsResponseDTO;
    }
}
