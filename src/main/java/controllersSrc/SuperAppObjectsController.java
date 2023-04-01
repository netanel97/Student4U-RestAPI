package controllersSrc;

import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

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

	/**
	 * This code defines a method that handles a GET request for retrieving a
	 * specific object from a "superapp" application. The method is annotated with
	 * the "@RequestMapping" annotation, which maps the method to the path
	 * "/superapp/objects/{superapp}/{InternalObjectId}", specifies the HTTP method
	 * as GET, and the response media type as JSON. The path contains two path
	 * variables: "superapp" and "InternalObjectId", which are used to identify the
	 * specific object to retrieve.
	 * 
	 * @param @PathVariable("superapp") String superapp
	 * @param @PathVariable("InternalObjectId") String internalObjectId
	 * @return ObjectBoundary.
	 */
	@RequestMapping(path = { "/superapp/objects/{superapp}/{InternalObjectId}" }, method = {
			RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@Override
	public ObjectBoundary retrieveObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId) {
		ObjectId objectId = new ObjectId(internalObjectId.toString());
		Location location = new Location(32.1133, 34.818);
		CreatedBy createdBy = new CreatedBy(new UserID("netanelhabas@gmail.com"));
		ObjectDetails objectDetails = new ObjectDetails(new HashMap<String, Object>());
		objectDetails.getObjectDeatils().put("key1", "bdika");
		ObjectBoundary objectBoundary = new ObjectBoundary(objectId, "type", "alias", true, location, createdBy,
				objectDetails);

		return objectBoundary;
	}

	/**
	 * This code defines a method that handles a GET request for retrieving all
	 * objects from a "superapp" application. The method is annotated with the
	 * "@RequestMapping" annotation, which maps the method to the path
	 * "/superapp/objects", specifies the HTTP method as GET, and the response media
	 * type as JSON.
	 * @param None
	 * @return Array of ObjectBoundary.
	 */

	@RequestMapping(path = { "/superapp/objects" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	@Override
	public ObjectBoundary[] getAllObjects() {

		return IntStream.range(0, 3)
				.mapToObj(i -> new ObjectBoundary(new ObjectId("" + i), "TYPE", "ALIAS", true, new Location(30.1, 30.2),
						new CreatedBy(new UserID("netanelhabas@gmail.com")),
						new ObjectDetails(new HashMap<String, Object>())))
				.toArray(ObjectBoundary[]::new);

	}
	
	/**
	A method that handles a PUT request for updating an object in a "superapp" application.
	@param superapp: a string representing the name of the "superapp" application.
	@param internalObjectId: a string representing the ID of the object to be updated in the "superapp".
	@param updateBoundary: an ObjectBoundary object representing the updated version of the object.
	*/

	@RequestMapping(path = { "/superapp/objects/{superapp}/{InternalObjectId}" }, method = {
			RequestMethod.PUT }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Override
	public void updateAnObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId, @RequestBody ObjectBoundary updateBoundary) {


		System.err.println("bdika: " + updateBoundary.toString());
	}

	/**

	A method that handles a POST request for creating a new object in a "superapp" application.

	@param newObjectBoundary: an ObjectBoundary object representing the new object to be created in the "superapp".
	@return the created ObjectBoundary object as a response in JSON format.
	*/
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
