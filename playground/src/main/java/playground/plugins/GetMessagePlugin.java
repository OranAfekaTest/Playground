package playground.plugins;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort.Direction;

import playground.aop.PlayerAuthorization;
import playground.dal.ActivityDao;
import playground.dal.ElementDao;
import playground.logic.entity.ActivityEntity;
import playground.logic.service.activity.ActivityNotFoundException;


@Component
public class GetMessagePlugin implements PlaygroundPlugin  {

	private ActivityDao activities;
	private ElementDao elements;
	private ObjectMapper jackson;
	
	@Autowired
	public GetMessagePlugin(ActivityDao activities) {
		this.activities=activities;
		this.jackson= new ObjectMapper();
		
	}
	
	@Override
	public Object invokeOperation(ActivityEntity activityForViewingMessage) {
		// Max - we should not search by Type.
		// If there are many messages in message board,
		// find by Type "PostMessage" will return all of them.
		// in order to view message, we are sending activity that contains its id,
		// i.e. attributes of activity for viewing a message are {"idOfMessageToView": "[MESSAGE_ID]"}
		try {			
			String idOfMessageToView = (String) activityForViewingMessage.getAttributes().get("idOfMessageToView");
			if (!activities.existsById(idOfMessageToView)) {
				throw new ActivityNotFoundException("Board does not contain such message.");
			}
			ActivityEntity messageToView = activities.findById(idOfMessageToView).get();
			HashMap<String, Object> messageToViewAttributes = jackson.readValue(messageToView.getJsonAttributes(), HashMap.class);
			String messageContent = (String) messageToViewAttributes.get("Content");
			if (messageContent == null) {
				messageContent = (String) messageToViewAttributes.get("content");
			}
			return new ActivityResponse("Passed view message activity. Here is message content: " + messageContent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
