/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * OrderedBiMap.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jorlib.frameworks.columnGeneration.util;

import java.util.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Bidirectional map. Contains several methods to get lists/arrays of the keys/values which allow deterministic iteration ordering.
 * This class is more flexible than Guava's {@code BiMap<K,V>} or Oracle's {@code LinkedHashMap<K,V>}. The map cannot hold duplicate keys!
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 * @param <K> Key
 * @param <V> Value
 */
public class OrderedBiMap<K,V> extends AbstractMap<K, V>{
	private final HashBiMap<K, V> biMap;
	private final ArrayList<K> keys;
	private final ArrayList<V> values;
	
	public OrderedBiMap(){
		biMap=HashBiMap.create();
		keys=new ArrayList<>();
		values=new ArrayList<>();
	}
	
	/**
	 * Put a key, value pair in the map. The key cannot be null, nor can you insert duplicate keys.
	 * @param  key key
	 * @param value value
	 */
	public V put(K key, V value){
		if(key==null)
			throw new IllegalArgumentException("Cannot insert null as a key in an OrderedBiMap");
		if(biMap.containsKey(key)){
			throw new RuntimeException("Should not override a key");
		}else{
			this.biMap.put(key, value);
			keys.add(key);
			values.add(value);
			return null;
		}
	}
	
	/**
	 * Get a key from the map (O(1))
	 * @param key key
	 */
	public V get(Object key){
		return biMap.get(key);
	}
	
	/**
	 * Remove a key. Removing keys is relatively expensive O(n) time, where n is the number of items in the list.
	 * @return returns null if they key was contained in the map, returns V
	 */
	public V remove(Object k){
		if(!this.containsKey(k))
			return null;
		int index=this.keys.indexOf(k);
		keys.remove(index);
		values.remove(index);
		return biMap.remove(k);
	}
	
	/**
	 * Returns whether the key is contained in the map. Runtime: O(1)
	 * @return whether the key is contained in the map. Runtime: O(1)
	 */
	public boolean containsKey(Object key){
		return biMap.containsKey(key);
	}
	
	/**
	 * Returns a set view of the map.
	 * @return a set view of the map.
	 */
	public Set<K> keySet(){
		return biMap.keySet();
	}
	
	/**
	 * Returns an unmodifiable list of the keys. Convenient for deterministic iterations
	 * @return an unmodifiable list of the keys. Convenient for deterministic iterations
	 */
	public List<K> keyList(){
		return Collections.unmodifiableList(keys);
	}
	
	/**
	 * Returns an unmodifiable Collection of the values. Convenient for deterministic iterations
	 * @return an unmodifiable Collection of the values. Convenient for deterministic iterations
	 */
	public Collection<V> values(){
		return Collections.unmodifiableCollection(values);
	}
	
	
	/**
	 * Returns the keys of this map as an array
	 * @param a array which will contain the keys
	 * @return keys array
	 */
	public K[] getKeysAsArray(K[] a){
		return keys.toArray(a);
	}
	
	/**
	 * Returns the values of this map as an array
	 * @param a array which will contain the values
	 * @return value array
	 */
	public V[] getValuesAsArray(V[] a){
		return values.toArray(a);
	}

	/**
	 * Returns the entrySet
	 * @return the entrySet
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return biMap.entrySet();
	}
	
	/**
	 * Returns the size of the map
	 * @return the size of the map
	 */
	@Override
	public int size(){
		return biMap.size();
	}
	
	/**
	 * Creates an inverted {@code BiMap<V,K>}
	 * @return inverted map
	 */
	public BiMap<V,K> inverse(){
		return biMap.inverse();
	}

}
