package superapp.entities;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.ListCrudRepository;

import superapp.data.MiniAppCommandEntity;

public interface MiniAppCommandCrud extends MongoRepository<MiniAppCommandEntity, String> {

}
