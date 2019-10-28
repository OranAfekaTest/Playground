package playground.layout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import playground.layout.io.NewUserForm;
import playground.layout.io.UserTO;
import playground.logic.entity.UserEntity;
import playground.logic.service.user.UserService;

@RestController
public class UserController {

	/** Class Attributes */
	private UserService userService;

	/** Class Getters & Setters */
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/** Class Methods */
	// POST /playground/users
	@RequestMapping(method = RequestMethod.POST, path = "/playground/users", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserTO register(@RequestBody NewUserForm newUserForm) throws Exception {
		UserEntity user = userService.addNewUser(newUserForm.getEmail(), newUserForm.getUsername(),
				newUserForm.getAvatar(), newUserForm.getRole());
		return new UserTO(user);
	}

	// GET /playground/users/confirm/{playground}/{email}/{code}
	@RequestMapping(method = RequestMethod.GET, path = "/playground/users/confirm/{playground}/{email}/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO verify(@PathVariable("playground") String playground, @PathVariable("email") String email,
			@PathVariable("code") String code) throws Exception {
		UserEntity user = userService.verifyUser(email, playground, code);
		return new UserTO(user);
	}

	// GET /playground/users/login/{playground}/{email}
	@RequestMapping(method = RequestMethod.GET, path = "/playground/users/login/{playground}/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserTO login(@PathVariable("playground") String playground, @PathVariable("email") String email)
			throws Exception {
		UserEntity user = userService.login(email, playground);
		return new UserTO(user);
	}

	// PUT /playground/users/{playground}/{email}
	@RequestMapping(method = RequestMethod.PUT, path = "/playground/users/{playground}/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(@PathVariable("playground") String playground, @PathVariable("email") String email,
			@RequestBody UserTO userTO) throws Exception {
		UserEntity originalUser = userService.login(email, playground);
		userService.updateUser(originalUser, userTO.toEntity());
	}
}
