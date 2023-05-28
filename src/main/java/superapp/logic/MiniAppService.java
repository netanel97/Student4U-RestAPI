package superapp.logic;

import superapp.boundaries.command.MiniAppCommandBoundary;

public interface MiniAppService {
	
    public Object runCommand(MiniAppCommandBoundary command);
}
