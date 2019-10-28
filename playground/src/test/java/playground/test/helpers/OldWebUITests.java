package playground.test.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import playground.layout.io.ActivityTO;
import playground.layout.io.ElementTO;
import playground.layout.io.Location;
import playground.layout.io.UserTO;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.service.activity.ActivityService;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserNotFoundException;
import playground.logic.service.user.UserService;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OldWebUITests {
/*
//	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
//	@Autowired
	private ActivityService activityService;
//	@Autowired
	private ElementService elementService;
//	@Autowired
	private UserService userService;

//	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground";
		System.err.println("Application port: " + this.url);
	}

	@Before
	public void setup() {

	}

	@After
	public void teardown() {
		this.activityService.cleanup();
		this.elementService.cleanup();
		this.userService.cleanup();
	}

	// Feature: System initialization
	@Test
	public void testServerIsBootingCorrectly() throws Exception {

	}
/*
	@Test
	public void contextLoads() throws Exception {
		assertThat(this.activityService).isNotNull();
		assertThat(this.elementService).isNotNull();
		assertThat(this.userService).isNotNull();
	}

	//	Helpers
	private Date parseDateFromString(String date) {
		String pattern = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date result = new Date();
		try {
			result = simpleDateFormat.parse(date);
		} catch (ParseException e) {
			System.err.println("getExistingElement - failed at parsing date");
		}
		return result;
	}
	private ElementEntity getExistingElement(String name, String creatorEmail, Location location,
			Date expirationDate, HashMap<String, Object> attributes) {
		ElementEntity result = getExistingElement(name, creatorEmail);
		result.setLocation(location);
		result.setExpirationDate(expirationDate);
		result.setAttributes(attributes);
		return result;
	}
	private ElementEntity getExistingElement(String name, String creatorEmail) {
		ElementEntity existingElement = new ElementEntity();
		try {
			UserEntity existingManager = this.userService.getUserByEmail(creatorEmail);
			Date expirationDate				 					= parseDateFromString("2019-02-01T09:15:00");
			HashMap<String, Object> attributes 	= new HashMap<String, Object>();
			ArrayList<String> favoriteFood 			= new ArrayList<String>();
			favoriteFood.add("orange");
			favoriteFood.add("apple");
			attributes.put("favoriteFood", favoriteFood);
			existingElement =  new ElementEntity(new ElementTO(
					existingManager.getPlayground(),
					new Location(0.0, 0.0),
					name,
					expirationDate,
					"Animal",
					attributes,
					existingManager.getPlayground(),
					existingManager.getEmail()));
		} catch (UserNotFoundException e) {
			//	Should get there, because user creation is done before
		}
		return existingElement;
	}
	
	// Feature: View elements by value
	private void populateTableUsersForViewElementsByValueTests() throws Exception {
		UserEntity existingManager 	= getVerifiedExistingManager();
		UserEntity existingUser 		=	getVerifiedExistingUser();
		this.userService.addNewUser(existingUser);
		this.userService.addNewUser(existingManager);
	}
	
	private void populateTableElementsForViewElementsByValueTests() throws Exception {
		String creatorEmail									= "janedoe@work.com";
		ElementEntity existingElementFox 		= getExistingElement("Fox", creatorEmail);
		HashMap<String, Object> attributes 	= new HashMap<String, Object>();
		ArrayList<String> favoriteFood 			= new ArrayList<String>();
		favoriteFood.add("strawberry");
		favoriteFood.add("apple");
		attributes.put("favoriteFood", favoriteFood);
		ElementEntity existingElementBear = getExistingElement(
				"Bear",
				creatorEmail,
				new Location(20.0, 20.0),
				parseDateFromString("2019-02-02T09:15:00"),
				attributes);
		attributes 		= new HashMap<String, Object>();
		favoriteFood 	= new ArrayList<String>();
		favoriteFood.add("banana");
		favoriteFood.add("apple");
		attributes.put("favoriteFood", favoriteFood);
		ElementEntity existingElementWolf = getExistingElement(
				"Wolf",
				creatorEmail,
				new Location(20.0, 20.0),
				parseDateFromString("2019-02-03T09:15:00"),
				attributes);
		attributes 		= new HashMap<String, Object>();
		favoriteFood 	= new ArrayList<String>();
		favoriteFood.add("potato");
		favoriteFood.add("apple");
		attributes.put("favoriteFood", favoriteFood);
		ElementEntity existingElementBoar = getExistingElement(
				"Boar",
				creatorEmail,
				new Location(20.0, 20.0),
				parseDateFromString("2019-02-04T09:15:00"),
				attributes);
		attributes 		= new HashMap<String, Object>();
		favoriteFood 	= new ArrayList<String>();
		favoriteFood.add("potato");
		favoriteFood.add("pineapple");
		attributes.put("favoriteFood", favoriteFood);
		ElementEntity existingElementDog 	= getExistingElement(
				"Dog",
				creatorEmail,
				new Location(20.0, 20.0),
				parseDateFromString("2019-02-05T09:15:00"),
				attributes);
		this.elementService.addNewElement(existingElementFox);
		this.elementService.addNewElement(existingElementBear);
		this.elementService.addNewElement(existingElementWolf);
		this.elementService.addNewElement(existingElementBoar);
		this.elementService.addNewElement(existingElementDog);
	}
	
	// Feature: Create activity
	private void populateTableUsersForCreateActivityTests() throws Exception {
		UserEntity existingManager 	= getVerifiedExistingManager();
		UserEntity existingUser 		=	getVerifiedExistingUser();
		this.userService.addNewUser(existingUser);
		this.userService.addNewUser(existingManager);
	}
	
	private void populateTableElementsForCreateActivityTests() throws Exception {
		ElementEntity existingElementFox = getExistingElement("Fox", "janedoe@work.com");
		this.elementService.addNewElement(existingElementFox);
	}
	
	@Test(expected = Exception.class)
	public void createActivityWithWrongValueAndVerifiedUser() throws Exception {
		//	Given database contains
		populateTableUsersForCreateActivityTests();
		populateTableElementsForCreateActivityTests();
		//	When I POST /playground/activities/2019A.kariv/johndoe@work.com
		UserEntity existingUser 						= this.userService.getUserByEmail("johndoe@work.com");
		ElementEntity existingElement 			= this.elementService.getElementById("2019A.kariv_janedoe@work.com_Fox");
		String playground 									= existingUser.getPlayground();
		String elementPlayground						=	existingElement.getPlayground();
		String elementId										= "";
		String type													=	"";
		String playerPlayground 						=	existingUser.getPlayground();
		String playerEmail									=	existingUser.getEmail();
		HashMap<String, Object> attributes 	= new HashMap<String, Object>();
		attributes.put("scoreUp", new Integer(10));
		ActivityTO postActivity							=	new ActivityTO (
				playground,
				elementPlayground,
				elementId,
				type,
				playerPlayground,
				playerEmail,
				attributes);
		this.restTemplate.postForObject(
				this.url + "/activities/{userPlayground}/{email}",
				postActivity, 
				ActivityTO.class,
				playerPlayground, playerEmail);
		//	Then response is <> 200
	}
*/
}
