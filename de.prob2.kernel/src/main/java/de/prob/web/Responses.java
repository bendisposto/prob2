package de.prob.web;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.prob.web.data.Message;

/**
 * 
 * @author bendisposto
 * 
 */
public class Responses {

	private final Cache<Integer, Message> responseCache;
	private int itemcount = 0;

	public Responses() {
		this(5, TimeUnit.SECONDS);
	}

	public Responses(int timeout, TimeUnit unit) {
		responseCache = CacheBuilder.newBuilder()
				.expireAfterWrite(timeout, unit).build();
	}

	public int size() {
		return itemcount;
	}

	public boolean isEmpty() {
		return itemcount == 0;
	}

	public Message get(int i) throws ReloadRequiredException {
		Message v = responseCache.getIfPresent(i);
		if (v == null)
			throw new ReloadRequiredException();
		return v;
	}

	public void add(Message message) {
		responseCache.put(itemcount++, message);
	}
	
	public void reset() {
		responseCache.invalidateAll();
		itemcount = 0;
	}

}
