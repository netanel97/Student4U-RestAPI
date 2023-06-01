package superapp.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.Constants;
import superapp.utils.MiniAppCommandConverter;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

@Component
public class MiniAppForum implements MiniAppService {

    private final SuperAppObjectCrud objectCrud;
    private final ObjectConverter objectConverter;
    private final MiniAppCommandConverter miniAppCommandConverter;

    @Autowired
    public MiniAppForum(SuperAppObjectCrud objectCrud, ObjectConverter objectConverter, MiniAppCommandConverter miniAppCommandConverter) {
        this.objectCrud = objectCrud;
        this.objectConverter = objectConverter;
        this.miniAppCommandConverter = miniAppCommandConverter;
    }

    /**
     * Checks the command that was chosen based on the command field in the MiniAppCommandBoundary the function received.
     * By default, if the command doesn't exist it will return the command itself.
     * @param command MiniAppCommandBoundary
     * @return Object
     **/
    @Override
    public Object runCommand(MiniAppCommandBoundary command) {
        String comm = command.getCommand();
        switch (comm) {
            case "Remove Thread": {
                return removeThread(command);
            }
            case "Get User Threads": {
                return getUserThreads(command);
            }
            case "Get Threads After": {
                return getThreadsAfter(command);
            }
            default:
                return command;
        }
    }

    /**
     * GET function that get all the threads that were created since a certain date.
     * The command attributes must contain: timestamp.
     * Optional command attributes: page, size.
     *
     * @param command MiniAppCommandBoundary
     * @return Object
     **/
    // TODO: need to fix time stamp, only 2 hours after the time input is filtered
    private Object getThreadsAfter(MiniAppCommandBoundary command) {
        Map<String, Object> commandAtt = command.getCommandAttributes();
        String dateString = (String) commandAtt.get("date");
        return this.objectCrud.findAllByTypeAndCreationTimestampAfterAndActiveIsTrue(Constants.THREAD, miniAppCommandConverter.stringToDate(dateString),
                PageRequest.of(miniAppCommandConverter.getPage(commandAtt), miniAppCommandConverter.getSize(commandAtt), Direction.ASC, "creationTimestamp"));
    }

    /**
     * GET function that get all the threads of a specific user.
     * The command attributes must contain: threads' creator user id.
     * Optional command attributes: page, size.
     *
     * @param command MiniAppCommandBoundary
     * @return List<SuperAppObjectEntity>
     **/
    private List<SuperAppObjectEntity> getUserThreads(MiniAppCommandBoundary command) {
        Map<String, Object> commandAtt = command.getCommandAttributes();
        String creator = (String) commandAtt.get(Constants.CREATOR);
        return this.objectCrud.findAllByTypeAndActiveIsTrueAndCreatedBy(Constants.THREAD, creator,
                PageRequest.of(miniAppCommandConverter.getPage(commandAtt), miniAppCommandConverter.getSize(commandAtt),Direction.ASC, "creationTimestamp" ));
    }

    /**
     * Removes a specific thread (Updating the active field to false which indicates that the object was deleted).
     *
     * @param command MiniAppCommandBoundary
     * @return SuperAppObjectBoundary
     */
    private SuperAppObjectBoundary removeThread(MiniAppCommandBoundary command) {
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));

        if (superAppObject.getType().equalsIgnoreCase(Constants.THREAD)) {
            superAppObject.setActive(false);
            this.objectCrud.save(superAppObject);
        }
        return this.objectConverter.entityToBoundary(superAppObject);// If the client send me type that don't match to
        // thread will return the object that he sent
    }
}
