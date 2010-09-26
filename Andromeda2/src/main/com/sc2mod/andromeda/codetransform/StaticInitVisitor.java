package com.sc2mod.andromeda.codetransform;

import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
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
	public void visit(StaticInitDeclNode staticInitDeclaration) {
		visitor.visit(staticInitDeclaration);
	}

	@Override
	public void visit(SourceFileNode andromedaFile) {
		andromedaFile.childrenAccept(this);
	}

	@Override
	public void visit(IncludeNode includedFile) {
		includedFile.childrenAccept(this);
	}
	
	@Override
	public void visit(MemberDeclListNode classBody) {
		classBody.childrenAccept(this);
	}

	@Override
	public void visit(InterfaceDeclNode interfaceDeclaration) {
		interfaceDeclaration.childrenAccept(this);
	}

	@Override
	public void visit(ClassDeclNode classDeclaration) {
		classDeclaration.childrenAccept(this);
	}

	@Override
	public void visit(GlobalStructureListNode fileContent) {
		fileContent.childrenAccept(this);
	}

	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		enrichDeclaration.getBody().childrenAccept(this);
	}
}
