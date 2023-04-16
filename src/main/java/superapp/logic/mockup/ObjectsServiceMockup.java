package superapp.logic.mockup;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.data.SuperAppObjectEntity;
import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.ObjectDetails;
import superapp.entities.ObjectId;
import superapp.entities.UserId;
import superapp.entities.CreatedBy;
import superapp.entities.Location;
import superapp.logic.ObjectsService;

@Service
public class ObjectsServiceMockup implements ObjectsService {

	private Map<String, SuperAppObjectEntity> databaseMockup;
	private String springApplicationName;
	private final String DELIMITER = "_";

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.LiranSorokin}")
	public void setSpringApplicationName(String springApllicationName) {
		this.springApplicationName = springApllicationName;
	}

	/**
	 * this method is invoked after values are injected to instance
	 */
	@PostConstruct
	public void init() {
		// create a thread safe map
		this.databaseMockup = Collections.synchronizedMap(new HashMap<>());

		System.err.println("******** " + this.springApplicationName);

		// Base data to work and check the code
		SuperAppObjectBoundary[] objects = IntStream.range(0, 3)
				.mapToObj(i -> new SuperAppObjectBoundary(new ObjectId("" + i), "TYPE", "ALIAS", true,
						new Location(30.1, 30.2), new CreatedBy(new UserId("netanelhabas@gmail.com")),
						new ObjectDetails(new HashMap<String, Object>())))
				.toArray(SuperAppObjectBoundary[]::new);
		for (SuperAppObjectBoundary objectBoundary : objects) {
//			objectBoundary.getCreatedBy().getUserID().setSuperApp(springApplicationName);;
			SuperAppObjectEntity superAppObjectEntity = this.boundaryToEntity(objectBoundary);
			databaseMockup.put(superAppObjectEntity.getObjectId(), superAppObjectEntity);
		}

		System.err.println("******** " + databaseMockup.toString());
	}

	/**
	 * Create a new Object
	 * 
	 * @param ObjectBondary new object boundary
	 * @return ObjectBoundary object boundary
	 */
	@Override
	public SuperAppObjectBoundary createObject(SuperAppObjectBoundary objectBoundary) {
		if (objectBoundary == null) {
			throw new RuntimeException("ObjectBoundary is null");
		}
		SuperAppObjectEntity superAppObjectEntity = this.boundaryToEntity(objectBoundary);

		superAppObjectEntity.setCreationTimestamp(new Date());
		superAppObjectEntity.setObjectId(springApplicationName + DELIMITER + UUID.randomUUID().toString());

		this.databaseMockup.put(superAppObjectEntity.getObjectId(), superAppObjectEntity);
		return this.entityToBoundary(superAppObjectEntity);
	}

	/**
	 * Update existing object in the desired fields
	 * 
	 * @param String                 Application name
	 * @param String                 Internal object id
	 * @param SuperAppObjectBoundary object boundary to change its attributes
	 * @return ObjectBoundary object boundary after update
	 */
	@Override
	public SuperAppObjectBoundary updateAnObject(String superapp, String internalObjectId,
			SuperAppObjectBoundary updateBoundary) {
		String attr = superapp + DELIMITER + internalObjectId;

		SuperAppObjectEntity existingObject = this.databaseMockup.get(attr);
		if (existingObject == null) {
			throw new RuntimeException("Could not find object by id: " + internalObjectId);
		}
		boolean dirtyFlag = false;
		if (updateBoundary.getActive() != null) {
			existingObject.setActive(updateBoundary.getActive());
			dirtyFlag = true;
		}
		if (updateBoundary.getAlias() != null) {
			existingObject.setAlias(updateBoundary.getAlias());
			dirtyFlag = true;
		}
		if (updateBoundary.getType() != null) {
			existingObject.setType(updateBoundary.getType());
			dirtyFlag = true;
		}
		if (updateBoundary.getLocation() != null) {
			existingObject.setLocation(this.boundaryToStr(updateBoundary.getLocation()));
			dirtyFlag = true;
		}
		if (updateBoundary.getObjectDetails() != null) {
			existingObject.setObjectDetails(updateBoundary.getObjectDetails());
			dirtyFlag = true;
		}

		if (dirtyFlag) {
			this.databaseMockup.put(attr, existingObject);
		}
		return this.entityToBoundary(existingObject);
	}

	/**
	 * Get specific object from DB
	 * 
	 * @param String Application name
	 * @param String internalObjectId
	 * @return ObjectBoundary requested object boundary
	 */
	@Override
	public Optional<SuperAppObjectBoundary> getSpecificObject(String superapp, String internalObjectId) {
		String attr = superapp + DELIMITER + internalObjectId;

		SuperAppObjectEntity requestedObject = this.databaseMockup.get(attr);
		if (requestedObject == null) {
			throw new RuntimeException("Could not find object by id: " + attr);
		} else {
			SuperAppObjectBoundary objectBoundary = this.entityToBoundary(requestedObject);
			return Optional.of(objectBoundary);
		}

//		return this.entityToBoundary(requestedObject);
	}

	/**
	 * Get all objects from DB
	 * 
	 * @return Array ObjectBoundary[]
	 */
	@Override
	public List<SuperAppObjectBoundary> getAllObjects() {
		return this.databaseMockup.values().stream().map(this::entityToBoundary).toList();
	}

	/**
	 * Delete all objects from DB
	 * 
	 */
	@Override
	public void deleteAllObjects() {
		this.databaseMockup.clear();
	}

	/**
	 * Convert super app object entity to object boundary
	 * 
	 * @param SuperAppObjectEntity super app object entity
	 * @return ObjectBoundary
	 */
	private SuperAppObjectBoundary entityToBoundary(SuperAppObjectEntity superAppObjectEntity) {
		SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
		objectBoundary.setActive(superAppObjectEntity.isActive());
		objectBoundary.setAlias(superAppObjectEntity.getAlias());
		objectBoundary.setCreatedBy(this.toBoundaryAsCreatedBy(superAppObjectEntity.getCreatedBy()));
		objectBoundary.setCreationTimestamp(superAppObjectEntity.getCreationTimestamp());
		objectBoundary.setLocation(this.toBoundaryAsLocation(superAppObjectEntity.getLocation()));
		objectBoundary.setObjectDetails(superAppObjectEntity.getObjectDetails());
		objectBoundary.setObjectId(this.toBoundaryAsObjectId(superAppObjectEntity.getObjectId()));
		objectBoundary.setType(superAppObjectEntity.getType());
		return objectBoundary;
	}

	/**
	 * Converts from ObjectBoundary to SuperAppObjectEntity
	 * 
	 * @param objectBoundary
	 * @return SuperAppObjectEntity SuperApp object entity
	 */
	private SuperAppObjectEntity boundaryToEntity(SuperAppObjectBoundary objectBoundary) {
		SuperAppObjectEntity superAppObjectEntity = new SuperAppObjectEntity();

		if (objectBoundary.getActive() != null) {
			superAppObjectEntity.setActive(objectBoundary.getActive());
		} else {
			superAppObjectEntity.setActive(false);
		}

		superAppObjectEntity.setAlias(objectBoundary.getAlias());
		superAppObjectEntity.setCreatedBy(this.boundaryToStr(objectBoundary.getCreatedBy()));
		superAppObjectEntity.setLocation(this.boundaryToStr(objectBoundary.getLocation()));
		superAppObjectEntity.setObjectDetails(objectBoundary.getObjectDetails());
		superAppObjectEntity.setType(objectBoundary.getType());

		return superAppObjectEntity;
	}

	/**
	 * Converts from the inserted 'CreatedBy' object to String
	 * 
	 * @param object
	 * @return String application name followed by delimiter and user email.
	 */
	private String boundaryToStr(CreatedBy createdBy) {
		String boundaryStr = createdBy.getUserID().getEmail();
		return springApplicationName + DELIMITER + boundaryStr;
	}

	/**
	 * Converts from the inserted 'Location' object to String
	 * 
	 * @param object
	 * @return String location latitude followed by delimiter and location
	 *         longitude.
	 */
	private String boundaryToStr(Location location) {
		String boundaryStr = location.getLat().toString() + DELIMITER + location.getLng().toString();
		return boundaryStr;
	}

	/**
	 * Converts String to 'ObjectId' object
	 * 
	 * @param objectStr object string
	 * @return ObjectId.
	 */
	private ObjectId toBoundaryAsObjectId(String objectStr) {
		if (objectStr != null) {
			String[] attr = objectStr.split(DELIMITER);

			ObjectId objectId = new ObjectId();
			objectId.setSpringApplicationName(attr[0]);
			objectId.setInternalObjectId(attr[1]);

			return objectId;
		} else {
			return null;
		}
	}

	/**
	 * Converts String to 'CreatedBy' object
	 * 
	 * @param createdByStr createdBy string
	 * @return CreatedBy.
	 */
	private CreatedBy toBoundaryAsCreatedBy(String createdByStr) {
		if (createdByStr != null) {
			String[] attr = createdByStr.split(DELIMITER);

			CreatedBy createdBy = new CreatedBy();
			createdBy.setUserID(new UserId(attr[1]));
			createdBy.getUserID().setSuperApp(attr[0]);

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
	private Location toBoundaryAsLocation(String locationStr) {
		if (locationStr != null) {
			String[] attr = locationStr.split(DELIMITER);

			Location location = new Location();
			location.setLat(Double.parseDouble(attr[0]));
			location.setLng(Double.parseDouble(attr[1]));

			return location;
		} else {
			return null;
		}
	}

}
