package playground.layout.io;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import playground.logic.entity.ActivityEntity;

public class ActivityTO {
	private String playground;
	private String id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String, Object> attributes;
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date creationDate;

	public ActivityTO() {
	}

	public ActivityTO(String playground, String id, String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, Map<String, Object> attributes, Date creationDate) {
		this.playground = playground;
		this.id = id;
		this.elementPlayground = elementPlayground;
		this.elementId = elementId;
		this.type = type;
		this.playerPlayground = playerPlayground;
		this.playerEmail = playerEmail;
		this.attributes = attributes;
		this.creationDate = creationDate;
	}

	public ActivityTO(String playground, String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, Map<String, Object> attributes) {
		this(playground, null, elementPlayground, elementId, type, playerPlayground, playerEmail, attributes, null);
	}

	public ActivityTO(ActivityEntity entity) {
		this();
		if (entity != null) {
			this.playground = entity.getPlayground();
			this.id = entity.getId();
			this.elementPlayground = entity.getElementPlayground();
			this.elementId = entity.getElementId();
			this.type = entity.getType();
			this.playerPlayground = entity.getPlayerPlayground();
			this.playerEmail = entity.getPlayerEmail();
			this.attributes = entity.getAttributes();
			this.creationDate = entity.getCreationDate();
		}
	}

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

	public String getElementPlayground() {
		return elementPlayground;
	}

	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public ActivityEntity toEntity() {
		ActivityEntity entity = new ActivityEntity();

		entity.setAttributes(attributes);
		entity.setElementId(elementId);
		entity.setElementPlayground(elementPlayground);
		entity.setId(id);
		entity.setPlayerEmail(playerEmail);
		entity.setPlayerPlayground(playerPlayground);
		entity.setPlayground(playground);
		entity.setType(type);
		entity.setCreationDate(creationDate);

		return entity;
	}

	@Override
	public String toString() {
		return "ActivityTO [playground=" + playground + ", id=" + id + ", elementPlayground=" + elementPlayground
				+ ", elementId=" + elementId + ", type=" + type + ", playerPlayground=" + playerPlayground
				+ ", playerEmail=" + playerEmail + ", attributes=" + attributes + "]";
	}
}
