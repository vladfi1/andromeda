package com.sc2mod.andromeda.util.visitors;

import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.SourceFileInfo;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public class TraceScopeScanVisitor extends VoidTreeScanVisitor{

	
	public Scope curScope;
	public SourceFileInfo curFileInfo;
	public Type curType;

	@Override
	public void visit(SourceFileNode andromedaFile) {
		//An included file brings a new scope
		Scope scopeBefore = curScope;
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
		Scope scopeBefore = curScope;
		Type typeBefore = curType;
		
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
		Type typeBefore = curType;
		Scope scopeBefore = curScope;
		
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
