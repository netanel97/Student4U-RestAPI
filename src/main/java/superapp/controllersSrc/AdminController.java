package superapp.controllersSrc;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import superapp.entities.CommandAttributes;
import superapp.entities.CommandId;
import superapp.entities.InvokedBy;
import superapp.entities.MiniAppCommandBoundary;
import superapp.entities.ObjectId;
import superapp.entities.TargetObject;
import superapp.entities.UserBoundary;
import superapp.entities.UserId;
import superapp.logic.ObjectsService;
import superapp.logic.UsersService;

@RestController
public class AdminController {

	private UsersService usersService;
	private ObjectsService objectsService;

	@Autowired
	public void setUsersService(UsersService usersService) {
		this.usersService = usersService;
	}

	@Autowired
	public void setObjectsService(ObjectsService objectsService) {
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

	public MiniAppCommandBoundary[] allMiniAppCommandBoundaries() {

		return IntStream.range(0, 3)
				.mapToObj(i -> new MiniAppCommandBoundary(new CommandId("miniapp", "122"), "doSomething" + i,
						new TargetObject(new ObjectId("1")), new InvokedBy(new UserId("jane@demo.org")),
						new CommandAttributes(new HashMap<String, String>())))
				.toArray(MiniAppCommandBoundary[]::new);
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

		return IntStream.range(0, 2)
				.mapToObj(
						i -> new MiniAppCommandBoundary(new CommandId(miniAppName.toString(), "122"), "doSomething" + i,
								new TargetObject(new ObjectId("" + i)), new InvokedBy(new UserId("jane@demo.org")),
								new CommandAttributes(new HashMap<String, String>())))
				.toArray(MiniAppCommandBoundary[]::new);
	}

	/**
	 * Delete all commands history. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.DELETE })

	public void deleteAllCommandsHistory() {
		System.err.println("Command History Deleted!");
	}

	// @@@!@!@!@!@!@!@!@!@ new functions sprint 3 do not
	// delete!@!!@!@!@!@!@!@!@!@!@!

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
