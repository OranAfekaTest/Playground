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
public class ViewNearbyElementsTest {
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
		System.err.println("Started ElementController -> View nearby elements test with");
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
	public void viewNearbyElementsWithDefaultPaginationAndVerifiedUser() throws Exception {
		// Given database contains
		Integer    amountOfPersistedElements = 5;
		Integer    amountOfNearbyElements    = 3;
		UserId     verifiedManagerId         = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager           = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		testHelpers.createElements(
				defaultPlayground,
				verifiedManager,
				amountOfPersistedElements,
				amountOfNearbyElements,
				new Double(100),
				new Double(100)
				);
		// When I GET /playground/elements/2019A.kariv/janedoe@work.com/near/0/0/10
		ElementTO[] actualReturnedElements = restTemplate.getForObject(
				url + "/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}",
				ElementTO[].class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
				0,
				0,
				10
				);
		// Then response contains array of ElementTO. Size of array is 3
		assertThat(new Integer(actualReturnedElements.length)).isEqualTo(amountOfNearbyElements);
	}
	@Test
	public void viewNearbyElementsWithCustomPaginationAndVerifiedUser() throws Exception {
		// Given database contains
		Integer    amountOfPersistedElements = 5;
		Integer    amountOfNearbyElements    = 3;
		UserId     verifiedManagerId         = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager           = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		testHelpers.createElements(
				defaultPlayground,
				verifiedManager,
				amountOfPersistedElements,
				amountOfNearbyElements,
				new Double(100),
				new Double(100)
				);
		// When I GET /playground/elements/2019A.kariv/janedoe@work.com/near/0/0/10
		Integer     page                   = new Integer(2);
		Integer     sizeOfPage             = new Integer(2);
		ElementTO[] actualReturnedElements = restTemplate.getForObject(
				url + "/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}?page={page}&size={size}",
				ElementTO[].class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail(),
				0,
				0,
				10,
				page,
				sizeOfPage
				);
		// Then response contains array of ElementTO. Size of array is 1
		Integer expectedAmountOfElements = new Integer(amountOfNearbyElements - (page - 1) * sizeOfPage);
		assertThat(new Integer(actualReturnedElements.length)).isEqualTo(expectedAmountOfElements);
	}
	
}
