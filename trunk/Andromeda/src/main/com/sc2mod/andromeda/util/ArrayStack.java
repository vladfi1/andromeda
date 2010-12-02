package com.sc2mod.andromeda.util;

import java.util.ArrayList;

public class ArrayStack<E> extends ArrayList<E>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public E pop(){
		return remove(size()-1);
	}

	public void push(E e) {
		add(e);
	}

	public E peek() {
		int size = size();
		if(size == 0){
			return null;
		}
		return get(size()-1);
	}
}
