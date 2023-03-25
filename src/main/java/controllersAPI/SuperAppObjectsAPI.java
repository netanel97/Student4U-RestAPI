package controllersAPI;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.PathVariable;

import entities.ObjectBoundary;
import org.springframework.web.bind.annotation.RequestBody;


public interface SuperAppObjectsAPI {

	
	ObjectBoundary retrieveObject(@PathVariable("superapp") String superapp, 
			@PathVariable("InternalObjectId") String internalObjectId);
	
	ArrayList<ObjectBoundary> getAllObjects();
	
	void updateAnObject(@PathVariable("superapp") String superapp, @PathVariable("InternalObjectId") String internalObjectId
			,@RequestBody ObjectBoundary updateBoundary);
//	
	ObjectBoundary createObject(@RequestBody ObjectBoundary newObjectBoundary);
	
}
