package playground.layout.controller.user;

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

import playground.layout.io.UserTO;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.user.UserService;
import playground.test.helpers.TestHelpers;
import playground.test.helpers.comparators.UserTOsComparator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginTest {
	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	@Autowired
	private UserService userService;
	@Value("${playground.verification.code}")
	private String defaultCode;
	@Value("${playground.basic.points}")
	private Long defaultPoints;
	private TestHelpers testHelpers;
	
	@PostConstruct
	public void init() {
		restTemplate = new RestTemplate();
		url = "http://localhost:" + port + "/playground";
		System.err.println("Started UserController -> Login test with");
		System.err.println("{\"defaultCode\":"+defaultCode+", \"defaultPoints\":"+defaultPoints+", \"url\":\""+url+"\"}");
	}
	@Before
	public void setup() {
		testHelpers = new TestHelpers(userService);
	}
	@After
	public void teardown() {
		userService.cleanup();
		testHelpers = null;
	}
	@Test
	public void testServerIsBootingAndContextPresent() throws Exception {
		assertThat(userService).isNotNull();
	}
	// Feature: Login
	@Test
	public void loginToRegisteredVerifiedAccount() throws Exception {
		// Given database contains
		UserId verifiedManagerId = testHelpers.registerVerifiedManager(defaultCode);
		// When I GET /playground/users/login/2019A.kariv/janedoe@work.com
		UserEntity verifiedManager    = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		UserTO     actualReturnedUser = restTemplate.getForObject(
				url + "/users/login/{playground}/{email}",
				UserTO.class,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail()
				);
		// Then response contains
		UserTO expectedReturnedUser = new UserTO(verifiedManager);
		assertThat(actualReturnedUser)
		.isNotNull()
		.usingComparator((u1,u2)-> new UserTOsComparator().compare(u1, u2))
		.isEqualTo(expectedReturnedUser);
	}
	@Test(expected = Exception.class)
	public void loginToRegisteredNonVerifiedAccount() throws Exception {
		// Given database contains
		UserId unverifiedPlayerId = testHelpers.registerUnverifiedPlayer();
		// When I GET /playground/users/login/2019A.kariv/dereckdoe@work.com
		UserEntity unverifiedPlayer = userService.getUserById(unverifiedPlayerId.getEmail(), unverifiedPlayerId.getPlayground());
		restTemplate.getForObject(
				url + "/users/login/{playground}/{email}",
				UserTO.class,
				unverifiedPlayer.getPlayground(),
				unverifiedPlayer.getEmail()
				);
		// Then response status is <> 2xx
	}

}
