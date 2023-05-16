package superapp.controllersSrc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.SuperAppObjectIdBoundary;
import superapp.logic.ObjectServiceWithPagainationSupport;

@RestController
public class NewSuperAppObjectController {
	private ObjectServiceWithPagainationSupport objects;

	@Autowired
	public NewSuperAppObjectController(ObjectServiceWithPagainationSupport objects) {
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
			@RequestBody SuperAppObjectIdBoundary child,
			@RequestParam(name = "userSuperapp", required = false) String userSuperapp,
			@RequestParam(name = "userEmail", required = false) String userEmail){
	
		this.objects.BindAnExistingObjectToExistingChildObject(superapp, internalObjectId, child,userSuperapp,userEmail);
		
	}
	
	@RequestMapping(method = {RequestMethod.GET},
			path ={"/superapp/objects/{superapp}/{internalObjectId}/children"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getAllChildrenOfAnExistingObject (
			@PathVariable("superapp") String superapp,@PathVariable("internalObjectId") String internalObjectId,
			@RequestParam(name = "userSuperapp", required = false) String userSuperapp,
			@RequestParam(name = "userEmail", required = false) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page){
		
		return this.objects
			.getAllChildrenOfAnExistingObject(superapp, internalObjectId,userSuperapp,userEmail,size,page)
			.toArray(new SuperAppObjectBoundary[0]);
	}
	
	
	@RequestMapping(method = {RequestMethod.GET},
			path ={"/superapp/objects/{superapp}/{internalObjectId}/parents"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[]  getAnArrayWithObjectParent (
			@PathVariable("superapp") String superapp,@PathVariable("internalObjectId") String internalObjectId,
			@RequestParam(name = "userSuperapp", required = false) String userSuperapp,
			@RequestParam(name = "userEmail", required = false) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page){
		
		return this.objects
			.getAnArrayWithObjectParent(superapp, internalObjectId,userSuperapp,userEmail,size,page)
			.toArray(new SuperAppObjectBoundary[0]);
	}
}
