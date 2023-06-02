package superapp.logic.mongo;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.geo.Box;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.object.Location;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.dal.UserCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.ObjectServiceWithPaginationSupport;
import superapp.logic.SuperAppObjectBadRequestException;
import superapp.logic.SuperAppObjectNotActiveException;
import superapp.logic.SuperAppObjectNotFoundException;
import superapp.logic.UnauthorizedAccessException;
import superapp.logic.UserNotFoundException;
import superapp.utils.Constants;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

@Service
public class ObjectsServiceMongoDb implements ObjectServiceWithPaginationSupport {
	private SuperAppObjectCrud databaseCrud;
	private UserCrud userCrud;
	private String springApplicationName;
	private String unauthorizedUserMessage = "User doesn't have permissions!";
	private String userNotFoundByIdMessage = "could not find user by id: ";
	private String deprecatedMethodMessage = "do not use this operation any more, as it is deprecated";
	private ObjectConverter objectConverter;
	private UserConverter userConverter;

	private Log logger = LogFactory.getLog(ObjectsServiceMongoDb.class);

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	@Autowired
	public ObjectsServiceMongoDb(SuperAppObjectCrud superAppObjectCrud, UserCrud userCrud,
			ObjectConverter objectConverter, UserConverter userConverter) {
		this.databaseCrud = superAppObjectCrud;
		this.userCrud = userCrud;
		this.objectConverter = objectConverter;
		this.userConverter = userConverter;
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
	 * @param object new object boundary
	 * @return ObjectBoundary object boundary
	 */
	@Override
	public SuperAppObjectBoundary createObject(SuperAppObjectBoundary object) {
		checkObjectValidity(object);
		SuperAppObjectEntity superAppObjectEntity = this.objectConverter.boundaryToEntity(object);
		logger.trace("Passed the boundaryToEntity in create an object and the SuperAppObjectEntity is: "
				+ superAppObjectEntity);
		superAppObjectEntity.setCreationTimestamp(new Date());
		superAppObjectEntity.setObjectId(springApplicationName + Constants.DELIMITER + UUID.randomUUID().toString());
		logger.trace(
				"Passed the setCreationTimestamp and setObjectId in create an object and the SuperAppObjectEntity is: "
						+ superAppObjectEntity);

		superAppObjectEntity = this.databaseCrud.save(superAppObjectEntity);
		logger.trace("Passed the save to mongodb in create an object and the SuperAppObjectEntity is: "
				+ superAppObjectEntity);
		return this.objectConverter.entityToBoundary(superAppObjectEntity);
	}

	private void checkObjectValidity(SuperAppObjectBoundary object) {
		logger.trace("Entering checkObjectValidity method with object: " + object);
		if (object == null) {
			logger.warn("ObjectBoundary is null");
			throw new SuperAppObjectBadRequestException("ObjectBoundary is null");
		}
		if (object.getCreatedBy() == null) {
			logger.warn("CreatedBy object cannot be null " + object);
			throw new SuperAppObjectBadRequestException("CreatedBy object is null");
		}
		if (object.getCreatedBy().getUserId() == null) {
			logger.warn("UserId object cannot be null " + object);
			throw new SuperAppObjectBadRequestException("UserId object is null");
		}

		if (!checkEmail(object.getCreatedBy().getUserId().getEmail())) {
			logger.warn("The email address is invalid " + object);
			throw new SuperAppObjectBadRequestException("The email address is invalid");
		}

		UserEntity userEntity = userCrud.findById(userConverter.userIdToString(object.getCreatedBy().getUserId()))
				.orElseThrow(() -> new UserNotFoundException("User was not found"));
		logger.trace("Passed the searching userEntity in create an object and the UserEntity is: " + userEntity);
		if (object.getAlias() == null || object.getAlias().isEmpty()) {
			logger.warn("Alias object cannot be null or empty " + object);
			throw new SuperAppObjectBadRequestException("Alias object is null or empty");

		}
		if (object.getType() == null || object.getType().isEmpty()) {
			logger.warn("Type object cannot be null or empty " + object);
			throw new SuperAppObjectBadRequestException("Type object is null or empty");
		}

		if (object.getLocation() == null) {
			logger.warn("Location object cannot be null " + object);
			throw new SuperAppObjectBadRequestException("Location object is null");
		}

		Double lat = object.getLocation().getLat(), lng = object.getLocation().getLng();

		if (lat == null || lat < Math.negateExact(Constants.LATITUDE_RANGE) || lat > Constants.LATITUDE_RANGE) {
			logger.warn("Latitude value in Location object is null or not in range(-" + Constants.LATITUDE_RANGE
					+ " <-> " + Constants.LATITUDE_RANGE + ")");
			throw new SuperAppObjectBadRequestException("Latitude value in Location object is null or not in range(-"
					+ Constants.LATITUDE_RANGE + " <-> " + Constants.LATITUDE_RANGE + ")");
		}

		if (lng == null || lng < Math.negateExact(Constants.LONGITUDE_RANGE) || lng > Constants.LONGITUDE_RANGE) {
			logger.warn("Longitude value in Location object is null or not in range(-" + Constants.LONGITUDE_RANGE
					+ " <-> " + Constants.LONGITUDE_RANGE + ")");
			throw new SuperAppObjectBadRequestException("Longitude value in Location object is null or not in range(-"
					+ Constants.LONGITUDE_RANGE + " <-> " + Constants.LONGITUDE_RANGE + ")");
		}

		if (userEntity.getRole() != UserRole.SUPERAPP_USER) {
			logger.warn("The user is not allowed to create an object " + object);
			throw new UnauthorizedAccessException("The user is not allowed");
		}

	}

	/**
	 * Check email validity
	 *
	 * @param email email
	 * @return boolean true if the email valid else false
	 */
	private boolean checkEmail(String email) {
		if (email.isEmpty()) {
			return false;
		}
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

		logger.trace("Passed the regex in checkEmail and the regex is: " + regex);

		return Pattern.compile(regex).matcher(email).matches();

	}

	@Override
	@Deprecated
	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update) {
		logger.warn("the method updateAnObject is deprecated");
		throw new DepreacatedOpterationException(deprecatedMethodMessage);
	}

