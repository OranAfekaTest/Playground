package playground.logic.jpa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.MyLog;
import playground.dal.ElementDao;
import playground.layout.io.Location;
import playground.logic.entity.ElementEntity;
import playground.logic.entity.UserEntity;
import playground.logic.service.element.ElementAlreadyExistsException;
import playground.logic.service.element.ElementIllegalAccess;
import playground.logic.service.element.ElementIllegalInputException;
import playground.logic.service.element.ElementNotFoundException;
import playground.logic.service.element.ElementService;

@Service
public class JpaElementService implements ElementService{
	
	private ElementDao elements;
	private ElementIdGeneratorDao idGenerator;
	
	@Autowired
	public JpaElementService(ElementDao elements, ElementIdGeneratorDao IdGenerator) {
		super();
		this.elements = elements;
		this.idGenerator = IdGenerator;
	}

	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.elements.deleteAll();
		
	}

	@Override
	@Transactional
	@MyLog
	public ElementEntity addNewElement(ElementEntity element)
			throws ElementAlreadyExistsException, ElementIllegalAccess {
		validateElementFields(element);
		String newElementId = element.getId();
		if (newElementId != null) {
			if (elements.existsById(newElementId)) {
				throw new ElementAlreadyExistsException("element exists with id: " + element.getId());				
			}
		}
		ElementIdGenerator tmp = this.idGenerator.save(new ElementIdGenerator()); 
		Long dummyId           = tmp.getId();
		idGenerator.delete(tmp);
		element.setId(element.getPlayground() + dummyId);
		element.setCreationDate(new Date());
		return elements.save(element);
	}

	@Override
	@Transactional(readOnly=true)
	@MyLog
	public ElementEntity getElementById(String id) throws ElementNotFoundException {
		return 
				this.elements.findById(id)
				.orElseThrow(()->
					new ElementNotFoundException(
						"no element with id: " + id + " was found"));
	}

	@Override
	@Transactional(readOnly=true)
	@MyLog
	public List<ElementEntity> getAll(UserEntity user, int size, int page) {
		if (user.getRole().equals("player")) {
			return elements.findAllByExpirationDateAfter(new Date(),
					PageRequest.of(page, size));
		}
		else {
			return this.elements.findAll(
					PageRequest.of(page, size, Direction.DESC, "creationDate"))
					.getContent();			
		}
	}
	
	@Override
	@Transactional
	@MyLog
	public List<ElementEntity> getAllNearby(UserEntity user, int size, int page, double x, double y, double distance) {
		double minX = x - distance;
		double maxX = x + distance;
		double minY = y - distance;
		double maxY = y + distance;
		if (user.getRole().equals("player")) {
			return elements.findAllByLocationXGreaterThanAndLocationXLessThanAndLocationYGreaterThanAndLocationYLessThanAndExpirationDateAfter(
					minX, maxX, minY, maxY, new Date(), PageRequest.of(page, size));
		}
		else {
			return elements.findAllByLocationXGreaterThanAndLocationXLessThanAndLocationYGreaterThanAndLocationYLessThan(
					minX, maxX, minY, maxY, PageRequest.of(page, size));
		}
	}
	
	@Override
	@Transactional
	@MyLog
	public List<ElementEntity> getAllByValue(UserEntity user, int size, int page, String valueType, Object value) {
		if (user.getRole().equals("player")) {
			switch(valueType) {
			case "name": return this.elements.findAllByTypeLikeIgnoreCaseAndExpirationDateAfter(
					(String)value, new Date(), PageRequest.of(page, size));
			case "type": return this.elements.findAllByTypeLikeIgnoreCaseAndExpirationDateAfter(
					(String)value, new Date(), PageRequest.of(page, size));
			case "location": return this.elements.findAllByLocationXEqualsAndLocationYEqualsAndExpirationDateAfter(
					((Location) value).getX(),((Location) value).getY(), new Date(), PageRequest.of(page, size));
			default: return new ArrayList<ElementEntity>();
			}
		}
		else {
			switch(valueType) {
			case "name": return this.elements.findAllByNameLikeIgnoreCase((String)value, PageRequest.of(page, size));
			case "type": return this.elements.findAllByTypeLikeIgnoreCase((String)value, PageRequest.of(page, size));
			case "location": return this.elements.findAllByLocationXEqualsAndLocationYEquals(((Location) value).getX(),((Location) value).getY(), PageRequest.of(page, size));
			default: return new ArrayList<ElementEntity>();			
		 }
		}
		
		// Oran: need more cases for valueType? the code below handles only "type" case
		
		/*List<ElementEntity> result           = new ArrayList<ElementEntity>();
		int filteredPages                    = 0; // This is the first page
		int collectedMatches                 = 0;
		int matchesToSkip                    = page * size;
		List<ElementEntity> nextPageToFilter = elements.findAll(
				PageRequest.of(filteredPages, size, Direction.DESC, "creationDate"))
				.getContent();
		while(!nextPageToFilter.isEmpty()) {
			List<ElementEntity> currentPageMatches = nextPageToFilter.stream()
			.filter(element -> {
				if (valueType.equals("type")) {
					return element.getType().equals(value);
				}
				return false;
			})
			.collect(Collectors.toList());
			// Max: Skip matches to get to final result's desired page
			if (!currentPageMatches.isEmpty()) {
				matchesToSkip = paginateOverElementsThatMatchedPredicate(currentPageMatches, matchesToSkip);
				// Max: If no need to skip matches, they can be added to final result
				collectedMatches = mergeMatchedElementsWithFinalResult(result, currentPageMatches, collectedMatches, size);
			}
			if (collectedMatches == size) {
				break;
			}
			filteredPages++;
			nextPageToFilter = elements.findAll(
					PageRequest.of(filteredPages, size, Direction.DESC, "creationDate"))
					.getContent();
		}
		return result;*/
	}

	@Override
	@Transactional
	@MyLog
	public void updateElement(String id, ElementEntity element) throws ElementNotFoundException {
		ElementEntity existing = this.getElementById(id);
		if (existing == null) {
			throw new ElementNotFoundException("no element for id: " + id);
		}
		validateElementFieldsForUpdateFeature(existing, element);
		if (element.getMoreAttributes() != null && !element.getMoreAttributes().isEmpty()) {
			existing.setMoreAttributes(element.getMoreAttributes());
		}
		if (element.getPlayground() != null && element.getPlayground() != existing.getPlayground()) {
			existing.setPlayground(element.getPlayground());
		}
		if (element.getExpirationDate() != null && element.getExpirationDate() != existing.getExpirationDate()) {
			existing.setExpirationDate(element.getExpirationDate());
		}
		if (element.getType() != null && element.getType() != existing.getType()) {
			existing.setType(element.getType());
		}
		if (element.getName() != null && element.getName() != existing.getName()) {
			existing.setName(element.getName());
		}
		if (element.getLocation() != null && !element.getLocation().equals(existing.getLocation())) {
			existing.setLocation(element.getLocation());
		}
		
			this.elements.save(existing);
		
	}

	@Override
	@MyLog
	public void validateAuthorization(String role)
			throws ElementIllegalAccess {
		if (!role.equals("manager")) {
			throw new ElementIllegalAccess("Only managers are authorized to create new elements");
		}
	}

	@Override
	@MyLog
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

	@Override
	public void validateElementFieldsForUpdateFeature(ElementEntity persistedElement, ElementEntity updatedElement) throws ElementIllegalInputException {
		validateElementFields(updatedElement);
		System.err.println("Persisted Element:");
		System.err.println(persistedElement);
		System.err.println("Updated   Element:");
		System.err.println(updatedElement);
		if (   !persistedElement.getId().equals(updatedElement.getId())
				|| !persistedElement.getPlayground().equals(updatedElement.getPlayground())
				|| !persistedElement.getType().equals(updatedElement.getType())
				|| !persistedElement.getCreatorPlayground().equals(updatedElement.getCreatorPlayground())
				|| !persistedElement.getCreatorEmail().equals(updatedElement.getCreatorEmail())) {
			throw new ElementIllegalInputException("Attempt to update one of fields that are not to be edited");
		}
		// Max: Creation dates of both objects are in different formats
		//      Different formats doesn't mean different times
		//      Hence, I re-format both of them before comparison
		String           pattern                               = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat simpleDateFormat                      = new SimpleDateFormat(pattern);
		String           formattedPersistedElementCreationDate = simpleDateFormat.format(persistedElement.getCreationDate());
		String           formattedUpdatedElementCreationDate   = simpleDateFormat.format(updatedElement.getCreationDate());
		try {
			if (!simpleDateFormat.parse(formattedPersistedElementCreationDate).equals(simpleDateFormat.parse(formattedUpdatedElementCreationDate))) {
				throw new ElementIllegalInputException("Attempt to update one of fields that are not to be edited");
			}
		} catch (ParseException e) {
			System.err.println("Failed to parse Dates " + formattedPersistedElementCreationDate);
			System.err.println("Failed to parse Dates " + formattedUpdatedElementCreationDate);
		}
	}
	
	// Helpers
	// Will paginate over databse,
	// filter by predicate, skip some of matches to simulate pagination of
	// elements that meet predicate
	public int paginateOverElementsThatMatchedPredicate(List<ElementEntity> matches, Integer matchesToSkip) {
		for (int i = 0; i < matches.size(); i++) {
			if (matchesToSkip > 0) {
				matches.remove(i);
				i--;
				matchesToSkip -= 1;
			}
			else {
				break;
			}
		}
		return matchesToSkip;
	}
	public int mergeMatchedElementsWithFinalResult(List<ElementEntity> result, List<ElementEntity> matches, int collectedMatches, int pageSize) {
		if (!matches.isEmpty()) {
			if (collectedMatches + matches.size() > pageSize) {
				Iterator<ElementEntity> iteratorForCurrentMatches = matches.iterator();
				while (collectedMatches < pageSize) {
					result.add(iteratorForCurrentMatches.next());
					collectedMatches++;
				}
			}
			else {
				result.addAll(matches);
				collectedMatches += matches.size();
			}
		}
		return collectedMatches;
	}
}
