package com.sc2mod.andromeda.util.visitors;

import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.parsing.SourceFileInfo;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;

public class TraceScopeScanVisitor extends VoidTreeScanVisitor{

	
	public IScope curScope;
	public SourceFileInfo curFileInfo;
	public IType curType;

	@Override
	public void visit(SourceFileNode andromedaFile) {
		//An included file brings a new scope
		IScope scopeBefore = curScope;
		curScope = andromedaFile.getSemantics();
		
		SourceFileInfo infoBefore = curFileInfo;
		curFileInfo = andromedaFile.getFileInfo();
		
		andromedaFile.childrenAccept(this);
		
		curFileInfo = infoBefore;
		curScope = scopeBefore;
	}
	
	protected void onClassVisit(ClassDeclNode c){}
	
	@Override
	public void visit(ClassDeclNode classDeclaration) {
		//Remember old type (in case we have nested classes)
		IScope scopeBefore = curScope;
		IType typeBefore = curType;
		
		//Set current type
		curType = classDeclaration.getSemantics();
		curScope = curType;
		
		//do additional stuff
		onClassVisit(classDeclaration);
		
		//Process body
		classDeclaration.childrenAccept(this);
		
		//Reset to type before
		curType = typeBefore;
		curScope = scopeBefore;
	}
	
	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		//Remember old type (in case we have nested classes)
		IType typeBefore = curType;
		IScope scopeBefore = curScope;
		
		//Set current type
		Enrichment en = enrichDeclaration.getSemantics();
		curType = en.getEnrichedType();
		curScope = en;
		
		//Process body
		enrichDeclaration.getBody().accept(this);
		
		//Reset to type before
		curType = typeBefore;
		curScope = scopeBefore;
	}
}
