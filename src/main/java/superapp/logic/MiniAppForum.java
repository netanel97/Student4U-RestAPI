package superapp.logic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import superapp.boundaries.command.MiniAppCommandBoundary;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;

import superapp.miniapps.ForumThread;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

@Service("Forum")
public class MiniAppForum implements MiniAppService {


    private final SuperAppObjectCrud objectCrud;
    private final ObjectConverter objectConverter;
    private final UserConverter userConverter;

    @Autowired
    public MiniAppForum(SuperAppObjectCrud objectCrud, ObjectConverter objectConverter, UserConverter userConverter) {
        this.objectCrud = objectCrud;
        this.objectConverter = objectConverter;
        this.userConverter = userConverter;
    }

    @Override
    public Object runCommand(MiniAppCommandBoundary command) {
        String comm = command.getCommand();
        switch (comm) {
            case "Comment On Thread":{
                commentOnThread(command);
                break;
            }
            case "Remove Thread": {
                removeThread(command);
                break;
            }
            case "Get User Threads":{
            	return getUserThreads(command);
            }
            default:
                throw new MiniAppCommandNotFoundException("Undefined command: " + comm);
        }
        return comm;
    }

    private Object getUserThreads(MiniAppCommandBoundary command) {
		return this.objectCrud.findAllByCreatedByAndType("2023b.Liran.Sorokin-Student4U_adam@gmail.com", "thread");
	
    }

	private void commentOnThread(MiniAppCommandBoundary command) {
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectBoundary superAppObject = this.objectCrud.findById(targetObjId).map(this.objectConverter::entityToBoundary)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
     
        ForumThread targetThread = (ForumThread) superAppObject.getObjectDetails().get("forumThread");
        targetThread.getComments().add((String) command.getCommandAttributes().get("comment"));
        superAppObject.getObjectDetails().put("forumThread",targetThread);
        this.objectCrud.save(this.objectConverter.boundaryToEntity(superAppObject));
    }
    /**
     * 
     * @param command
     */
    private void removeThread(MiniAppCommandBoundary command) {
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
        if(superAppObject.getType().toLowerCase().equals("thread")) {
            superAppObject.setActive(false);
            this.objectCrud.save(superAppObject);
        	
        }
        else  {
        	
        }
        
   

    }


}
