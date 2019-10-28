package playground.layout.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import playground.layout.io.ActivityTO;
import playground.layout.io.ElementTO;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.service.element.ElementService;
import playground.logic.service.user.UserService;

@RestController
public class ElementController {
	
	private UserService userService;
	private ElementService elementService;
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	// POST /playground/elements/{userPlayground}/{email}
	// createElement
	@RequestMapping(method = RequestMethod.POST, path = "/playground/elements/{userPlayground}/{email}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @RequestBody ElementTO element) throws Exception {		
		
		UserEntity user = userService.login(email, userPlayground);
		elementService.validateAuthorization(user.getRole());
		ElementEntity newElement = element.toEntity();
		ElementEntity persistedElement = this.elementService.addNewElement(newElement);
		return new ElementTO(persistedElement);
	}

	// PUT /playground/elements/{userPlayground}/{email}/{id}
	// editElement
	@RequestMapping(method = RequestMethod.PUT, path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("playground") String playground,
			@PathVariable("id") String id, @RequestBody ElementTO element) throws Exception {
		UserEntity user = userService.login(email, userPlayground);
		elementService.validateAuthorization(user.getRole());
		ElementEntity updatedElement = element.toEntity();
		elementService.updateElement(id, updatedElement);
	}

	// GET /playground/elements/{userPlayground}/{email}/{playground}/{id}
	// viewElement
	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO viewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("playground") String playground,
			@PathVariable("id") String id) throws Exception {
		userService.login(email, userPlayground);
		System.err.println("Looking for " + id);
		return new ElementTO(elementService.getElementById(id));
	}

	// GET /playground/elements/{userPlayground}/{email}/all
	// viewAllElements
	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "1") int page) throws Exception {
		UserEntity loggedInUser = userService.login(email, userPlayground);
		int pageIndex = page - 1;
		return this.elementService.getAll(loggedInUser, size, pageIndex).stream().map(ElementTO::new).collect(Collectors.toList())
				.toArray(new ElementTO[0]);
	}

	// GET /playground/elements/near/{userPlayground}/{email}/{x}/{y}/{distance}
	// viewNearbyElements
	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("x") double x, @PathVariable("y") double y,
			@PathVariable("distance") double distance,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "1") int page) throws Exception {
		UserEntity loggedInUser = userService.login(email, userPlayground);
		int pageIndex = page - 1;
		return this.elementService.getAllNearby(loggedInUser, size, pageIndex, x, y, distance).stream().map(ElementTO::new)
				.collect(Collectors.toList()).toArray(new ElementTO[0]);
	}

	// GET
	// /playground/elements/search/{userPlayground}/{email}/{attributeName}/{value},
	// viewElementsByValue
	@RequestMapping(method = RequestMethod.GET, path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] viewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("attributeName") String attributeName,
			@PathVariable("value") Object value,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "1") int page) throws Exception {
		UserEntity loggedInUser = userService.login(email, userPlayground);
		int indexPage = page - 1;
		return this.elementService.getAllByValue(loggedInUser, size, indexPage, attributeName, value).stream().map(ElementTO::new)
				.collect(Collectors.toList()).toArray(new ElementTO[0]);
	}
}
