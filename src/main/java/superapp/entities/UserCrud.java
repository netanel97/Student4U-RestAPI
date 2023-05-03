package superapp.entities;

import org.springframework.data.repository.ListCrudRepository;

import superapp.data.UserEntity;

public interface UserCrud extends ListCrudRepository<UserEntity, String> {

}
