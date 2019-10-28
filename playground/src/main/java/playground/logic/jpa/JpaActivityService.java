package playground.logic.jpa;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationContext;
import playground.aop.MyLog;
import playground.dal.ActivityDao;
import playground.layout.io.ActivityTO;
import playground.layout.io.ElementTO;
import playground.logic.entity.ActivityEntity;
import playground.logic.service.activity.ActivityAlreadyExistsException;
import playground.logic.service.activity.ActivityIllegalInputException;
import playground.logic.service.activity.ActivityNotFoundException;
import playground.logic.service.activity.ActivityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import playground.plugins.PlaygroundPlugin;

@Service
public class JpaActivityService implements ActivityService{
	
	private ActivityDao activities;
	private ActivityIdGeneratorDao idGenerator;
    private ApplicationContext spring;
    private ObjectMapper jackson;
	
	@Autowired
    public JpaActivityService(ActivityDao activities, ActivityIdGeneratorDao idGenerator, ApplicationContext spring) {
		super();
		this.activities = activities;
		this.idGenerator = idGenerator;
        this.spring = spring;
        jackson = new ObjectMapper();
	}
	
	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.activities.deleteAll();		
	}

	@Override
	@Transactional
	@MyLog
	public ActivityEntity addNewActivity(ActivityEntity activity)
			throws ActivityAlreadyExistsException, ActivityIllegalInputException {
		validateActivity(activity);
		String activityId = activity.getId();
		if (activityId != null) {
			if (activities.existsById(activityId)) {
				throw new ActivityAlreadyExistsException("activity exists with id: " + activity.getId());				
			}
		}
		ActivityIdGenerator tmp = this.idGenerator.save(new ActivityIdGenerator()); 
		Long dummyId = tmp.getId();
		this.idGenerator.delete(tmp);
		activity.setId(activity.getPlayerPlayground() + dummyId);
		activity.setCreationDate(new Date());
    try {
    	String type = activity.getType();
      String className = "playground.plugins." + type + "Plugin";
      System.err.println("ACTIVITY TYPE: " + className);
      Class<?> theClass = Class.forName(className);

      PlaygroundPlugin plugin = (PlaygroundPlugin) this.spring.getBean(theClass);
      Object rv = plugin.invokeOperation(activity);
      Map<String, Object> rvMap = this.jackson.readValue(this.jackson.writeValueAsString(rv), Map.class);
      activity.getAttributes().putAll(rvMap);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    return this.activities.save(activity);
	}

	@Override
	@Transactional(readOnly=true)
	@MyLog
	public ActivityEntity getActivityById(String id) throws ActivityNotFoundException {
		return 
				this.activities.findById(id)
				.orElseThrow(()->
					new ActivityNotFoundException(
						"no activity with id: " + id));
	}

	@Override
	@Transactional(readOnly=true)
	@MyLog
	public List<ActivityEntity> getAll(int size, int page) {
		
		return this.activities.findAll(
				PageRequest.of(page, size, Direction.DESC, "creationDate"))
			.getContent();
		
		
		/*List<ActivityEntity> allList = new ArrayList<>();
		this.activities
				.findAll()
				.forEach(o->allList.add(o));
		
		return 
			allList
			.stream() // ActivityEntity stream
			.skip(size * page) // ActivityEntity stream 
			.limit(size) // ActivityEntity stream
			.collect(Collectors.toList()); // List
*/	}

	@Override
	@Transactional
	@MyLog
	public void updateActivity(String id, ActivityEntity entityUpdates) throws ActivityNotFoundException {
		ActivityEntity existing = this.getActivityById(id);
		if (existing == null) {
			throw new ActivityNotFoundException("no activity for id: " + id);
		}
		if (entityUpdates.getElementPlayground() != null && entityUpdates.getElementPlayground() != existing.getElementPlayground()) {
			existing.setElementPlayground(entityUpdates.getElementPlayground());
		}
		if (entityUpdates.getPlayerPlayground() != null && entityUpdates.getPlayerPlayground() != existing.getPlayerPlayground()) {
			existing.setPlayerPlayground(entityUpdates.getPlayerPlayground());
		}
		if (entityUpdates.getPlayerEmail() != null && entityUpdates.getPlayerEmail() != existing.getPlayerEmail()) {
			existing.setPlayerEmail(entityUpdates.getPlayerEmail());
		}
		if (entityUpdates.getElementId() != null && entityUpdates.getElementId() != existing.getElementId()) {
			existing.setElementId(entityUpdates.getElementId());
		}
		if (entityUpdates.getType() != null && entityUpdates.getType() != existing.getType()) {
			existing.setType(entityUpdates.getType());
		}
		if (entityUpdates.getPlayground() != null && entityUpdates.getPlayground() != existing.getPlayground()) {
			existing.setPlayground(entityUpdates.getPlayground());
		}
		if (entityUpdates.getId() != null && entityUpdates.getId() != existing.getId()) {
			existing.setId(entityUpdates.getId());
		}
		if (entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().isEmpty()) {
			existing.setAttributes(entityUpdates.getAttributes());
		}
		this.activities.save(existing);
		
	}

	@Override
	public void validateActivity(ActivityEntity activity) throws ActivityIllegalInputException {
		if (   activity.getPlayground() == null        || activity.getPlayerPlayground().isEmpty()
				|| activity.getElementPlayground() == null || activity.getPlayerPlayground().isEmpty()
				|| activity.getElementId() == null         || activity.getElementId().isEmpty()
				|| activity.getType() == null              || activity.getType().isEmpty()
				|| activity.getPlayerPlayground() == null  || activity.getPlayerPlayground().isEmpty()
				|| activity.getPlayerEmail() == null       || activity.getPlayerEmail().isEmpty()) {
			throw new ActivityIllegalInputException("One of the acitivity fields is illegal");
		}
	}
	
}
