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
	
	public List<SuperAppObjectEntity> findAllByType(
			@Param("type") String type, 
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByAlias(
			@Param("alias") String alias, 
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByLatBetweenAndLngBetween(
			@Param("minLat") double minLat,
			@Param("maxLat") double maxLat,
			@Param("minLong") double minLong,
			@Param("maxLong") double maxLong,
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByTypeAndActiveIsTrue(
			@Param("type") String type, 
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByAliasAndActiveIsTrue(
			@Param("alias") String alias, 
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByLatBetweenAndLngBetweenAndActiveIsTrue(
			@Param("minLat") double minLat,
			@Param("maxLat") double maxLat,
			@Param("minLong") double minLong,
			@Param("maxLong") double maxLong,
			Pageable pageable);

	
	
}
