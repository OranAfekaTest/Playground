package playground.layout.controller.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import playground.layout.io.ActivityTO;
import playground.layout.io.ElementTO;
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
public class ViewActivitiesTest {
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
	private ObjectMapper jackson;
	@PostConstruct
	public void init() {
		restTemplate = new RestTemplate();
		url = "http://localhost:" + port + "/playground";
		System.err.println("Started ActivityController -> View elements test with");
		System.err.println("{\"defaultCode\":"+defaultCode+", \"defaultPlayground\":"+defaultPlayground+", \"url\":\""+url+"\"}");
		jackson = new ObjectMapper();
	}
	@Before
	public void setup() {
		testHelpers = new TestHelpers(userService, elementService, activityService);
	}
	@After
	public void teardown() {
		userService.cleanup();
		elementService.cleanup();
		testHelpers = null;
	}
	@Test
	public void viewActivitiesByElementPlaygroundAndElementIdAndTypeWithPlayerAndDefaultPagination() throws Exception {
		// Given database contains
		UserId     verifiedManagerId         = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager           = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId        = testHelpers.createMessageBoardElement(defaultPlayground, verifiedManager);
		UserId     verifiedPlayerId          = testHelpers.registerVerifiedPlayer(defaultCode);
		ElementEntity persistedElement       = elementService.getElementById(persistedElementId);
		UserEntity    verifiedPlayer         = userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		int           postedMessagesAmount   = 5;
		String[]      postedMessageIds       = testHelpers.postMessages(verifiedPlayer, persistedElement, postedMessagesAmount);
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("activityType",      "PostMessage");
		attributes.put("elementId",         persistedElement.getId());
		attributes.put("elementPlayground", persistedElement.getPlayground());
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"ViewActivities",
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail(),
				attributes);
		// When I POST /playground/activities/2019A.kariv/janedoe@work.com
		ActivityTO  actualReturnedElement = restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity,
				ActivityTO.class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
		    "ViewActivities");
		String rawReturnedActivites        = (String) actualReturnedElement.getAttributes().get("createActivityresponseMessage");
		List<ActivityTO> returnedActivites = jackson.readValue(rawReturnedActivites, new TypeReference<List<ActivityTO>>() {});
		assertThat(postedMessageIds.length == returnedActivites.size());
	}
	@Test(expected = Exception.class)
	public void viewActivitiesByElementPlaygroundAndElementIdAndTypeWithManagerAndDefaultPagination() throws Exception {
		// Given database contains
		UserId     verifiedManagerId         = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager           = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId        = testHelpers.createMessageBoardElement(defaultPlayground, verifiedManager);
		UserId     verifiedPlayerId          = testHelpers.registerVerifiedPlayer(defaultCode);
		ElementEntity persistedElement       = elementService.getElementById(persistedElementId);
		UserEntity    verifiedPlayer         = userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		int           postedMessagesAmount   = 5;
		testHelpers.postMessages(verifiedPlayer, persistedElement, postedMessagesAmount);
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("activityType",      "PostMessage");
		attributes.put("elementId",         persistedElement.getId());
		attributes.put("elementPlayground", persistedElement.getPlayground());
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"ViewActivities",
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
				attributes);
		// When I POST /playground/activities/2019A.kariv/janedoe@work.com
		restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity,
				ActivityTO.class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
		    "ViewActivities");
		// And status code <> 200
	}
	@Test
	public void viewActivitiesByElementPlaygroundAndElementIdAndTypeWithPlayerAndCustomPagination() throws Exception {
		// Given database contains
		UserId        verifiedManagerId    = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity    verifiedManager      = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String        persistedElementId   = testHelpers.createMessageBoardElement(defaultPlayground, verifiedManager);
		UserId        verifiedPlayerId     = testHelpers.registerVerifiedPlayer(defaultCode);
		ElementEntity persistedElement     = elementService.getElementById(persistedElementId);
		UserEntity    verifiedPlayer       = userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		Integer       postedMessagesAmount = 5;
		Integer       page                 = 2;
		Integer       sizeOfPage           = 2;
		testHelpers.postMessages(verifiedPlayer, persistedElement, postedMessagesAmount);
		// When I POST /playground/activities/2019A.kariv/janedoe@work.com
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("activityType",      "PostMessage");
		attributes.put("elementId",         persistedElement.getId());
		attributes.put("elementPlayground", persistedElement.getPlayground());
		attributes.put("page", page.toString());
		attributes.put("sizeOfPage", sizeOfPage.toString());
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"ViewActivities",
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail(),
				attributes);
		ActivityTO actualReturnedElement = restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity,
				ActivityTO.class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail()
		    );
		String rawReturnedActivites        = (String) actualReturnedElement.getAttributes().get("createActivityresponseMessage");
		List<ActivityTO> returnedActivites = jackson.readValue(rawReturnedActivites, new TypeReference<List<ActivityTO>>() {});
		assertThat(returnedActivites.size() == (postedMessagesAmount - (page - 1) * sizeOfPage));
	}
}
