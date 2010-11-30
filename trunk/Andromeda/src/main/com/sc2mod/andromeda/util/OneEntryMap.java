package com.sc2mod.andromeda.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Map that is optimized for having only up to one entry in most situations.
 * 
 * If there is only one entry, it is directly saved in a class field. Once there
 * are more entries, a hash map is created and all calls are delegated to it.
 * @author gex
 *
 */
public class OneEntryMap<K,V> implements Map<K, V> {

	private K key;
	private V value;
	private HashMap<K, V> delegate;
	private int modCount;
	
	@Override
	public void clear() {
		delegate = null;
		key = null;
		value = null;
		modCount++;
	}

	@Override
	public boolean containsKey(Object key) {
		if(delegate == null)
			return key != null && key.equals(this.key);
		return delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		if(delegate == null)
			return value != null && value.equals(this.value);
		return delegate.containsValue(value);
	}



	@Override
	public V get(Object key) {
		if(delegate != null){
			return delegate.get(key);
		}
		
		if(this.key != null && this.key.equals(key)){
			return this.value;
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return key == null && delegate == null;
	}



	@Override
	public V put(K key, V value) {
		if(delegate != null){
			return delegate.put(key, value);
		}
		modCount++;
		if(this.key == null || this.key.equals(key)){
			this.key = key;
			V val = this.value;
			this.value = value;
			return val;
		} else {
			delegate = new HashMap<K, V>();
			delegate.put(key, value);
			delegate.put(this.key, this.value);
			this.key = null;
			this.value = null;
			return null;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> e : m.entrySet()){
			put(e.getKey(),e.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		V val;
		if(delegate != null){
			return delegate.remove(key);
		} 
		
		if(this.key != null && this.key.equals(key)){
			this.key = null;
			val = this.value;
			this.value = null;
			modCount++;
			return val;
		} else {
			return null;
		}
		
	}

	@Override
	public int size() {
		if(delegate != null)
			return delegate.size();
		if(key != null)
			return 1;
		return 0;
	}

	@Override
	public Collection<V> values() {
		if(delegate != null){
			return delegate.values();
		}
		return new ValueBag();
	}
	
	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	private class ValueBag implements Collection<V>{

		private Collection<V> vDelegate;
		private boolean checkDelegate(){
			if(vDelegate == null && delegate != null){
				vDelegate = delegate.values();
			}
			return vDelegate != null;
		}
		
		@Override
		public boolean add(V e) {
			throw new Error("Not allowed!");
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			throw new Error("Not allowed!");
		}

		@Override
		public void clear() {
			OneEntryMap.this.clear();
		}

		@Override
		public boolean contains(Object o) {
			return OneEntryMap.this.containsValue(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for(Object o : c){
				if(!contains(o))
					return false;
			}
			return true;
		}

		@Override
		public boolean isEmpty() {
			return OneEntryMap.this.isEmpty();
		}

		@Override
		public Iterator<V> iterator() {
			if(checkDelegate()){
				return vDelegate.iterator();
			}
			return new SingleValueIterator<V>(value);
		}

		@Override
		public boolean remove(Object o) {
			throw new Error("Not allowed!");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new Error("Not allowed!");
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new Error("Not allowed!");
		}

		@Override
		public int size() {
			return OneEntryMap.this.size();
		}

		@Override
		public Object[] toArray() {
			if(checkDelegate()){
				return vDelegate.toArray();
			}
			Object[] o;
			if(key != null){
				o = new Object[1];
				o[0] = value;
			} else {
				o = new Object[0];
			}
			return o;
		}
			

		@Override
		public <T> T[] toArray(T[] a) {
			throw new Error("3");
//			if(checkDelegate()){
//				return vDelegate.toArray(a);
//			}
//
//			if(key != null){
//				if(a.length < 1){
//					a = (T[]) new Object[1];
//				}
//				a[0] = value;
//			} else {
//			}
//			return o;
		}
		
	}
}

