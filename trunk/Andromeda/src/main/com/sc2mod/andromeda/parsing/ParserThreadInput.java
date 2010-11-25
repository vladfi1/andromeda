package com.sc2mod.andromeda.parsing;

import java.util.List;

import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public abstract class ParserThreadInput {
	
	private Source src;
	private InclusionType incType;
	private SyntaxNode importedBy;
	private String anticipatedName;

	protected ParserThreadInput(Source src, InclusionType incType, String anticipatedName, SyntaxNode importedBy){
		this.src = src;
		this.incType = incType;
		this.anticipatedName = anticipatedName;
		this.importedBy = importedBy;
	}

	public abstract void connect(SourceFileNode f);
	
	public Source getSource(){
		return src;
	}
	
	public InclusionType getInclusionType(){
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
}