package superapp.entities;

import java.util.HashMap;

public class ObjectDetails {

	private HashMap<String, Object> objectDeatils;
	public ObjectDetails() {
		super();
	}
	public ObjectDetails(HashMap<String, Object> objectDeatils) {
		super();
		this.objectDeatils = objectDeatils;
	}
	public HashMap<String, Object> getObjectDeatils() {
		return objectDeatils;
	}
	public void setObjectDeatils(HashMap<String, Object> objectDeatils) {
		this.objectDeatils = objectDeatils;
	}
	@Override
	public String toString() {
		return "ObjectDetails [objectDeatils=" + objectDeatils + "]";
	}
	
	


	
	
	

}
