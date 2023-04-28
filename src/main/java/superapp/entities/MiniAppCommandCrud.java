package superapp.entities;


import org.springframework.data.repository.ListCrudRepository;

import superapp.data.MiniAppCommandEntity;

public interface MiniAppCommandCrud extends ListCrudRepository<MiniAppCommandEntity, String> {

}
