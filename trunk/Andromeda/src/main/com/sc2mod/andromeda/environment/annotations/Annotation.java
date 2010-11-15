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
	public AnnotationNode getDefinition() {
		return an;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
