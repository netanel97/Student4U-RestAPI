package superapp.entities;

import java.util.HashMap;

public class ObjectDetails {

	private HashMap<String, Object> objectDetails;
	public ObjectDetails() {
		super();
	}
	public ObjectDetails(HashMap<String, Object> objectDetails) {
		super();
		this.objectDetails = objectDetails;
	}
	public HashMap<String, Object> getObjectDetails() {
		return objectDetails;
	}
	public void setObjectDetails(HashMap<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}
	@Override
	public String toString() {
		return "ObjectDetails [objectDeatils=" + objectDetails + "]";
	}
	
	


	
	
	

}
