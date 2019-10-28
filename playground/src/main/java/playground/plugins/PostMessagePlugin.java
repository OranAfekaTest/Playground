package playground.plugins;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.aop.PlayerAuthorization;
import playground.dal.ElementDao;
import playground.dal.UserDao;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.service.element.ElementNotFoundException;
import playground.logic.service.user.UserNotFoundException;
import playground.logic.service.user.UserUnauthorizedAccessException;

@Component
public class PostMessagePlugin implements PlaygroundPlugin {
	private ObjectMapper jackson;
	private UserDao users;
	private ElementDao elements;
	private final int POINTS_FOR_POSTING_MESSAGE = 5;

	@Autowired
	public PostMessagePlugin(UserDao users, ElementDao elements) {
		this.users = users;
		this.elements = elements;
	}

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Override
	@PlayerAuthorization
	public Object invokeOperation(ActivityEntity activity) {
		try {
			Map<String, String> activityJsonAttributes = this.jackson.readValue(activity.getJsonAttributes(),
					Map.class);
			// check if there is a message
			if (activityJsonAttributes.get("Content") == null && activityJsonAttributes.get("content") == null)
				throw new MessageNotFoundException("No Message Content Found in Activity Attributes");
			ElementEntity messageBoard = elements.findByIdLikeAndPlaygroundLike(activity.getElementId(),
					activity.getElementPlayground());
			// check if there is a message board
			if (messageBoard == null)
				throw new ElementNotFoundException("No Element Found with Id " + activity.getElementId());
			if (!messageBoard.getType().equals("Message board") && !messageBoard.getType().equals("message board")
					&& !messageBoard.getType().equals("MessageBoard"))
				throw new ElementNotFoundException("Element is not a Message Board");

			UserEntity user = users.findByEmailLike(activity.getPlayerEmail());
			// check if there is a user with this email
			// only player can post messages
			if (user == null)
				throw new UserNotFoundException("No User Found with Email " + activity.getPlayerEmail());
			//if (!user.getRole().equals("player") && !user.getRole().equals("Player"))
				//throw new UserUnauthorizedAccessException("Only Player can Post Messages");
			user.setPoints(user.getPoints() + POINTS_FOR_POSTING_MESSAGE);
			return new ActivityResponse("You got extra " + POINTS_FOR_POSTING_MESSAGE + " points, and now have "
					+ user.getPoints() + " points!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
