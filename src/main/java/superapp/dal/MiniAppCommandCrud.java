package superapp.dal;

//removed unnecessary import.
import org.springframework.data.mongodb.repository.MongoRepository;

import superapp.data.MiniAppCommandEntity;

public interface MiniAppCommandCrud extends MongoRepository<MiniAppCommandEntity, String> {

}
