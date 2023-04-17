package superapp.logic;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import superapp.entities.MiniAppCommandBoundary;

public interface MiniAppCommandsService {
	Object invokeCommand(MiniAppCommandBoundary miniAppCommandBoundary, String miniAppName);

	public List<MiniAppCommandBoundary> getAllCommands();
	
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
	
	public void deleteAllCommands();
	
}
