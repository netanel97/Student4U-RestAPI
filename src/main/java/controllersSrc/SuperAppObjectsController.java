package controllersSrc;

import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
		ObjectId objectId = new ObjectId(superapp.toString(), internalObjectId.toString());
		Location location = new Location(32.1133, 34.818);
		CreatedBy createdBy = new CreatedBy(new UserID(superapp.toString(), "netanelhabas@gmail.com"));
		ObjectDetails objectDetails = new ObjectDetails(new HashMap<String,Object>());
		objectDetails.getObjectDeatils().put("key1","bdika");
		ObjectBoundary objectBoundary = new ObjectBoundary(objectId, "type", "alias", true, location,
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
		ObjectDetails objectDetails = new ObjectDetails(new HashMap<String,Object>());
		objectDetails.getObjectDeatils().put("key1","bdika");
		ObjectBoundary objectBoundary = new ObjectBoundary(objectId, "type", "alias", true, location,
				createdBy, objectDetails);

		ObjectBoundary objectBoundary2 = new ObjectBoundary(objectId2, "type", "alias", true, location,
				createdBy, objectDetails);

		ArrayList<ObjectBoundary> allObjectsBoundary = new ArrayList<>();
		allObjectsBoundary.add(objectBoundary);
		allObjectsBoundary.add(objectBoundary2);
		return allObjectsBoundary;
	}

	@RequestMapping(path = { "/superapp/objects/{superapp}/{InternalObjectId}" }
			, method = { RequestMethod.PUT }, 
			consumes = {MediaType.APPLICATION_JSON_VALUE })
	@Override
	public void updateAnObject(@PathVariable("superapp") String superapp, @PathVariable("InternalObjectId") String internalObjectId
			,@RequestBody ObjectBoundary updateBoundary) {
		
//		updateBoundary.setObjectId(new ObjectId());
//		updateBoundary.getObjectId().setSuperapp(superapp.toString());
//		updateBoundary.getObjectId().setInternalObjectId(internalObjectId.toString());
		System.err.println("bdika: "+updateBoundary.toString());
	}
//	
	

	@RequestMapping(path = { "/superapp/objects" }, method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, // returns a new JSON
			consumes = { MediaType.APPLICATION_JSON_VALUE }) // takes a JSON as argument
	@Override
	public ObjectBoundary createObject(ObjectBoundary newObjectBoundary) {
		ObjectBoundary created = new ObjectBoundary();
		created.setObjectId(new ObjectId());
		System.err.println("CREATED A NEW Boundary!\n" + created.toString());
		return created;
	}

}
