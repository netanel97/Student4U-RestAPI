package controllersSrc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@RequestMapping(
		path = {"/hello"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message hello () {
		return new Message("Hello World!");
	}

	@RequestMapping(
			path = {"/hello/{firstName}/{lastName}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message hello (
			@PathVariable("firstName") String firstName, 
			@PathVariable("lastName") String lastName) {
		Message rv = new Message("Hello World!");
		Map<String, Object> moreDataWithName = new TreeMap<>();
		moreDataWithName.put("firstName", firstName);
		moreDataWithName.put("lastName", lastName);
		
		
		rv.setMoreData(moreDataWithName);
		return rv;
	}

	@RequestMapping(
			path = {"/hello/{name}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message hello (@PathVariable("name") String name) {
		Message rv = new Message ("Hello " + name + "!");
		
		rv.setId(2L);
		rv.setTimestamp(new Date());
		rv.setImportant(false);
		
		Map<String, Object> moreData = new HashMap<>();
		moreData.put("name", "Jane Smith");
		Map<String, Object> object2 = new HashMap<>();
		object2.put("object2Name", "Default");
		moreData.put("details", object2);
				
		rv.setMoreData(moreData);
		
		
		return rv;
	}
}
