package com.sc2mod.andromeda.codetransform;

import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.ClassBody;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.Visitor;
import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;

/**
 * Visits only StaticInitDeclarations, which are then passed on to another Visitor.
 * This is used on library files.
 * @author XPilot
 */
public class StaticInitVisitor extends VisitorAdaptor {

	private Visitor visitor;
	
	public StaticInitVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
	
	@Override
	public void visit(StaticInitDeclaration staticInitDeclaration) {
		visitor.visit(staticInitDeclaration);
	}

	@Override
	public void visit(AndromedaFile andromedaFile) {
		andromedaFile.childrenAccept(this);
	}

	@Override
	public void visit(ClassBody classBody) {
		classBody.childrenAccept(this);
	}

	@Override
	public void visit(ClassDeclaration classDeclaration) {
		classDeclaration.childrenAccept(this);
	}

	@Override
	public void visit(FileContent fileContent) {
		fileContent.childrenAccept(this);
	}

}
