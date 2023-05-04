package superapp.controllersSrc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import superapp.logic.DataManagerWithRelationsSupport;

@RestController
public class NewSuperAppObjectController {
	private DataManagerWithRelationsSupport objects;

	@Autowired
	public NewSuperAppObjectController(DataManagerWithRelationsSupport objects) {
		super();
		this.objects = objects;

		
	}

}
