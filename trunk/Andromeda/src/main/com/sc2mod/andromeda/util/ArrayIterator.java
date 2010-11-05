package com.sc2mod.andromeda.util;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {

	private T[] array;
	private int position;
	
	public ArrayIterator(T[] array){
		this.array = array;
	}
	
	@Override
	public boolean hasNext() {
		return position<array.length;
	}

	@Override
	public T next() {
		return array[position++];
	}

	@Override
	public void remove() {
		throw new Error("Remove not supported!");
	}

	
}
