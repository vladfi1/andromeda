package com.sc2mod.andromeda.environment.scopes.content;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.UsageType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.generic.GenericTypeInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericUtil;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class GenericContentSet extends InheritableContentSet {

	private GenericTypeInstance type;
	
	public GenericContentSet(IType scope, GenericTypeInstance type) {
		super(scope);
		this.type = type;
	}
	
	private void copyOnDemand() {
		if(type.hasCopiedDownContent())
			return;
		
		type.setCopiedDownContent();
		
		if(!type.getGenericParent().hasCopiedDownContent()){
			throw new InternalError("Copying down content from a type which hasn't copied down content yet");
		}
		System.out.println("Copying down for " + type);
		GenericUtil.copyContentFromGenericParent(type.getTypeProvider(),this, type.getGenericParent().getContent(), type.getTypeArguments());
	}
	
	@Override
	public Iterator<IScopedElement> getDeepIterator(boolean includeOperations,
			boolean includeSubpackaes) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public Iterable<IScopedElement> iterateDeep(boolean includeOperations,
			boolean includeSubpackaes) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	IScopedElement resolve(String name, IScope from, UsageType accessType,
			Signature sig, SyntaxNode where,
			EnumSet<ScopedElementType> allowedTypes) {
		copyOnDemand();
		return super.resolve(name, from, accessType, sig, where, allowedTypes);
	}



	@Override
	public Set<Entry<String, IScopedElement>> viewEntries() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public Collection<IScopedElement> viewValues() {
		copyOnDemand();
		return super.viewValues();
	}
	
	@Override
	protected HashMap<String, IScopedElement> getContentSet() {
		copyOnDemand();
		return super.getContentSet();
	}

	

}
