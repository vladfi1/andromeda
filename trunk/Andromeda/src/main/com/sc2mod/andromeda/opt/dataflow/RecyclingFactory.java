package com.sc2mod.andromeda.opt.dataflow;

import java.util.ArrayList;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * An abstract factory for types which allows to recycle
 * old instances.
 * 
 * New instances can be created by calling the create() method.
 * This method is protected so it must be internally called in order to
 * be used. This should ensure that initialization tasks can be done
 * which might need parameters. 
 * 
 * An element is returned to the factory by calling the release method.
 * Note that this factory DOES NOT check if the element is already in the
 * free list. This means you must take care that you never call release
 * twice for a node! Otherwise you will get horrible bugs.
 * 
 * @author gex
 *
 * @param <T>
 */
public abstract class RecyclingFactory<T> {

	private ArrayList<T> freeNodes;
	
	protected abstract T createNew();
	
	protected RecyclingFactory(int initialListSize){
		freeNodes = new ArrayList<T>(256);
	}
	
	protected T create(){
		T node;
		if(freeNodes.isEmpty()){
			node = createNew();
		} else {
			node = freeNodes.remove(freeNodes.size()-1);
		}
		return node;
	}
	
	public void release(T node){
		freeNodes.add(node);
	}
}
