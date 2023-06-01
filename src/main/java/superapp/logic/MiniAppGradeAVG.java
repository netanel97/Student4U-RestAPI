package superapp.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import superapp.boundaries.command.MiniAppCommandBoundary;
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

    private Object calculateAVG(MiniAppCommandBoundary command) {
        float AVG = 0f;
        float allPoints = 0f;
        Map<String, Object> commandAtt = command.getCommandAttributes();
        List<SuperAppObjectEntity> allGrades = this.objectCrud.findAllByTypeAndActiveIsTrueAndCreatedBy(Constants.GRADE,(String) commandAtt.get(Constants.CREATOR),
                PageRequest.of(miniAppCommandConverter.getPage(commandAtt), miniAppCommandConverter.getSize(commandAtt)));
        for (SuperAppObjectEntity s:allGrades) {
            AVG += (int) s.getObjectDetails().get("grade") * (int)  s.getObjectDetails().get("points");
            allPoints += (int) s.getObjectDetails().get("points");
        }
        HashMap<String,Object> res = new HashMap<>();
        res.put("numberOfGrades",allGrades.size());
        if(allGrades.size()>0){
            res.put("averageGrade",AVG/allPoints);
        }
        else{
            res.put("averageGrade",0);
        }
        return objectConverter.objToJson(res);
    }

    private SuperAppObjectEntity removeGrade(MiniAppCommandBoundary command) {
        String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
        SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
                .orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
        superAppObject.setActive(false);
        return this.objectCrud.save(superAppObject);
    }

}
