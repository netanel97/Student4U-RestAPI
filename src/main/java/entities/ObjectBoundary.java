package entities;

public class ObjectBoundary {
	
	private ObjectId objectId;
	private String type;
	private String alias;
	private Boolean active;
	private String creationTimestamp;
	private Location location;
	private CreatedBy createdBy;
	private ObjectDetails objectDetails;
	
	
	public ObjectBoundary() {
		super();
	}
	
	
	public ObjectBoundary(ObjectId objectId, String type, String alias, Boolean active, String creationTimestamp,
			Location location, CreatedBy createdBy, ObjectDetails objectDetails) {
		super();
		this.objectId = objectId;
		this.type = type;
		this.alias = alias;
		this.active = active;
		this.creationTimestamp = creationTimestamp;
		this.location = location;
		this.createdBy = createdBy;
		this.objectDetails = objectDetails;
	}
	public ObjectId getObjectId() {
		return objectId;
	}
	public void setObjectId(ObjectId objectId) {
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
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public String getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public CreatedBy getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(CreatedBy createdBy) {
		this.createdBy = createdBy;
	}
	public ObjectDetails getObjectDetails() {
		return objectDetails;
	}
	public void setObjectDetails(ObjectDetails objectDetails) {
		this.objectDetails = objectDetails;
	}
	@Override
	public String toString() {
		return "ObjectBoundary [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active=" + active
				+ ", creationTimestamp=" + creationTimestamp + ", location=" + location + ", createdBy=" + createdBy
				+ ", objectDetails=" + objectDetails + "]";
	}
	
	
	
	

	

}
