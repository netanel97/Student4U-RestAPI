package superapp.controllersSrc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.SuperAppObjectIdBoundary;
import superapp.logic.DataManagerWithRelationsSupport;

@RestController
public class NewSuperAppObjectController {
	private DataManagerWithRelationsSupport objects;

	@Autowired
	public NewSuperAppObjectController(DataManagerWithRelationsSupport objects) {
		super();
		this.objects = objects;
	}
	
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}/children"},
			method = {RequestMethod.PUT},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void bindObject(
			@PathVariable("superapp")String superapp , 
			@PathVariable("internalObjectId") String internalObjectId,
			@RequestBody SuperAppObjectIdBoundary child){
	
		this.objects.BindAnExistingObjectToExistingChildObject(superapp, internalObjectId, child);
		
	}
	
	@RequestMapping(method = {RequestMethod.GET},
			path ={"/superapp/objects/{superapp}/{internalObjectId}/children"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getAllChildrenOfAnExistingObject (
			@PathVariable("superapp") String superapp,@PathVariable("internalObjectId") String internalObjectId){
		
		return this.objects
			.getAllChildrenOfAnExistingObject(superapp, internalObjectId)
			.toArray(new SuperAppObjectBoundary[0]);
	}
	
	
	@RequestMapping(method = {RequestMethod.GET},
			path ={"/superapp/objects/{superapp}/{internalObjectId}/parents"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[]  getAnArrayWithObjectParent (
			@PathVariable("superapp") String superapp,@PathVariable("internalObjectId") String internalObjectId){
		
		return this.objects
			.getAnArrayWithObjectParent(superapp, internalObjectId)
			.toArray(new SuperAppObjectBoundary[0]);
	}
}
