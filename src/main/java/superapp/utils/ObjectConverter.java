package superapp.utils;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import superapp.logic.mongo.ObjectsServiceMongoDb;

import static superapp.utils.Constants.DELIMITER;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ObjectConverter {

	private String springApplicationName;
	private Log logger = LogFactory.getLog(ObjectConverter.class);

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
	 * @param superAppObjectEntity super app object entity
	 * @return ObjectBoundary
	 */
	public SuperAppObjectBoundary entityToBoundary(SuperAppObjectEntity superAppObjectEntity) {
		logger.trace("Entering to function entityToBoundary with the super superAppObjectEntity: {%s}".formatted(superAppObjectEntity));
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
		logger.trace("Exiting from function entityToBoundary with the objectBoundary: {%s}".formatted(objectBoundary));
		return objectBoundary;
	}

	/**
	 *     private SuperAppObjectBoundary entityToBoundary(SuperAppObjectEntity superAppObjectEntity) {
	 *         SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
	 *         objectBoundary.setActive(superAppObjectEntity.isActive());
	 *         objectBoundary.setAlias(superAppObjectEntity.getAlias());
	 *         objectBoundary.setCreatedBy(this.objectConverter.toBoundaryAsCreatedBy(superAppObjectEntity.getCreatedBy()));
	 *         objectBoundary.setCreationTimestamp(superAppObjectEntity.getCreationTimestamp());
	 *
	 *         objectBoundary.setLocation(this.objectConverter.toBoundaryAsLocation(superAppObjectEntity.getLat(),
	 *                 superAppObjectEntity.getLng()));
	 *         objectBoundary.setObjectDetails(superAppObjectEntity.getObjectDetails());
	 *         objectBoundary.setObjectId(this.objectConverter.toBoundaryAsObjectId(superAppObjectEntity.getObjectId()));
	 *         objectBoundary.setType(superAppObjectEntity.getType());
	 *         return objectBoundary;
	 *     }
	 */


	
	/**
	 * Converts String to 'CreatedBy' object
	 * 
	 * @param createdByStr createdBy string
	 * @return CreatedBy.
	 */
	public CreatedBy toBoundaryAsCreatedBy(String createdByStr) {
		logger.trace("Entering to function toBoundaryAsCreatedBy with the createdByStr: {%s}".formatted(createdByStr));
		if (createdByStr != null) {
			String[] attr = createdByStr.split("_");

			CreatedBy createdBy = new CreatedBy();
			createdBy.setUserId(new UserId(attr[1]));
			createdBy.getUserId().setSuperapp(attr[0]);
			logger.trace("Exiting from function toBoundaryAsCreatedBy with the createdBy: {%s}".formatted(createdByStr));
			return createdBy;
		} else {
			return null;
		}
	}
	/**
	 * Converts String to Location object
	 * 
	 * @param lng location string
	 *  @param lat location string
	 * @return Location.
	 */
	
	public Location toBoundaryAsLocation(Double lat, Double lng) {
		logger.trace("Entering to function toBoundaryAsLocation with the lat: {%s} and lng: {%s}".formatted(lat,lng));
		if (lat != null && lng != null) {

			Location location = new Location();
			location.setLat(lat);
			location.setLng(lng);
			logger.trace("Exiting from function toBoundaryAsLocation with the location: {%s}".formatted(location));
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
		logger.trace("Entering to function toBoundaryAsObjectId with the objectStr: {%s}".formatted(objectStr));
		if (objectStr != null) {
			String[] attr = objectStr.split("_");

			ObjectId objectId = new ObjectId();
			objectId.setSuperapp(attr[0]);
			objectId.setInternalObjectId(attr[1]);
			logger.trace("Exiting from function toBoundaryAsObjectId with the objectId: {%s}".formatted(objectId));
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
		logger.trace("Entering to function objectIdToString with the objectId: {%s}".formatted(objectId));
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
		logger.trace("Entering to function isActiveObject with the objectId: {%s}".formatted(objectId));
		Optional<SuperAppObjectEntity> objectOptional = superAppObjectCrud.findById(objectId);
		if(objectOptional.isEmpty()) {
			logger.warn("Object {%s} not exist".formatted(objectId));
			throw new SuperAppObjectNotActiveException("Object {%s} not exist".formatted(objectId));
		}
		logger.trace("Exiting from function isActiveObject with the object: {%s}".formatted(objectOptional.get()));
		return objectOptional.get().isActive();
	}
	
	/**
	 * Converts from the inserted 'CreatedBy' object to String
	 *
	 * @param createdBy
	 * @return String application name followed by delimiter and user email.
	 */
	public String boundaryToStr(CreatedBy createdBy) {
		logger.trace("Entering to function boundaryToStr with the createdBy: {%s}".formatted(createdBy));
		String boundaryStr = createdBy.getUserId().getEmail();
		logger.trace("Exiting from function boundaryToStr with the boundaryStr: " + springApplicationName + DELIMITER + boundaryStr);
		return springApplicationName + DELIMITER + boundaryStr;
	}


	/**
	 * Converts from ObjectBoundary to SuperAppObjectEntity
	 *
	 * @param objectBoundary
	 * @return SuperAppObjectEntity SuperApp object entity
	 */
	public SuperAppObjectEntity boundaryToEntity(SuperAppObjectBoundary objectBoundary) {
		logger.trace("Entering to function boundaryToEntity with the objectBoundary: {%s}".formatted(objectBoundary));
		SuperAppObjectEntity superAppObjectEntity = new SuperAppObjectEntity();
		if (objectBoundary.getActive() != null) {
			logger.trace("Active is not null");
			superAppObjectEntity.setActive(objectBoundary.getActive());
		} else {
			logger.trace("Active is null");
			superAppObjectEntity.setActive(false);
		}

		superAppObjectEntity.setAlias(objectBoundary.getAlias());
		superAppObjectEntity.setCreatedBy(this.boundaryToStr(objectBoundary.getCreatedBy()));

		GeoJsonPoint gLocation = new GeoJsonPoint(objectBoundary.getLocation().getLat(), objectBoundary.getLocation().getLng());
		superAppObjectEntity.setLocation(gLocation);

		superAppObjectEntity.setObjectDetails(objectBoundary.getObjectDetails());
		superAppObjectEntity.setType(objectBoundary.getType());
		logger.trace("Exiting from function boundaryToEntity with the superAppObjectEntity: {%s}".formatted(superAppObjectEntity));
		return superAppObjectEntity;
	}

	public String objToJson(Map<String,Object> res){
		Gson gson = new Gson();
		return gson.toJson(res);
	}


}
