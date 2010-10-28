package com.sc2mod.andromeda.util;

import java.util.ArrayList;

public class ArrayStack<E> extends ArrayList<E>{
	
	public E pop(){
		return remove(size()-1);
	}
}
