package de.prob.bmotion;

import com.google.gson.JsonElement;

public interface IObserver {

	public IBMotionTransformer getBMotionObserver(JsonElement jsonObserver);

}
