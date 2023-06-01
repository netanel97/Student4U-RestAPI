package superapp.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.Constants;
import superapp.utils.ObjectConverter;
import superapp.utils.UserConverter;

@Component
public class MiniAppForum implements MiniAppService {

	private final SuperAppObjectCrud objectCrud;
	private final ObjectConverter objectConverter;

	@Autowired
	public MiniAppForum(SuperAppObjectCrud objectCrud, ObjectConverter objectConverter) {
		this.objectCrud = objectCrud;
		this.objectConverter = objectConverter;
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
		String dateString = (String) commandAtt.get("date");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Date date;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Date format is incorrect.");
		}
		int page = 0;// default
		int size = 15;// default
		if(commandAtt.containsKey("page")){
			page = (int) commandAtt.get("page");
		}
		if(commandAtt.containsKey("size")){
			size = (int) commandAtt.get("size");
		}

		return this.objectCrud.findAllByTypeAndCreationTimestampAfter(Constants.THREAD,date,
				PageRequest.of(page, size, Direction.ASC, "creationTimestamp"));
	}

	private Object getUserThreads(MiniAppCommandBoundary command) {
		String creator = (String) command.getCommandAttributes().get("creator");
		return this.objectCrud.findAllByTypeAndCreatedByAndActiveIsTrue(Constants.THREAD, creator);
	}

	private SuperAppObjectBoundary removeThread(MiniAppCommandBoundary command) {
		String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
		SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
				.orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));
		if (superAppObject.getType().toLowerCase().equals(Constants.THREAD)) {
			superAppObject.setActive(false);
			this.objectCrud.save(superAppObject);
			SuperAppObjectBoundary superAppObjectBoundary = this.objectConverter.entityToBoundary(superAppObject);
			return superAppObjectBoundary;
		}
		return this.objectConverter.entityToBoundary(superAppObject);// If the client send me type that don't match to
		// thread will return the object that he sent
	}

}
