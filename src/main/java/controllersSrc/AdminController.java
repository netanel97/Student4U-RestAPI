package controllersSrc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	public ArrayList<MiniAppCommandBoundary> allMiniAppCommandBoundaries() {
		CommandId commandId = new CommandId("miniapp", "122");
		TargetObject targetObject = new TargetObject(new ObjectId("1"));
		InvokedBy invokedBy = new InvokedBy(new UserID("jane@demo.org"));
		CommandAttributes commandAttributes = new CommandAttributes(new HashMap<String, String>());
		commandAttributes.getKey1().put("key1subkey1", "anything");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd.HH:mm:ss");
		MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary(commandId, "doSomething",
				targetObject, invokedBy, commandAttributes);
		ArrayList<MiniAppCommandBoundary> allMiniAppCommandBoundaries = new ArrayList<>();
		allMiniAppCommandBoundaries.add(miniAppCommandBoundary);
		System.out.println("kofim shmenim");
		return allMiniAppCommandBoundaries;
	}

	/**
	 * Export Commands history of a specific MiniApp. Receives HTTP Method 'GET'.
	 * 
	 * @return Array of a specific MiniApp Command Boundaries.
	 */
	@RequestMapping(path = { "/superapp/admin/miniapp/{miniAppName}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public ArrayList<MiniAppCommandBoundary> specificMiniAppCommandBoundaries(
			@PathVariable("miniAppName") String miniAppName) {

		CommandId commandId = new CommandId(miniAppName.toString(), "122");
		TargetObject targetObject = new TargetObject(new ObjectId("1"));
		InvokedBy invokedBy = new InvokedBy(new UserID("jane@demo.org"));
		CommandAttributes commandAttributes = new CommandAttributes(new HashMap<String, String>());
		commandAttributes.getKey1().put("key1subkey1", "anything");
		MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary(commandId, "doSomething",
				targetObject, invokedBy, commandAttributes);
		ArrayList<MiniAppCommandBoundary> specificMiniAppCommandBoundaries = new ArrayList<>();
		specificMiniAppCommandBoundaries.add(miniAppCommandBoundary);
		return specificMiniAppCommandBoundaries;
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
