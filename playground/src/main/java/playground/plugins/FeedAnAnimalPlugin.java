package playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.aop.PlayerAuthorization;
import playground.dal.UserDao;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.jpa.JpaElementService;
import playground.logic.jpa.JpaUserService;
import playground.logic.service.element.ElementNotFoundException;
import playground.logic.service.user.UserNotFoundException;
import playground.logic.service.user.UserUnauthorizedAccessException;

@Component
public class FeedAnAnimalPlugin implements PlaygroundPlugin {
	//why using DAO's instead of our services?
	//because it throws exception: User isn't a manager
	private UserDao users;
	private JpaElementService elements;
	private final int POINTS_FOR_FEEDING_AN_ANIMAL = 3;

	@Autowired
	public FeedAnAnimalPlugin(UserDao users, JpaElementService elements) {
		this.users = users;
		this.elements = elements;
	}
	

	@Override
	@PlayerAuthorization
	public Object invokeOperation(ActivityEntity activity) {
		// element Id is already DummyID + playground
		try {
			ElementEntity element = this.elements.getElementById(activity.getElementId());
			System.err.println("Found element " + element.toString());
			if (!element.getType().equals("Animal") && !element.getType().equals("animal"))
				throw new ElementNotFoundException("No Animal with Id: " + activity.getElementId() + " in Playground: "
						+ activity.getElementPlayground());
			System.err.println("Found element " + element.toString());
			UserEntity user = users.findByEmailLike(activity.getPlayerEmail());
			if (user == null)
				throw new UserNotFoundException("No User Found with Email " + activity.getPlayerEmail());
			//if (!user.getRole().equals("player") && !user.getRole().equals("Player"))
				//throw new UserUnauthorizedAccessException("Only Player can feed an Animal");
			user.setPoints(user.getPoints() - POINTS_FOR_FEEDING_AN_ANIMAL);
			users.save(user);
			return new ActivityResponse(
					"You lost " + POINTS_FOR_FEEDING_AN_ANIMAL + " Points, and now You have " + user.getPoints() + " Points!");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}
