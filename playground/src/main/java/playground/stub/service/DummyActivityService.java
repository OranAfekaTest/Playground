package playground.stub.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import playground.layout.io.ActivityTO;
import playground.logic.entity.ActivityEntity;
import playground.logic.service.activity.ActivityAlreadyExistsException;
import playground.logic.service.activity.ActivityIllegalInputException;
import playground.logic.service.activity.ActivityNotFoundException;
import playground.logic.service.activity.ActivityService;

//@Service
public class DummyActivityService implements ActivityService {

	private Map<String, ActivityEntity> activities;

	@PostConstruct
	public void init() {
		this.activities = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public ActivityEntity addNewActivity(ActivityEntity activity)
			throws ActivityAlreadyExistsException, ActivityIllegalInputException {
		if (activity.getId() == null) {
			activity.setId(activity.getElementId() + "_" + activity.getType());
		}
		validateActivity(activity);
		if (this.activities.containsKey(activity.getId())) {
			throw new ActivityAlreadyExistsException("Activity exists with id: " + activity.getId());
		}
		this.activities.put(activity.getId(), activity);
		return activity;
	}

	@Override
	public ActivityEntity getActivityById(String id) throws ActivityNotFoundException {
		ActivityEntity rv = this.activities.get(id);
		if (rv == null) {
			throw new ActivityNotFoundException("no activity for id: " + id);
		}

		return rv;
	}

	@Override
	public List<ActivityEntity> getAll(int size, int page) {
		return new ArrayList<>(this.activities.values() // collection of ActivityEntity
		)// list copy of ActivityEntity
				.stream() // ActivityEntity stream
				.skip(size * page) // ActivityEntity stream
				.limit(size) // ActivityEntity stream
				.collect(Collectors.toList()); // List

	}

	@Override
	public void cleanup() {
		this.activities.clear();

	}

	@Override
	public void updateActivity(String id, ActivityEntity entityUpdates) throws ActivityNotFoundException {
		synchronized (this.activities) {
			ActivityEntity existing = this.activities.get(id);
			if (existing == null) {
				throw new ActivityNotFoundException("no activity for id: " + id);
			}
			boolean dirty = false;
			if (entityUpdates.getElementPlayground() != null
					&& entityUpdates.getElementPlayground() != existing.getElementPlayground()) {
				existing.setElementPlayground(entityUpdates.getElementPlayground());
				dirty = true;
			}
			if (entityUpdates.getPlayerPlayground() != null
					&& entityUpdates.getPlayerPlayground() != existing.getPlayerPlayground()) {
				existing.setPlayerPlayground(entityUpdates.getPlayerPlayground());
				dirty = true;
			}
			if (entityUpdates.getPlayerEmail() != null && entityUpdates.getPlayerEmail() != existing.getPlayerEmail()) {
				existing.setPlayerEmail(entityUpdates.getPlayerEmail());
				dirty = true;
			}
			if (entityUpdates.getElementId() != null && entityUpdates.getElementId() != existing.getElementId()) {
				existing.setElementId(entityUpdates.getElementId());
				dirty = true;
			}
			if (entityUpdates.getType() != null && entityUpdates.getType() != existing.getType()) {
				existing.setType(entityUpdates.getType());
				dirty = true;
			}
			if (entityUpdates.getPlayground() != null && entityUpdates.getPlayground() != existing.getPlayground()) {
				existing.setPlayground(entityUpdates.getPlayground());
				dirty = true;
			}
			if (entityUpdates.getId() != null && entityUpdates.getId() != existing.getId()) {
				existing.setId(entityUpdates.getId());
				dirty = true;
			}
			if (entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().isEmpty()) {
				existing.setAttributes(entityUpdates.getAttributes());
				dirty = true;
			}
			if (dirty) {
				this.activities.put(existing.getId(), existing);
			}
		}
	}

	@Override
	public void validateActivity(ActivityEntity activity) throws ActivityIllegalInputException {
		if (activity.getElementId() == null || activity.getElementId().isEmpty() || activity.getType() == null
				|| activity.getType().isEmpty()) {
			throw new ActivityIllegalInputException("One of the acitivity fields is illegal");
		}
	}
}
