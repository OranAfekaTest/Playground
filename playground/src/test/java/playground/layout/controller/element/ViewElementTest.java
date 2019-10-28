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
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserService;
import playground.test.helpers.TestHelpers;
import playground.test.helpers.comparators.ElementTOsComparator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ViewElementTest {
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
		System.err.println("Started ElementController -> View element test with");
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
	public void viewAnElementWithVerifiedUser() throws Exception {
		// Given database contains
		UserId     verifiedManagerId  = testHelpers.registerVerifiedManager(defaultCode);
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		String     persistedElementId = testHelpers.createElement(defaultPlayground, verifiedManager);
		// When I GET /playground/elements/2019A.kariv/janedoe@work.com/2019A.kariv/{id}
		ElementEntity persistedElement      = elementService.getElementById(persistedElementId);
		ElementTO     expectedElement       = new ElementTO(persistedElement);
		ElementTO     actualReturnedElement = restTemplate.getForObject(
				url + "/elements/{userPlayground}/{email}/{playground}/{id}",
				ElementTO.class,
				persistedElement.getCreatorPlayground(),
				persistedElement.getCreatorEmail(),
				persistedElement.getPlayground(),
				persistedElement.getId());
		// Then response contains
		assertThat(actualReturnedElement)
		.isNotNull()
		.usingComparator((e1,e2)-> new ElementTOsComparator().compare(e1, e2))
		.isEqualTo(expectedElement);
	}
}
