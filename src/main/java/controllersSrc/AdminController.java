package controllersSrc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import controllersAPI.AdminAPI;
import entities.CommandAttributes;
import entities.CommandId;
import entities.InvokedBy;
import entities.MiniAppCommandBoundary;
import entities.ObjectId;
import entities.TargetObject;
import entities.UserBoundary;
import entities.UserID;
import entities.eUserRole;

@RestController
public class AdminController implements AdminAPI {
	/**
	 * Export all existing users. Receives HTTP Method 'GET'.
	 * 
	 * @return Array of all UserBoundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/users" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public UserBoundary[] exportAllUsers() {

		return IntStream.range(0, 2)
				.mapToObj(i -> new UserBoundary(new UserID("user" + i), eUserRole.STUDENT, "User " + i, ""))
				.toArray(UserBoundary[]::new);
	} 

	/**
	 * Export all MiniApps Commands history. Receives HTTP Method 'GET'.
	 * 
	 * @return Array of all MiniApp Command Boundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public MiniAppCommandBoundary[] allMiniAppCommandBoundaries() {
		
		return IntStream.range(0, 3)
				.mapToObj(i -> new MiniAppCommandBoundary(new CommandId("miniapp", "122"), "doSomething" + i, new TargetObject(new ObjectId("1")), new InvokedBy(new UserID("jane@demo.org")), new CommandAttributes(new HashMap<String, String>())))
				.toArray(MiniAppCommandBoundary[]::new);
	}

	/**
	 * Export Commands history of a specific MiniApp. Receives HTTP Method 'GET'.
	 * 
	 * @return Array of a specific MiniApp Command Boundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp/{miniAppName}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public MiniAppCommandBoundary[] specificMiniAppCommandBoundaries(
			@PathVariable("miniAppName") String miniAppName) {
		
		return IntStream.range(0, 2)
				.mapToObj(i -> new MiniAppCommandBoundary(new CommandId(miniAppName.toString(), "122"), "doSomething" + i, new TargetObject(new ObjectId(""+i)), new InvokedBy(new UserID("jane@demo.org")), new CommandAttributes(new HashMap<String, String>())))
				.toArray(MiniAppCommandBoundary[]::new);
	}

	/**
	 * Deletes all users in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */
	@RequestMapping(path = { "/superapp/admin/users" }, method = { RequestMethod.DELETE })
	@Override
	public void deleteAllUsersInTheSuperApp() {
		System.err.println("Users Deleted!");
	}

	/**
	 * Deletes all users in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @param None
	 * @return Nothing
	 */
	@RequestMapping(path = { "/superapp/admin/objects" }, method = { RequestMethod.DELETE })
	@Override
	public void deleteAllObjectsInTheSuperApp() {
		System.err.println("Objects Deleted!");
	}

	/**
	 * Deletes all users in the SuperApp. Receives HTTP Method 'DELETE'.
	 * 
	 * @paramNone
	 * @return Nothing
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.DELETE })
	@Override
	public void deleteAllCommandsHistory() {
		System.err.println("Command History Deleted!");
	}

}
