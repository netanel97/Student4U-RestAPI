package superapp.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.Constants;
import superapp.utils.MiniAppCommandConverter;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MiniAppGradeAVG implements MiniAppService {


    private final SuperAppObjectCrud objectCrud;
    private final ObjectConverter objectConverter;
    private final MiniAppCommandConverter miniAppCommandConverter;

    @Autowired
    public MiniAppGradeAVG(SuperAppObjectCrud objectCrud, ObjectConverter objectConverter, MiniAppCommandConverter miniAppCommandConverter) {
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
        String comm = command.getCommand();
        switch (comm) {
            case "Calculate AVG": {
                return this.calculateAVG(command);
            }
            case "Remove Grade": {
                return this.removeGrade(command);
            }
            default:
                return command;
        }
    }

    /**
     * Calculates the user average grades.
     * The command attributes must contain: target user id.
     * Optional command attributes: page, size.
     *
     * @param command MiniAppCommandBoundary
     * @return Object -  a JSON we create from the avg and number of grades.
     **/
    private Object calculateAVG(MiniAppCommandBoundary command) {
        float AVG = 0f;
        float allPoints = 0f;
        Map<String, Object> commandAtt = command.getCommandAttributes();
        List<SuperAppObjectEntity> allGrades = this.objectCrud.findAllByTypeAndActiveIsTrueAndCreatedBy(Constants.GRADE, (String) commandAtt.get(Constants.CREATOR),
                PageRequest.of(miniAppCommandConverter.getPage(commandAtt), miniAppCommandConverter.getSize(commandAtt)));
        for (SuperAppObjectEntity s : allGrades) {
            AVG += (int) s.getObjectDetails().get("grade") * (int) s.getObjectDetails().get("points");
            allPoints += (int) s.getObjectDetails().get("points");
        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("numberOfGrades", allGrades.size());
        if (allGrades.size() > 0) {
            res.put("averageGrade", AVG / allPoints);
        } else {
            res.put("averageGrade", 0);
        }
        return objectConverter.objToJson(res);
    }

    /**
     * Removes a specific grade (Updating the active field to false which indicates that the object was deleted).
     *
     * @param command MiniAppCommandBoundary
     * @return SuperAppObjectBoundary
     */
    private SuperAppObjectBoundary removeGrade(MiniAppCommandBoundary command) {
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
        if (superAppObject.getType().equalsIgnoreCase(Constants.GRADE)) {
            superAppObject.setActive(false);
            this.objectCrud.save(superAppObject);
        }
        return this.objectConverter.entityToBoundary(superAppObject);// If the client send me type that don't match to
        // thread will return the object that he sent
    }
}
