package superapp.controllersSrc;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import superapp.controllersAPI.MiniAppCommandAPI;
import superapp.entities.MiniAppCommandBoundary;
import superapp.entities.ObjectBoundary;
import superapp.entities.ObjectId;

	/**
	*The MiniAppCommandController class offers a means to execute commands on a particular mini app within the SuperApp system by providing an endpoint.
	*To utilize this class, one must create an instance of it.
	*The desired endpoint should be mapped to the invokeMiniAppCommand() method.
	*The invokeMiniAppCommand() function will handle the incoming HTTP POST requests and provide the corresponding response.
	*/

@RestController
public class MiniAppCommandController implements MiniAppCommandAPI {

	/**This function processes an HTTP POST request to execute a command for a specific mini app.
	*It requires two parameters to be passed: the mini app's name for which the command is to be executed, and the command itself.
	*The function returns the command object that was initially provided as a parameter.
	*
	* @param @RequestBody MiniAppCommandBoundary miniAppCommandBoundary 
	* @param @PathVariable("miniAppName") String miniAppName
	* @return Nothing
	*/
	
	@RequestMapping(path = { "/superapp/miniapp/{miniAppName}" }, method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, // returns a new JSON
			consumes = { MediaType.APPLICATION_JSON_VALUE }) // takes a JSON as argument
	@Override
	public Object invokeMiniAppCommand(@RequestBody MiniAppCommandBoundary miniAppCommandBoundary,  @PathVariable("miniAppName") String miniAppName) {
		// TODO Auto-generated method stub
		MiniAppCommandBoundary newMiniAppCommandBoundary = new MiniAppCommandBoundary();
		newMiniAppCommandBoundary.setCommand(miniAppName + "command");
		System.err.println("Invoked a mini app command!\n" + newMiniAppCommandBoundary.toString());
		return miniAppCommandBoundary;
	}
	

}