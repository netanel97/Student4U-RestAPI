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

@Service("Calendar")
public class MiniAppCalendar implements MiniAppService {

	private final SuperAppObjectCrud objectCrud;
	private final ObjectConverter objectConverter;

	@Autowired
	public MiniAppCalendar(SuperAppObjectCrud objectCrud, ObjectConverter objectConverter) {
		this.objectCrud = objectCrud;
		this.objectConverter = objectConverter;
	}

	@Override
	public Object runCommand(MiniAppCommandBoundary command) {
		String comm = command.getCommand();
		switch (comm) {
		case "Find By Date": {
			findEventsByDate(command);
			break;
		}
		case "Find Future Events": {
			findFutureEvents(command);
			break;
		}
		case "Find Past Events": {
			findFutureEvents(command);
			break;
		}
		case "Remove Event": {
			removeEvent(command);
			break;
		}
		case "Update Participants": {
			updateParticipants(command);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + comm);
		}
		return comm;
	}

//	private void commentOnThread(MiniAppCommandBoundary command) {
//		String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
//		SuperAppObjectBoundary superAppObject = this.objectCrud.findById(targetObjId)
//				.map(this.objectConverter::entityToBoundary)
//				.orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
//		System.err.println(superAppObject.getObjectDetails().get("forumThread"));
//		ForumThread targetThread = (ForumThread) superAppObject.getObjectDetails().get("forumThread");
//		targetThread.getComments().add((String) command.getCommandAttributes().get("comment"));
//		superAppObject.getObjectDetails().put("forumThread", targetThread);
//		this.objectCrud.save(this.objectConverter.boundaryToEntity(superAppObject));
//	}

	private void updateParticipants(MiniAppCommandBoundary command) {
		// TODO Auto-generated method stub

	}

	private void findFutureEvents(MiniAppCommandBoundary command) {
		// TODO Auto-generated method stub

	}

	private void findEventsByDate(MiniAppCommandBoundary command) {
		
//		String targetObjDate = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
//		SuperAppObjectEntity superAppObject = this.objectCrud.findByDate(targetObjDate)
//				.orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
		
	}

	private void removeEvent(MiniAppCommandBoundary command) {
		String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
		SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
				.orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
		superAppObject.setActive(false);
		this.objectCrud.save(superAppObject);
	}

	// TODO: check how to show only the object details
	private void showThread(MiniAppCommandBoundary command) {
		String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
		SuperAppObjectBoundary superAppObject = this.objectCrud.findById(targetObjId)
				.map(this.objectConverter::entityToBoundary)
				.orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
	}
}
