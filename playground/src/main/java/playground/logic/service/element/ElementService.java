package playground.logic.service.element;

import java.util.List;

import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;

public interface ElementService {

	public void cleanup();
	
	public ElementEntity addNewElement(ElementEntity element) throws ElementAlreadyExistsException, ElementIllegalInputException;
	
	public ElementEntity getElementById(String id) throws ElementNotFoundException;
	
	public List<ElementEntity> getAll(UserEntity user, int size, int page);
	
	public List<ElementEntity> getAllNearby(UserEntity user, int size, int page, double x, double y, double distance);
	
	public List<ElementEntity> getAllByValue(UserEntity user, int size, int page, String valueType, Object value);

	public void updateElement(String id, ElementEntity element) throws ElementNotFoundException;
	
	public void validateAuthorization(String role) throws ElementIllegalAccess;
	
	public void validateElementFields(ElementEntity element) throws ElementIllegalInputException;
	
	public void validateElementFieldsForUpdateFeature(ElementEntity persistedElement, ElementEntity updatedElement) throws ElementIllegalInputException;
}
