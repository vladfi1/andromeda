package com.sc2mod.andromeda.parsing;

import java.io.File;

import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.syntaxNodes.CompilationUnitIdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ImportNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.PackageDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.Pair;

public class ImportResolver {

	private SourceManager sourceManager;

	public ImportResolver(SourceManager sourceManager){
		this.sourceManager = sourceManager;
	}
	
	public static String getImportString(ImportNode i){
		return dotNameFromInputName(i.getImportName()).toString();
	}
	
	public Pair<Source, InclusionType> resolveImport(String qualifiedName, SyntaxNode where){
		return sourceManager.resolveInclude(qualifiedName, where);
	}
	
	public Pair<Source, InclusionType> resolveImport(ImportNode i){
		String name = nameFromInputName(i.getImportName()).toString();
		return resolveImport(name, i);
	}
	
	private static StringBuilder nameFromInputName(CompilationUnitIdentifierNode importName) {
		if(importName.getPrefix() != null){
			 return nameFromInputName(importName.getPrefix()).append(File.separator).append(importName.getName().getId());
		} else {
			return new StringBuilder(importName.getName().getId());
		}
	}
	
	private static StringBuilder dotNameFromInputName(CompilationUnitIdentifierNode importName) {
		if(importName.getPrefix() != null){
			 return dotNameFromInputName(importName.getPrefix()).append(".").append(importName.getName().getId());
		} else {
			return new StringBuilder(importName.getName().getId());
		}
	}

	public Pair<Source, InclusionType> resolveInclude(IncludeNode i){
		String name = i.getName();
		return sourceManager.resolveInclude(name, i);
	}
	
	public String resolvePackage(PackageDeclNode pdn){
		IdentifierNode id = pdn.getUnitName();
		if(id == null){
			return null;
		}
		String pkg = (pdn.getPackageName() == null ? new StringBuilder() : dotNameFromInputName(pdn.getPackageName()).append("."))
			.append(id.getId()).toString();
		return pkg;
		
	}
}
