package playground.logic.service.activity;

import java.util.List;

import playground.layout.io.ActivityTO;
import playground.logic.entity.ActivityEntity;

public interface ActivityService {

	public void cleanup();

	public ActivityEntity addNewActivity(ActivityEntity activity) throws ActivityAlreadyExistsException, ActivityIllegalInputException;

	public ActivityEntity getActivityById(String id) throws ActivityNotFoundException;

	public List<ActivityEntity> getAll(int size, int page);
	
	public void updateActivity(String id, ActivityEntity entityUpdates) throws ActivityNotFoundException;
	
	public void validateActivity(ActivityEntity activity) throws ActivityIllegalInputException;
}
