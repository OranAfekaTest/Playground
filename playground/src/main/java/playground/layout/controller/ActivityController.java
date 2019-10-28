package playground.layout.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import playground.layout.io.ActivityTO;
import playground.logic.entity.ActivityEntity;
import playground.logic.service.activity.ActivityService;
import playground.logic.service.user.UserService;

@RestController
public class ActivityController {

	private UserService userService;
	private ActivityService activityService;
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	// POST /playground/activities/{userPlayground}/{email}
	@RequestMapping(method = RequestMethod.POST, path = "/playground/activities/{userPlayground}/{email}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object createActivity(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @RequestBody ActivityTO activityTO) throws Exception {
		userService.login(email, userPlayground);
		ActivityEntity entity = activityService.addNewActivity(activityTO.toEntity());
		return new ActivityTO(entity);
	}
}
