package superapp.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import superapp.boundaries.object.CreatedBy;
import superapp.boundaries.object.Location;
import superapp.boundaries.object.ObjectId;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.UserId;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.SuperAppObjectNotActiveException;

import static superapp.utils.Constants.DELIMITER;

import java.util.Optional;

@Component
public class ObjectConverter {

	private String springApplicationName;

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}
	/**
	 * Convert super app object entity to object boundary
	 * 
	 * @param SuperAppObjectEntity super app object entity
	 * @return ObjectBoundary
	 */
	public SuperAppObjectBoundary entityToBoundary(SuperAppObjectEntity superAppObjectEntity) {
		SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
		objectBoundary.setActive(superAppObjectEntity.isActive());
		objectBoundary.setAlias(superAppObjectEntity.getAlias());
		objectBoundary.setCreatedBy(this.toBoundaryAsCreatedBy(superAppObjectEntity.getCreatedBy()));
		objectBoundary.setCreationTimestamp(superAppObjectEntity.getCreationTimestamp());
		objectBoundary
			.setLocation(this.toBoundaryAsLocation(superAppObjectEntity.getLocation().getX(), superAppObjectEntity.getLocation().getY()));
		objectBoundary.setObjectDetails(superAppObjectEntity.getObjectDetails());
		objectBoundary.setObjectId(this.toBoundaryAsObjectId(superAppObjectEntity.getObjectId()));
		objectBoundary.setType(superAppObjectEntity.getType());
		return objectBoundary;
	}
	
	/**
	 * Converts String to 'CreatedBy' object
	 * 
	 * @param createdByStr createdBy string
	 * @return CreatedBy.
	 */
	public CreatedBy toBoundaryAsCreatedBy(String createdByStr) {
		if (createdByStr != null) {
			String[] attr = createdByStr.split("_");

			CreatedBy createdBy = new CreatedBy();
			createdBy.setUserId(new UserId(attr[1]));
			createdBy.getUserId().setSuperapp(attr[0]);

			return createdBy;
		} else {
			return null;
		}
	}
	/**
	 * Converts String to Location object
	 * 
	 * @param locationStr location string
	 * @return Location.
	 */
	
	public Location toBoundaryAsLocation(Double lat, Double lng) {
		if (lat != null && lng != null) {

			Location location = new Location();
			location.setLat(lat);
			location.setLng(lng);

			return location;
		} else {
			return null;
		}
	}
	
	/**
	 * Converts String to 'ObjectId' object
	 * 
	 * @param objectStr object string
	 * @return ObjectId.
	 */
	public ObjectId toBoundaryAsObjectId(String objectStr) {
		if (objectStr != null) {
			String[] attr = objectStr.split("_");

			ObjectId objectId = new ObjectId();
			objectId.setSuperapp(attr[0]);
			objectId.setInternalObjectId(attr[1]);

			return objectId;
		} else {
			return null;
		}
	}

	/**
	 * Convert from ObjectId to string with delimiter
	 *
	 * @param objectId user id
	 * @return String application name followed by delimiter and Internal Object Id
	 */
	public String objectIdToString(ObjectId objectId) {
		return springApplicationName + DELIMITER + objectId.getInternalObjectId();
	}

	
	/**
	 *  Checks if the object is the specified objectId is active or not
	 * @param objectId
	 * @param superAppObjectCrud
	 * @return true if the object exists
	 * @throws SuperAppObjectNotActiveException if the object doesn't exist.
	 */
	
	
	public boolean isActiveObject(String objectId,SuperAppObjectCrud superAppObjectCrud) {
		
		Optional<SuperAppObjectEntity> objectOptional = superAppObjectCrud.findById(objectId);
		if(objectOptional.isEmpty()) {
			throw new SuperAppObjectNotActiveException("Object {%s} not exist".formatted(objectId));
		}
		return objectOptional.get().isActive();
	}
	
	/**
	 * Converts from the inserted 'CreatedBy' object to String
	 *
	 * @param createdBy
	 * @return String application name followed by delimiter and user email.
	 */
	public String boundaryToStr(CreatedBy createdBy) {
		String boundaryStr = createdBy.getUserId().getEmail();
		return springApplicationName + DELIMITER + boundaryStr;
	}


	/**
	 * Converts from ObjectBoundary to SuperAppObjectEntity
	 *
	 * @param objectBoundary
	 * @return SuperAppObjectEntity SuperApp object entity
	 */
	public SuperAppObjectEntity boundaryToEntity(SuperAppObjectBoundary objectBoundary) {
		SuperAppObjectEntity superAppObjectEntity = new SuperAppObjectEntity();

		if (objectBoundary.getActive() != null) {
			superAppObjectEntity.setActive(objectBoundary.getActive());
		} else {
			superAppObjectEntity.setActive(false);
		}

		superAppObjectEntity.setAlias(objectBoundary.getAlias());
		superAppObjectEntity.setCreatedBy(this.boundaryToStr(objectBoundary.getCreatedBy()));
//		superAppObjectEntity.setLat(objectBoundary.getLocation().getLat());
//		superAppObjectEntity.setLng(objectBoundary.getLocation().getLng());
		
		GeoJsonPoint gLocation = new GeoJsonPoint(objectBoundary.getLocation().getLat(), objectBoundary.getLocation().getLng());
		superAppObjectEntity.setLocation(gLocation);

		superAppObjectEntity.setObjectDetails(objectBoundary.getObjectDetails());
		superAppObjectEntity.setType(objectBoundary.getType());

		return superAppObjectEntity;
	}



}
