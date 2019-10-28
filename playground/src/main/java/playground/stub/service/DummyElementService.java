package playground.stub.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.service.element.ElementAlreadyExistsException;
import playground.logic.service.element.ElementIllegalAccess;
import playground.logic.service.element.ElementIllegalInputException;
import playground.logic.service.element.ElementNotFoundException;
import playground.logic.service.element.ElementService;

//@Service
public class DummyElementService implements ElementService {

	private Map<String, ElementEntity> elements;

	@PostConstruct
	public void init() {
		this.elements = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public ElementEntity addNewElement(ElementEntity element)
			throws ElementAlreadyExistsException, ElementIllegalInputException {
		if (element.getId() == null) {
			element.setId(element.getCreatorPlayground() + "_" + element.getCreatorEmail() + "_" + element.getName());
		}
		if (element.getCreationDate() == null) {
			element.setCreationDate(new Date());
		}
		validateElementFields(element);
		if (this.elements.containsKey(element.getId())) {
			throw new ElementAlreadyExistsException("Element exists with id: " + element.getId());
		}
		this.elements.put(element.getId(), element);
		return element;
	}

	@Override
	public ElementEntity getElementById(String id) throws ElementNotFoundException {
		ElementEntity rv = this.elements.get(id);
		if (rv == null) {
			throw new ElementNotFoundException("no element for id: " + id);
		}

		return rv;
	}

	@Override
	public List<ElementEntity> getAll(UserEntity user, int size, int page) {
		return new ArrayList<ElementEntity>(elements.values() // collection of ElementEntity
		) // list copy of ElementEntity
				.stream() // ElementEntity stream
				.skip(size * page) // ElementEntity stream
				.limit(size) // ElementEntity stream
				.collect(Collectors.toList()); // List
	}

	@Override
	public List<ElementEntity> getAllNearby(UserEntity user, int size, int page, double x, double y, double distance) {
		// Get all nearby elements
		ArrayList<ElementEntity> allNearbyElements = (ArrayList<ElementEntity>) new ArrayList<ElementEntity>(
				elements.values())
						.stream()
						.filter(element -> x - distance < element.getLocation().getX()
								&& element.getLocation().getX() < x + distance
								&& y - distance < element.getLocation().getY()
								&& element.getLocation().getY() < y + distance)
						.collect(Collectors.toList());
		// Skip some of nearby elements according to pagination
		return allNearbyElements.stream().skip(size * page).limit(size).collect(Collectors.toList());
	}

	@Override
	public List<ElementEntity> getAllByValue(UserEntity user, int size, int page, String attributeName, Object value) {
		System.err.println("Searching " + attributeName + " = " + value);
		// Get all elements of the same type
		ArrayList<ElementEntity> elementsWithTheSameValue = (ArrayList<ElementEntity>) new ArrayList<ElementEntity>(
				elements.values()).stream().filter(element -> {
					if (attributeName.equals("type")) {
						return element.getType().equals(value);
					}
					return false;
				}).collect(Collectors.toList());
		// Skip some of nearby elements according to pagination
		return elementsWithTheSameValue.stream().skip(size * page).limit(size).collect(Collectors.toList());
	}

	@Override
	public void updateElement(String id, ElementEntity entityUpdates) throws ElementNotFoundException {
		ElementEntity existing = this.elements.get(id);
		if (existing == null) {
			throw new ElementNotFoundException("no element for id: " + id);
		}
		validateElementFields(entityUpdates);
		boolean dirty = false;
		if (entityUpdates.getMoreAttributes() != null && !entityUpdates.getMoreAttributes().isEmpty()) {
			existing.setMoreAttributes(entityUpdates.getMoreAttributes());
			dirty = true;
		}
		if (entityUpdates.getPlayground() != null && entityUpdates.getPlayground() != existing.getPlayground()) {
			existing.setPlayground(entityUpdates.getPlayground());
			dirty = true;
		}
		if (entityUpdates.getExpirationDate() != null
				&& entityUpdates.getExpirationDate() != existing.getExpirationDate()) {
			existing.setExpirationDate(entityUpdates.getExpirationDate());
			dirty = true;
		}
		if (entityUpdates.getType() != null && entityUpdates.getType() != existing.getType()) {
			existing.setType(entityUpdates.getType());
			dirty = true;
		}
		if (entityUpdates.getName() != null && entityUpdates.getName() != existing.getName()) {
			existing.setName(entityUpdates.getName());
			dirty = true;
		}
		if (entityUpdates.getLocation() != null && !entityUpdates.getLocation().equals(existing.getLocation())) {
			existing.setLocation(entityUpdates.getLocation());
			dirty = true;
		}
		if (dirty) {
			this.elements.put(existing.getId(), existing);
		}

	}

	@Override
	public void cleanup() {
		this.elements.clear();
	}

	@Override
	public void validateAuthorization(String role) throws ElementIllegalAccess {
		if (role == null || !role.equals("manager")) {
			throw new ElementIllegalAccess("Only managers are authorized to create new elements");
		}
	}

	@Override
	public void validateElementFields(ElementEntity element) throws ElementIllegalInputException {
		if (   element.getPlayground()        == null || element.getPlayground().isEmpty()
				|| element.getLocation()          == null
			  || element.getName()              == null || element.getName().isEmpty()
			  || element.getExpirationDate()    == null
			  || element.getType()              == null || element.getType().isEmpty()
			  || element.getCreatorPlayground() == null || element.getCreatorPlayground().isEmpty()
				|| element.getCreatorEmail()      == null || element.getCreatorEmail().isEmpty()) {			
			throw new ElementIllegalInputException("One of the element fields has illegal value");
		}
	}

	public void validateElementFieldsForUpdateFeature(ElementEntity persistedElement, ElementEntity updatedElement) throws ElementIllegalInputException {
		validateElementFields(updatedElement);
		if (   !persistedElement.getId().equals(updatedElement.getId())
				|| !persistedElement.getCreationDate().equals(updatedElement.getCreationDate())
				|| !persistedElement.getPlayground().equals(updatedElement.getPlayground())
				|| !persistedElement.getCreatorPlayground().equals(updatedElement.getCreatorPlayground())
				|| !persistedElement.getCreatorEmail().equals(updatedElement.getCreatorEmail())) {
			throw new ElementIllegalInputException("Attempt to update one of fields that are not to be edited");
		}
	}
}
