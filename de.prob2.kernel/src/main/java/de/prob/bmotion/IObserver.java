package de.prob.bmotion;

import com.google.gson.JsonElement;

public interface IObserver {

	public BMotionObserver getBMotionObserver(JsonElement jsonObserver);

}
