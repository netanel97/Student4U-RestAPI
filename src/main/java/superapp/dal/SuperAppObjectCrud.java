package superapp.dal;


import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import superapp.data.SuperAppObjectEntity;

public interface SuperAppObjectCrud extends MongoRepository<SuperAppObjectEntity, String> {
	
	public List<SuperAppObjectEntity> findAllByActiveIsTrue(Pageable pageable);
	
	public List<SuperAppObjectEntity> findByParentsContainingAndActiveIsTrue(SuperAppObjectEntity parent,Pageable pageable);
	
	public List<SuperAppObjectEntity> findByChildrenContainingAndActiveIsTrue(SuperAppObjectEntity child,Pageable pageable);
	
	public List<SuperAppObjectEntity> findByParentsContaining(SuperAppObjectEntity parent,Pageable pageable);
	
	public List<SuperAppObjectEntity> findByChildrenContaining(SuperAppObjectEntity child,Pageable pageable);

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
	
	public List<SuperAppObjectEntity> findAllByTypeAndActiveIsTrueAndCreatedBy(
			@Param("type") String type,
			@Param("createdBy") String createBy,
			Pageable pageable);

    @Query("{'location': { $near: { $geometry: { type: 'Point', coordinates: [?0, ?1] }, $maxDistance: ?2 }}}")
    public List<SuperAppObjectEntity> findByLocationNear(
            @Param("lat")double lat,
            @Param("lng")double lng,
            @Param("distance")double distance,
            Pageable pageable);
    
    @Query("{'location': { $near: { $geometry: { type: 'Point', coordinates: [?0, ?1] }, $maxDistance: ?2 }}}")
    public List<SuperAppObjectEntity> findByLocationNearAndActiveIsTrue(
            @Param("lat")double lat,
            @Param("lng")double lng,
            @Param("distance")double distance,
            Pageable pageable);
	
    public List<SuperAppObjectEntity> findAllByTypeAndCreationTimestampAfterAndActiveIsTrue(
			@Param("type") String type,
    		@Param("creationTimestamp") Date creationTimestamp,
    		Pageable pageable);
}
