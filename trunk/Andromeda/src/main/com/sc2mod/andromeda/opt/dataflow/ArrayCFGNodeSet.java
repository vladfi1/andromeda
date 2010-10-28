package com.sc2mod.andromeda.opt.dataflow;

import java.util.Iterator;

public class ArrayCFGNodeSet implements ICFGNodeSet {
	
	private CFGNode[] nodes;
	private int size = 0;
	
	public ArrayCFGNodeSet(){
		nodes = new CFGNode[4];
	}

	@Override
	public void add(CFGNode add) {
		for(int i=0, size=this.size ; i<size ; i++){
			if(nodes[i]==add) return;
		}
		if(size == nodes.length){
			CFGNode[] newArray = new CFGNode[nodes.length<<1];
			System.arraycopy(nodes, 0, newArray, 0, nodes.length);
			nodes = newArray;
		}
		nodes[size++] = add;
	}

	@Override
	public void addSet(ICFGNodeSet set) {
		for(CFGNode c : set){
			add(c);
		}
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public CFGNode peek() {
		if(size != 0) return nodes[size-1];
		return null;
	}

	@Override
	public CFGNode pop() {
		if(size != 0) return nodes[(--size)-1];
		throw new RuntimeException("pop on empty set");
	}

	@Override
	public Iterator<CFGNode> iterator() {
		return new Iterator<CFGNode>() {
			private int pos = 0;
			
			@Override
			public void remove() {
				throw new Error("Remove not supported!");
			}
			
			@Override
			public CFGNode next() {
				if(pos >= size) return null;
				return nodes[pos++];
			}
			
			@Override
			public boolean hasNext() {
				return pos < size;
			}
		};
	}

	@Override
	public void clear() {
		//we do no nulling here, since CFGNodes are reused anyway, so there are no GC issues
		size = 0;
	}
	
	

}
