package superapp.logic.mongo;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.user.UserBoundary;
import superapp.dal.UserCrud;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.logic.UnauthorizedAccessException;
import superapp.logic.UserNotAcceptableException;
import superapp.logic.UserNotFoundException;
import superapp.logic.UsersServiceWithPaginationSupport;
import superapp.utils.UserConverter;

@Service
public class UsersServiceMongoDb implements UsersServiceWithPaginationSupport {
    private UserCrud databaseCrud;
    private String springApplicationName;
    private final String DELIMITER = "_";
    private UserConverter userConverter;
    private Log logger = LogFactory.getLog(UsersServiceMongoDb.class);

    /**
     * this method injects a configuration value of spring
     */
    @Value("${spring.application.name:2023b.Liran.Sorokin-Student4U}")
    public void setSpringApplicationName(String springApplicationName) {
        logger.trace("Entering to setSpringApplicationName and set the springApplicationName with the name: " + springApplicationName);
        this.springApplicationName = springApplicationName;
    }

    @Autowired
    public UsersServiceMongoDb(UserCrud userCrud, UserConverter userConverter) {
        logger.trace("Entering to UsersServiceMongoDb constructor");
        this.databaseCrud = userCrud;
        this.userConverter = userConverter;

    }

    /**
     * this method is invoked after values are injected to instance
     */
    @PostConstruct
    public void init() {
        logger.trace("Entering to init method to print the springApplicationName");
        System.err.println("******** " + this.springApplicationName);
    }

    /**
     * Creates a new user
     *
     * @param user new UserBoundary
     * @return UserBoundary User boundary
     */
    @Override
    public UserBoundary createUser(UserBoundary user) {
        logger.trace("Entering to createUser method");
        if (user == null) {
            logger.warn("UserBoundary is null " + user);
            throw new UserNotAcceptableException("UserBoundary is null");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            logger.warn("Username is null, empty or blank " + user);
            throw new UserNotAcceptableException("Username is null, empty or blank");
        }
        if (user.getAvatar() == null || user.getAvatar().isBlank()) {
            logger.warn("Avatar is null, empty or blank " + user);
            throw new UserNotAcceptableException("Avatar is null, empty or blank");
        }
        if (user.getRole() == null) {
            logger.warn("Role is null " + user);
            throw new UserNotAcceptableException("Role is null");
        }
        if (!checkEmail(user.getUserId().getEmail())) {
            logger.warn("The email address is invalid "+ user);
            throw new UserNotAcceptableException("The email address is invalid");
        }

        UserEntity userEntity = this.userConverter.boundaryToEntity(user);
        userEntity = this.databaseCrud.save(userEntity);
        logger.trace("Saving userEntity to DB " + userEntity);
        logger.trace("Exiting from createUser method");
        return this.userConverter.entityToBoundary(userEntity);
    }

    /**
     * Login with a specific user
     *
     * @param userSuperApp Application name
     * @param userEmail    user email
     * @return Optional<UserBoundary> UserBoundary
     */
    @Override
    public Optional<UserBoundary> login(String userSuperApp, String userEmail) {
        logger.trace("Entering to login method");
        String userId = userSuperApp + DELIMITER + userEmail;
        logger.trace("User id is: " + userId);
        logger.trace("Exiting from login method");
        return this.databaseCrud.findById(userId).map(this.userConverter::entityToBoundary);
    }

