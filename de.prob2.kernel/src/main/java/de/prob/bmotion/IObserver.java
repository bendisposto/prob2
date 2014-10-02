package de.prob.bmotion;

import com.google.gson.JsonElement;
import de.prob.bmotion.BMotionObserver;

public interface IObserver {

	public BMotionObserver getBMotionObserver(JsonElement jsonObserver);

}
