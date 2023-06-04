package superapp.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.mongo.MiniAppCommandsServiceMongoDb;
import superapp.utils.Constants;
import superapp.utils.MiniAppCommandConverter;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

@Component
public class MiniAppForum implements MiniAppService {

    private final SuperAppObjectCrud objectCrud;
    private final ObjectConverter objectConverter;
    private final MiniAppCommandConverter miniAppCommandConverter;

    private Log logger = LogFactory.getLog(MiniAppForum.class);


    @Autowired
    public MiniAppForum(SuperAppObjectCrud objectCrud, ObjectConverter objectConverter, MiniAppCommandConverter miniAppCommandConverter) {
        logger.trace("Entering MiniAppForum constructor");
        this.objectCrud = objectCrud;
        this.objectConverter = objectConverter;
        this.miniAppCommandConverter = miniAppCommandConverter;
    }

    /**
     * Checks the command that was chosen based on the command field in the MiniAppCommandBoundary the function received.
     * By default, if the command doesn't exist it will return the command itself.
     *
     * @param command MiniAppCommandBoundary
     * @return Object
     **/
    @Override
    public Object runCommand(MiniAppCommandBoundary command) {
        logger.trace("Entering runCommand function with the following command: " + command.toString());
        String comm = command.getCommand();
        switch (comm) {
            case "Remove Thread": {
                logger.trace("Entering Remove Thread case");
                return removeThread(command);
            }
            case "Get User Threads": {
                logger.trace("Entering Get User Threads case");
                return getUserThreads(command);
            }
            case "Get Threads After": {
                logger.trace("Entering Get Threads After case");
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
        logger.trace("Searching objects by TypeAndCreationTimestampAfterAndActiveIsTrue....");
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
        logger.trace("Entering getUserThreads function");
        Map<String, Object> commandAtt = command.getCommandAttributes();
        String creator = (String) commandAtt.get(Constants.CREATOR);
        logger.trace("Searching objects by TypeAndActiveIsTrueAndCreatedBy....");
        return this.objectCrud.findAllByTypeAndActiveIsTrueAndCreatedBy(Constants.THREAD, creator,
                PageRequest.of(miniAppCommandConverter.getPage(commandAtt), miniAppCommandConverter.getSize(commandAtt), Direction.ASC, "creationTimestamp"));
    }

    /**
     * Removes a specific thread (Updating the active field to false which indicates that the object was deleted).
     *
     * @param command MiniAppCommandBoundary
     * @return SuperAppObjectBoundary
     */
    private SuperAppObjectBoundary removeThread(MiniAppCommandBoundary command) {
        logger.trace("Entering removeThread function");
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));

        for (SuperAppObjectEntity child : superAppObject.getChildren()) {
            child.setActive(false);
            this.objectCrud.save(child);

        }
        logger.trace("Get SuperAppObjectEntity from the DB: " + superAppObject);
        if (superAppObject.getType().equalsIgnoreCase(Constants.THREAD)) {
            logger.trace("The object is a thread, setting active to false");
            superAppObject.setActive(false);
            this.objectCrud.save(superAppObject);
            logger.trace("Saved the object to the DB");
        }
        logger.trace("Converting the object to SuperAppObjectBoundary and returning it");
        return this.objectConverter.entityToBoundary(superAppObject);// If the client send me type that don't match to
        // thread will return the object that he sent
    }


}
