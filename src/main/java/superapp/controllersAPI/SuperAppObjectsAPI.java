package superapp.controllersAPI;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import superapp.entities.SuperAppObjectBoundary;

public interface SuperAppObjectsAPI {

	SuperAppObjectBoundary retrieveObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId);

	SuperAppObjectBoundary[] getAllObjects();

	void updateAnObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestBody SuperAppObjectBoundary updateBoundary);

//	
	SuperAppObjectBoundary createObject(@RequestBody SuperAppObjectBoundary newObjectBoundary);

}
