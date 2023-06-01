package superapp.logic;

import java.util.List;

import superapp.boundaries.command.MiniAppCommandBoundary;

	public interface MiniAppCommandsServiceWithPaginationSupport extends MiniAppCommandsService{
	
	public Object invokeCommand(MiniAppCommandBoundary command, String miniAppName, Boolean async);
	
	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page);
	
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail, int size, int page);
	
	public void deleteAllCommands(String userSuperapp, String userEmail);

	public void listenToCommandQueue(String json);

	public MiniAppCommandBoundary aSyncHandleCommand(MiniAppCommandBoundary command);
	
	
}
