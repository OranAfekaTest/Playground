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
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.activity.ActivityService;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserService;
import playground.test.helpers.TestHelpers;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ViewMessageTest {
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
		System.err.println("Started ActivityController -> View message test with");
		System.err.println("{\"defaultCode\":"+defaultCode+", \"defaultPlayground\":"+defaultPlayground+", \"url\":\""+url+"\"}");
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
	public void viewMessage() throws Exception {
		// Given database contains
		UserId        verifiedManagerId    = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity    verifiedManager      = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String        persistedElementId   = testHelpers.createMessageBoardElement(defaultPlayground, verifiedManager);
		UserId        verifiedPlayerId     = testHelpers.registerVerifiedPlayer(defaultCode);
		ElementEntity persistedElement     = elementService.getElementById(persistedElementId);
		UserEntity    verifiedPlayer       = userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		int           postedMessagesAmount = 1;
		String[]      postedMessageIds     = testHelpers.postMessages(verifiedPlayer, persistedElement, postedMessagesAmount);
		// When I POST /playground/activities/2019A.kariv/johndoe@work.com
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("idOfMessageToView", postedMessageIds[0]);
		ActivityTO postActivity =	new ActivityTO(
				persistedElement.getPlayground(),
				persistedElement.getPlayground(),
				persistedElement.getId(),
				"GetMessage",
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail(),
				attributes);
		ActivityTO actualReturnedActivity = restTemplate.postForObject(
				url + "/activities/{userPlayground}/{email}",
				postActivity, 
				ActivityTO.class,
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail());
		assertThat(actualReturnedActivity.getAttributes().get("createActivityresponseMessage").equals("Passed view message activity. Here is message content: Posting messages for life 0"));
		}
}
