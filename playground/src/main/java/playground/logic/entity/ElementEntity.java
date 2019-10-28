package playground.logic.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import playground.layout.io.Location;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {
	private String playground;
	private String id;
	private Double locationX;
	private Double locationY;
	private String name;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date creationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date expirationDate;
	private String type;
	private Map<String, Object> moreAttributes;
	private String creatorPlayground;
	private String creatorEmail;

	/** Class Constructors */
	public ElementEntity() {
	}

	public ElementEntity(String playground, String id, Double x, Double y, String name, Date creationDate,
			Date expirationDate, String type, Map<String, Object> attributes, String creatorPlayground,
			String creatorEmail) {
		this();
		this.playground = playground;
		this.id = id;
		this.locationX = x;
		this.locationY = y;
		this.name = name;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.type = type;
		this.moreAttributes = attributes;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
	}

	/** Class Getters & Setters */
	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Double getLocationX() {
		return locationX;
	}
	
	public void setLocationX(Double x) {
		locationX = x;
	}
	
	public Double getLocationY() {
		return locationY;
	}
	
	public void setLocationY(Double y) {
		locationY = y;
	}

	@Transient
	public Location getLocation() {
		return new Location(locationX, locationY);
	}

	public void setLocation(Location location) {
		setLocationX(location.getX());
		setLocationY(location.getY());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Transient
	public Map<String, Object> getMoreAttributes() {
		return moreAttributes;
	}

	public void setMoreAttributes(Map<String, Object> attributes) {
		this.moreAttributes = attributes;
	}
	
	@Lob
	public String getJsonAttributes() {
		try {
			return new ObjectMapper().writeValueAsString(this.moreAttributes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setJsonAttributes(String jsonAttributes) {
		try {
			this.moreAttributes = new ObjectMapper().readValue(jsonAttributes, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	@Override
	public String toString() {
		return "ElementTO [playground=" + playground + ", id=" + id + ", location= (" + locationX + ", " + locationY
				+ "), name=" + name + ", creationDate=" + creationDate + ", expirationDate=" + expirationDate
				+ ", type=" + type + ", attributes=" + moreAttributes + ", creatorPlayground=" + creatorPlayground
				+ ", creatorEmail=" + creatorEmail + "]";
	}
}
