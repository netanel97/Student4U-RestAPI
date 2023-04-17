package superapp.logic.mockup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import superapp.data.UserEntity;
import superapp.entities.MiniAppCommandBoundary;
import superapp.logic.MiniAppCommandsService;

public class MiniAppCommandsServiceMockup implements MiniAppCommandsService{
	private Map<String, UserEntity> databaseMockup;
	private String springApplicationName;
	private String DELIMITER = "_";
	
	/**
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.LiranSorokin}")
	public void setSpringApplicationName(String springApllicationName) {
		this.springApplicationName = springApllicationName;
	}

	/**
	 * this method is invoked after values are injected to instance
	 */
	@PostConstruct
	public void init() {
		// create a thread safe map
		this.databaseMockup = Collections.synchronizedMap(new HashMap<>());
		System.err.println("******** " + this.springApplicationName);
	}
	
	
	@Override
	public Object invokeCommand(MiniAppCommandBoundary miniAppCommandBoundary, String miniAppName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<MiniAppCommandBoundary> getAllCommands() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Delete all users from DB
	 * 
	 */
	@Override
	public void deleteAllCommands() {
		this.databaseMockup.clear();

		
	}
	
	
}
