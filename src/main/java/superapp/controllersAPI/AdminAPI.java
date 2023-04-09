package superapp.controllersAPI;



import org.springframework.web.bind.annotation.PathVariable;

import superapp.entities.MiniAppCommandBoundary;
import superapp.entities.UserBoundary;

public interface AdminAPI {
	UserBoundary[] exportAllUsers();

	MiniAppCommandBoundary[] allMiniAppCommandBoundaries();
	
	MiniAppCommandBoundary[] specificMiniAppCommandBoundaries(
			@PathVariable("miniAppName") String miniAppName);
	
	void deleteAllUsersInTheSuperApp();
	
	void deleteAllObjectsInTheSuperApp();
	
	void deleteAllCommandsHistory();
	
}