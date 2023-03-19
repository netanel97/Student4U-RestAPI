package controllersSrc;

import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import controllersAPI.SuperAppObjectsAPI;
import entities.CreatedBy;
import entities.Location;
import entities.ObjectBoundary;
import entities.ObjectDetails;
import entities.ObjectId;
import entities.UserID;

@RestController
public class SuperAppObjectsController implements SuperAppObjectsAPI {

	@RequestMapping(path = { "/superapp/objects/{superapp}/{InternalObjectId}" }, method = {
			RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@Override
	public ObjectBoundary retrieveObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId) {
		ObjectId objectId = new ObjectId("superapp_test", "1");
		Location location = new Location(30.1, 30.2);
		CreatedBy createdBy = new CreatedBy(new UserID("superapp_test", "netanelhabas@gmail.com"));
		ObjectDetails objectDetails = new ObjectDetails("bdika1", "bdika2");
		ObjectBoundary objectBoundary = new ObjectBoundary(objectId, "type", "alias", true, "timestamp", location,
				createdBy, objectDetails);

		return objectBoundary;
	}

	@RequestMapping(path = { "/superapp/objects" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	@Override
	public ArrayList<ObjectBoundary> getAllObjects() {

		ObjectId objectId = new ObjectId("superapp_test", "1");
		ObjectId objectId2 = new ObjectId("superapp_test2", "2");

		Location location = new Location(30.1, 30.2);
		CreatedBy createdBy = new CreatedBy(new UserID("superapp_test", "netanelhabas@gmail.com"));
		ObjectDetails objectDetails = new ObjectDetails("bdika1", "bdika2");
		ObjectBoundary objectBoundary = new ObjectBoundary(objectId, "type", "alias", true, "timestamp", location,
				createdBy, objectDetails);

		ObjectBoundary objectBoundary2 = new ObjectBoundary(objectId2, "type", "alias", true, "timestamp", location,
				createdBy, objectDetails);

		ArrayList<ObjectBoundary> allObjectsBoundary = new ArrayList<>();
		allObjectsBoundary.add(objectBoundary);
		allObjectsBoundary.add(objectBoundary2);
		return allObjectsBoundary;
	}

}
