package playground.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.entity.ActivityEntity;
import playground.logic.entity.UserEntity;
import playground.logic.jpa.JpaUserService;
import playground.logic.service.user.UserUnauthorizedAccessException;

@Component
@Aspect
public class PlayerAuthorizationAspect {

	private JpaUserService users;
	
	@Autowired
	public PlayerAuthorizationAspect(JpaUserService users) {
		this.users = users;
	}
	
	@Around("@annotation(playground.aop.PlayerAuthorization) && args(activity,..)")
	public Object isPlayer (ProceedingJoinPoint joinPoint, ActivityEntity activity) throws Throwable {
		UserEntity user = users.getUserById(activity.getPlayerEmail(), activity.getPlayerPlayground());
		
		if (!user.getRole().equals("player") && !user.getRole().equals("Player"))
			throw new UserUnauthorizedAccessException("Only Player can preform this action.");	
		
		return joinPoint.proceed();
	}
	
}
