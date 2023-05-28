package superapp.logic.mongo;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.object.CreatedBy;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.dal.UserCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.ObjectServiceWithPaginationSupport;
import superapp.logic.SuperAppObjectNotActiveException;
import superapp.logic.SuperAppObjectNotFoundException;
import superapp.logic.UnauthorizedAccessException;
import superapp.logic.UserNotFoundException;
import superapp.utils.ObjectConverter;

@Service
public class ObjectsServiceMongoDb implements ObjectServiceWithPaginationSupport {
	private SuperAppObjectCrud databaseCrud;
	private UserCrud userCrud;
	private String springApplicationName;
	private final String DELIMITER = "_";
	private String unauthorizedUserMessage = "User doesn't have permissions!";
	private ObjectConverter objectConverter;

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	@Autowired
	public ObjectsServiceMongoDb(SuperAppObjectCrud superAppObjectCrud, UserCrud userCrud,ObjectConverter objectConverter) {
		this.databaseCrud = superAppObjectCrud;
		this.userCrud = userCrud;
		this.objectConverter = objectConverter;
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

		if (!checkEmail(object.getCreatedBy().getUserId().getEmail())) {
			throw new SuperAppObjectNotFoundException("The email address is invalid");
		}

		if (object.getAlias() == null || object.getAlias().isEmpty()) {
			throw new SuperAppObjectNotFoundException("Alias object is null or empty");

		}
		if (object.getType() == null || object.getType().isEmpty()) {
			throw new SuperAppObjectNotFoundException("Type object is null or empty");

		}
		SuperAppObjectEntity superAppObjectEntity = this.boundaryToEntity(object);

		superAppObjectEntity.setCreationTimestamp(new Date());
		superAppObjectEntity.setObjectId(springApplicationName + DELIMITER + UUID.randomUUID().toString());

		superAppObjectEntity = this.databaseCrud.save(superAppObjectEntity);
		return this.objectConverter.entityToBoundary(superAppObjectEntity);
	}

	/**
	 * 
	 * @param String email
	 * @return boolean true if the email valid else false
	 */
	private boolean checkEmail(String email) {
		if (email.isEmpty()) {
			return false;
		}
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

//	    Pattern pattern = Pattern.compile(regex);  
//	    Matcher matcher = pattern.matcher(email);  
//	    return matcher.matches();

		return Pattern.compile(regex).matcher(email).matches();

	}



