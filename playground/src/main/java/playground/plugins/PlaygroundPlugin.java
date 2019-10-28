package playground.plugins;

import playground.logic.entity.ActivityEntity;

public interface PlaygroundPlugin {
	public Object invokeOperation (ActivityEntity activity); 
}
