package com.sc2mod.andromeda.util;

import java.util.ArrayList;

import com.sc2mod.andromeda.parsing.ParserThread;

public class ArrayStack<E> extends ArrayList<E>{
	
	public E pop(){
		return remove(size()-1);
	}

	public void push(E e) {
		add(e);
	}
}
