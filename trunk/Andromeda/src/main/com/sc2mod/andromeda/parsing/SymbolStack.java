/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.sc2mod.andromeda.parser.cup.Symbol;
import com.sc2mod.andromeda.problems.InternalProgramError;


/**
 * Optimized speedy symbol stack.
 * @author J. 'gex' Finis
 *
 */
public class SymbolStack extends Stack<Symbol>{

	private Symbol[] stack = new Symbol[2048];
	private int stackPointer = 0;
	public Symbol elementAt(int i) {
		return stack[i];
	}

	@Override
	public boolean empty() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Symbol peek() {
		return stack[stackPointer-1];
	}

	@Override
	public synchronized Symbol pop() {
		return stack[--stackPointer];
	}

	@Override
	public Symbol push(Symbol item) {
		stack[stackPointer++] = item;
		return item;
	}

	@Override
	public synchronized int search(Object o) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public void add(int index, Symbol element) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean add(Symbol e) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Symbol> c) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean addAll(int index, Collection<? extends Symbol> c) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void addElement(Symbol obj) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized int capacity() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Object clone() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void copyInto(Object[] anArray) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public Enumeration<Symbol> elements() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void ensureCapacity(int minCapacity) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean equals(Object o) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Symbol firstElement() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Symbol get(int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized int hashCode() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized int indexOf(Object o, int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void insertElementAt(Symbol obj, int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean isEmpty() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Symbol lastElement() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized int lastIndexOf(Object o, int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Symbol remove(int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void removeAllElements() {
		stackPointer = 0;
	}

	@Override
	public synchronized boolean removeElement(Object obj) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void removeElementAt(int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	protected synchronized void removeRange(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Symbol set(int index, Symbol element) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void setElementAt(Symbol obj, int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void setSize(int newSize) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized int size() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized List<Symbol> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized Object[] toArray() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized String toString() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public synchronized void trimToSize() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public Iterator<Symbol> iterator() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public ListIterator<Symbol> listIterator() {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}

	@Override
	public ListIterator<Symbol> listIterator(int index) {
		// TODO Auto-generated method stub
		throw new InternalProgramError("Not implemented!");
	}
	
	
	
}
