package superapp.controllersSrc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.logic.MiniAppCommandsServiceWithPaginationSupport;
import superapp.logic.ObjectServiceWithPaginationSupport;
import superapp.logic.UsersServiceWithPaginationSupport;

@RestController
public class AdminController {

	private UsersServiceWithPaginationSupport usersService;
	private ObjectServiceWithPaginationSupport objectsService;
	private MiniAppCommandsServiceWithPaginationSupport miniAppCommandsService;

	@Autowired
	public void setAdminService(UsersServiceWithPaginationSupport usersService,ObjectServiceWithPaginationSupport objectsService,
			MiniAppCommandsServiceWithPaginationSupport miniAppCommandsService) {
		this.usersService = usersService;
		this.miniAppCommandsService = miniAppCommandsService;
		this.objectsService = objectsService;

	}



	/**
	 * Export all MiniApps Commands history. Receives HTTP Method 'GET'.
	 * 
	 * @param None
	 * @return Array of all MiniApp Command Boundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public MiniAppCommandBoundary[] getAllMiniAppCommandsHistory(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		List<MiniAppCommandBoundary> allBoundaries = this.miniAppCommandsService.getAllCommands(userSuperapp, userEmail, size, page);
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

	public MiniAppCommandBoundary[] specificMiniAppCommandBoundaries(
			@PathVariable("miniAppName") String miniAppName,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page){

		List<MiniAppCommandBoundary> allBoundaries = this.miniAppCommandsService.getAllMiniAppCommands(miniAppName, userSuperapp, userEmail, size, page);
		return allBoundaries.toArray(new MiniAppCommandBoundary[0]);

	}

	/**
	 * Deletes all commands history in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */

	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.DELETE })
	public void deleteAllCommandsHistory(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		this.miniAppCommandsService.deleteAllCommands(userSuperapp, userEmail);
	}

	/**
	 * Delete all users in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */

	@RequestMapping(path = { "/superapp/admin/users" }, method = { RequestMethod.DELETE })
	public void deleteAllUsers(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		this.usersService.deleteAllUsers(userSuperapp, userEmail);
	}

	/**
	 * Export all existing users. Receives HTTP Method 'GET'.
	 * 
	 * @param None
	 * @return Array of all UserBoundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/users" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary[] getAllUsers(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		List<UserBoundary> allBoundaries = this.usersService.getAllUsers(userSuperapp, userEmail, size, page);
		return allBoundaries.toArray(new UserBoundary[0]);
	}

	/**
	 * Delete all objects in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */
	@RequestMapping(path = { "/superapp/admin/objects" }, method = { RequestMethod.DELETE })
	public void deleteAllObjectsInTheSuperApp(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		this.objectsService.deleteAllObjects(userSuperapp, userEmail);
	}

}
