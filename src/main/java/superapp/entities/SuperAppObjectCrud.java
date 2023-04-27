package superapp.entities;


import org.springframework.data.repository.ListCrudRepository;

import superapp.data.SuperAppObjectEntity;

public interface SuperAppObjectCrud extends ListCrudRepository<SuperAppObjectEntity, String> {
	
}
