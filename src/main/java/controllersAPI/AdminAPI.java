package controllersAPI;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.PathVariable;

import entities.MiniAppCommandBoundary;
import entities.UserBoundary;

public interface AdminAPI {
	UserBoundary[] exportAllUsers();
	
	///need to change from arraylist to array

	ArrayList<MiniAppCommandBoundary> allMiniAppCommandBoundaries();

	ArrayList<MiniAppCommandBoundary> specificMiniAppCommandBoundaries(
			@PathVariable("miniAppName") String internalObjectId);
	
	void deleteAllUsersInTheSuperApp();
	
	void deleteAllObjectsInTheSuperApp();
	
	void deleteAllCommandsHistory();
	
}