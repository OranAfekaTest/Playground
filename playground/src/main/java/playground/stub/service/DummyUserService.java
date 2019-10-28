package playground.stub.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.user.UserUnverifiedException;
import playground.logic.service.ConstantsValues;
import playground.logic.service.user.UserAlreadyExistsException;
import playground.logic.service.user.UserIllegalInputException;
import playground.logic.service.user.UserNotFoundException;
import playground.logic.service.user.UserService;
import playground.logic.service.user.UserUnauthorizedAccessException;

//@Service
public class DummyUserService implements UserService {
	
	/** Class Attributes */
	private Map<UserId, UserEntity> users;

	/** Class Methods */
	@PostConstruct
	public void init() {
		this.users = Collections.synchronizedMap(new HashMap<>());
	}
	
	@Override
	public void cleanup() {
		this.users.clear();
	}

	@Override
	public UserEntity addNewUser(String email, String username, String avatar, String role)
			throws UserAlreadyExistsException, UserIllegalInputException {
		validateUserEmail(email);
		validateUserRole(role);
		
		UserId id = new UserId(email, ConstantsValues.PLAYGROUND.getValue());
		if (users.containsKey(id))
			throw new UserAlreadyExistsException("User exists with email: " + email+", and playground: "+ ConstantsValues.PLAYGROUND.getValue());
		
		String userUsername = username;
		if (checkStringIsEmpty(userUsername))
			userUsername = ConstantsValues.USERNAME.getValue();

		String userAvatar = avatar;
		if (checkStringIsEmpty(userAvatar))
			userAvatar = ConstantsValues.AVATAR.getValue();

		UserEntity user = new UserEntity(email, ConstantsValues.PLAYGROUND.getValue(), userUsername, userAvatar, role, 0L, ConstantsValues.VERIFICATION_CODE.getValue());
		users.put(id, user);
		
		return user;
	}

	@Override
	public UserEntity getUserById(String email, String playground) throws UserNotFoundException {		
		UserId id = new UserId(email, playground);
		
		UserEntity user = users.get(id);
		if (user == null)
			throw new UserNotFoundException(
					"User doesn't exists with email: " + email + ", and playground: " + playground);

		return user;
	}
	
	@Override
	public UserEntity verifyUser(String email, String playground, String code) throws UserNotFoundException, UserUnverifiedException {
		UserEntity user = getUserById(email, playground);
		
		if (user.getCode().equals(code)) {
			user.setCode("");
			UserId id = new UserId(email, playground);
			users.put(id, user);
			return user;
		}else
			throw new UserUnverifiedException("User code is wrong");
		
	}

	@Override
	public UserEntity login(String email, String playground) throws UserNotFoundException, UserUnverifiedException {
		UserEntity user = getUserById(email, playground);

		if (!user.getCode().isEmpty())
			throw new UserUnverifiedException("Can not login to unverfied user account");

		return user;
	}

	@Override
	public void updateUser(UserEntity originalUser, UserEntity newValues) throws UserIllegalInputException, UserNotFoundException, UserUnauthorizedAccessException {
		synchronized (users) {
			boolean dirty = false;
			
			String value = newValues.getEmail();
			validateUserEmail(value);
			if(!value.equals(originalUser.getEmail()))
				throw new UserIllegalInputException("User email field has illegal value");
					
			value = newValues.getPlayground();
			if(!value.equals(originalUser.getPlayground()))
				throw new UserIllegalInputException("User playground has illegal value");
			
			value = newValues.getUsername();
			if (!checkStringIsEmpty(value)) {
				originalUser.setUsername(value);
				dirty = true;
			}
			else
				throw new UserIllegalInputException("User username field has illegal value");
			
			value = newValues.getAvatar();
			if (!checkStringIsEmpty(value)) {
				originalUser.setAvatar(value);
				dirty = true;
			}
			else
				throw new UserIllegalInputException("User avatar field has illegal value");

			value = newValues.getRole();
			if (!checkStringIsEmpty(value)) {
				validateUserRole(value);
				if(originalUser.getIsManager()) {
					originalUser.setRole(value);
					dirty = true;
				}
				else
					throw new UserUnauthorizedAccessException("User isn't a manager");
			}else
				throw new UserIllegalInputException("User role field has illegal value");
			
			value = ""+newValues.getPoints();
			if(!checkStringIsEmpty(value))
				if(!value.equals(""+originalUser.getPoints()))
					throw new UserIllegalInputException("User points field has illegal value");

			if (dirty) {
				UserId id = new UserId(originalUser.getEmail(), originalUser.getPlayground());
				users.put(id, originalUser);
			}
		}
	}
	
	private void validateUserEmail(String email) throws UserIllegalInputException {
		if (checkStringIsEmpty(email))
			throw new UserIllegalInputException("User email field has illegal value");
	}

	private void validateUserRole(String role) throws UserIllegalInputException {
		if (checkStringIsEmpty(role) || (!role.equals("player") && !role.equals("manager")))
			throw new UserIllegalInputException("User role field has illegal value");
	}

	private boolean checkStringIsEmpty(String value) {
		if (value == null || value.isEmpty())
			return true;
		return false;
	}

}
