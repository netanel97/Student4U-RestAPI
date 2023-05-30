package superapp.logic;

import java.util.List;
import java.util.Optional;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;

public interface ObjectServiceWithPaginationSupport extends ObjectsService {

	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update, String userSuperapp, String userEmail);

	public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page);

	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId,
			String userSuperapp, String userEmail);

	public void BindAnExistingObjectToExistingChildObject(String superapp, String internalObjectId,
			SuperAppObjectIdBoundary child, String userSuperapp, String userEmail);

	public List<SuperAppObjectBoundary> getAllChildrenOfAnExistingObject(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page);

	public List<SuperAppObjectBoundary> getAnArrayWithObjectParent(String superapp, String internalObjectId,
			String userSuperapp, String userEmail, int size, int page);

	public List<SuperAppObjectBoundary> searchObjectsByAlias(String superapp, String email, String alias, int size, int page);

	public List<SuperAppObjectBoundary> searchObjectsByLocation(String superapp, String email, double lat, double lng,
			double distance, String units, int size, int page);

	public List<SuperAppObjectBoundary> searchObjectsByType(String superapp, String email, String type, int size, int page);

	public void deleteAllObjects(String userSuperapp, String userEmail);

//	/**
//	 * Search DB for objects in a square area around the point.
//	 * 
//	 * @param superapp superapp name
//	 * @param email    user email
//	 * @param lat      latitude
//	 * @param lng      longitude
//	 * @param distance distance
//	 * @param units    distance units
//	 * @param size     how many items in page
//	 * @param page     current page
//	 * @return List of SuperAppObjectBoundary all objects matching criteria
//	 */
//	List<SuperAppObjectBoundary> searchObjectsByLocationCircle(String superapp, String email, double lat, double lng,
//			double distance, String units, int size, int page);
}
