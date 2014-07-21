package de.prob.bmotion;

import com.google.gson.JsonElement;


public interface IObserver {

	public IBMotionGroovyObserver getBMotionGroovyObserver(
			BMotionStudioSession bmsSession, JsonElement jsonObserver);

}
