package superapp.logic;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.dal.SuperAppObjectCrud;
import superapp.data.SuperAppObjectEntity;

import superapp.logic.mongo.ObjectsServiceMongoDb;
import static superapp.utils.Constants.EVENT;
import static superapp.utils.Constants.DEFAULT_PAGE_VALUE_INT;
import static superapp.utils.Constants.DEFAULT_SIZE_VALUE_INT;
import superapp.utils.ObjectConverter;

@Component
public class MiniAppCalendar implements MiniAppService {

	private final SuperAppObjectCrud objectCrud;
	private final ObjectConverter objectConverter;

	private Log logger = LogFactory.getLog(ObjectsServiceMongoDb.class);

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
			logger.trace("Entering Find By Date case");
			return findEventsByDate(command);
		}
		case "Remove Event": {
			logger.trace("Entering Remove Event case");
			return removeEvent(command);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + comm);
		}
	}

	private List<SuperAppObjectBoundary> findEventsByDate(MiniAppCommandBoundary command) {
		logger.trace("Entering findEventsByDate function");
		int page, size;
		String stringDateStr = (String) command.getCommandAttributes().get("date");

		if (!command.getCommandAttributes().containsKey("page")) {
			page = DEFAULT_PAGE_VALUE_INT;
		} else {
			page = (int) command.getCommandAttributes().get("page");
		}
		if (!command.getCommandAttributes().containsKey("size")) {
			size = DEFAULT_SIZE_VALUE_INT;
		} else {
			size = (int) command.getCommandAttributes().get("size");
		}

		logger.trace("Searching objects by ByDateAndActiveIsTrue....");
		List<SuperAppObjectBoundary> answer = this.objectCrud
				.findAllByDateAndActiveIsTrue(stringDateStr,
						PageRequest.of(page, size, Direction.ASC, "type", "creationTimestamp", "objectId"))
				.stream() // Stream<SuperAppObjectBoundary>
				.map(this.objectConverter::entityToBoundary) // Stream<SuperAppObject>
				.toList(); // List<SuperAppObject>
		return answer;

	}

	private SuperAppObjectBoundary removeEvent(MiniAppCommandBoundary command) {
		logger.trace("Entering removeEvent function");
		String targetObjId = objectConverter.objectIdToString(command.getTargetObject().getObjectId());
		SuperAppObjectEntity superAppObject = this.objectCrud.findById(targetObjId)
				.orElseThrow(() -> new SuperAppObjectNotFoundException("Super app object was not found"));

		for (SuperAppObjectEntity child : superAppObject.getChildren()) {
			child.setActive(false);
			this.objectCrud.save(child);
		}
		logger.trace("Get SuperAppObjectEntity from the DB: " + superAppObject);
		if (superAppObject.getType().equalsIgnoreCase(EVENT)) {
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
