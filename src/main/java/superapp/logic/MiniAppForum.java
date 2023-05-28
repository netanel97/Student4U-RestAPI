package superapp.logic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import superapp.boundaries.command.MiniAppCommandBoundary;

import superapp.boundaries.object.ObjectId;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.dal.UserCrud;
import superapp.data.UserEntity;
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
            case "Show Thread": {
                showThread(command);
                break;
            }
            case "Comment On Thread":{
                commentOnThread(command);
                break;
            }
            case "Remove Thread": {
                removeThread(command);
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + comm);
        }
        return comm;
    }

    private void commentOnThread(MiniAppCommandBoundary command) {
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectBoundary superAppObject = this.objectCrud.findById(targetObjId).map(this.objectConverter::entityToBoundary)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
        System.err.println(superAppObject.getObjectDetails().get("forumThread"));
        ForumThread targetThread = (ForumThread) superAppObject.getObjectDetails().get("forumThread");
        targetThread.getComments().add((String) command.getCommandAttributes().get("comment"));
        superAppObject.getObjectDetails().put("forumThread",targetThread);
        this.objectCrud.save(this.objectConverter.boundaryToEntity(superAppObject));
    }

    private void removeThread(MiniAppCommandBoundary command) {
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectBoundary superAppObject = this.objectCrud.findById(targetObjId).map(this.objectConverter::entityToBoundary)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
        String creatorId = this.userConverter.userIdToString(superAppObject.getCreatedBy().getUserId());
        String invokerId = userConverter.userIdToString(command.getInvokedBy().getUserId());
        if(creatorId.equals(invokerId)){
            this.objectCrud.deleteById(targetObjId);
        }
        else {
            throw new UnauthorizedAccessException("User is not authorized to delete this thread");
        }
    }

    //TODO: check how to show only the object details
    private void showThread(MiniAppCommandBoundary command){
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectBoundary superAppObject = this.objectCrud.findById(targetObjId).map(this.objectConverter::entityToBoundary)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
    }
}