	/**
	 * Update existing object in the desired fields
	 *
	 * @param objectSuperApp   Application name
	 * @param internalObjectId Internal object id
	 * @param update           object boundary to change its attributes
	 * @param userSuperapp     userSuperapp
	 * @param userEmail        userEmail
	 * @return ObjectBoundary object boundary after update
	 */
	@Override
	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update, String userSuperapp, String userEmail) {
		logger.trace("Entering to update an object");
		String attr = objectSuperApp + Constants.DELIMITER + internalObjectId;
		String userId = userSuperapp + Constants.DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user in update an object and the UserEntity is: " + user);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check in update an object and the user role is: " + user.getRole());
			SuperAppObjectEntity existingObject = this.databaseCrud.findById(attr)
					.orElseThrow(() -> new SuperAppObjectNotFoundException(
							"could not update superapp object by id: " + attr + " because it does not exist"));
			logger.trace("Passed the searching object in update an object and the SuperAppObjectEntity is: "
					+ existingObject);
			boolean dirtyFlag = false;
			if (update.getActive() != null) {
				logger.trace(
						"Passed the update active in update an object and the update active is: " + update.getActive());
				existingObject.setActive(update.getActive());
				logger.trace("Updating the Active in existingObject to " + existingObject.isActive());
				dirtyFlag = true;
			}
			if (update.getAlias() != null && !update.getAlias().isEmpty()) {
				logger.trace(
						"Passed the update alias in update an object and the update alias is: " + update.getAlias());
				existingObject.setAlias(update.getAlias());
				logger.trace("Updating the Alias in existingObject to " + existingObject.getAlias());
				dirtyFlag = true;
			}
			if (update.getType() != null && !update.getType().isEmpty()) {
				logger.trace("Passed the update type in update an object and the update type is: " + update.getType());
				existingObject.setType(update.getType());
				logger.trace("Updating the Type in existingObject to " + existingObject.getType());
				dirtyFlag = true;
			}
			if (update.getLocation() != null) {
				logger.trace("Passed the update location in update an object and the update location is: "
						+ update.getLocation());
				// GeoJsonPoint returns Longitude first and then Latitude
				Location updatedLocation = update.getLocation();
				GeoJsonPoint geoJsonPoint = new GeoJsonPoint(updatedLocation.getLng(), updatedLocation.getLat());
				existingObject.setLocation(geoJsonPoint);
				logger.trace("Updated location in existingObject to " + existingObject.getLocation());
				dirtyFlag = true;
			}
			if (update.getObjectDetails() != null) {
				logger.trace("Passed the update objectDetails in update an object and the update objectDetails is: "
						+ update.getObjectDetails());
				existingObject.setObjectDetails(update.getObjectDetails());
				logger.trace("Updated the ObjectDetails in existingObject to " + existingObject.getObjectDetails());
				dirtyFlag = true;
			}

			if (dirtyFlag) {
				existingObject = this.databaseCrud.save(existingObject);
				logger.trace("Updated the existingObject in the database and the existingObject is: " + existingObject);
			}
			return this.objectConverter.entityToBoundary(existingObject);
		} else {
			logger.warn("User doesn't have permissions to update an object!");
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	@Override
	@Deprecated
	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId) {
		logger.warn("the method getSpecificObject is deprecated");
		throw new DepreacatedOpterationException(deprecatedMethodMessage);

	}

	/**
	 * Get specific object from DB
	 *
	 * @param objectSuperApp   Application name
	 * @param internalObjectId internalObjectId
	 * @return ObjectBoundary requested object boundary
	 */
	@Override
	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId,
			String userSuperapp, String userEmail) {
		logger.trace("Entering to the method getSpecificObject with the parameters: " + objectSuperApp + " "
				+ internalObjectId + " " + userSuperapp + " " + userEmail);
		String attr = objectSuperApp + Constants.DELIMITER + internalObjectId;
		String userId = userSuperapp + Constants.DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user in getSpecificObject and the UserEntity is: " + user);
		SuperAppObjectEntity superAppObjectEntity = this.databaseCrud.findById(attr)
				.orElseThrow(() -> new SuperAppObjectNotFoundException("Could not find superapp with id: " + attr));
		logger.trace("Passed the searching object in getSpecificObject and the SuperAppObjectEntity is: "
				+ superAppObjectEntity);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check in getSpecificObject and the user role is: " + user.getRole());
			return Optional.of(superAppObjectEntity).map(this.objectConverter::entityToBoundary);
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (superAppObjectEntity.isActive()) {
				logger.trace("Passed the user role check in getSpecificObject and the user role is: " + user.getRole());
				return Optional.of(superAppObjectEntity).map(this.objectConverter::entityToBoundary);
			} else {
				logger.warn("Supper app object is not active");
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
		} else {
			logger.warn("User doesn't have permissions to get an object!");
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	@Override
	@Deprecated
	public List<SuperAppObjectBoundary> getAllObjects() {
		logger.warn("the method getAllObjects is deprecated");
		throw new DepreacatedOpterationException(deprecatedMethodMessage);
	}

	/**
	 * Get all objects from DB
	 *
	 * @param userSuperapp superapp name
	 * @param userEmail    user email
	 * @param size         how many items in page
	 * @param page         current page
	 * @return Array ObjectBoundary[]
	 */
	@Override
	public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {
		logger.trace("Entering to the method getAllObjects with the parameters: " + userSuperapp + " " + userEmail + " "
				+ size + " " + page);
		String userId = userSuperapp + Constants.DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user in getAllObjects and the UserEntity is: " + user);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check in getAllObjects and the user role is: " + user.getRole());
			return this.databaseCrud.findAll(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId")) // List<SuperAppObjectBoundary>
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			logger.trace("Passed the user role check in getAllObjects and the user role is: " + user.getRole());
			return this.databaseCrud
					.findAllByActiveIsTrue(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId")) // List<SuperAppObjectBoundary>
					.stream() // Stream<SuperAppObjectBoundary>
					.filter(object -> object.isActive()).map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			logger.warn("User doesn't have permissions to get all objects!");
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	@Override
	@Deprecated
	public void deleteAllObjects() {
		logger.warn("the method deleteAllObjects is deprecated");
		throw new DepreacatedOpterationException(deprecatedMethodMessage);
	}

	@Override
	public void deleteAllObjects(String userSuperapp, String userEmail) {
		logger.trace("Entering to the method deleteAllObjects with the parameters: " + userSuperapp + " " + userEmail);
		String userId = userSuperapp + Constants.DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user in deleteAllObjects and the UserEntity is: " + user);
		if (user.getRole() == UserRole.ADMIN) {
			logger.trace("Passed the user role check in deleteAllObjects and the user role is: " + user.getRole());
			this.databaseCrud.deleteAll();
		} else {
			logger.warn("User doesn't have permissions to delete all objects!");
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	/**
	 * Update children field of parent in DB. If use is MINIAPP_USER show only
	 * active children.
	 *
	 * @param superapp
	 * @param internalObjectId
	 * @param childId          how many items in page
	 * @param superapp         superapp name
	 * @param userEmail        user email
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public void BindAnExistingObjectToExistingChildObject(String superapp, String internalObjectId,
			SuperAppObjectIdBoundary childId, String userSuperapp, String userEmail) {
		logger.trace("Entering to the method BindAnExistingObjectToExistingChildObject with the parameters: " + superapp
				+ " " + internalObjectId + " " + childId + " " + userSuperapp + " " + userEmail);
		String userId = userSuperapp + Constants.DELIMITER + userEmail;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user in BindAnExistingObjectToExistingChildObject and the UserEntity is: "
				+ user);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			String attr1 = superapp + Constants.DELIMITER + internalObjectId;
			SuperAppObjectEntity parent = this.databaseCrud.findById(attr1)
					.orElseThrow(() -> new SuperAppObjectNotFoundException(
							"could not find origin message by id: " + internalObjectId));
			logger.trace("Passed the searching parent  and the SuperAppObjectEntity is: " + parent);
			String attr = childId.getSuperapp() + Constants.DELIMITER + childId.getInternalObjectId();// child
			SuperAppObjectEntity child = this.databaseCrud.findById(attr).orElseThrow(
					() -> new SuperAppObjectNotFoundException("could not find origin message by id: " + attr));
			if (!child.addParent(parent) || !parent.addChild(child)) {
				logger.warn("Could not bind parent object:" + parent + " to child object: " + child);
				throw new SuperAppObjectBadRequestException(
						"Could not bind parent object:" + parent + " to child object: " + child);
			}
			parent.addChild(child);
			child.addParent(parent);
			this.databaseCrud.save(child);
			this.databaseCrud.save(parent);
			logger.trace("Saved the child and parent objects");
		} else {
			logger.warn("User doesn't have permissions to bind objects!");
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}

	}

	/**
	 * Search DB for children objects of a parent. If use is MINIAPP_USER show only
	 * active children.
	 *
	 * @param superapp
	 * @param internalObjectId
	 * @param superapp         superapp name
	 * @param userEmail        user email
	 * @param size             how many items in page
	 * @param page             current page
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public List<SuperAppObjectBoundary> getAllChildrenOfAnExistingObject(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page) {
		logger.trace("Entering to the method getAllChildrenOfAnExistingObject with the parameters: " + superapp + " "
				+ internalObjectId + " " + userSuperapp + " " + userEmail + " " + size + " " + page);
		String userId = userSuperapp + Constants.DELIMITER + userEmail;
		String attr = superapp + Constants.DELIMITER + internalObjectId;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user and the UserEntity is: " + user);
		SuperAppObjectEntity parent = this.databaseCrud.findById(attr).orElseThrow(
				() -> new SuperAppObjectNotFoundException("could not find origin message by id: " + internalObjectId));
		logger.trace("Passed the searching parent and the SuperAppObjectEntity is: " + parent);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			List<SuperAppObjectEntity> children = this.databaseCrud.findByParentsContaining(parent,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			logger.trace("Passed the searching children and the SuperAppObjectEntity is: " + children);
			return children.stream() // Stream<MessageEntity>
					.map(this.objectConverter::entityToBoundary) // Stream<Message>
					.toList();
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			if (!parent.isActive()) {
				logger.warn("Supper app object is not active");
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
			List<SuperAppObjectEntity> children = this.databaseCrud.findByParentsContainingAndActiveIsTrue(parent,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			return children.stream().map(this.objectConverter::entityToBoundary).toList();
		} else {
			logger.warn("User doesn't have permissions to get children!");
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	/**
	 * Search DB for parents objects of a child. If use is MINIAPP_USER show only
	 * active parents.
	 *
	 * @param superapp
	 * @param internalObjectId
	 * @param superapp         superapp name
	 * @param userEmail        user email
	 * @param size             how many items in page
	 * @param page             current page
	 * @return List of SuperAppObjectBoundary all objects matching criteria
	 */
	@Override
	public List<SuperAppObjectBoundary> getAnArrayWithObjectParent(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page) {
		logger.trace("Entering to the method getAnArrayWithObjectParent with the parameters: " + superapp + " "
				+ internalObjectId + " " + userSuperapp + " " + userEmail + " " + size + " " + page);
		String userId = userSuperapp + Constants.DELIMITER + userEmail;
		String attr = superapp + Constants.DELIMITER + internalObjectId;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user and the UserEntity is: " + user);
		SuperAppObjectEntity child = this.databaseCrud.findById(attr).orElseThrow(
				() -> new SuperAppObjectNotFoundException("could not find origin message by id: " + internalObjectId));
		logger.trace("Passed the searching child and the SuperAppObjectEntity is: " + child);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			List<SuperAppObjectEntity> parents = this.databaseCrud.findByChildrenContaining(child,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			logger.trace("Passed the searching parents and the SuperAppObjectEntity is: " + parents);
			return parents.stream() // Stream<MessageEntity>
					.map(this.objectConverter::entityToBoundary) // Stream<Message>
					.toList();
		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			if (!child.isActive()) {
				logger.warn("Supper app object is not active");
				throw new SuperAppObjectNotActiveException("Supper app object is not active");
			}
			List<SuperAppObjectEntity> parents = this.databaseCrud.findByChildrenContainingAndActiveIsTrue(child,
					PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
			logger.trace("Passed the searching parents and the SuperAppObjectEntity is: " + parents);
			return parents.stream() // Stream<MessageEntity>
					.map(this.objectConverter::entityToBoundary) // Stream<Message>
					.toList();
		} else {
			logger.warn("User doesn't have permissions to get parents!");
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	/**
	 * /** Search DB for objects by their type.
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
		logger.trace("Entering to the method searchObjectsByType with the parameters: " + superapp + " " + email + " "
				+ type + " " + size + " " + page);
		String userId = superapp + Constants.DELIMITER + email;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user and the UserEntity is: " + user);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findAllByType(type,
							PageRequest.of(page, size, Direction.ASC, "type", "creationTimestamp", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findAllByTypeAndActiveIsTrue(type,
							PageRequest.of(page, size, Direction.ASC, "type", "creationTimestamp", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			logger.warn("User doesn't have permissions to search objects by type!");
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
		logger.trace("Entering to the method searchObjectsByAlias with the parameters: " + superapp + " " + email + " "
				+ alias + " " + size + " " + page);
		String userId = superapp + Constants.DELIMITER + email;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user and the UserEntity is: " + user);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findAllByAlias(alias,
							PageRequest.of(page, size, Direction.ASC, "alias", "creationTimestamp", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findAllByAliasAndActiveIsTrue(alias,
							PageRequest.of(page, size, Direction.ASC, "alias", "creationTimestamp", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			logger.warn("User doesn't have permissions to search objects by alias!");
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
		logger.trace("Entering to the method searchObjectsByLocation with the parameters: " + superapp + " " + email
				+ " " + lat + " " + lng + " " + distance + " " + units + " " + size + " " + page);
		String userId = superapp + Constants.DELIMITER + email;
		double calculatedDistance = calculateDistanceUsingGeoUnits(distance, units);

		double minLat = lat - calculatedDistance, maxLat = lat + calculatedDistance, minLng = lng - calculatedDistance,
				maxLng = lng + calculatedDistance;
		
		Box box = new Box(new double[] {minLng, minLat}, new double[] {maxLng, maxLat});
		
		logger.trace("Passed the distance calculation and the minLat is: " + minLat + " maxLat is: " + maxLat
				+ " minLng is: " + minLng + " maxLng is: " + maxLng);
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user and the UserEntity is: " + user);
		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findAllByLocationWithin(box,
							PageRequest.of(page, size, Direction.ASC, "location", "creationTimestamp", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findAllByLocationWithinAndActiveIsTrue(box,
							PageRequest.of(page, size, Direction.ASC, "location", "creationTimestamp", "objectId"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			logger.warn("User doesn't have permissions to search objects by location!");
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
	public List<SuperAppObjectBoundary> searchObjectsByLocationCircle(String superapp, String email, double lat,
			double lng, double distance, String units, int size, int page) {
		logger.trace("Entering to the method searchObjectsByLocationCircle with the parameters: " + superapp + " "
				+ email + " " + lat + " " + lng + " " + distance + " " + units + " " + size + " " + page);
		String userId = superapp + Constants.DELIMITER + email;
		UserEntity user = this.userCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userNotFoundByIdMessage + userId));
		logger.trace("Passed the searching user and the UserEntity is: " + user);
		double calculatedDistance = calculateDistanceUsingGeoUnits(distance, units);

		if (user.getRole() == UserRole.SUPERAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findByLocationNear(lat, lng, calculatedDistance,
							PageRequest.of(page, size, Direction.ASC, "location", "creationTimestamp", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>

		} else if (user.getRole() == UserRole.MINIAPP_USER) {
			logger.trace("Passed the user role check and the user role is: " + user.getRole());
			return this.databaseCrud
					.findByLocationNearAndActiveIsTrue(lat, lng, calculatedDistance,
							PageRequest.of(page, size, Direction.ASC, "type", "creationTimestamp", "id"))
					.stream() // Stream<SuperAppObjectBoundary>
					.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
					.toList(); // List<SuperAppObject>
		} else {
			logger.warn("User doesn't have permissions to search objects by location!");
			throw new UnauthorizedAccessException(unauthorizedUserMessage);
		}
	}

	private double calculateDistanceUsingGeoUnits(double distance, String units) {
		Metrics distanceUnits;
		switch (units.toUpperCase()) {
		case "KILOMETERS":
			logger.trace("Passed the distance units check and the units are: " + units);
			distanceUnits = Metrics.KILOMETERS;
			break;
		case "MILES":
			logger.trace("Passed the distance units check and the units are: " + units);
			distanceUnits = Metrics.MILES;
			break;
		default:
			logger.trace("Passed the distance units check and the units are: " + units);
			distanceUnits = Metrics.NEUTRAL;
		}
		double calculatedDistance = Metrics.valueOf(distanceUnits.toString()).getMultiplier() * distance;
		return calculatedDistance;
	}

	
//	private SuperAppObjectBoundary entityToBoundary(SuperAppObjectEntity superAppObjectEntity) {
//		SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
//		objectBoundary.setActive(superAppObjectEntity.isActive());
//		objectBoundary.setAlias(superAppObjectEntity.getAlias());
//		objectBoundary.setCreatedBy(this.objectConverter.toBoundaryAsCreatedBy(superAppObjectEntity.getCreatedBy()));
//		objectBoundary.setCreationTimestamp(superAppObjectEntity.getCreationTimestamp());
//		objectBoundary.setLocation(this.objectConverter.toBoundaryAsLocation(superAppObjectEntity.getLat(),
//				superAppObjectEntity.getLng()));
//		objectBoundary.setObjectDetails(superAppObjectEntity.getObjectDetails());
//		objectBoundary.setObjectId(this.objectConverter.toBoundaryAsObjectId(superAppObjectEntity.getObjectId()));
//		objectBoundary.setType(superAppObjectEntity.getType());
//		return objectBoundary;
//	}
}
