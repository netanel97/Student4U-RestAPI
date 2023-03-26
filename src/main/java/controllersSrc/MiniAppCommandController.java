package controllersSrc;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import controllersAPI.MiniAppCommandAPI;
import entities.MiniAppCommandBoundary;
import entities.ObjectBoundary;
import entities.ObjectId;

public class MiniAppCommandController implements MiniAppCommandAPI {
	
	@RequestMapping(path = { "/superapp/miniapp/{miniAppName}" }, method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, // returns a new JSON
			consumes = { MediaType.APPLICATION_JSON_VALUE }) // takes a JSON as argument
	@Override
	public Object invokeMiniAppCommand(MiniAppCommandBoundary miniAppCommandBoundary,  @PathVariable("miniAppName") String miniAppName) {
		// TODO Auto-generated method stub
		Object miniAppCommand = new Object();
		// miniAppCommand.setObjectId(new ObjectId());
		System.err.println("Invoked a mini app command!\n" + miniAppCommand.toString());
		return miniAppCommand;
	}
	
//	@Override
//	public Object invokeMiniAppCommand(ObjectBoundary newObjectBoundary) {
//		ObjectBoundary created = new ObjectBoundary();
//		created.setObjectId(new ObjectId());
//		System.err.println("CREATED A NEW Boundary!\n" + created.toString());
//		return created;
//	}


}
