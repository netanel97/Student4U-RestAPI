package superapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.logic.MiniAppCommandsServiceWithPaginationSupport;

/**
 * The MiniAppCommandController class offers a means to execute commands on a
 * particular mini app within the SuperApp system by providing an endpoint. To
 * utilize this class, one must create an instance of it. The desired endpoint
 * should be mapped to the invokeMiniAppCommand() method. The
 * invokeMiniAppCommand() function will handle the incoming HTTP POST requests
 * and provide the corresponding response.
 */

@RestController
public class MiniAppCommandController {
	private MiniAppCommandsServiceWithPaginationSupport miniAppCommandsService;

	@Autowired
	public void setMiniAppCommandsService(MiniAppCommandsServiceWithPaginationSupport miniAppCommandsService) {
		this.miniAppCommandsService = miniAppCommandsService;
	}



	/**
	 * This function processes an HTTP POST request to execute a command for a
	 * specific mini app. It requires two parameters to be passed: the mini app's
	 * name for which the command is to be executed, and the command itself. The
	 * function returns the command object that was initially provided as a
	 * parameter.
	 *
	 * @param @RequestBody                 MiniAppCommandBoundary
	 *                                     miniAppCommandBoundary
	 * @param @PathVariable("miniAppName") String miniAppName
	 * @return Object
	 */
	@RequestMapping(path = { "/superapp/miniapp/{miniAppName}" }, method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, // returns a new JSON
			consumes = { MediaType.APPLICATION_JSON_VALUE }) // takes a JSON as argument
	public Object invokeMiniAppCommand(@RequestBody MiniAppCommandBoundary miniAppCommandBoundary,
			@PathVariable("miniAppName") String miniAppName,
			@RequestParam(name = "async", required = false, defaultValue = "false") Boolean asyncFlag) {

		return miniAppCommandsService.invokeCommand(miniAppCommandBoundary, miniAppName, asyncFlag);

	}

}
