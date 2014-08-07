package de.prob.bmotion;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;


public interface IObserver {

	public IBMotionGroovyObserver getBMotionGroovyObserver(
			BMotionStudioSession bmsSession, JsonElement jsonObserver);

	public String getModelData(String dataParameter, HttpServletRequest req);

}
