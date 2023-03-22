package controllersSrc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

@RestController
public class AdminController implements AdminAPI {

	@Override
	public int exportAllUsers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@RequestMapping(path = { "/superapp/admin/miniapp" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public ArrayList<MiniAppCommandBoundary> allMiniAppCommandBoundaries() {
		CommandId commandId = new CommandId("supperapp", "miniapp", "122");
		TargetObject targetObject = new TargetObject(new ObjectId("superapp", "1"));
		InvokedBy invokedBy = new InvokedBy(new UserID("supperapp", "jane@demo.org"));
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

	@RequestMapping(path = { "/superapp/admin/miniapp/{miniAppName}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@Override
	public ArrayList<MiniAppCommandBoundary> specificMiniAppCommandBoundaries(
			@PathVariable("miniAppName") String miniAppName) {

		CommandId commandId = new CommandId("supperapp", miniAppName.toString(), "122");
		TargetObject targetObject = new TargetObject(new ObjectId("superapp", "1"));
		InvokedBy invokedBy = new InvokedBy(new UserID("supperapp", "jane@demo.org"));
		CommandAttributes commandAttributes = new CommandAttributes(new HashMap<String, String>());
		commandAttributes.getKey1().put("key1subkey1", "anything");
		MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary(commandId, "doSomething",
				targetObject, invokedBy, commandAttributes);
		ArrayList<MiniAppCommandBoundary> specificMiniAppCommandBoundaries = new ArrayList<>();
		specificMiniAppCommandBoundaries.add(miniAppCommandBoundary);
		return specificMiniAppCommandBoundaries;
	}
}
