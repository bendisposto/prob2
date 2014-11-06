package de.prob.bmotion;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class VisualisationRegistry {

    private final Map<String, AbstractBMotionStudioSession> registry = new HashMap<String, AbstractBMotionStudioSession>();

    public void register(final String name, final AbstractBMotionStudioSession bmsSession) {
        registry.put(name, bmsSession);
    }

    public void unregister(final String name) {
        registry.remove(name);
    }

    public AbstractBMotionStudioSession get(String name) {
        return registry.get(name);
    }

}
