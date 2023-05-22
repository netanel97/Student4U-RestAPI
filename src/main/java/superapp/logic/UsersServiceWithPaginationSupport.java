package superapp.logic;

import java.util.List;
import java.util.Optional;

import superapp.entities.UserBoundary;

public interface UsersServiceWithPaginationSupport extends UsersService{
	public UserBoundary createUser(UserBoundary user);

	public Optional<UserBoundary> login(String userSuperApp, String userEmail);

	public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update);

	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page);

	public void deleteAllUsers(String userSuperApp, String userEmail);
}
