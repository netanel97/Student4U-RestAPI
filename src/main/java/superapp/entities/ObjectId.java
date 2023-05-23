package superapp.entities;

import org.springframework.stereotype.Component;

@Component
public class ObjectId {

	private String superapp;
	private String internalObjectId;

	public ObjectId() {
		super();
	};

	public ObjectId(String internalObjectId) {
		super();
		this.internalObjectId = internalObjectId;
	}

	public String getSuperapp() {
		return superapp;
	}

	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}

	public String getInternalObjectId() {
		return internalObjectId;
	}

	public void setInternalObjectId(String internalObjectId) {
		this.internalObjectId = internalObjectId;
	}

	@Override
	public String toString() {
		return "ObjectId [superapp=" + superapp + ", internalObjectId=" + internalObjectId + "]";
	}
}
