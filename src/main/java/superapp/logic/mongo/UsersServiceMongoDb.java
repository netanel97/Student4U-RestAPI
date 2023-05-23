package superapp.logic.mongo;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.UserBoundary;
import superapp.entities.UserCrud;
import superapp.entities.UserId;
import superapp.logic.UnauthorizedAccessException;
import superapp.logic.UserNotAcceptableException;
import superapp.logic.UserNotFoundException;
import superapp.logic.UsersService;
import superapp.logic.UsersServiceWithPaginationSupport;

@Service
public class UsersServiceMongoDb implements UsersServiceWithPaginationSupport {
	private UserCrud databaseCrud;
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
	public UsersServiceMongoDb(UserCrud userCrud) {
		this.databaseCrud = userCrud;
	}

	/**
	 * this method is invoked after values are injected to instance
	 */
	@PostConstruct
	public void init() {
		System.err.println("******** " + this.springApplicationName);
	}

	/**
	 * Creates a new user
	 * 
	 * @param UserBoundary new user boundary
	 * @return UserBoundary User boundary
	 */
	@Override
	public UserBoundary createUser(UserBoundary user) {
		if (user == null) {
			throw new UserNotAcceptableException("UserBoundary is null");
		}
		if (user.getUsername() == null || user.getUsername().isBlank()) {
			throw new UserNotAcceptableException("Username is null, empty or blank");
		}
		if (user.getAvatar() == null || user.getAvatar().isBlank()) {
			throw new UserNotAcceptableException("Avatar is null, empty or blank");
		}
		if (user.getRole() == null) {
			throw new UserNotAcceptableException("Role is null");
		}
		if (!checkEmail(user.getUserId().getEmail())) {
			throw new UserNotAcceptableException("The email address is invalid");
		}

		UserEntity userEntity = this.boundaryToEntity(user);

		// put userEntity in DB
		userEntity = this.databaseCrud.save(userEntity);

		return this.entityToBoundary(userEntity);
	}

	/**
	 * Login with a specific user
	 * 
	 * @param String Application name
	 * @param String user email
	 * @return Optional<UserBoundary> UserBoundary
	 */
	@Override
	public Optional<UserBoundary> login(String userSuperApp, String userEmail) {
		String userId = userSuperApp + DELIMITER + userEmail;

		return this.databaseCrud.findById(userId).map(this::entityToBoundary);
	}

	/**
	 * Update existing user in the desired fields
	 * 
	 * @param String       Application name
	 * @param String       userEmail
	 * @param UserBoundary user boundary to change his attributes
	 * @return UserBoundary user boundary after update
	 */
	@Override
	public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {
		String attr = userSuperApp + DELIMITER + userEmail;

		UserEntity existingUser = this.databaseCrud.findById(attr)
				.orElseThrow(() -> new UserNotFoundException(
				"Could not update superapp object by id: " + attr + " because it does not exist"));
		if (existingUser == null) {
			throw new UserNotFoundException("Could not find user by email: " + userEmail);
		}
		boolean dirtyFlag = false;
		if (update.getAvatar() != null && !update.getAvatar().isBlank()) {
			existingUser.setAvatar(update.getAvatar());
			dirtyFlag = true;
		}
		if (update.getRole() != null) {
			try {
				UserRole role = UserRole.valueOf(update.getRole());
				existingUser.setRole(role);
				dirtyFlag = true;
			} catch (Exception e) {
				throw new UserNotAcceptableException("Bad role inserted. Could not find role: " + update.getRole());
			}
		}
		if (update.getUsername() != null && !update.getUsername().isBlank()) {
			existingUser.setUserName(update.getUsername());
			dirtyFlag = true;
		}
		if (dirtyFlag) {
			existingUser = this.databaseCrud.save(existingUser);
		}
		return this.entityToBoundary(existingUser);

	}

	/**
	 * Get all users from DB
	 * 
	 * @return List<UserBoundary>
	 */
	
	@Override
	@Deprecated
	public List<UserBoundary> getAllUsers() {
//		return this.databaseCrud
//	            .findAll() // List<SuperAppObjectBoundary>
//	            .stream() // Stream<SuperAppObjectBoundary>
//	            .map(this::entityToBoundary) // Stream<SuperAppObject>
//	            .toList(); // Lis

		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

	}
	
	
	@Override
	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page) {
		
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.databaseCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		if (user.getRole() == UserRole.ADMIN) {
			return this.databaseCrud.findAll(PageRequest.of(page, size, Direction.ASC, "userId")) // List<UserEntity>
					.stream() // Stream<UserEntity>
					.map(this::entityToBoundary) // Stream<UserBoundary>
					.toList(); // List<UserBoundary>
		} else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}
	}

	/**
	 * Delete all users from DB
	 * 
	 */
	@Override
	@Deprecated
	public void deleteAllUsers() {
		this.databaseCrud.deleteAll();
		
		throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");
	}
	
	@Override
	public void deleteAllUsers(String userSuperapp, String userEmail) {
		String userId = userSuperapp + DELIMITER + userEmail;
		UserEntity user = this.databaseCrud.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
		
		if (user.getRole() == UserRole.ADMIN) {
			this.databaseCrud.deleteAll();
		} 
		else {
			throw new UnauthorizedAccessException("User doesn't have permissions!");
		}	
	}

	/**
	 * Check if email is valid.
	 * 
	 * @param String email
	 * @return boolean true if the email valid else false
	 */
	private boolean checkEmail(String email) {
		if (email.isEmpty()) {
			return false;
		}
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

		return Pattern.compile(regex).matcher(email).matches();
	}

	/**
	 * Convert user entity to user boundary
	 * 
	 * @param UserEntity user entity
	 * @return UserBoundary
	 */
	private UserBoundary entityToBoundary(UserEntity userEntity) {
		UserBoundary userBoundary = new UserBoundary();
		userBoundary.setAvatar(userEntity.getAvatar());
		userBoundary.setRole(userEntity.getRole().toString());
		userBoundary.setUserId(this.toBoundaryUserId(userEntity.getUserId()));
		userBoundary.setUsername(userEntity.getUserName());
		return userBoundary;

	}

	/**
	 * Convert from String user id to UserId
	 * 
	 * @param String user id
	 * @return UserId
	 */
	private UserId toBoundaryUserId(String userId) {
		if (userId != null) {
			UserId newUserId = new UserId();
			String[] attr = userId.split(DELIMITER);
			newUserId.setSuperapp(attr[0]);
			newUserId.setEmail(attr[1]);
			return newUserId;
		} else {
			return null;
		}
	}

	/**
	 * Convert from user Boundary to user Entity
	 * 
	 * @param UserBoundary user boundary
	 * @return UserEntity user entity
	 */
	private UserEntity boundaryToEntity(UserBoundary userBoundary) {
		UserEntity userEntity = new UserEntity();
		userEntity.setAvatar(userBoundary.getAvatar());

		try {
			UserRole role = UserRole.valueOf(userBoundary.getRole());
			userEntity.setRole(role);
		} catch (Exception e) {
			throw new RuntimeException("Could not find role: " + userBoundary.getRole());
		}
		userEntity.setUserId(this.boundaryToStr(userBoundary.getUserId()));
		userEntity.setUserName(userBoundary.getUsername());
		return userEntity;

	}

	/**
	 * Convert from UserId to string with delimiter
	 * 
	 * @param UserId user id
	 * @return String application name followed by delimiter and user email
	 */
	private String boundaryToStr(UserId userId) {
		return springApplicationName + DELIMITER + userId.getEmail();
	}

}