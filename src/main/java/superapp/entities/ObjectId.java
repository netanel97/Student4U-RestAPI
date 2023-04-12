package superapp.entities;

import org.springframework.stereotype.Component;

@Component
public class ObjectId {

	private String springApplicationName;
	private String internalObjectId;

	public ObjectId() {
		super();
	};

	public ObjectId(String internalObjectId) {
		super();
		this.internalObjectId = internalObjectId;
	}

	public String getSpringApplicationName() {
		return springApplicationName;
	}

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
