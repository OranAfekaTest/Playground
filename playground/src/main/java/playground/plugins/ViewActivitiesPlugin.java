package playground.plugins;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.aop.PlayerAuthorization;
import playground.dal.ActivityDao;
import playground.dal.ElementDao;
import playground.dal.UserDao;
import playground.layout.io.ActivityTO;
import playground.logic.entity.ActivityEntity;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.service.activity.ActivityNotFoundException;
import playground.logic.service.element.ElementNotFoundException;
import playground.logic.service.user.UserNotFoundException;

@Component
public class ViewActivitiesPlugin implements PlaygroundPlugin {
	private static String DEFAULT_PAGE = "1";
	private static String SIZE_OF_PAGE = "5";
	private ObjectMapper jackson;
	private UserDao      users;
	private ActivityDao  activities;

	@Autowired
	public ViewActivitiesPlugin(UserDao users, ElementDao elements, ActivityDao activities) {
		this.users = users;
		this.activities = activities;
	}

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Override
	@PlayerAuthorization
	public Object invokeOperation(ActivityEntity activity) {
		try {
			UserEntity user = users.findByEmailLike(activity.getPlayerEmail());
			if (user == null)
				throw new UserNotFoundException("No User Found with Email " + activity.getPlayerEmail());
			Map<String, String> activityJsonAttributes = jackson.readValue(
					activity.getJsonAttributes(), Map.class);
			// Validate that values for selection are present.
			if (activityJsonAttributes.get("activityType") == null &&
					activityJsonAttributes.get("ActivityType") == null
					) {
				throw new ActivityNotFoundException("Can not search for activities without type value.");
			}
			if (activityJsonAttributes.get("elementId") == null &&
					activityJsonAttributes.get("ElementId") == null) {
				throw new ActivityNotFoundException("Can not search for activities without element id value.");
			}
			if (activityJsonAttributes.get("elementPlayground") == null &&
					activityJsonAttributes.get("ElementPlyaground") == null) {
				throw new ActivityNotFoundException("Can not search for activities without element playground value.");
			}
			String activityType = activityJsonAttributes.get("activityType");
			if (activityType == null) {
				activityType = activityJsonAttributes.get("ActivityType");
			}
			String elementId = activityJsonAttributes.get("elementId");
			if (elementId == null) {
				elementId = activityJsonAttributes.get("ElementId");
			}
			String elementPlayground = activityJsonAttributes.get("elementPlayground");
			if (elementPlayground == null) {
				elementPlayground = activityJsonAttributes.get("ElementPlayground");
			}
			String rawPage = activityJsonAttributes.get("page");
			if (rawPage == null) {
				rawPage = activityJsonAttributes.get("Page");
				if (rawPage == null) {
					rawPage = DEFAULT_PAGE;
				}
			}
			Integer page = new Integer(rawPage);
			page         -= 1; // Because we start from 0.
			String rawPageSize = activityJsonAttributes.get("sizeOfPage");
			if (rawPageSize == null) {
				rawPageSize = activityJsonAttributes.get("SizeOfPage");
				if (rawPageSize == null) {
					rawPageSize = SIZE_OF_PAGE;
				}
			}
			Integer pageSize = new Integer(rawPageSize);
			List<ActivityEntity> foundActivities = activities.findAllByElementPlaygroundLikeAndElementIdLikeAndTypeLike(
					elementPlayground, elementId, activityType, PageRequest.of(
							page, pageSize, Direction.DESC, "creationDate"));
			ActivityTO[] foundActivitiesTO = foundActivities.stream().map(a -> new ActivityTO(a)).collect(Collectors.toList()).toArray(new ActivityTO[0]);
			String rawFoundActivitiesTO = jackson.writeValueAsString(foundActivitiesTO);
		  return new ActivityResponse(rawFoundActivitiesTO);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
