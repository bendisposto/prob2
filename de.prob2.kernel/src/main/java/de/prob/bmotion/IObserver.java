package de.prob.bmotion;

import com.google.gson.JsonElement;

public interface IObserver {

	public BMotionTransformer getBMotionObserver(JsonElement jsonObserver);

}
