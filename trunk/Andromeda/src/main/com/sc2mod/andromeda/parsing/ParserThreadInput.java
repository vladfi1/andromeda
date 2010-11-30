package com.sc2mod.andromeda.parsing;

import java.util.List;

import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.Pair;

public abstract class ParserThreadInput {
	
	private Source src;
	private InclusionType incType;
	private SyntaxNode importedBy;
	private String anticipatedName;
	private ImportResolver resolver;

	protected ParserThreadInput(Source src, InclusionType incType, String anticipatedName, SyntaxNode importedBy){
		this.src = src;
		this.incType = incType;
		this.anticipatedName = anticipatedName;
		this.importedBy = importedBy;
	}
	
	protected ParserThreadInput(String anticipatedName, SyntaxNode importedBy, ImportResolver resolver2){
		this.resolver = resolver2;
		this.anticipatedName = anticipatedName;
		this.importedBy = importedBy;
	}
	

	public abstract void connect(SourceFileNode f);
	
	public Source getSource(){
		resolveIfNecessary();
		return src;
	}
	
	private void resolveIfNecessary() {
		if(this.resolver != null){
			ImportResolver resolver = this.resolver;
			this.resolver = null;
			
			Pair<Source, InclusionType> src = resolver.resolveImport(anticipatedName, importedBy);
			if(src == null)
				throw Problem.ofType(ProblemId.IMPORTED_COMPILATION_UNIT_NOT_FOUND).details(anticipatedName)
					.at(importedBy)
					.raiseUnrecoverable();
			this.incType = src._2;
			this.src = src._1;
			
		}
	}

	public InclusionType getInclusionType(){
		resolveIfNecessary();
		return incType;
	}
	
	public String getImportName(){
		return anticipatedName;
	}
	
	public SyntaxNode getImportedBy(){
		return importedBy;
	}
}

class ParserInputFactory{
	
	public static ParserThreadInput create(final IncludeNode i, Source src, InclusionType incType, String anticipatedName){
		return new ParserThreadInput(src,incType,anticipatedName,i) {
			
			@Override
			public void connect(SourceFileNode f) {
				i.setIncludeContent(f);
			}
		};
		
	}
	
	public static ParserThreadInput create(final List<SourceFileNode> list, Source src, InclusionType incType, String anticipatedName, SyntaxNode importedBy){
		return new ParserThreadInput(src,incType, anticipatedName, importedBy) {
			
			@Override
			public void connect(SourceFileNode f) {
				list.add(f);
			}
		};
	}
	
	public static ParserThreadInput create(final List<SourceFileNode> list,String anticipatedName, SyntaxNode importedBy, ImportResolver resolver){
		return new ParserThreadInput(anticipatedName, importedBy,resolver) {
			
			@Override
			public void connect(SourceFileNode f) {
				list.add(f);
			}
		};
	}
}