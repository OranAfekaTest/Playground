package playground.layout.io;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import playground.logic.entity.ElementEntity;

public class ElementTO {

	/** Class Attributes */
	private String playground;
	private String id;
	private Location location;
	private String name;
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date creationDate;
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date expirationDate;
	private String type;
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;

	/** Class Constructors */
	public ElementTO() {
	}

	public ElementTO(String playground, String id, Location location,
			String name, Date creationDate, Date expirationDate, String type,
			Map<String, Object> attributes, String creatorPlayground,
			String creatorEmail) {
		super();
		this.playground = playground;
		this.id = id;
		this.location = location;
		this.name = name;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.type = type;
		this.attributes = attributes;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
	}
	
	// Max: constructor without id and creation date is sufficient
	//      no need for one without id and another without creation date
	public ElementTO(String playground, Location location,
			String name, Date expirationDate, String type,
			Map<String, Object> attributes, String creatorPlayground,
			String creatorEmail) {
		this(playground, null, location, name, null, expirationDate, type,
				attributes, creatorPlayground, creatorEmail);
	}
	
	public ElementTO(ElementTO other) {
		super();
		this.playground = other.playground;
		this.id = other.id;
		this.location = other.location;
		this.name = other.name;
		this.creationDate = other.creationDate;
		this.expirationDate = other.expirationDate;
		this.type = other.type;
		this.attributes = other.attributes;
		this.creatorPlayground = other.creatorPlayground;
		this.creatorEmail = other.creatorEmail;
	}
	
	public ElementTO(ElementEntity entity) {
		super();
		if(entity != null) {
			this.playground = entity.getPlayground();
			this.id = entity.getId();
			this.location = entity.getLocation();
			this.name = entity.getName();
			this.creationDate = entity.getCreationDate();
			this.expirationDate = entity.getExpirationDate();
			this.type = entity.getType();
			this.attributes = entity.getMoreAttributes();
			this.creatorPlayground = entity.getCreatorPlayground();
			this.creatorEmail = entity.getCreatorEmail();	
		}	
	}
	
	/** Class Getters & Setters */
	public String getPlayground() {
		return playground;
	}
	
	public void setPlayground(String playground) {
		this.playground = playground;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public String getCreatorPlayground() {
		return creatorPlayground;
	}

	public void setCreatorPlayground(String creatorPlayground) {
		this.creatorPlayground = creatorPlayground;
	}
	
	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}
	
	/** Class Methods */
	public ElementEntity toEntity() {
		ElementEntity entity = new ElementEntity();
		
		entity.setMoreAttributes(attributes);
		entity.setCreationDate(creationDate);
		entity.setCreatorEmail(creatorEmail);
		entity.setCreatorPlayground(creatorPlayground);
		entity.setExpirationDate(expirationDate);
		entity.setId(id);
		entity.setLocation(location);
		entity.setName(name);
		entity.setPlayground(playground);
		entity.setType(type);
		
		return entity;
	}
	
	@Override
	public String toString() {
		return "ElementTO [playground=" + playground + ", id=" + id
				+ ", location=" + location + ", name=" + name
				+ ", creationDate=" + creationDate + ", expirationDate="
				+ expirationDate + ", type=" + type + ", attributes="
				+ attributes + ", creatorPlayground=" + creatorPlayground
				+ ", creatorEmail=" + creatorEmail + "]";
	}
}
