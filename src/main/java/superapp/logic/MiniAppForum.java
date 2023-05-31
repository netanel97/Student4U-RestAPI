package superapp.logic;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

@Component
public class MiniAppForum implements MiniAppService {

	private final SuperAppObjectCrud objectCrud;
	private final ObjectConverter objectConverter;
	private final UserConverter userConverter;
	private final String THREAD = "thread";

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

	private Object getThreadsAfter(MiniAppCommandBoundary command) {
		Map<String, Object> commandAtt = command.getCommandAttributes();
		String date = (String) commandAtt.get("date");
		int page = (int) commandAtt.get("page");
		int size = (int) commandAtt.get("size");
		return this.objectCrud.findByCreationTimestampAfter(date,
				PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"));
	}

	private Object getUserThreads(MiniAppCommandBoundary command) {
		String creator = (String) command.getCommandAttributes().get("creator");
		return this.objectCrud.findAllByTypeAndCreatedByAndActiveIsTrue(THREAD, creator);
	}

	private SuperAppObjectBoundary removeThread(MiniAppCommandBoundary command) {
		String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
		SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
				.orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
		if (superAppObject.getType().toLowerCase().equals(THREAD)) {
			superAppObject.setActive(false);
			this.objectCrud.save(superAppObject);
			SuperAppObjectBoundary superAppObjectBoundary = this.objectConverter.entityToBoundary(superAppObject);
			return superAppObjectBoundary;
		}
		return this.objectConverter.entityToBoundary(superAppObject);// If the client send me type that don't match to
																		// thread i will return the object that he sent
	}

}
