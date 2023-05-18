package superapp.logic.mongo;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.entities.CreatedBy;
import superapp.entities.Location;
import superapp.entities.ObjectId;
import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.SuperAppObjectCrud;
import superapp.entities.SuperAppObjectIdBoundary;
import superapp.entities.UserCrud;
import superapp.entities.UserId;
import superapp.logic.ObjectServiceWithPagainationSupport;
import superapp.logic.SuperAppObjectNotActiveException;
import superapp.logic.SuperAppObjectNotFoundException;
import superapp.logic.UserNotAcceptableException;
import superapp.logic.UserNotFoundException;
@Service
public class ObjectsServiceMongoDb implements ObjectServiceWithPagainationSupport {
	private SuperAppObjectCrud databaseCrud;
	private UserCrud userCrud;
	private String springApplicationName;
	private final String DELIMITER = "_";

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	
	@Autowired
	public ObjectsServiceMongoDb(SuperAppObjectCrud superAppObjectCrud, UserCrud userCrud) {
		this.databaseCrud = superAppObjectCrud;
		this.userCrud = userCrud;
	}
	
	/**
	 * this method is invoked after values are injected to instance
	 */
	@PostConstruct
	public void init() {
		System.err.println("******** " + this.springApplicationName);
	}

	/**
	 * Create a new Object
	 * 
	 * @param ObjectBondary new object boundary
	 * @return ObjectBoundary object boundary
	 */
	@Override
	public SuperAppObjectBoundary createObject(SuperAppObjectBoundary object) {
		if (object == null) {
			throw new SuperAppObjectNotFoundException("ObjectBoundary is null");
		}
		if (object.getCreatedBy() == null) {
			throw new SuperAppObjectNotFoundException("CreatedBy object is null");
		}
		if (object.getCreatedBy().getUserId() == null) {
			throw new SuperAppObjectNotFoundException("UserId object is null");
		}
		
		if(!checkEmail(object.getCreatedBy().getUserId().getEmail())) {
			throw new SuperAppObjectNotFoundException("The email address is invalid");
		}
	
		if(object.getAlias() == null || object.getAlias().isEmpty()) {
			throw new SuperAppObjectNotFoundException("Alias object is null or empty");

		}
		if(object.getType() == null || object.getType().isEmpty()) {
			throw new SuperAppObjectNotFoundException("Type object is null or empty");

		}
		SuperAppObjectEntity superAppObjectEntity = this.boundaryToEntity(object);

		superAppObjectEntity.setCreationTimestamp(new Date());
		superAppObjectEntity.setObjectId(springApplicationName + DELIMITER + UUID.randomUUID().toString());
		
		superAppObjectEntity = this.databaseCrud.save(superAppObjectEntity);
		return this.entityToBoundary(superAppObjectEntity);
	}
	
