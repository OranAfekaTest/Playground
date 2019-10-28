package playground.dal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.entity.ActivityEntity;
import playground.logic.entity.UserEntity;
import playground.logic.entity.UserId;

public interface UserDao extends CrudRepository<UserEntity, UserId>{
	
	//TODO - REMOVE!!!!!!!!
	UserEntity findByEmailLike(@Param("email") String email);
	
}

