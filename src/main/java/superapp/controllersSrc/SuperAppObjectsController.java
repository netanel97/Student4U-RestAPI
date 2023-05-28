package superapp.controllersSrc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.logic.ObjectServiceWithPaginationSupport;

@RestController
public class SuperAppObjectsController {

	private ObjectServiceWithPaginationSupport objectsService;

	@Autowired
	private void setObjectsService(ObjectServiceWithPaginationSupport objectsService) {
		this.objectsService = objectsService;
	}

	/**
	 * A method that handles a POST request for creating a new object in a
	 * "superapp" application.
	 * 
	 * @param newObjectBoundary: an ObjectBoundary object representing the new
	 *                           object to be created in the "superapp".
	 * @return the created ObjectBoundary object as a response in JSON format.
	 */
	@RequestMapping(path = { "/superapp/objects" }, method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, // returns a new JSON
			consumes = { MediaType.APPLICATION_JSON_VALUE }) // takes a JSON as argument
	public SuperAppObjectBoundary createAnObject(@RequestBody SuperAppObjectBoundary object) {
		return this.objectsService.createObject(object);
	}

	/**
	 * A method that handles a PUT request for updating an object in a "super app"
	 * application.
	 * 
	 * @param superapp:         a string representing the name of the "super app"
	 *                          application.
	 * @param internalObjectId: a string representing the ID of the object to be
	 *                          updated in the "superapp".
	 * @param updateBoundary:   an ObjectBoundary object representing the updated
	 *                          version of the object.
	 */
	@RequestMapping(path = { "/superapp/objects/{superapp}/{InternalObjectId}" }, method = {
			RequestMethod.PUT }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void updateAnObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestBody SuperAppObjectBoundary updateBoundary,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		this.objectsService.updateAnObject(superapp, internalObjectId, updateBoundary, userSuperapp, userEmail);
//		this.objectsService.updateAnObject(superapp, internalObjectId, updateBoundary);
	}

	/**
	 * This code defines a method that handles a GET request for retrieving a
	 * specific object from a "superapp" application. The method is annotated with
	 * the "@RequestMapping" annotation, which maps the method to the path
	 * "/superapp/objects/{superapp}/{InternalObjectId}", specifies the HTTP method
	 * as GET, and the response media type as JSON. The path contains two path
	 * variables: "superapp" and "InternalObjectId", which are used to identify the
	 * specific object to retrieve.
	 * 
	 * @param @PathVariable("superapp")         String superapp
	 * @param @PathVariable("InternalObjectId") String internalObjectId
	 * @return ObjectBoundary.
	 */
	// TODO: need to check if required is true/false
	@RequestMapping(path = { "/superapp/objects/{superapp}/{InternalObjectId}" }, method = {
			RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public SuperAppObjectBoundary retrieveObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		return this.objectsService.getSpecificObject(superapp, internalObjectId, userSuperapp, userEmail)
				.orElseThrow(() -> new RuntimeException("Could not find object by id: " + internalObjectId));
	}

	/**
	 * This code defines a method that handles a GET request for retrieving all
	 * objects from a "superapp" application. The method is annotated with the
	 * "@RequestMapping" annotation, which maps the method to the path
	 * "/superapp/objects", specifies the HTTP method as GET, and the response media
	 * type as JSON.
	 * 
	 * @param None
	 * @return Array of ObjectBoundary.
	 */
	@RequestMapping(path = { "/superapp/objects" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public SuperAppObjectBoundary[] getAllObjects(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		// List<SuperAppObjectBoundary> allBoundaries =
		// this.objectsService.getAllObjects();
		List<SuperAppObjectBoundary> allBoundaries = this.objectsService.getAllObjects(userSuperapp, userEmail, size,
				page);

		return allBoundaries.toArray(new SuperAppObjectBoundary[0]);
	}

	/**
	 * Search for objects in DB by their type
	 * 
	 * @param type         object type
	 * @param userSuperapp user's superapp name
	 * @param userEmail    user's email
	 * @param size         how many items in page
	 * @param page         current page
	 * @return Array of SuperAppObjectBoundary all objects matching criteria
	 */
	@RequestMapping(path = { "/superapp/objects/search/byType/{type}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public SuperAppObjectBoundary[] searchObjectsByType(@PathVariable("type") String type,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		List<SuperAppObjectBoundary> allTypeBoundaries = this.objectsService.searchObjectsByType(userSuperapp,
				userEmail, type, size, page);

		return allTypeBoundaries.toArray(new SuperAppObjectBoundary[0]);
	}

	/**
	 * Search for objects in DB by their alias.
	 * 
	 * @param alias        object alias
	 * @param userSuperapp user's superapp name
	 * @param userEmail    user's email
	 * @param size         how many items in page
	 * @param page         current page
	 * @return Array of SuperAppObjectBoundary all objects matching criteria
	 */
	@RequestMapping(path = { "/superapp/objects/search/byAlias/{alias}" }, method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public SuperAppObjectBoundary[] searchObjectsByAlias(@PathVariable("alias") String alias,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		List<SuperAppObjectBoundary> allAliasBoundaries = this.objectsService.searchObjectsByAlias(userSuperapp,
				userEmail, alias, size, page);

		return allAliasBoundaries.toArray(new SuperAppObjectBoundary[0]);
	}

	/**
	 * Search DB for objects in a square area around the point.
	 * 
	 * @param lat          latitude
	 * @param lng          longitude
	 * @param distance     distance
	 * @param units        distance units
	 * @param userSuperapp superapp name
	 * @param userEmail    user email
	 * @param size         how many items in page
	 * @param page         current page
	 * @return Array of SuperAppObjectBoundary all objects matching criteria
	 */
	@RequestMapping(path = { "/superapp/objects/search/byLocation/{lat}/{lng}/{distance}" }, method = {
			RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public SuperAppObjectBoundary[] searchObjectsByLocation(@PathVariable("lat") Double lat,
			@PathVariable("lng") Double lng, @PathVariable("distance") Double distance,
			@RequestParam(name = "units", required = false, defaultValue = "NEUTRAL") String units,
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		List<SuperAppObjectBoundary> allLocationBoundaries = this.objectsService.searchObjectsByLocation(userSuperapp,
				userEmail, lat, lng, distance, units, size, page);

		return allLocationBoundaries.toArray(new SuperAppObjectBoundary[0]);
	}

}