	/**
	 * 
	 * @param String email
	 * @return boolean true if the email valid else false
	 */
	private boolean checkEmail(String email) {
		if(email.isEmpty()) {
			return false;
		}
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

//	    Pattern pattern = Pattern.compile(regex);  
//	    Matcher matcher = pattern.matcher(email);  
//	    return matcher.matches();
		
		return Pattern.compile(regex)
	      .matcher(email)
	      .matches();	
		

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
	@Deprecated
	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update) {

		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");
	}
	
	
	@Override
	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update, String userSuperapp, String userEmail) {
		String attr = objectSuperApp + DELIMITER + internalObjectId;
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(()->new UserNotFoundException("could not find user by id: " + userId));
		if(user.getRole() == UserRole.SUPERAPP_USER) {
			SuperAppObjectEntity existingObject = this.databaseCrud.findById(attr)
					.orElseThrow(()->new SuperAppObjectNotFoundException("could not update superapp object by id: " + attr + " because it does not exist"));
			
			if (existingObject == null) {
				throw new SuperAppObjectNotFoundException("Could not find object by id: " + internalObjectId);
			}
			if(existingObject.isActive()) {
				boolean dirtyFlag = false;
				if (update.getActive() != null) {
					existingObject.setActive(update.getActive());
					dirtyFlag = true;
				}
				if (update.getAlias() != null &&  !update.getAlias().isEmpty()) {
					existingObject.setAlias(update.getAlias());
					dirtyFlag = true;
				}
				if (update.getType() != null && !update.getType().isEmpty()) {
					existingObject.setType(update.getType());
					dirtyFlag = true;
				}
				if (update.getLocation() != null) {
					existingObject.setLocation(this.boundaryToStr(update.getLocation()));
					dirtyFlag = true;
				}
				if (update.getObjectDetails() != null) {
					existingObject.setObjectDetails(update.getObjectDetails());
					dirtyFlag = true;
				}
		
				if (dirtyFlag) {
					existingObject = this.databaseCrud.save(existingObject);
				}
				return this.entityToBoundary(existingObject);
			}
			else {
				throw new SuperAppObjectNotActiveException("Superapp object not active!");
			}
		}
		else {
			throw new UserNotAcceptableException("User doesn't have permissions!");
		}
	}


	/**
	 * Get specific object from DB
	 * 
	 * @param String Application name
	 * @param String internalObjectId
	 * @return ObjectBoundary requested object boundary
	 */
	@Override
	@Deprecated
	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId) {
		String attr = objectSuperApp + DELIMITER + internalObjectId;
		return this.databaseCrud.findById(attr).
					map(this::entityToBoundary);
					//.orElseThrow(()->new SuperAppObjectNotFoundException("could not update superapp object by id: " + attr + " because it does not exist"));
//		}

	}
	
	
	@Override
	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId,
			String userSuperapp, String userEmail) {
		String attr = objectSuperApp + DELIMITER + internalObjectId;
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(()->new UserNotFoundException("could not find user by id: " + userId));
		if(user.getRole() == UserRole.SUPERAPP_USER) {
			return this.databaseCrud.findById(attr).
					map(this::entityToBoundary);
		}
		else {
			throw new UserNotAcceptableException("User doesn't have permissions!");
		}
	}

	/**
	 * Get all objects from DB
	 * 
	 * @return Array ObjectBoundary[]
	 */
	@Override
	@Deprecated
	public List<SuperAppObjectBoundary> getAllObjects() {
//		return this.databaseCrud
//	            .findAll() // List<SuperAppObjectBoundary>
//	            .stream() // Stream<SuperAppObjectBoundary>
//	            .map(this::entityToBoundary) // Stream<SuperAppObject>
//	            .toList(); // Lis

		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

	}
	
	@Override
	public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(()->new UserNotFoundException("could not find user by id: " + userId));
		if(user.getRole() == UserRole.SUPERAPP_USER) {
			return this.databaseCrud
		            .findAll(PageRequest.of(page, size,Direction.ASC,"creationTimestamp","objectId")) // List<SuperAppObjectBoundary>
		            .stream() // Stream<SuperAppObjectBoundary>
		            .map(this::entityToBoundary) // Stream<SuperAppObject>
		            .toList(); // List<SuperAppObject>
		}
		else {
			throw new UserNotAcceptableException("User doesn't have permissions!");
		}
	}
	

	@Override
	public void deleteAllObjects() {
		this.databaseCrud.deleteAll();
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
		String boundaryStr = createdBy.getUserId().getEmail();
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
			objectId.setSuperApp(attr[0]);
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
			createdBy.setUserId(new UserId(attr[1]));
			createdBy.getUserId().setSuperApp(attr[0]);

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

	//TODO ask eyal about userEmail..
	@Override
	public void BindAnExistingObjectToExistingChildObject(String superapp, String internalObjectId, SuperAppObjectIdBoundary childId,
		String userSuperapp, String userEmail) {
		String attr1 = superapp + DELIMITER + internalObjectId;
		SuperAppObjectEntity parent = this.databaseCrud.findById(attr1)
				.orElseThrow(()->new SuperAppObjectNotFoundException("could not find origin message by id: " + internalObjectId));
		String attr = childId.getSuperapp() + DELIMITER + childId.getInternalObjectId();//child
		SuperAppObjectEntity child = this.databaseCrud.findById(attr)
				.orElseThrow(()->new SuperAppObjectNotFoundException("could not find origin message by id: " + attr));
		
		if (!child.addParent(parent) || !parent.addChild(child))//failed to add to the Set
			throw new SuperAppObjectNotFoundException("Could not bind parent object:" + parent + " to child object: " + child);

		this.databaseCrud.save(child);
		this.databaseCrud.save(parent);
		
	}


	@Override
	public List<SuperAppObjectBoundary> getAllChildrenOfAnExistingObject(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page) {
		String attr1 = superapp + DELIMITER + internalObjectId;
		SuperAppObjectEntity parent = 
				  this.databaseCrud
					.findById(attr1)
					.orElseThrow(()->new SuperAppObjectNotFoundException("could not find origin message by id: " + internalObjectId));
		
		Set<SuperAppObjectEntity> responsesSet = parent.getChildren();
		return responsesSet
				.stream() // Stream<MessageEntity>
				.map(this::entityToBoundary) // Stream<Message>
				.toList();
	}


	@Override
	public List<SuperAppObjectBoundary> getAnArrayWithObjectParent(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page) {
		String attr1 = superapp + DELIMITER + internalObjectId;
		SuperAppObjectEntity child = 
				  this.databaseCrud
					.findById(attr1)
					.orElseThrow(()->new SuperAppObjectNotFoundException("could not find origin message by id: " + internalObjectId));
		
		Set<SuperAppObjectEntity> responsesSet = child.getParents();
		return responsesSet
				.stream() // Stream<MessageEntity>
				.map(this::entityToBoundary) // Stream<Message>
				.toList();
	}


	





	
	
}
