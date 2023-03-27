package controllersAPI;

import org.springframework.web.bind.annotation.PathVariable;

import entities.MiniAppCommandBoundary;

public interface MiniAppCommandAPI {
	MiniAppCommandBoundary invokeMiniAppCommand(MiniAppCommandBoundary miniAppCommandBoundary, @PathVariable("miniAppName") String miniAppName);
}
