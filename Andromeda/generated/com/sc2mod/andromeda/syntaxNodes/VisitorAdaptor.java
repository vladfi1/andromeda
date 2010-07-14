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

public abstract class VisitorAdaptor implements Visitor {

  public void visit(LiteralType literalType) { visit(); }
  public void visit(TypeCategory typeCategory) { visit(); }
  public void visit(ModifierType modifierType) { visit(); }
  public void visit(ClassMemberType classMemberType) { visit(); }
  public void visit(AccessType accessType) { visit(); }
  public void visit(AssignmentType assignmentType) { visit(); }
  public void visit(UnaryOperator unaryOperator) { visit(); }
  public void visit(BinaryOperator binaryOperator) { visit(); }
  public void visit(AssignmentOperatorType assignmentOperatorType) { visit(); }
  public void visit(FileContent fileContent) { visit(); }
  public void visit(Modifiers modifiers) { visit(); }
  public void visit(AnnotationList annotationList) { visit(); }
  public void visit(TypeParamList typeParamList) { visit(); }
  public void visit(TypeList typeList) { visit(); }
  public void visit(ClassBody classBody) { visit(); }
  public void visit(VariableDeclarators variableDeclarators) { visit(); }
  public void visit(ParameterList parameterList) { visit(); }
  public void visit(VariableInitializers variableInitializers) { visit(); }
  public void visit(StatementList statementList) { visit(); }
  public void visit(ExpressionList expressionList) { visit(); }
  public void visit(AndromedaFile andromedaFile) { visit(); }
  public void visit(PackageDecl packageDecl) { visit(); }
  public void visit(Literal literal) { visit(); }
  public void visit(Annotation annotation) { visit(); }
  public void visit(TypeParam typeParam) { visit(); }
  public void visit(VariableDeclaratorId variableDeclaratorId) { visit(); }
  public void visit(AccessorBody accessorBody) { visit(); }
  public void visit(MethodHeader methodHeader) { visit(); }
  public void visit(MethodDeclarator methodDeclarator) { visit(); }
  public void visit(Parameter parameter) { visit(); }
  public void visit(ArrayInitializer arrayInitializer) { visit(); }
  public void visit(LocalVariableDeclaration localVariableDeclaration) { visit(); }
  public void visit(Dims dims) { visit(); }
  public void visit(GlobalStructure globalStructure) { visit(); }
  public void visit(ClassDeclaration classDeclaration) { visit(); }
  public void visit(EnrichDeclaration enrichDeclaration) { visit(); }
  public void visit(FunctionDeclaration functionDeclaration) { visit(); }
  public void visit(GlobalVarDeclaration globalVarDeclaration) { visit(); }
  public void visit(GlobalInitDeclaration globalInitDeclaration) { visit(); }
  public void visit(InterfaceDeclaration interfaceDeclaration) { visit(); }
  public void visit(StructDeclaration structDeclaration) { visit(); }
  public void visit(IncludedFile includedFile) { visit(); }
  public void visit(TypeAlias typeAlias) { visit(); }
  public void visit(TypeExtension typeExtension) { visit(); }
  public void visit(InstanceLimitSetter instanceLimitSetter) { visit(); }
  public void visit(Type type) { visit(); }
  public void visit(BasicType basicType) { visit(); }
  public void visit(SimpleType simpleType) { visit(); }
  public void visit(QualifiedType qualifiedType) { visit(); }
  public void visit(ArrayType arrayType) { visit(); }
  public void visit(PointerType pointerType) { visit(); }
  public void visit(FuncPointer funcPointer) { visit(); }
  public void visit(ClassMemberDeclaration classMemberDeclaration) { visit(); }
  public void visit(MethodDeclaration methodDeclaration) { visit(); }
  public void visit(FieldDeclaration fieldDeclaration) { visit(); }
  public void visit(StaticInitDeclaration staticInitDeclaration) { visit(); }
  public void visit(AccessorDeclaration accessorDeclaration) { visit(); }
  public void visit(VariableDeclarator variableDeclarator) { visit(); }
  public void visit(VariableDecl variableDecl) { visit(); }
  public void visit(VariableAssignDecl variableAssignDecl) { visit(); }
  public void visit(VariableArrayInitDecl variableArrayInitDecl) { visit(); }
  public void visit(Statement statement) { visit(); }
  public void visit(BlockStatement blockStatement) { visit(); }
  public void visit(ExpressionStatement expressionStatement) { visit(); }
  public void visit(LocalTypeDeclarationStatement localTypeDeclarationStatement) { visit(); }
  public void visit(LocalVariableDeclarationStatement localVariableDeclarationStatement) { visit(); }
  public void visit(IfThenElseStatement ifThenElseStatement) { visit(); }
  public void visit(WhileStatement whileStatement) { visit(); }
  public void visit(DoWhileStatement doWhileStatement) { visit(); }
  public void visit(ForStatement forStatement) { visit(); }
  public void visit(ForCountStatement forCountStatement) { visit(); }
  public void visit(ForEachStatement forEachStatement) { visit(); }
  public void visit(BreakStatement breakStatement) { visit(); }
  public void visit(ContinueStatement continueStatement) { visit(); }
  public void visit(ReturnStatement returnStatement) { visit(); }
  public void visit(DeleteStatement deleteStatement) { visit(); }
  public void visit(ThrowStatement throwStatement) { visit(); }
  public void visit(ExplicitConstructorInvocationStatement explicitConstructorInvocationStatement) { visit(); }
  public void visit(EmptyStatement emptyStatement) { visit(); }
  public void visit(Expression expression) { visit(); }
  public void visit(Assignment assignment) { visit(); }
  public void visit(FieldAccess fieldAccess) { visit(); }
  public void visit(ArrayAccess arrayAccess) { visit(); }
  public void visit(ConditionalExpression conditionalExpression) { visit(); }
  public void visit(BinaryExpression binaryExpression) { visit(); }
  public void visit(UnaryExpression unaryExpression) { visit(); }
  public void visit(InstanceofExpression instanceofExpression) { visit(); }
  public void visit(CastExpression castExpression) { visit(); }
  public void visit(SuperExpression superExpression) { visit(); }
  public void visit(ThisExpression thisExpression) { visit(); }
  public void visit(MethodInvocation methodInvocation) { visit(); }
  public void visit(ArrayCreationExpression arrayCreationExpression) { visit(); }
  public void visit(ClassInstanceCreationExpression classInstanceCreationExpression) { visit(); }
  public void visit(LiteralExpression literalExpression) { visit(); }
  public void visit(MetaClassExpression metaClassExpression) { visit(); }
  public void visit(ParenthesisExpression parenthesisExpression) { visit(); }
  public void visit(KeyOfExpression keyOfExpression) { visit(); }

  public void visit() { }

}
