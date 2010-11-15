package com.sc2mod.andromeda.util;

import java.util.Iterator;

public class SingleValueIterator<V> implements Iterator<V> {

	private V v;
	
	public SingleValueIterator(V v){
		this.v = v;
	}

	@Override
	public boolean hasNext() {
		return v != null;
	}

	@Override
	public V next() {
		V v = this.v;
		this.v = null;
		return v;
	}

	@Override
	public void remove() {
		throw new Error("Remove not supported!");
	}
	
	
}