    /**
     * Update existing user in the desired fields
     *
     * @param userSuperApp Application name
     * @param userEmail    userEmail
     * @param UserBoundary user boundary to change his attributes
     * @return UserBoundary user boundary after update
     */
    @Override
    public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {
        logger.trace("Entering to updateUser method with the following parameters: " + userSuperApp + " " + userEmail + " " + update);
        String attr = userSuperApp + DELIMITER + userEmail;

        UserEntity existingUser = this.databaseCrud.findById(attr)
                .orElseThrow(() -> new UserNotFoundException(
                        "Could not update superapp object by id: " + attr + " because it does not exist"));
        logger.trace("Passed the searching user and the UserEntity is: " + existingUser);
        if (existingUser == null) {
            logger.warn("Could not find user by email: " + userEmail);
            throw new UserNotFoundException("Could not find user by email: " + userEmail);
        }
        boolean dirtyFlag = false;
        if (update.getAvatar() != null && !update.getAvatar().isBlank()) {
            logger.trace("Updating avatar to: " + update.getAvatar());
            existingUser.setAvatar(update.getAvatar());
            dirtyFlag = true;
        }
        if (update.getRole() != null) {
            try {
                logger.trace("Updating role to: " + update.getRole());
                UserRole role = UserRole.valueOf(update.getRole());
                existingUser.setRole(role);
                logger.trace("Updating succeeded");
                dirtyFlag = true;
            } catch (Exception e) {
                logger.warn("Bad role inserted. Could not find role: " + update.getRole());
                throw new UserNotAcceptableException("Bad role inserted. Could not find role: " + update.getRole());
            }
        }
        if (update.getUsername() != null && !update.getUsername().isBlank()) {
            logger.trace("Updating username to: " + update.getUsername());
            existingUser.setUserName(update.getUsername());
            dirtyFlag = true;
        }
        if (dirtyFlag) {
            logger.trace("Saving the updated user to DB");
            existingUser = this.databaseCrud.save(existingUser);
        }
        logger.trace("Exiting from updateUser method");
        return this.userConverter.entityToBoundary(existingUser);

    }

    /**
     * Get all users from DB
     *
     * @return List<UserBoundary>
     */

    @Override
    @Deprecated
    public List<UserBoundary> getAllUsers() {
        logger.warn("the method getAllUsers is deprecated");
        throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");

    }


    @Override
    public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page) {
        logger.trace("Entering to getAllUsers method with the following parameters: " + userSuperapp + " " + userEmail + " " + size + " " + page);
        String userId = userSuperapp + DELIMITER + userEmail;
        UserEntity user = this.databaseCrud.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
        logger.trace("Passed the searching user and the UserEntity is: " + user);
        if (user.getRole() == UserRole.ADMIN) {
            logger.trace("Exiting from getAllUsers method");
            return this.databaseCrud.findAll(PageRequest.of(page, size, Direction.ASC, "userId")) // List<UserEntity>
                    .stream() // Stream<UserEntity>
                    .map(this.userConverter::entityToBoundary) // Stream<UserBoundary>
                    .toList(); // List<UserBoundary>
        } else {
            logger.warn("User doesn't have permissions!");
            throw new UnauthorizedAccessException("User doesn't have permissions!");
        }
    }

    /**
     * Delete all users from DB
     */
    @Override
    @Deprecated
    public void deleteAllUsers() {
        logger.warn("the method deleteAllUsers is deprecated");
        throw new DepreacatedOpterationException("do not use this operation any more, as it is deprecated");
    }

    @Override
    public void deleteAllUsers(String userSuperapp, String userEmail) {
        logger.trace("Entering to deleteAllUsers method with the following parameters: " + userSuperapp + " " + userEmail);
        String userId = userSuperapp + DELIMITER + userEmail;
        UserEntity user = this.databaseCrud.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("could not find user by id: " + userId));
        logger.trace("Passed the searching user and the UserEntity is: " + user);
        if (user.getRole() == UserRole.ADMIN) {
            logger.trace("Passed the role check and deleting all users");
            this.databaseCrud.deleteAll();
        } else {
            logger.warn("User doesn't have permissions! " + user);
            throw new UnauthorizedAccessException("User doesn't have permissions!");
        }
    }

    /**
     * Check if email is valid.
     *
     * @param email email
     * @return boolean true if the email valid else false
     */
    private boolean checkEmail(String email) {
        logger.trace("Entering to checkEmail method with the following parameters: " + email);
        if (email.isEmpty()) {
            return false;
        }
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        logger.trace("Exiting from checkEmail method");
        return Pattern.compile(regex).matcher(email).matches();
    }


}