package com.sc2mod.andromeda.util.visitors;

import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.NoResultVisitorAdapter;

public class VoidResultErrorVisitor<P> extends NoResultVisitorAdapter<P>{

	public void visitDefault(SyntaxNode s, P state) {
		throw new InternalProgramError(s,"Unimplemented method: Visitor " + this.getClass().getSimpleName() +
										" does not define visit for class " + s.getClass().getSimpleName());
	}
}
