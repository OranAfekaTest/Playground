package playground.layout.controller.element;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
import playground.test.helpers.comparators.ElementEntitiesComparator;
import playground.test.helpers.TestHelpers;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UpdateElementTest {
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
		System.err.println("Started ElementController -> Update element test with");
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
	public void updateAnExistingElementWithCorrectElementDataAndVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity           persistedElement = this.elementService.getElementById(persistedElementId);
		ElementTO               putElement       = new ElementTO(persistedElement);
		HashMap<String, Object> attributes       = new HashMap<String, Object>();
		ArrayList<String>       favoriteFood     = new ArrayList<String>();
		favoriteFood.add("banana");
		favoriteFood.add("strawberry");
		attributes.put("favoriteFood", favoriteFood);
		putElement.setLocation(new Location(15.0, 15.0));
		putElement.setExpirationDate(testHelpers.parseDateFromString("2020-01-01T09:15:00"));
		putElement.setAttributes(attributes);
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then database retrieves for {id}
		ElementEntity expectedElement         = putElement.toEntity();
		ElementEntity updatedPersistedElement = elementService.getElementById(expectedElement.getId()); 
		assertThat(updatedPersistedElement)
		.isNotNull()
		.usingComparator((e1, e2) -> new ElementEntitiesComparator().compare(e1, e2))
		.isEqualTo(expectedElement);
		// And response status is 200
	}
	@Test(expected = Exception.class)
	public void updateExistingElementWithIllegalElementDataAndVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement = elementService.getElementById(persistedElementId);
		ElementTO     putElement       = new ElementTO(persistedElement);
		putElement.setPlayground("");
		putElement.setId("");
		putElement.setLocation(null);
		putElement.setName("");
		putElement.setCreationDate(null);
		putElement.setExpirationDate(null);
		putElement.setType("");
		putElement.setAttributes(null);
		putElement.setCreatorPlayground("");
		putElement.setCreatorEmail("");
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 2xx
	}
	@Test(expected = Exception.class)
	public void updateAnExistingElementWithCorrectElementDataAndWrongManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		UserId     verifiedPlayerId   =	testHelpers.registerVerifiedPlayer(defaultCode);
		UserEntity verifiedPlayer     = userService.getUserById(verifiedPlayerId.getEmail(), verifiedManagerId.getPlayground());
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity           persistedElement = elementService.getElementById(persistedElementId);
		ElementTO               putElement       = new ElementTO(persistedElement);
		HashMap<String, Object> attributes       = new HashMap<String, Object>();
		ArrayList<String>       favoriteFood     = new ArrayList<String>();
		favoriteFood.add("banana");
		favoriteFood.add("strawberry");
		attributes.put("favoriteFood", favoriteFood);
		putElement.setLocation(new Location(15.0, 15.0));
		putElement.setExpirationDate(testHelpers.parseDateFromString("2020-01-01T09:15:00"));
		putElement.setAttributes(attributes);
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 200
	}
	@Test(expected = Exception.class)
	public void illegalUpdateOfFieldIdForExistingElementWithVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement = this.elementService.getElementById(persistedElementId);
		ElementTO     putElement       = new ElementTO(persistedElement);
		putElement.setId("nonexistingvalueforsure");
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 200
	}
	@Test(expected = Exception.class)
	public void illegalUpdateOfFieldPlaygroundForExistingElementWithVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement = elementService.getElementById(persistedElementId);
		ElementTO     putElement       = new ElementTO(persistedElement);
		putElement.setPlayground("nonexistingvalueforsure");
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 200		
	}
	@Test(expected = Exception.class)
	public void illegalUpdateOfFieldCreationDateForExistingElementWithVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement = elementService.getElementById(persistedElementId);
		ElementTO     putElement       = new ElementTO(persistedElement);
		putElement.setCreationDate(putElement.getExpirationDate());
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 200		
	}
	@Test(expected = Exception.class)
	public void illegalUpdateOfFieldTypeForExistingElementWithVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement = elementService.getElementById(persistedElementId);
		ElementTO     putElement       = new ElementTO(persistedElement);
		putElement.setType("nonexistingvalueforsure");
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 200		
	}
	@Test(expected = Exception.class)
	public void illegalUpdateOfFieldCreatorPlaygroundForExistingElementWithVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement = elementService.getElementById(persistedElementId);
		ElementTO     putElement       = new ElementTO(persistedElement);
		putElement.setCreatorPlayground("nonexistingvalueforsure");
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 200
	}
	@Test(expected = Exception.class)
	public void illegalUpdateOfFieldCreatorEmailForExistingElementWithVerifiedManager() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId =	testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I PUT /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement = elementService.getElementById(persistedElementId);
		ElementTO     putElement       = new ElementTO(persistedElement);
		putElement.setCreatorEmail("nonexistingvalueforsure");
		restTemplate.put(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				putElement,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId()
				);
		// Then response status is <> 200
	}
}
