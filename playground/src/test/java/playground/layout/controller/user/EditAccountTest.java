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
import playground.test.helpers.comparators.UserEntitiesComparator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EditAccountTest {
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
		System.err.println("Started UserController -> Edit account test with");
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
	// Feature: Edit account
	@Test
	public void editVerifiedAccountWithCorrectUserData() throws Exception {
		// Given database contains
		UserId verifiedManagerId = testHelpers.registerVerifiedManager(defaultCode);
		// When I PUT /playground/users/2019A.kariv/janedoe@work.com with
		UserEntity verifiedManager = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		UserTO     putUser         = new UserTO(verifiedManager);
		putUser.setUsername("Updated" + verifiedManager.getUsername());
		putUser.setAvatar(verifiedManager.getAvatar() + "/300x400");
		putUser.setRole("player");
		restTemplate.put(
				url + "/users/{playground}/{email}", putUser,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail()
				);
		// Then database retrieves for janedoe@work.com
		UserEntity expectedUser = putUser.toEntity();
		// Following sets are needed to meet comparator requirements
		// Either way, we did not try to edit netiher code, nor role
		expectedUser.setPoints(verifiedManager.getPoints());
		expectedUser.setCode(verifiedManager.getCode());
		UserEntity updatedPersistedUser = userService.getUserById(verifiedManager.getEmail(), verifiedManager.getPlayground());
		assertThat(updatedPersistedUser)
		.isNotNull()
		.usingComparator((u1,u2)-> new UserEntitiesComparator().compare(u1, u2))
		.isEqualTo(expectedUser);
		// And response status is 200
	}
	@Test(expected = Exception.class)
	public void editVerifiedAccountWithIllegalUserData() throws Exception {
		// Given database contains
		UserId verifiedManagerId = testHelpers.registerVerifiedManager(defaultCode);
		// When I PUT /playground/users/2019A.kariv/janedoe@work.com with
		UserEntity verifiedManager = userService.getUserById(verifiedManagerId.getEmail(), verifiedManagerId.getPlayground());
		UserTO     putUser         = new UserTO(verifiedManager);
		putUser.setEmail("");
		putUser.setPlayground("");
		putUser.setUsername("");
		putUser.setAvatar("");
		putUser.setRole("");
		restTemplate.put(
				url + "/users/{playground}/{email}", putUser,
				verifiedManager.getPlayground(),
				verifiedManager.getEmail()
				);
		// Then status is <> 2xx
	}
	@Test(expected = Exception.class)
	public void unauthorizedEditOfRoleFieldInVerifiedAccount() throws Exception {
		// Given database contains
		UserId verifiedPlayerId = testHelpers.registerVerifiedPlayer(defaultCode);
		// When I PUT /playground/users/2019A.kariv/johndoe@work.com with
		UserEntity verifiedPlayer = this.userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		UserTO     putUser        =	new UserTO(verifiedPlayer);
		putUser.setRole("manager");
		restTemplate.put(url + "/users/{playground}/{email}", putUser,
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail()
				);
		// Then status is <> 2xx
	}
	@Test(expected = Exception.class)
	public void unauthorizedEditOfPointsFieldInVerifiedAccount() throws Exception {
		// Given database contains
		UserId verifiedPlayerId = testHelpers.registerVerifiedPlayer(defaultCode);
		// When I PUT /playground/users/2019A.kariv/johndoe@work.com with
		UserEntity verifiedPlayer = this.userService.getUserById(verifiedPlayerId.getEmail(), verifiedPlayerId.getPlayground());
		UserTO     putUser        =	new UserTO(verifiedPlayer);
		putUser.setPoints(new Long(1000));
		restTemplate.put(url + "/users/{playground}/{email}", putUser,
				verifiedPlayer.getPlayground(),
				verifiedPlayer.getEmail()
				);
		// Then status is <> 2xx
	}
}
