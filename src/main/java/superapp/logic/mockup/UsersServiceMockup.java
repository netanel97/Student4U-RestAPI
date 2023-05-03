//package superapp.logic.mockup;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import jakarta.annotation.PostConstruct;
//import superapp.data.UserEntity;
//import superapp.data.UserRole;
//import superapp.entities.UserBoundary;
//import superapp.entities.UserId;
//import superapp.logic.UsersService;
//
//@Service
//public class UsersServiceMockup implements UsersService {
//	private Map<String, UserEntity> databaseMockup;
//	private String springApplicationName;
//	private String DELIMITER = "_";
//
//
//	/**
//	 * this method injects a configuration value of spring
//	 */
//	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
//	public void setSpringApplicationName(String springApllicationName) {
//		this.springApplicationName = springApllicationName;
//	}
//	
//
//	/**
//	 * this method is invoked after values are injected to instance
//	 */
//	@PostConstruct
//	public void init() {
//		// create a thread safe map
//		this.databaseMockup = Collections.synchronizedMap(new HashMap<>());
//		System.err.println("******** " + this.springApplicationName);
//	}
//
//	/**
//	 * Login with a specific user
//	 * 
//	 * @param String Application name
//	 * @param String user email
//	 * @return Optional<UserBoundary> UserBoundary
//	 */
//	@Override
//	public Optional<UserBoundary> login(String userSuperApp, String userEmail) {
//		String userId = userSuperApp + DELIMITER + userEmail;
//		UserEntity entity = this.databaseMockup.get(userId);
//		if (entity == null) {
//			return Optional.empty();
//		} else {
//			UserBoundary boundary = this.entityToBoundary(entity);
//			return Optional.of(boundary);
//		}
//	}
//
//	/**
//	 * Creates a new user
//	 * 
//	 * @param UserBoundary new user boundary
//	 * @return UserBoundary User boundary
//	 */
//
//	@Override
//	public UserBoundary createUser(UserBoundary userBoundary) {
//		if (userBoundary == null) {
//			throw new RuntimeException("UserBoundary is null");
//		}
//		UserEntity userEntity = this.boundaryToEntity(userBoundary);
//
//		this.databaseMockup.put(userEntity.getUserId(), userEntity);
//		return this.entityToBoundary(userEntity);
//	}
//
//	/**
//	 * Update existing user in the desired fields
//	 * 
//	 * @param String       Application name
//	 * @param String       userEmail
//	 * @param UserBoundary user boundary to change his attributes
//	 * @return UserBoundary user boundary after update
//	 */
//
//	@Override
//	public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {
//		String attr = userSuperApp + DELIMITER + userEmail;
//
//		UserEntity existingUser = this.databaseMockup.get(attr);
//		if (existingUser == null) {
//			throw new RuntimeException("Could not find user by email: " + userEmail);
//		}
//		boolean dirtyFlag = false;
//		if (update.getAvatar() != null) {
//			existingUser.setAvatar(update.getAvatar());
//			dirtyFlag = true;
//		}
//		if (update.getRole() != null) {
//			try {
//				UserRole role = UserRole.valueOf(update.getRole());
//				existingUser.setRole(role);
//				dirtyFlag = true;
//			} catch (Exception e) {
//				throw new RuntimeException("Could not find role: " + update.getRole());
//			}
//		}
//		if (update.getUserId() != null) {
//			existingUser.setUserId(boundaryToStr(update.getUserId()));
//			dirtyFlag = true;
//		}
//		if (update.getUsername() != null) {
//			existingUser.setUserName(update.getUsername());
//			dirtyFlag = true;
//		}
//		if (dirtyFlag) {
//			this.databaseMockup.put(attr, existingUser);
//		}
//		return this.entityToBoundary(existingUser);
//
//	}
//
//	/**
//	 * Get all users from DB
//	 * 
//	 * @return List<UserBoundary>
//	 */
//
//	@Override
//	public List<UserBoundary> getAllUsers() {
//		return this.databaseMockup.values().stream().map(this::entityToBoundary).toList();
//	}
//
//	/**
//	 * Convert user entity to user boundary
//	 * 
//	 * @param UserEntity user entity
//	 * @return UserBoundary
//	 */
//	private UserBoundary entityToBoundary(UserEntity userEntity) {
//		UserBoundary userBoundary = new UserBoundary();
//		userBoundary.setAvatar(userEntity.getAvatar());
//		userBoundary.setRole(userEntity.getRole().toString());
//		userBoundary.setUserId(this.toBoundaryUserId(userEntity.getUserId()));
//		userBoundary.setUsername(userEntity.getUserName());
//		return userBoundary;
//
//	}
//
//	/**
//	 * Convert from String user id to UserId
//	 * 
//	 * @param String user id
//	 * @return UserId
//	 */
//	private UserId toBoundaryUserId(String userId) {
//		if (userId != null) {
//			UserId newUserId = new UserId();
//			String[] attr = userId.split(DELIMITER);
//			newUserId.setSuperApp(attr[0]);
//			newUserId.setEmail(attr[1]);
//			return newUserId;
//		} else {
//			return null;
//		}
//	}
//
//	/**
//	 * Convert from user Boundary to user Entity
//	 * 
//	 * @param UserBoundary user boundary
//	 * @return UserEntity user entity
//	 */
//	private UserEntity boundaryToEntity(UserBoundary userBoundary) {
//		UserEntity userEntity = new UserEntity();
//		userEntity.setAvatar(userBoundary.getAvatar());
//
//		try {
//			UserRole role = UserRole.valueOf(userBoundary.getRole());
//			userEntity.setRole(role);
//		} catch (Exception e) {
//			throw new RuntimeException("Could not find role: " + userBoundary.getRole());
//		}
//		userEntity.setUserId(this.boundaryToStr(userBoundary.getUserId()));
//		userEntity.setUserName(userBoundary.getUsername());
//		return userEntity;
//
//	}
//
//	/**
//	 * Convert from UserId to string with delimiter
//	 * 
//	 * @param UserId user id
//	 * @return String application name followed by delimiter and user email
//	 */
//	private String boundaryToStr(UserId userId) {
//		return springApplicationName + DELIMITER + userId.getEmail();
//	}
//
//	/**
//	 * Delete all users from DB
//	 * 
//	 */
//	@Override
//	public void deleteAllUsers() {
//		this.databaseMockup.clear();
//
//	}
//
//}
