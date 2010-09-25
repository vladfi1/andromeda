package com.sc2mod.andromeda.codetransform;

import com.sc2mod.andromeda.syntaxNodes.SourceFile;
import com.sc2mod.andromeda.syntaxNodes.ClassBody;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.IncludedFile;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclaration;
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
	public void visit(SourceFile andromedaFile) {
		andromedaFile.childrenAccept(this);
	}

	@Override
	public void visit(IncludedFile includedFile) {
		includedFile.childrenAccept(this);
	}
	
	@Override
	public void visit(ClassBody classBody) {
		classBody.childrenAccept(this);
	}

	@Override
	public void visit(InterfaceDeclaration interfaceDeclaration) {
		interfaceDeclaration.childrenAccept(this);
	}

	@Override
	public void visit(ClassDeclaration classDeclaration) {
		classDeclaration.childrenAccept(this);
	}

	@Override
	public void visit(FileContent fileContent) {
		fileContent.childrenAccept(this);
	}

	@Override
	public void visit(EnrichDeclaration enrichDeclaration) {
		enrichDeclaration.getBody().childrenAccept(this);
	}
}
