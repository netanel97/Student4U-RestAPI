package superapp.logic;

import java.util.List;
import java.util.Optional;

import superapp.entities.SuperAppObjectBoundary;

public interface ObjectsService {

	public SuperAppObjectBoundary createObject(SuperAppObjectBoundary newObjectBoundary);

	public SuperAppObjectBoundary updateAnObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update);

	public Optional<SuperAppObjectBoundary> getSpecificObject(String objectSuperApp, String internalObjectId);

	public List<SuperAppObjectBoundary> getAllObjects();

	public void deleteAllObjects();

}