	@Override
	@Deprecated
	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update) {

		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");
	}

	/**
	 * Update existing object in the desired fields
	 * 
	 * @param String                 Application name
	 * @param String                 Internal object id
	 * @param SuperAppObjectBoundary object boundary to change its attributes
	 * @param String                 userSuperapp
	 * @param String                 userEmail
	 * @return ObjectBoundary object boundary after update
	 */
	@Override
	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update, String userSuperapp, String userEmail) {
		String attr = objectSuperApp + DELIMITER + internalObjectId;
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			SuperAppObjectEntity existingObject = this.databaseCrud.findById(attr)
					.orElseThrow(() -> new SuperAppObjectNotFoundException(
							"could not update superapp object by id: " + attr + " because it does not exist"));
//			if (!existingObject.isActive()) {
//				throw new SuperAppObjectNotActiveException("Superapp object not active!");
//			} // TODO: need to ask Eyal if we need to check active on PUT with SUPER APP USER
			boolean dirtyFlag = false;
			if (update.getActive() != null) {
				existingObject.setActive(update.getActive());
				dirtyFlag = true;
			}
			if (update.getAlias() != null && !update.getAlias().isEmpty()) {
				existingObject.setAlias(update.getAlias());
				dirtyFlag = true;
			}
			if (update.getType() != null && !update.getType().isEmpty()) {
				existingObject.setType(update.getType());
				dirtyFlag = true;
			}
			if (update.getLocation() != null) {
				existingObject.setLat(update.getLocation().getLat());
				existingObject.setLng(update.getLocation().getLng());
				dirtyFlag = true;
			}
			if (update.getObjectDetails() != null) {
				existingObject.setObjectDetails(update.getObjectDetails());
				dirtyFlag = true;
			}

			if (dirtyFlag) {
				existingObject = this.databaseCrud.save(existingObject);
			}
			return this.objectConverter.entityToBoundary(existingObject);
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	@Override
	@Deprecated
	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId) {
		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");



	}
	
	/**
	 * Get specific object from DB
	 * 
	 * @param String Application name
	 * @param String internalObjectId
	 * @return ObjectBoundary requested object boundary
	 */
	@Override
	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId,
			String userSuperapp, String userEmail) {
		String attr = objectSuperApp + DELIMITER + internalObjectId;
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		SuperAppObjectEntity superAppObjectEntity = this.databaseCrud.findById(attr)
				.orElseThrow(() -> new SuperAppObjectNotFoundException("Could not find superapp with id: " + attr));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			return Optional.of(superAppObjectEntity).map(this.objectConverter::entityToBoundary);
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (superAppObjectEntity.isActive()) {
				return Optional.of(superAppObjectEntity).map(this.objectConverter::entityToBoundary);
			} else {
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}


	@Override
	@Deprecated
	public List<SuperAppObjectBoundary> getAllObjects() {
		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

	}
	/**
	 * Get all objects from DB
	 * @param superapp superapp name
	 * @param email    user email
	 * @param size     how many items in page
	 * @param page     current page
	 * @return Array ObjectBoundary[]
	 */
	@Override
	public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			return this.databaseCrud.findAll(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId")) // List<SuperAppObjectBoundary>
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByActiveIsTrue(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId")) // List<SuperAppObjectBoundary>
					.stream() // Stream<SuperAppObjectBoundary>
					.filter(object -> object.isActive()).map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	@Override
	@Deprecated
	public void deleteAllObjects() {
		this.databaseCrud.deleteAll();

		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");
	}

	@Override
	public void deleteAllObjects(String userSuperapp, String userEmail) {
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));

		if (user.getRole() == UserRole.ADMIN) {
			this.databaseCrud.deleteAll();
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	/**
	 * Update children field of parent in DB.
	 * If use is MINIAPP_USER show only active children.
	 * 
	 * @param superapp
	 * @param internalObjectId
	 * @param childId     how many items in page
	 * @param superapp superapp name
	 * @param email    user email
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public void BindAnExistingObjectToExistingChildObject(String superapp, String internalObjectId,
			SuperAppObjectIdBoundary childId, String userSuperapp, String userEmail) {
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			String attr1 = superapp + DELIMITER + internalObjectId;
			SuperAppObjectEntity parent = this.databaseCrud.findById(attr1)
					.orElseThrow(() -> new SuperAppObjectNotFoundException(
							"could not find origin message by id: " + internalObjectId));
			String attr = childId.getSuperapp() + DELIMITER + childId.getInternalObjectId();// child
			SuperAppObjectEntity child = this.databaseCrud.findById(attr).orElseThrow(
					() -> new SuperAppObjectNotFoundException("could not find origin message by id: " + attr));
			if (!child.addParent(parent) || !parent.addChild(child))// failed to add to the Set
				throw new SuperAppObjectNotFoundException(
						"Could not bind parent object:" + parent + " to child object: " + child);
			parent.addChild(child);
			child.addParent(parent);
			this.databaseCrud.save(child);
			this.databaseCrud.save(parent);
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}

	}

	/**
	 * Search DB for children objects of a parent.
	 * If use is MINIAPP_USER show only active children.
	 * 
	 * @param superapp
	 * @param internalObjectId
	 * @param superapp superapp name
	 * @param email    user email
	 * @param size     how many items in page
	 * @param page     current page
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public List<SuperAppObjectBoundary> getAllChildrenOfAnExistingObject(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page) {
		String userId = userSuperapp + DELIMITER + userEmail;
		String attr = superapp + DELIMITER + internalObjectId;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		SuperAppObjectEntity parent = this.databaseCrud.findById(attr)
				.orElseThrow(() -> new SuperAppObjectNotFoundException(
						"could not find origin message by id: " + internalObjectId));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			List<SuperAppObjectEntity> children = this.databaseCrud.findByParentsContaining(parent,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			return children.stream() // Stream<MessageEntity>
					.map(this.objectConverter::entityToBoundary) // Stream<Message>
					.toList();
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (!parent.isActive()) {
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
			List<SuperAppObjectEntity> children = this.databaseCrud.findByParentsContainingAndActiveIsTrue(parent,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			return children.stream().map(this.objectConverter::entityToBoundary).toList();
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	/**
	 * Search DB for parents objects of a child.
	 * If use is MINIAPP_USER show only active parents.
	 * 
	 * @param superapp
	 * @param internalObjectId
	 * @param superapp superapp name
	 * @param email    user email
	 * @param size     how many items in page
	 * @param page     current page
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public List<SuperAppObjectBoundary> getAnArrayWithObjectParent(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page) {
		String userId = userSuperapp + DELIMITER + userEmail;
		String attr = superapp + DELIMITER + internalObjectId;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		SuperAppObjectEntity child = this.databaseCrud.findById(attr).orElseThrow(
				() -> new SuperAppObjectNotFoundException("could not find origin message by id: " + internalObjectId));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			List<SuperAppObjectEntity> parents = this.databaseCrud.findByChildrenContaining(child,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			return parents.stream() // Stream<MessageEntity>
					.map(this.objectConverter::entityToBoundary) // Stream<Message>
					.toList();
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (child.isActive()) {
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
			List<SuperAppObjectEntity> parents = this.databaseCrud.findByChildrenContainingAndActiveIsTrue(child,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			return parents.stream() // Stream<MessageEntity>
					.map(this.objectConverter::entityToBoundary) // Stream<Message>
					.toList();
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	/**
	 * Search DB for objects by their type.
	 * 
	 * @param superapp superapp name
	 * @param email    user email
	 * @param type     object type
	 * @param size     how many items in page
	 * @param page     current page
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public List<SuperAppObjectBoundary> searchObjectsByType(String superapp, String email, String type, int size,
			int page) {
		String userId = superapp + DELIMITER + email;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			return this.databaseCrud.findAllByType(type, PageRequest.of(page, size, Direction.ASC, "type", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByTypeAndActiveIsTrue(type, PageRequest.of(page, size, Direction.ASC, "type", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			throw new UnauthorizedAccessException(unauthorizedUserMessage);
		}
	}

	/**
	 * Search DB for objects by their alias.
	 * 
	 * @param superapp superapp name
	 * @param email    user email
	 * @param alias    object alias
	 * @param size     how many items in page
	 * @param page     current page
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public List<SuperAppObjectBoundary> searchObjectsByAlias(String superapp, String email, String alias, int size,
			int page) {
		String userId = superapp + DELIMITER + email;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));

		if (user.getRole() == UserRole.SUPERAPP_USER) {
			return this.databaseCrud.findAllByAlias(alias, PageRequest.of(page, size, Direction.ASC, "type", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByAliasAndActiveIsTrue(alias, PageRequest.of(page, size, Direction.ASC, "type", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			throw new UnauthorizedAccessException(unauthorizedUserMessage);
		}
	}

	/**
	 * Search DB for objects in a square area around the point.
	 * 
	 * @param superapp superapp name
	 * @param email    user email
	 * @param lat      latitude
	 * @param lng      longitude
	 * @param distance distance
	 * @param units    distance units
	 * @param size     how many items in page
	 * @param page     current page
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public List<SuperAppObjectBoundary> searchObjectsByLocation(String superapp, String email, double lat, double lng,
			double distance, String units, int size, int page) {
		String userId = superapp + DELIMITER + email;

		double minLat = lat - distance, maxLat = lat + distance, minLng = lng - distance, maxLng = lng + distance;

		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));

		if (user.getRole() == UserRole.SUPERAPP_USER) {
			return this.databaseCrud
					.findAllByLatBetweenAndLngBetween(minLat, maxLat, minLng, maxLng,
							PageRequest.of(page, size, Direction.ASC, "type", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByLatBetweenAndLngBetweenAndActiveIsTrue(minLat, maxLat, minLng, maxLng,
							PageRequest.of(page, size, Direction.ASC, "type", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			throw new UnauthorizedAccessException(unauthorizedUserMessage);
		}
	}


	private SuperAppObjectBoundary entityToBoundary(SuperAppObjectEntity superAppObjectEntity) {
		SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
		objectBoundary.setActive(superAppObjectEntity.isActive());
		objectBoundary.setAlias(superAppObjectEntity.getAlias());
		objectBoundary.setCreatedBy(this.objectConverter.toBoundaryAsCreatedBy(superAppObjectEntity.getCreatedBy()));
		objectBoundary.setCreationTimestamp(superAppObjectEntity.getCreationTimestamp());
		objectBoundary
				.setLocation(this.objectConverter.toBoundaryAsLocation(superAppObjectEntity.getLat(), superAppObjectEntity.getLng()));
		objectBoundary.setObjectDetails(superAppObjectEntity.getObjectDetails());
		objectBoundary.setObjectId(this.objectConverter.toBoundaryAsObjectId(superAppObjectEntity.getObjectId()));
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
		superAppObjectEntity.setLat(objectBoundary.getLocation().getLat());
		superAppObjectEntity.setLng(objectBoundary.getLocation().getLng());
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

//	private String boundaryToStr(Location location) {
//		String boundaryStr = location.getLat().toString() + DELIMITER + location.getLng().toString();
//		return boundaryStr;
//	}
	
	

	
	




}
