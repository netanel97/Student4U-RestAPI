package superapp.entities;

public class TargetObject {
	ObjectId objectId;
	
	public TargetObject() {
		super();
	}

	public TargetObject(ObjectId objectId) {
		super();
		this.objectId = objectId;
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	@Override
	public String toString() {
		return "TargetObject [objectId=" + objectId + "]";
	}
	
	
}
