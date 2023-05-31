package superapp.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

@Component
public class MiniAppGradeAVG implements MiniAppService{

	
    private final SuperAppObjectCrud objectCrud;
    private final ObjectConverter objectConverter;
    private final UserConverter userConverter;

    @Autowired
    public MiniAppGradeAVG(SuperAppObjectCrud objectCrud, ObjectConverter objectConverter, UserConverter userConverter) {
        this.objectCrud = objectCrud;
        this.objectConverter = objectConverter;
        this.userConverter = userConverter;
    }
	
	
	@Override
	public Object runCommand(MiniAppCommandBoundary command) {
        String comm = command.getCommand();
        switch (comm) {
		case "Remove Grade": {
			this.removeGrade(command);
			break;
			
		}
		default:
            return command;
		}
        return comm;

	}

	private void removeGrade(MiniAppCommandBoundary command) {
	  String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
        superAppObject.setActive(false);
        this.objectCrud.save(superAppObject);
	}

}
