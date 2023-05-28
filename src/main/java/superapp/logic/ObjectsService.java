package superapp.logic;

import java.util.List;
import java.util.Optional;

import superapp.boundaries.object.SuperAppObjectBoundary;

public interface ObjectsService {

	public SuperAppObjectBoundary createObject(SuperAppObjectBoundary object);

	@Deprecated
	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update);
	@Deprecated
	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId);

	@Deprecated
	public List<SuperAppObjectBoundary> getAllObjects();

	@Deprecated
	public void deleteAllObjects();

}
