package superapp.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "SuperAppObjects")
public class SuperAppObjectEntity {

	@Id
	private String objectId;
	private String type;
	private String alias;
	private boolean active;
	private Date creationTimestamp;
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	private GeoJsonPoint location;
	private Double lat;
	private Double lng;
	private String createdBy;
	private Map<String, Object> objectDetails;
	@DBRef(lazy = true)
	private Set<SuperAppObjectEntity> parents = new HashSet<>();
	@DBRef(lazy = true)
	private Set<SuperAppObjectEntity> children = new HashSet<>();

	public SuperAppObjectEntity() {
		super();
	}

	public SuperAppObjectEntity(String objectId, String type, String alias, boolean active, GeoJsonPoint location,
			String createdBy, Map<String, Object> objectDetails) {
		super();
		this.objectId = objectId;
		this.type = type;
		this.alias = alias;
		this.active = active;
		this.creationTimestamp = new Date();
		this.location = location;
		setLat(location.getX());
		setLng(location.getY());
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

	public GeoJsonPoint getLocation() {
		return location;
	}

	public void setLocation(GeoJsonPoint location) {
		if (location != null) {
			this.location = location;
			this.lat = location.getY();
			this.lng = location.getX();
		} else {
			this.lat = null;
			this.lng = null;
		}
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	public Set<SuperAppObjectEntity> getParents() {
		return parents;
	}

	public void setParents(Set<SuperAppObjectEntity> parents) {
		this.parents = parents;
	}

	public Set<SuperAppObjectEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<SuperAppObjectEntity> children) {
		this.children = children;
	}

	public boolean addChild(SuperAppObjectEntity child) {
		return this.children.add(child);
	}

	public boolean addParent(SuperAppObjectEntity parent) {
		return this.parents.add(parent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(objectId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuperAppObjectEntity other = (SuperAppObjectEntity) obj;
		return Objects.equals(objectId, other.objectId);
	}

	@Override
	public String toString() {
		return "SuperAppObjectEntity [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active="
				+ active + ", creationTimestamp=" + creationTimestamp + ", lat=" + lat + ", lng=" + lng + ", location="
				+ location + ", createdBy=" + createdBy + ", objectDetails=" + objectDetails + ", parents=" + parents
				+ ", children=" + children + "]";
	}

}
