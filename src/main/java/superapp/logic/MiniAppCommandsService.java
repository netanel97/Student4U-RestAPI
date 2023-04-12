package superapp.logic;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import superapp.entities.MiniAppCommandBoundary;

public interface MiniAppCommandsService {
	Object invokeMiniAppCommand(@RequestBody MiniAppCommandBoundary miniAppCommandBoundary,
			@PathVariable("miniAppName") String miniAppName);

	public MiniAppCommandBoundary[] getAllCommands();
	
	public MiniAppCommandBoundary[] getAllMiniAppCommands(@PathVariable("miniAppName") String miniAppName);
	
	public void deleteAllObjects();
	
}
