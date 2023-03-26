package controllersAPI;

import org.springframework.web.bind.annotation.PathVariable;

import entities.MiniAppCommandBoundary;

public interface MiniAppCommandAPI {
	Object invokeMiniAppCommand(MiniAppCommandBoundary miniAppCommandBoundary, @PathVariable("miniAppName") String miniAppName);
}
