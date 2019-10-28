package playground.logic.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.MyLog;
import playground.dal.UserDao;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.ConstantsValues;
import playground.logic.service.user.UserAlreadyExistsException;
import playground.logic.service.user.UserIllegalInputException;
import playground.logic.service.user.UserNotFoundException;
import playground.logic.service.user.UserService;
import playground.logic.service.user.UserUnauthorizedAccessException;
import playground.logic.service.user.UserUnverifiedException;

@Service
public class JpaUserService implements UserService {	

	/** Class Attributes */
	private UserDao users;
	
	/** Class Constructors */
	@Autowired
	public JpaUserService(UserDao users) {
		this.users = users;
	}

	/** Class Methods */
	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		users.deleteAll();
	}

	@Override
	@Transactional
	@MyLog
	public UserEntity addNewUser(String email, String username, String avatar, String role)
			throws UserAlreadyExistsException, UserIllegalInputException {
		validateUserEmail(email);
		validateUserRole(role);

		UserId userId = new UserId(email, ConstantsValues.PLAYGROUND.getValue());
		if (users.existsById(userId))
			throw new UserAlreadyExistsException("User exists with email: " + userId.getEmail()+", and playground: "+ userId.getPlayground());

		String userUsername = username;
		if (checkStringIsEmpty(userUsername))
			userUsername = ConstantsValues.USERNAME.getValue();

		String userAvatar = avatar;
		if (checkStringIsEmpty(userAvatar))
			userAvatar = ConstantsValues.AVATAR.getValue();

		return users.save(
				new UserEntity(email, ConstantsValues.PLAYGROUND.getValue(), userUsername, userAvatar, role, 0L, ConstantsValues.VERIFICATION_CODE.getValue()));
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public UserEntity getUserById(String email, String playground) throws UserNotFoundException {
		UserId id = new UserId(email, playground);
		
		return users.findById(id).orElseThrow(() -> new UserNotFoundException(
				"User doesn't exists with email: " + email + ", and playground: " + playground));
	}
	
	@Override
	@Transactional
	@MyLog
	public UserEntity verifyUser(String email, String playground, String code) throws UserNotFoundException, UserUnverifiedException {
		UserEntity user = getUserById(email, playground);

		if (user.getCode().equals(code)) {
			user.setCode("");
			users.save(user);
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
	@Transactional
	@MyLog
	public void updateUser(UserEntity originalUser, UserEntity newValues)
			throws UserIllegalInputException, UserNotFoundException, UserUnauthorizedAccessException {

		String value = newValues.getEmail();
		if(!value.equals(originalUser.getEmail()))
			throw new UserIllegalInputException("User email field has illegal value");
				
		value = newValues.getPlayground();
		if(!value.equals(originalUser.getPlayground()))
			throw new UserIllegalInputException("User playground has illegal value");
		
		value = newValues.getUsername();
		if (!checkStringIsEmpty(value))
			originalUser.setUsername(value);
		else
			throw new UserIllegalInputException("User username field has illegal value");
		
		value = newValues.getAvatar();
		if (!checkStringIsEmpty(value))
			originalUser.setAvatar(value);
		else
			throw new UserIllegalInputException("User avatar field has illegal value");

		value = newValues.getRole();
		if (!checkStringIsEmpty(value)) {
			validateUserRole(value);
			if(originalUser.getIsManager())
				originalUser.setRole(value);
			else
				throw new UserUnauthorizedAccessException("User isn't a manager");
		}else
			throw new UserIllegalInputException("User role field has illegal value");
		
		value = ""+newValues.getPoints();
		if(!checkStringIsEmpty(value))
			if(!value.equals(""+originalUser.getPoints()))
				throw new UserIllegalInputException("User points field has illegal value");

		users.save(originalUser);
	}
	
	private void validateUserEmail(String email) throws UserIllegalInputException {
		if (checkStringIsEmpty(email))
			throw new UserIllegalInputException("User email field has illegal value");
	}

	private void validateUserRole(String role) throws UserIllegalInputException {
		if (checkStringIsEmpty(role) || (!role.equals(ConstantsValues.PLAYER.getValue()) && !role.equals(ConstantsValues.MANAGER.getValue())))
			throw new UserIllegalInputException("User role field has illegal value");
	}

	private boolean checkStringIsEmpty(String value) {
		if (value == null || value.isEmpty())
			return true;
		return false;
	}

}
