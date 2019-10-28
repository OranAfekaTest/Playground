package playground.layout.controller.element;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
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

import playground.layout.io.ElementTO;
import playground.layout.io.Location;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserService;
import playground.test.helpers.TestHelpers;
import playground.test.helpers.comparators.ElementEntitiesComparator;
import playground.test.helpers.comparators.ElementTOsComparator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CreateElementTest {
	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	@Autowired
	private ElementService elementService;
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
		System.err.println("Started ElementController -> Create element test with");
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
	public void testServerIsBootingAndContextPresent() throws Exception {
		assertThat(userService).isNotNull();
		assertThat(elementService).isNotNull();
	}
	@Test
	public void createAnElementWithCorrectElementDataAndVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager   = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		testHelpers.createElement(defaultPlayground, verifiedManager);
		// When POST /playground/elements/2019A.kariv/janedoe@work.com
		Location                location       = new Location(10.0, 10.0);
		String                  name           =	"Bear";
		Date                    expirationDate = testHelpers.parseDateFromString("2019-12-01T09:15:00");
		String                  type           = "Animal";
		HashMap<String, Object> attributes     = new HashMap<String, Object>();
		ArrayList<String>       favoriteFood   = new ArrayList<String>();
		favoriteFood.add("grapefruit");
		favoriteFood.add("apple");
		attributes.put("favoriteFood", favoriteFood);
		ElementTO postElement = new ElementTO(
				defaultPlayground,
				location,
				name,
				expirationDate,
				type,
				attributes,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail());
		ElementTO actualReturnedElement = restTemplate.postForObject(
				url + "/elements/{userPlayground}/{email}",
				postElement,
				ElementTO.class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail()
				);
		// Then database retrieves for id of actualReturnedElement
		ElementEntity expectedElement  = postElement.toEntity();
		ElementEntity persistedElement = elementService.getElementById(actualReturnedElement.getId());
		assertThat(persistedElement)
		.isNotNull()
		.usingComparator((e1,e2)-> new ElementEntitiesComparator().compareForCreationOfNewElement(e1, e2))
		.isEqualTo(expectedElement);
		// And response contains
		assertThat(actualReturnedElement)
		.isNotNull()
		.usingComparator((e1,e2)-> new ElementTOsComparator().compareForCreationOfNewElement(e1, e2))
		.isEqualTo(postElement);
	}
	@Test(expected = Exception.class)
	public void createAnElementWithIllegalElementDataAndVerifiedManager() throws Exception {
		// Given database contains
		UserId verifiedManagerId = testHelpers.registerVerifiedManager(defaultCode);
		// When POST /playground/elements/2019A.kariv/janedoe@work.com
		UserEntity verifiedManager = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		ElementTO  postElement     = new ElementTO("",	null,	"",	null, "",	null,	"",	"");
		restTemplate.postForObject(
				url + "/elements/{userPlayground}/{email}",
				postElement, 
				ElementTO.class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail()
				);
		// Then response status is <> 2xx
	}
	@Test(expected = Exception.class)
	public void createAnElementWithCorrectElementDataAndUIllegalManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager   = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		UserId     verifiedPlayerId  = testHelpers.registerVerifiedPlayer(defaultCode);
		testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I POST /playground/elements/2019A.kariv/johndoe@work.com
		UserEntity              verifiedPlayer = userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		Location                location       = new Location(10.0, 10.0);
		String                  name           =	"Bear";
		Date                    expirationDate = testHelpers.parseDateFromString("2019-12-01T09:15:00");
		String                  type           =	"Animal";
		HashMap<String, Object> attributes     = new HashMap<String, Object>();
		ArrayList<String>       favoriteFood   = new ArrayList<String>();
		favoriteFood.add("grapefruit");
		favoriteFood.add("apple");
		attributes.put("favoriteFood", favoriteFood);
		ElementTO postElement = new ElementTO(
				defaultPlayground,
				location,
				name,
				expirationDate,
				type,
				attributes,
				verifiedManager.getPlayground(),
				verifiedManager.getPlayground()
				);
		restTemplate.postForObject(
				url + "/elements/{userPlayground}/{email}",
				postElement, 
				ElementTO.class,
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail()
				);
		// Then response status is <> 2xx
	}
}
