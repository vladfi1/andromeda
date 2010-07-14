/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.syntaxNodes;

public class AndromedaFile extends SyntaxNode {

  private SyntaxNode parent;
  private PackageDecl packageDecl;
  private FileContent imports;
  private FileContent content;
  private com.sc2mod.andromeda.environment.Scope scope;
  private com.sc2mod.andromeda.parsing.AndromedaFileInfo fileInfo;

  public AndromedaFile (PackageDecl packageDecl, FileContent imports, FileContent content) {
    this.packageDecl = packageDecl;
    if (packageDecl != null) packageDecl.setParent(this);
    this.imports = imports;
    if (imports != null) imports.setParent(this);
    this.content = content;
    if (content != null) content.setParent(this);
  }

  public PackageDecl getPackageDecl() {
    return packageDecl;
  }

  public void setPackageDecl(PackageDecl packageDecl) {
    this.packageDecl = packageDecl;
  }

  public FileContent getImports() {
    return imports;
  }

  public void setImports(FileContent imports) {
    this.imports = imports;
  }

  public FileContent getContent() {
    return content;
  }

  public void setContent(FileContent content) {
    this.content = content;
  }

  public com.sc2mod.andromeda.environment.Scope getScope() {
    return scope;
  }

  public void setScope(com.sc2mod.andromeda.environment.Scope scope) {
    this.scope = scope;
  }

  public com.sc2mod.andromeda.parsing.AndromedaFileInfo getFileInfo() {
    return fileInfo;
  }

  public void setFileInfo(com.sc2mod.andromeda.parsing.AndromedaFileInfo fileInfo) {
    this.fileInfo = fileInfo;
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (packageDecl != null) packageDecl.accept(visitor);
    if (imports != null) imports.accept(visitor);
    if (content != null) content.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (packageDecl != null) packageDecl.traverseTopDown(visitor);
    if (imports != null) imports.traverseTopDown(visitor);
    if (content != null) content.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (packageDecl != null) packageDecl.traverseBottomUp(visitor);
    if (imports != null) imports.traverseBottomUp(visitor);
    if (content != null) content.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("AndromedaFile(\n");
      if (packageDecl != null)
        buffer.append(packageDecl.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (imports != null)
        buffer.append(imports.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (content != null)
        buffer.append(content.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [AndromedaFile]");
    return buffer.toString();
  }
}
