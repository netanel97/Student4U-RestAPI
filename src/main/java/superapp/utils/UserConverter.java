package superapp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserId;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.mongo.ObjectsServiceMongoDb;

import static superapp.utils.Constants.DELIMITER;

@Component
public class UserConverter {
	private String springApplicationName;
	private Log logger = LogFactory.getLog(UserConverter.class);

	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}
	
	/**
	 * Convert user entity to user boundary
	 * 
	 * @param userEntity user entity
	 * @return UserBoundary
	 */
	public UserBoundary entityToBoundary(UserEntity userEntity) {
		logger.trace("Entering to function entityToBoundary with the following userEntity: " + userEntity);
		UserBoundary userBoundary = new UserBoundary();
		userBoundary.setAvatar(userEntity.getAvatar());
		userBoundary.setRole(userEntity.getRole().toString());
		userBoundary.setUserId(this.toBoundaryUserId(userEntity.getUserId()));
		userBoundary.setUsername(userEntity.getUserName());
		logger.trace("Exiting from function entityToBoundary with the following userBoundary: " + userBoundary);
		return userBoundary;

	}
	
	/**
	 * Convert from String user id to UserId
	 * 
	 * @param userId user id
	 * @return UserId
	 */
	public UserId toBoundaryUserId(String userId) {
		logger.trace("Entering to function toBoundaryUserId with the following userId: " + userId);
		if (userId != null) {
			UserId newUserId = new UserId();
			String[] attr = userId.split(DELIMITER);
			newUserId.setSuperapp(attr[0]);
			newUserId.setEmail(attr[1]);
			logger.trace("Exiting from function toBoundaryUserId with the following newUserId: " + newUserId);
			return newUserId;
		} else {
			return null;
		}
	}
	
	/**
	 * Convert from user Boundary to user Entity
	 *
	 * @param userBoundary user boundary
	 * @return UserEntity user entity
	 */
	public UserEntity boundaryToEntity(UserBoundary userBoundary) {
		logger.trace("Entering to function boundaryToEntity with the following userBoundary: " + userBoundary);
		UserEntity userEntity = new UserEntity();
		userEntity.setAvatar(userBoundary.getAvatar());

		try {
			logger.trace("Trying to find role: " + userBoundary.getRole());
			UserRole role = UserRole.valueOf(userBoundary.getRole());
			userEntity.setRole(role);
		} catch (Exception e) {
			logger.warn("Could not find role: " + userBoundary.getRole());
			throw new RuntimeException("Could not find role: " + userBoundary.getRole());
		}
		userEntity.setUserId(this.userIdToString(userBoundary.getUserId()));
		userEntity.setUserName(userBoundary.getUsername());
		logger.trace("Exiting from function boundaryToEntity with the following userEntity: " + userEntity);
		return userEntity;

	}
	/**
	 * Convert from UserId to string with delimiter
	 * 
	 * @param userId user id
	 * @return String application name followed by delimiter and user email
	 */
	public String userIdToString(UserId userId) {
		logger.trace("Entering to function userIdToString with the following userId: " + userId);
		logger.trace("Exiting from function userIdToString with the following string: " + springApplicationName + DELIMITER + userId.getEmail());
		return springApplicationName + DELIMITER + userId.getEmail();
	}
	
	

}
