package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.types.ArrayType;
import com.sc2mod.andromeda.environment.types.generic.GenericStructInstance;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;

public class StructIndexCalculationVisitor extends ParameterSemanticsVisitorAdapter<Void, Integer>{

	@Override
	public Integer visitDefault(SemanticsElement semanticsElement, Void state) {
		return -1;
	}
	
	@Override
	public Integer visit(ArrayType arrayType, Void state) {
		return arrayType.getWrappedType().accept(this,null);
	}
	
	@Override
	public Integer visit(GenericStructInstance genericStructInstance, Void state) {
		return genericStructInstance.getStructId();
	}
	
	@Override
	public Integer visit(StructImpl structImpl, Void state) {
		return structImpl.getStructId();
	}
}
