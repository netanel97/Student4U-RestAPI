package superapp.entities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ObjectId {

	@Value("${spring.application.name:2023b.LiranSorokin}")
	private String springApplicationName;
	
	private String internalObjectId;

	public ObjectId() {
		super();
	};

	public ObjectId(String internalObjectId) {
		super();
		this.internalObjectId = internalObjectId;
	}
	
	@PostConstruct
	private void init(){
		System.err.println("Initializing ObjectId with springApplicationName: " + springApplicationName);
	}

	public String getSpringApplicationName() {
		return springApplicationName;
	}

	/*
	 * this method injects a configuration value of spring
	 */
	@Value("${spring.application.name:2023b.LiranSorokin}")
	public void setSpringApplicationName(String springApllicationName) {
		this.springApplicationName = springApllicationName;
	}

	public String getInternalObjectId() {
		return internalObjectId;
	}

	public void setInternalObjectId(String internalObjectId) {
		this.internalObjectId = internalObjectId;
	}

	@Override
	public String toString() {
		return "ObjectId [springApplicationName=" + springApplicationName + ", internalObjectId=" + internalObjectId + "]";
	}
}
