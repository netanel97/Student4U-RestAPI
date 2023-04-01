package controllersAPI;



import org.springframework.web.bind.annotation.PathVariable;

import entities.MiniAppCommandBoundary;
import entities.UserBoundary;

public interface AdminAPI {
	UserBoundary[] exportAllUsers();

	MiniAppCommandBoundary[] allMiniAppCommandBoundaries();
	
	MiniAppCommandBoundary[] specificMiniAppCommandBoundaries(
			@PathVariable("miniAppName") String miniAppName);
	
	void deleteAllUsersInTheSuperApp();
	
	void deleteAllObjectsInTheSuperApp();
	
	void deleteAllCommandsHistory();
	
}