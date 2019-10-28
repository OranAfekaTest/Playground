package playground.test.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import playground.layout.io.ActivityTO;
import playground.layout.io.ElementTO;
import playground.layout.io.Location;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.activity.ActivityAlreadyExistsException;
import playground.logic.service.activity.ActivityIllegalInputException;
import playground.logic.service.activity.ActivityService;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserNotFoundException;
import playground.logic.service.user.UserService;

public class TestHelpers {	
	private UserService    userService;
	private ElementService elementService;
	private ActivityService activityService;
	
	public TestHelpers(UserService userService) {
		this.userService = userService;
	}
	
	public TestHelpers(UserService userService, ElementService elementService) {
		this(userService);
		this.elementService = elementService;
	}
	
	public TestHelpers(UserService userService, ElementService elementService, ActivityService activityService) {
		this(userService, elementService);
		this.activityService = activityService;
	}
	
	private UserEntity verifyUser(UserEntity user, String verificationCode) throws UserNotFoundException {
		return userService.verifyUser(user.getEmail(), user.getPlayground(), verificationCode);
	}
	public UserId registerVerifiedManager(String verificationCode) throws UserNotFoundException {
		UserEntity newManager = userService.addNewUser(
				"janedoe@work.com",
				"Jane",
				"https://www.blogtyrant.com/gravatar_jane.jpg",
				"manager"
				);
		UserEntity verifiedManager = verifyUser(newManager, verificationCode);
		return new UserId(verifiedManager.getEmail(), verifiedManager.getPlayground());
	}
	public UserId registerVerifiedPlayer(String verificationCode) throws UserNotFoundException {
		UserEntity newPlayer = userService.addNewUser(
				"johndoe@work.com",
				"John",
				"https://www.blogtyrant.com/gravatar_john.jpg",
				"player"
				);
		UserEntity verifiedPlayer = verifyUser(newPlayer, verificationCode);
		return new UserId(verifiedPlayer.getEmail(), verifiedPlayer.getPlayground());
	}
	public UserId registerUnverifiedPlayer() {
		UserEntity newPlayer = userService.addNewUser(
				"dereckdoe@work.com",
				"Dereck",
				"https://www.blogtyrant.com/gravatar_dereck.jpg",
				"player"
				);
		return new UserId(newPlayer.getEmail(), newPlayer.getPlayground());
	}
	
