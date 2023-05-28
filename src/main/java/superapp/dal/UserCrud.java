package superapp.dal;


import org.springframework.data.mongodb.repository.MongoRepository;

import superapp.data.UserEntity;

public interface UserCrud extends MongoRepository<UserEntity, String> {
	

}
