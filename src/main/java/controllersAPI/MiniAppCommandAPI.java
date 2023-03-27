package controllersAPI;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import entities.MiniAppCommandBoundary;

public interface MiniAppCommandAPI {
	Object invokeMiniAppCommand(@RequestBody MiniAppCommandBoundary miniAppCommandBoundary, @PathVariable("miniAppName") String miniAppName);
}

//test