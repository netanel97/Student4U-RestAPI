package superapp.entities;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import superapp.data.SuperAppObjectEntity;

public interface SuperAppObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {
	
	public List<SuperAppObjectEntity> findAllByActiveIsTrue(
			@Param("active") boolean active, 
			Pageable pageable);
	
}
