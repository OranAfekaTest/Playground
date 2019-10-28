package playground.layout.controller.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import playground.layout.io.ActivityTO;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.activity.ActivityService;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserService;
import playground.test.helpers.TestHelpers;
import playground.test.helpers.comparators.ActivityEntitiesComparator;
import playground.test.helpers.comparators.ActivityTOsComparator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostMessageTest {
	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	@Autowired
	private ElementService elementService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private UserService userService;
	@Value("${playground.verification.code}")
	private String defaultCode;
	@Value("${playground.name}")
	private String defaultPlayground;
	private TestHelpers testHelpers;
	@PostConstruct
	public void init() {
		restTemplate = new RestTemplate();
		url = "http://localhost:" + port + "/playground";
		System.err.println("Started ActivityController -> Post message test with");
		System.err.println("{\"defaultCode\":"+defaultCode+", \"defaultPlayground\":"+defaultPlayground+", \"url\":\""+url+"\"}");
	}
	@Before
	public void setup() {
		testHelpers = new TestHelpers(userService, elementService);
	}
	@After
	public void teardown() {
		userService.cleanup();
		elementService.cleanup();
		testHelpers = null;
	}
	@Test
	public void postMessageWithVerifiedPlayerAndMessageBoard() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId = testHelpers.createMessageBoardElement(defaultPlayground, verifiedManager);
		UserId     verifiedPlayerId   = testHelpers.registerVerifiedPlayer(defaultCode);
		// When I POST /playground/activities/2019A.kariv/johndoe@work.com
		ElementEntity           persistedElement = elementService.getElementById(persistedElementId);
		UserEntity              verifiedPlayer   = userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		HashMap<String, Object> attributes       = new HashMap<String, Object>();
		attributes.put("Content", "Posting messages for life");
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"PostMessage",
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail(),
				attributes);
		ActivityTO actualReturnedActivity = restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity, 
				ActivityTO.class,
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail());
		// Then database retrieves for id of actualReturnedActivity
		ActivityEntity expectedActivity  = postActivity.toEntity();
		ActivityEntity persistedActivity = activityService.getActivityById(actualReturnedActivity.getId());
		assertThat(persistedActivity)
		.isNotNull()
		.usingComparator((a1, a2)-> new ActivityEntitiesComparator().compareForCreationOfNewActivity(a1, a2))
		.isEqualTo(expectedActivity);
		// And response contains
		assertThat(actualReturnedActivity)
		.isNotNull()
		.usingComparator((a1, a2)-> new ActivityTOsComparator().compareForCreationOfNewActivity(a1, a2))
		.isEqualTo(postActivity);
	}
	@Test(expected = Exception.class)
	public void postMessageWithVerifiedManagerAndMessageBoard() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId = testHelpers.createMessageBoardElement(defaultPlayground, verifiedManager);
		// When I POST /playground/activities/2019A.kariv/janedoe@work.com
		ElementEntity           persistedElement = elementService.getElementById(persistedElementId);
		HashMap<String, Object> attributes       = new HashMap<String, Object>();
		attributes.put("Content", "Posting messages for life");
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"PostMessage",
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
				attributes);
		restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity, 
				ActivityTO.class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail());
		// Then response status <> 200
	}
	@Test(expected = Exception.class)
	public void postMessageWithVerifiedPlayerAndNotMessageBoard() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId = testHelpers.createElement(defaultPlayground, verifiedManager);
		UserId     verifiedPlayerId   = testHelpers.registerVerifiedPlayer(defaultCode);
		// When I POST /playground/activities/2019A.kariv/johndoe@work.com
		ElementEntity           persistedElement = elementService.getElementById(persistedElementId);
		UserEntity              verifiedPlayer   = userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		HashMap<String, Object> attributes       = new HashMap<String, Object>();
		attributes.put("Content", "Posting messages for life");
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"PostMessage",
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail(),
				attributes);
		restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity, 
				ActivityTO.class,
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail());
	// Then response status <> 200
	}
	@Test(expected = Exception.class)
	public void postMessageWithUnverifiedPlayerAndMessageBoard() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId = testHelpers.createMessageBoardElement(defaultPlayground, verifiedManager);
		UserId     unverifiedPlayerId = testHelpers.registerUnverifiedPlayer();
		// When I POST /playground/activities/2019A.kariv/dereckdoe@work.com
		ElementEntity           persistedElement = elementService.getElementById(persistedElementId);
		UserEntity              unverifiedPlayer = userService.getUserById(unverifiedPlayerId.getEmail(), unverifiedPlayerId.getPlayground());
		HashMap<String, Object> attributes       = new HashMap<String, Object>();
		attributes.put("Content", "Posting messages for life");
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"PostMessage",
				unverifiedPlayer.getPlayground(),
				unverifiedPlayer.getEmail(),
				attributes);
		restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity, 
				ActivityTO.class,
				unverifiedPlayer.getPlayground(),
				unverifiedPlayer.getEmail());
	// Then response status <> 200
	}
}
