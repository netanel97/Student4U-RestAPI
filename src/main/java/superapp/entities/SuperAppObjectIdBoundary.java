package superapp.entities;

public class SuperAppObjectIdBoundary {
	private String superapp;
	private String internalObjectId;
	
	public SuperAppObjectIdBoundary() {
	
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
		return "SuperAppObjectIdBoundary [superapp=" + superapp + ", internalObjectId=" + internalObjectId + "]";
	}
	


}
