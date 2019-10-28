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

import playground.layout.io.NewUserForm;
import playground.layout.io.UserTO;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;
import playground.logic.service.user.UserService;
import playground.test.helpers.TestHelpers;
import playground.test.helpers.comparators.UserEntitiesComparator;
import playground.test.helpers.comparators.UserTOsComparator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RegisterAccountTest {
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
	@Value("${playground.name}")
	private String defaultPlayground;
	private TestHelpers testHelpers;
	
	@PostConstruct
	public void init() {
		restTemplate = new RestTemplate();
		url = "http://localhost:" + port + "/playground";
		System.err.println("Started UserController -> Register account test with");
		System.err.println("{\"defaultCode\":"+defaultCode+", \"defaultPoints\":"+defaultPoints+""
				+ ", \"defaultPlayground\":"+defaultPlayground+"\"url\":\""+url+"\"}");
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
	// Feature: Register account
	@Test
	public void registerAnAccountWithValidUserForm() throws Exception {
		// Given database contains
		testHelpers.registerVerifiedManager(defaultCode);
		// When I POST /playground/users with
		String      email              = "johndoe@work.com";
		String      playground         = defaultPlayground;
		Long        points             = defaultPoints;
		String      verificationCode   = defaultCode;
		String      username           = "John";
		String      avatar             = "https://www.blogtyrant.com/gravatar_john.jpg";
		String      role               = "player";
		NewUserForm postUser           = new NewUserForm(email, username, avatar, role);
		UserTO      actualReturnedUser = restTemplate.postForObject(
				url + "/users",
				postUser,
				UserTO.class
				);
		// Then database retrieves for email
		UserEntity expectedUser = new UserEntity(email, playground, username, avatar, role, points, verificationCode);
		UserEntity createdUser	=	userService.getUserById(email, playground);
		assertThat(createdUser)
		.isNotNull()
		.usingComparator((u1,u2)-> new UserEntitiesComparator().compare(u1, u2))
		.isEqualTo(expectedUser);
		// And response contains
		UserTO expectedUserTO = new UserTO(expectedUser);
		assertThat(actualReturnedUser)
		.isNotNull()
		.usingComparator((u1,u2)-> new UserTOsComparator().compare(u1, u2))
		.isEqualTo(expectedUserTO);
	}
	@Test(expected = Exception.class)
	public void registerAnAccountWithInvalidUserForm() throws Exception {
		// Given server is up
		// When I POST /playground/users with
		String email         = "";
		String username      = "";
		String avatar        = "https://www.blogtyrant.com/gravatar_john.jpg";
		String role          = "player";
		NewUserForm postUser = new NewUserForm(email, username, avatar, role);
		try {
			restTemplate.postForObject(
					url + "/users",
					postUser, 
					UserTO.class
					);
		} catch (Exception e) {
			// expected to fail
		}
		// Then database throws exception for retrieving by email
		userService.getUserById(email, defaultPlayground);
		// and response status is <> 2xx
	}
	@Test(expected = Exception.class)
	public void registerAnExistingAccount() throws Exception {
		// Given database contains
		UserId existingManagerId = testHelpers.registerVerifiedManager(defaultCode);
		// When I POST /playground/users with
		UserEntity  existingManager = userService.getUserById(existingManagerId.getEmail(), existingManagerId.getPlayground());
		NewUserForm postUser        = new NewUserForm(
				existingManager.getEmail(),
				existingManager.getUsername(),
				existingManager.getAvatar(),
				existingManager.getRole());
		restTemplate.postForObject(
				url + "/users",
				postUser, 
				UserTO.class
				);
		// Then response status is <> 2xx
	}
}
