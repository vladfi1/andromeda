package com.sc2mod.andromeda.environment.types.casting;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;

class CastDecisionVisitorConcatenate extends ParameterSemanticsVisitorAdapter<IType, Boolean>{

	@Override
	public Boolean visitDefault(SemanticsElement semanticsElement, IType to) {
		IType from = (IType) semanticsElement;
		
		return CastUtil.canImplicitCast(from, to);
	}
}
