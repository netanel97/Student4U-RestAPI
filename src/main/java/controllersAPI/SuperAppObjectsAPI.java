package controllersAPI;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.PathVariable;

import entities.ObjectBoundary;

public interface SuperAppObjectsAPI {

	
	ObjectBoundary retrieveObject(@PathVariable("superapp") String superapp, 
			@PathVariable("InternalObjectId") String internalObjectId);
	
	ArrayList<ObjectBoundary> getAllObjects();
}
