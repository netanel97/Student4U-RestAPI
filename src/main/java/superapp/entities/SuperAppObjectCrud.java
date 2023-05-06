package superapp.entities;


import org.springframework.data.mongodb.repository.MongoRepository;

import superapp.data.SuperAppObjectEntity;

public interface SuperAppObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {
	
}
