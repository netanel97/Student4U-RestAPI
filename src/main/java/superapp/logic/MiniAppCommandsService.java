package superapp.logic;

import java.util.List;
import superapp.entities.MiniAppCommandBoundary;

public interface MiniAppCommandsService {
	Object invokeCommand(MiniAppCommandBoundary miniAppCommandBoundary, String miniAppName);

	public List<MiniAppCommandBoundary> getAllCommands();
	
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
	
	public void deleteAllCommands();
	
}
