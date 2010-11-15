package com.sc2mod.andromeda.environment.annotations;

import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class Annotation implements SemanticsElement, IDefined{
	
	private AnnotationNode an;
	private IAnnotationType at;
	
	public Annotation(IAnnotationType atype, AnnotationNode an){
		this.an = an;
		this.at = atype;
	}
	
	public IAnnotationType getAnnotationType(){
		return at;
	}
	

	@Override
	public void accept(VoidSemanticsVisitor visitor) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P> void accept(NoResultSemanticsVisitor<P> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public <P, R> R accept(ParameterSemanticsVisitor<P, R> visitor, P state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public AnnotationNode getDefinition() {
		return an;
	}

}
