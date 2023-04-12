package superapp.logic;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import superapp.entities.ObjectBoundary;

public interface ObjectsService {

	public ObjectBoundary createObject(@RequestBody ObjectBoundary newObjectBoundary);
	
	public void updateAnObject(@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestBody ObjectBoundary updateBoundary);
	
	public ObjectBoundary getSpecificObject(@PathVariable("superapp") String superapp, 
			@PathVariable("InternalObjectId") String internalObjectId);
	
	public ObjectBoundary[] getAllObjects();
	
	public void deleteAllObjects();
	
}
