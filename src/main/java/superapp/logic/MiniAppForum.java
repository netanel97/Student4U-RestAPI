package superapp.logic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import superapp.boundaries.command.MiniAppCommandBoundary;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.dal.UserCrud;

@Service("Forum")
public class MiniAppForum implements MiniAppService{

	
    private final SuperAppObjectCrud objectCrud;
    private final UserCrud userCrud;

    @Autowired
    public MiniAppForum(UserCrud userCrud, SuperAppObjectCrud objectCrud) {
        this.objectCrud = objectCrud;
        this.userCrud = userCrud;
    }
	
	@Override
	public Object runCommand(MiniAppCommandBoundary command)  {
		String comm = command.getCommand();
		switch (comm) {
		case "createNewThread": {
			
			this.createNewThread(command);
			break;
		}
		case "":{
		}

		default:
			throw new IllegalArgumentException("Unexpected value: " + comm);
		}
		return comm;
	}
		

	
//	private SuperAppObjectBoundary entityToBoundary(SuperAppObjectEntity superAppObjectEntity) {
//		SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
//		objectBoundary.setActive(superAppObjectEntity.isActive());
//		objectBoundary.setAlias(superAppObjectEntity.getAlias());
//		objectBoundary.setCreatedBy(this.toBoundaryAsCreatedBy(superAppObjectEntity.getCreatedBy()));
//		objectBoundary.setCreationTimestamp(superAppObjectEntity.getCreationTimestamp());
//		objectBoundary
//		.setLocation(this.toBoundaryAsLocation(superAppObjectEntity.getLocation().getX(), superAppObjectEntity.getLocation().getY()));
//		objectBoundary.setObjectDetails(superAppObjectEntity.getObjectDetails());
//		objectBoundary.setObjectId(this.toBoundaryAsObjectId(superAppObjectEntity.getObjectId()));
//		objectBoundary.setType(superAppObjectEntity.getType());
//		return objectBoundary;
//	}
//	
//	private CreatedBy toBoundaryAsCreatedBy(String createdByStr) {
//		if (createdByStr != null) {
//			String[] attr = createdByStr.split("_");
//
//			CreatedBy createdBy = new CreatedBy();
//			createdBy.setUserId(new UserId(attr[1]));
//			createdBy.getUserId().setSuperapp(attr[0]);
//
//			return createdBy;
//		} else {
//			return null;
//		}
//	}
//	
//	private Location toBoundaryAsLocation(Double lat, Double lng) {
//		if (lat != null && lng != null) {
//
//			Location location = new Location();
//			location.setLat(lat);
//			location.setLng(lng);
//
//			return location;
//		} else {
//			return null;
//		}
//	}
//	
//	private ObjectId toBoundaryAsObjectId(String objectStr) {
//		if (objectStr != null) {
//			String[] attr = objectStr.split("_");
//
//			ObjectId objectId = new ObjectId();
//			objectId.setSuperapp(attr[0]);
//			objectId.setInternalObjectId(attr[1]);
//
//			return objectId;
//		} else {
//			return null;
//		}
//	}


	private void createNewThread(MiniAppCommandBoundary command) {
		SuperAppObjectBoundary superAppObjectBoundary = new SuperAppObjectBoundary();
	}
	

}
