/**
 * 
 */
package de.prob.animator;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.prob.parser.ISimplifiedROMap;

/**
 * The default implementation of {@link SimplifiedROMap}, which is just a
 * wrapper around a {@link Map}.
 * 
 * @author plagge
 */
class SimplifiedROMap<K, V> implements ISimplifiedROMap<K, V> {
	private final Map<K, V> map;

	public SimplifiedROMap(final Map<K, V> map) {
		this.map = map;
	}

	@Override
	public V get(final K key) {
		return map.get(key);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Set<Entry<K, V>> entrySet = map.entrySet();
		for (Entry<K, V> entry : entrySet) {
			sb.append(entry.getKey());
			sb.append("->");
			sb.append(entry.getValue());
			sb.append(" ");
		}
		return sb.toString();
	}
}
