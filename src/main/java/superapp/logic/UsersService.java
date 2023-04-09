package superapp.logic;


import java.util.List;
import java.util.Optional;

import superapp.entities.UserBoundary;

public interface UsersService {

	public Optional<UserBoundary> login(String userSuperApp,String userEmail);

	public UserBoundary createUser(UserBoundary user);

	public UserBoundary updateUser(String userSuperApp,String userEmail,UserBoundary  update);

	public List<UserBoundary> getAllUsers();

	public void deleteAllUsers();

}
