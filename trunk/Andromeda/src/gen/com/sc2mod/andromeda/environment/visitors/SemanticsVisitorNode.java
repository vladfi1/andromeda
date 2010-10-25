package com.sc2mod.andromeda.environment.visitors;
public interface SemanticsVisitorNode {

	public void accept(VoidSemanticsVisitor visitor);
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state);
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state);

}
