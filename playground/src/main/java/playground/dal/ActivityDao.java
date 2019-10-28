package playground.dal;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.domain.Pageable;

import playground.logic.entity.ActivityEntity;

@RepositoryRestResource
public interface ActivityDao extends PagingAndSortingRepository<ActivityEntity, String> {
	
	List<ActivityEntity> findAllByTypeLikeIgnoreCase(@Param("type") String type, Pageable pageable);
	List<ActivityEntity> findAllByElementPlaygroundLikeAndElementIdLikeAndTypeLike(
			@Param("elementPlayground") String elementPlayground,
			@Param("elementId") String elementId,
			@Param("activityType") String activityType,
			Pageable pageable);
	public List<ActivityEntity> findAllByTypeAndJsonAttributesLike(
			@Param("type") String type, 
			@Param("moreAttributes") String moreAttributes, 
			Pageable pageable);
}