	public Date parseDateFromString(String date) {
		String           pattern          = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date             result           = null;
		try {
			result = simpleDateFormat.parse(date);
		} catch (ParseException e) {
			System.err.println("Failed to parse Date " + date);
		}
		return result;
	}
	public String createElement(String playground, Double x, Double y, String name, Date expirationDate, String type,
			Map<String, Object> attributes, String creatorPlayground,String creatorEmail) {
		ElementTO elementTO = new ElementTO(
				playground,
				new Location(x, y),
				name,
				expirationDate,
				type,
				attributes,
				creatorPlayground,
				creatorEmail);
		ElementEntity persistedElement = elementService.addNewElement(elementTO.toEntity());
		return persistedElement.getId();
	}
	public String createElement(String playground, UserEntity verifiedManager) {
		Date                    expirationDate = parseDateFromString("2019-02-01T09:15:00");
		HashMap<String, Object> attributes     = new HashMap<String, Object>();
		ArrayList<String>       favoriteFood   = new ArrayList<String>();
		favoriteFood.add("orange");
		favoriteFood.add("apple");
		attributes.put("favoriteFood", favoriteFood);
		String persistedElementId = createElement(
				playground,
				new Double(0.0),
				new Double(0.0),
				"Fox",
				expirationDate,
				"Animal",
				attributes,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail());
		return persistedElementId;
	}
	public String persistActivity(ActivityEntity newActivity) throws ActivityAlreadyExistsException, ActivityIllegalInputException {
		ActivityEntity persistedActivity = activityService.addNewActivity(newActivity); 
		return persistedActivity.getId();
	}
	public String createMessageBoardElement(String playground, UserEntity verifiedManager) {
		Date                    expirationDate = parseDateFromString("2019-02-01T09:15:00");
		HashMap<String, Object> attributes     = new HashMap<String, Object>();
		String persistedMessageBoard = createElement(
				playground,
				new Double(0.0),
				new Double(0.0),
				"Messag board",
				expirationDate,
				"message board",
				attributes,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail());
		return persistedMessageBoard;
	}
	public String[] createElements(String playground, UserEntity verifiedManager, int amount) {
		ArrayList<String> persistedElementIds = new ArrayList<String>();
		for (int i = 0; i < amount; i++) {
			Date                    expirationDate = parseDateFromString("2050-02-01T09:15:0" + i);
			HashMap<String, Object> attributes     = new HashMap<String, Object>();
			ArrayList<String>       favoriteFood   = new ArrayList<String>();
			favoriteFood.add("orange");
			favoriteFood.add("apple");
			attributes.put("favoriteFood", favoriteFood);
			String persistedElementId = createElement(
					playground,
					new Double(i),
					new Double(i),
					"Fox_" + i,
					expirationDate,
					"Animal",
					attributes,
					verifiedManager.getPlayground(),
					verifiedManager.getEmail()
					);
			persistedElementIds.add(persistedElementId);
		}
		return persistedElementIds.toArray(new String[0]);
	}
	public String[] createElements(String playground, UserEntity verifiedManager, int amount,
			int amountOfNearby, Double furthestX, Double furthestY) {
		ArrayList<String> persistedElementIds = new ArrayList<String>();
		Collections.addAll(persistedElementIds, createElements(playground, verifiedManager, amountOfNearby));
		for (int i = amountOfNearby; i < amount; i++) {
			Date                    expirationDate = parseDateFromString("2050-02-01T09:15:0" + i);
			HashMap<String, Object> attributes     = new HashMap<String, Object>();
			ArrayList<String>       favoriteFood   = new ArrayList<String>();
			favoriteFood.add("orange");
			favoriteFood.add("apple");
			attributes.put("favoriteFood", favoriteFood);
			String persistedElementId = createElement(
					playground,
					furthestX,
					furthestY,
					"Fox_" + i,
					expirationDate,
					"Animal",
					attributes,
					verifiedManager.getPlayground(),
					verifiedManager.getEmail()
					);
			persistedElementIds.add(persistedElementId);
		}
		return persistedElementIds.toArray(new String[0]);
	}
	public String[] createElements(String playground, UserEntity verifiedManager, int amount,
			int amountOfSameType, String differentType) {
		ArrayList<String> persistedElementIds = new ArrayList<String>();
		Collections.addAll(persistedElementIds, createElements(playground, verifiedManager, amountOfSameType));
		for (int i = amountOfSameType; i < amount; i++) {
			Date                    expirationDate = parseDateFromString("2050-02-01T09:15:0" + i);
			HashMap<String, Object> attributes     = new HashMap<String, Object>();
			ArrayList<String>       favoriteFood   = new ArrayList<String>();
			favoriteFood.add("orange");
			favoriteFood.add("apple");
			attributes.put("favoriteFood", favoriteFood);
			String persistedElementId = createElement(
					playground,
					new Double(i),
					new Double(i),
					"Fox_" + i,
					expirationDate,
					differentType,
					attributes,
					verifiedManager.getPlayground(),
					verifiedManager.getEmail()
					);
			persistedElementIds.add(persistedElementId);
		}
		return persistedElementIds.toArray(new String[0]);
	}
	public String[] postMessages(UserEntity verifiedPlayer, ElementEntity messageBoard, int amount) throws ActivityAlreadyExistsException, ActivityIllegalInputException {
		ArrayList<String> messageIds = new ArrayList<String>();
		for (int i = 0; i < amount; i++) {
			HashMap<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("Content", "Posting messages for life " + i);
			ActivityTO postActivity =	new ActivityTO(
					messageBoard.getPlayground(),
					messageBoard.getPlayground(),
					messageBoard.getId(),
					"PostMessage",
					verifiedPlayer.getPlayground(),
					verifiedPlayer.getEmail(),
					attributes);
			String nextId = persistActivity(postActivity.toEntity());
			messageIds.add(nextId);
		}
		return messageIds.toArray(new String[0]);
	}
}
