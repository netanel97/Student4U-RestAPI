package superapp.controllersSrc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import superapp.entities.MiniAppCommandBoundary;
import superapp.entities.UserBoundary;
import superapp.logic.MiniAppCommandsService;
import superapp.logic.ObjectsService;
import superapp.logic.UsersService;

@RestController
public class AdminController {

	private UsersService usersService;
	private ObjectsService objectsService;
	private MiniAppCommandsService miniAppCommandsService;

	@Autowired
	public void setUsersService(UsersService usersService) {
		this.usersService = usersService;
	}

	@Autowired
	public void setObjectsService(ObjectsService objectsService) {
		this.objectsService = objectsService;
	}
	
	@Autowired
	public void setMiniAppCommandsService(MiniAppCommandsService miniAppCommandsService) {
		this.miniAppCommandsService = miniAppCommandsService;
	}

	/**
	 * Export all MiniApps Commands history. Receives HTTP Method 'GET'.
	 * 
	 * @param None
	 * @return Array of all MiniApp Command Boundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	
	public MiniAppCommandBoundary[] getAllMiniAppCommandsHistory() {
		
		List<MiniAppCommandBoundary> allBoundaries = this.miniAppCommandsService.getAllCommands();
		return allBoundaries.toArray(new MiniAppCommandBoundary[0]);
	}

	/**
	 * Export Commands history of a specific MiniApp. Receives HTTP Method 'GET'.
	 * 
	 * @param None
	 * @return Array of a specific MiniApp Command Boundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp/{miniAppName}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	
	public MiniAppCommandBoundary[] specificMiniAppCommandBoundaries(@PathVariable("miniAppName") String miniAppName) {

		List<MiniAppCommandBoundary> allBoundaries = this.miniAppCommandsService.getAllMiniAppCommands(miniAppName);
		return allBoundaries.toArray(new MiniAppCommandBoundary[0]);

	}

	/**
	 * Deletes all commands history in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */
	
	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.DELETE })
	public void deleteAllCommandsHistory() {
		this.miniAppCommandsService.deleteAllCommands();
	}

	/**
	 * Delete all users in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */

	@RequestMapping(path = { "/superapp/admin/users" }, method = { RequestMethod.DELETE })
	public void deleteAllUsers() {
		this.usersService.deleteAllUsers();
	}

	/**
	 * Export all existing users. Receives HTTP Method 'GET'.
	 * 
	 * @param None
	 * @return Array of all UserBoundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/users" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary[] getAllUsers() {
		List<UserBoundary> allBoundaries = this.usersService.getAllUsers();
		return allBoundaries.toArray(new UserBoundary[0]);
	}

	/**
	 * Delete all objects in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */
	@RequestMapping(path = { "/superapp/admin/objects" }, method = { RequestMethod.DELETE })
	public void deleteAllObjectsInTheSuperApp() {
		this.objectsService.deleteAllObjects();
	}

}
