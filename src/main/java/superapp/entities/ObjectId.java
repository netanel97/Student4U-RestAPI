package superapp.entities;

public class ObjectId {

	private final String SUPERAPP_NAME = "2023b.LiranSorokin";
	
	private String superapp;
	private String internalObjectId;

	public ObjectId() {
		super();
	};

	public ObjectId(String internalObjectId) {
		super();
		this.superapp = SUPERAPP_NAME;
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
