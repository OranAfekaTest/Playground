package playground.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.aop.PlayerAuthorization;
import playground.dal.ElementDao;
import playground.dal.UserDao;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.UserEntity;
import playground.logic.service.element.ElementNotFoundException;
import playground.logic.service.user.UserNotFoundException;
import playground.logic.service.user.UserUnauthorizedAccessException;

@Component
public class PokeAnAnimalPlugin implements PlaygroundPlugin {
	private UserDao users;
	private ElementDao elements;
	private final int POINTS_FOR_POKING_AN_ANIMAL = 10;

	@Autowired
	public PokeAnAnimalPlugin(UserDao users, ElementDao elements) {
		this.users = users;
		this.elements = elements;
	}

	@Override
	@PlayerAuthorization
	public Object invokeOperation(ActivityEntity activity) {
		try {
			// check if pet element exists in this playground is a user with this email
			if (this.elements.findByIdLikeAndPlaygroundLikeAndTypeLike(activity.getElementId(),
					activity.getElementPlayground(), "Animal") == null)
				throw new ElementNotFoundException("No Animal with Id: " + activity.getElementId() + " in Playground: "
						+ activity.getElementPlayground());
			UserEntity user = users.findByEmailLike(activity.getPlayerEmail());
			// check if there is a user with this email
			// only player can poke a animal
			if (user == null)
				throw new UserNotFoundException("No User Found with Email " + activity.getPlayerEmail());
			//if (!user.getRole().equals("player") && !user.getRole().equals("Player"))
				//throw new UserUnauthorizedAccessException("Only Player can Poke a Animal");
			user.setPoints(user.getPoints() + POINTS_FOR_POKING_AN_ANIMAL);
			users.save(user);
			return new ActivityResponse("You got Extra " + POINTS_FOR_POKING_AN_ANIMAL + " Points, and now Have "
					+ user.getPoints() + " Points!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
