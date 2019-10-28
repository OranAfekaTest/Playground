package playground.layout.controller.element;

import static org.assertj.core.api.Assertions.assertThat;

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
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserService;
import playground.test.helpers.TestHelpers;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ViewElementsByValueTest {
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
		System.err.println("Started ElementController -> View elements by value test with");
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
	public void viewElementsByValueWithCorrectValueAndDefaultPaginationAndVerifiedUser() throws Exception {
		// Given database contains
		Integer    amountOfPersistedElements = 4;
		Integer    amountOfSameTypeElements  = 3;
		UserId     verifiedManagerId         = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager           = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		testHelpers.createElements(
				defaultPlayground,
				verifiedManager,
				amountOfPersistedElements,
				amountOfSameTypeElements,
				"Pet"
				);
		// When I GET /playground/elements/2019A.kariv/janedoe@work.com/search/type/Animal
		ElementTO[] actualReturnedElements = restTemplate.getForObject(
				url + "/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
				ElementTO[].class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
				"type",
				"Animal"
				);
		// Then response contains array of Elements. Size of array is 3
		Integer expectedAmountOfElements = amountOfSameTypeElements;
		assertThat(new Integer(actualReturnedElements.length)).isEqualTo(expectedAmountOfElements);
	}
	@Test
	public void viewElementsByValueWithCorrectValueAndCustomPaginationAndVerifiedUser() throws Exception {
		// Given database contains
		Integer    amountOfPersistedElements = 4;
		Integer    amountOfSameTypeElements  = 3;
		UserId     verifiedManagerId         = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager           = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		testHelpers.createElements(
				defaultPlayground,
				verifiedManager,
				amountOfPersistedElements,
				amountOfSameTypeElements,
				"Pet"
				);
		// When I GET /playground/elements/2019A.kariv/janedoe@work.com/search/type/Animal
		Integer     page                   = new Integer(2);
		Integer     sizeOfPage             = new Integer(2);
		ElementTO[] actualReturnedElements = restTemplate.getForObject(
				url + "/elements/{userPlayground}/{email}/search/{attributeName}/{value}?page={page}&size={size}",
				ElementTO[].class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
				"type",
				"Animal",
				page,
				sizeOfPage
				);
		// Then response contains array of Elements. Size of array is 1
		Integer expectedAmountOfElements = amountOfSameTypeElements - (page - 1) * sizeOfPage;
		assertThat(new Integer(actualReturnedElements.length)).isEqualTo(expectedAmountOfElements);
	}
}
