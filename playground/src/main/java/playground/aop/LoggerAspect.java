package playground.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAspect {
	private Log log = LogFactory.getLog(LoggerAspect.class);
	
	@Around("@annotation(playground.aop.MyLog)")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable{
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String methodSignatue = "" + className + "." + methodName;

		log.info(methodSignatue + " - start");
		
		try {
			Object rv = joinPoint.proceed();
			log.info(methodSignatue + " - ended successfuly");
			return rv;
		}catch(Throwable e) {
			log.error(methodSignatue + " - ended with error " + e.getClass());
			throw e;
		}
	}
}
