package superapp.logic.mongo;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import superapp.logic.ObjectServiceWithPaginationSupport;
import superapp.logic.SuperAppObjectNotActiveException;
import superapp.logic.SuperAppObjectNotFoundException;
import superapp.logic.UnauthorizedAccessException;
import superapp.logic.UserNotFoundException;

@Service
public class ObjectsServiceMongoDb implements ObjectServiceWithPaginationSupport {
	private SuperAppObjectCrud databaseCrud;
	private UserCrud userCrud;
	private String springApplicationName;
	private final String DELIMITER = "_";
	private String unauthorizedUserMessage = "User doesn't have permissions!";

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
		return this.entityToBoundary(superAppObjectEntity);
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
			return this.entityToBoundary(existingObject);
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
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
		return this.databaseCrud.findById(attr).map(this::entityToBoundary);
		// .orElseThrow(()->new SuperAppObjectNotFoundException("could not update
		// superapp object by id: " + attr + " because it does not exist"));
//		}

	}

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
			return Optional.of(superAppObjectEntity).map(this::entityToBoundary);
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (superAppObjectEntity.isActive()) {
				return Optional.of(superAppObjectEntity).map(this::entityToBoundary);
			} else {
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
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
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			return this.databaseCrud.findAll(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId")) // List<SuperAppObjectBoundary>
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByActiveIsTrue(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId")) // List<SuperAppObjectBoundary>
					.stream() // Stream<SuperAppObjectBoundary>
					.filter(object -> object.isActive()).map(this::entityToBoundary) // Stream<SuperAppObject>
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

//			if (!child.isActive() || !parent.isActive()) {
//				throw new SuperAppObjectNotActiveException("At least one of the superapp objects is not active!");
//			}
//			// TODO: need to ask Eyal if we need to check active on PUT with SUPER APP USER

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
					.map(this::entityToBoundary) // Stream<Message>
					.toList();
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (!parent.isActive()) {
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
			List<SuperAppObjectEntity> children = this.databaseCrud.findByParentsContainingAndActiveIsTrue(parent,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			return children.stream().map(this::entityToBoundary).toList();
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

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
					.map(this::entityToBoundary) // Stream<Message>
					.toList();
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (child.isActive()) {
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
			List<SuperAppObjectEntity> parents = this.databaseCrud.findByChildrenContainingAndActiveIsTrue(child,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			return parents.stream() // Stream<MessageEntity>
					.map(this::entityToBoundary) // Stream<Message>
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
					.map(this::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByTypeAndActiveIsTrue(type, PageRequest.of(page, size, Direction.ASC, "type", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this::entityToBoundary) // Stream<SuperAppObject>
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
					.map(this::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByAliasAndActiveIsTrue(alias, PageRequest.of(page, size, Direction.ASC, "type", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this::entityToBoundary) // Stream<SuperAppObject>
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
					.map(this::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			return this.databaseCrud
					.findAllByLatBetweenAndLngBetweenAndActiveIsTrue(minLat, maxLat, minLng, maxLng,
							PageRequest.of(page, size, Direction.ASC, "type", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			throw new UnauthorizedAccessException(unauthorizedUserMessage);
		}
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
		objectBoundary
				.setLocation(this.toBoundaryAsLocation(superAppObjectEntity.getLat(), superAppObjectEntity.getLng()));
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
	private Location toBoundaryAsLocation(Double lat, Double lng) {
		if (lat != null && lng != null) {

			Location location = new Location();
			location.setLat(lat);
			location.setLng(lng);

			return location;
		} else {
			return null;
		}
	}

}
