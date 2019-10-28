package playground.dal;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import playground.logic.entity.ElementEntity;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;

@RepositoryRestResource
public interface ElementDao  extends PagingAndSortingRepository<ElementEntity, String>//extends CrudRepository<ElementEntity, String> {
{

	List<ElementEntity> findAllByNameLikeIgnoreCase(@Param("name") String name, Pageable pageable);

	List<ElementEntity> findAllByExpirationDateAfter(@Param("expirationDate") Date expirationDate, Pageable pageable);
	List<ElementEntity> findAllByLocationXGreaterThanAndLocationXLessThanAndLocationYGreaterThanAndLocationYLessThan(
			@Param("minX") double minX, @Param("maxX") double maxX,
			@Param("minY") double minY, @Param("maxY") double maxY,
			Pageable pageable);
	List<ElementEntity> findAllByLocationXGreaterThanAndLocationXLessThanAndLocationYGreaterThanAndLocationYLessThanAndExpirationDateAfter(
			@Param("minX") double minX, @Param("maxX") double maxX,
			@Param("minY") double minY, @Param("maxY") double maxY,
			@Param("expirationDate") Date expirationDate,
			Pageable pageable);
	
	List<ElementEntity> findAllByTypeLikeIgnoreCase(@Param("type") String type, Pageable pageable);
	List<ElementEntity> findAllByTypeLikeIgnoreCaseAndExpirationDateAfter(
			@Param("type") String type, @Param("expirationDate") Date expirationDate, Pageable pageable);

	List<ElementEntity> findAllByLocationXEqualsAndLocationYEquals(@Param("locationX")double locationX, @Param("locationY")double locationY, Pageable pageable);
	List<ElementEntity> findAllByLocationXEqualsAndLocationYEqualsAndExpirationDateAfter(
			@Param("locationX")double locationX, @Param("locationY")double locationY, 
			@Param("expirationDate") Date expirationDate, Pageable pageable);

	ElementEntity findByIdLikeAndPlaygroundLikeAndTypeLike(@Param("id") String id, @Param("playground") String playground, @Param("type") String type);

	ElementEntity findByIdLikeAndPlaygroundLike(@Param("id") String id,@Param("playground") String playground);
}
