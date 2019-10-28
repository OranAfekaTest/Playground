package playground.logic.service.user;

import playground.logic.entity.UserEntity;

public interface UserService {

	public void cleanup();

	public UserEntity addNewUser(String email, String username, String avatar, String role) throws UserAlreadyExistsException, UserIllegalInputException;
	
	public UserEntity getUserById(String email, String playground) throws UserNotFoundException;
	
	public UserEntity verifyUser(String email, String playground, String code) throws UserNotFoundException, UserUnverifiedException;
	
	public UserEntity login(String email, String playground) throws UserNotFoundException, UserUnverifiedException;

	public void updateUser(UserEntity originalUser, UserEntity newValues) throws UserIllegalInputException, UserNotFoundException, UserUnauthorizedAccessException;
	
}
