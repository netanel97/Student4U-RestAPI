package superapp.data;


import java.util.Date;
import java.util.HashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "SuperAppObjects")
public class SuperAppObjectEntity {
	@Id private String objectId;
	private String type;
	private String alias;
	private boolean active;
	private Date creationTimestamp;
	private String location;
	private String createdBy;
	private HashMap<String, Object> objectDetails;


	public SuperAppObjectEntity() {
		super();
	}

	public SuperAppObjectEntity(String objectId, String type, String alias, boolean active, String location,
			String createdBy, HashMap<String, Object> objectDetails) {
		super();
		this.objectId = objectId;
		this.type = type;
		this.alias = alias;
		this.active = active;
		this.creationTimestamp = new Date();
		this.location = location;
		this.createdBy = createdBy;
		this.objectDetails = objectDetails;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public HashMap<String, Object> getObjectDetails() {
		return objectDetails;
	}
	public void setObjectDetails(HashMap<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}
	@Override
	public String toString() {
		return "SuperAppObjectEntity [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active="
				+ active + ", creationTimestamp=" + creationTimestamp + ", location=" + location + ", createdBy="
				+ createdBy + ", objectDetails=" + objectDetails + "]";
	}

}
