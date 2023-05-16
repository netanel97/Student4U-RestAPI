package superapp.logic;

import java.util.List;

import superapp.entities.SuperAppObjectBoundary;
import superapp.entities.SuperAppObjectIdBoundary;

public interface DataManagerWithRelationsSupport extends ObjectsService {
	@Deprecated
	public void BindAnExistingObjectToExistingChildObject(String superapp,String internalObjectId,SuperAppObjectIdBoundary child);
	
	@Deprecated
	public List<SuperAppObjectBoundary> getAllChildrenOfAnExistingObject(String superapp,String internalObjectId);
	
	@Deprecated
	public List<SuperAppObjectBoundary> getAnArrayWithObjectParent(String superapp,String internalObjectId);
}


