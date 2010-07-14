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

public interface Visitor {

  public void visit(LiteralType literalType);
  public void visit(TypeCategory typeCategory);
  public void visit(ModifierType modifierType);
  public void visit(ClassMemberType classMemberType);
  public void visit(AccessType accessType);
  public void visit(AssignmentType assignmentType);
  public void visit(UnaryOperator unaryOperator);
  public void visit(BinaryOperator binaryOperator);
  public void visit(AssignmentOperatorType assignmentOperatorType);
  public void visit(FileContent fileContent);
  public void visit(Modifiers modifiers);
  public void visit(AnnotationList annotationList);
  public void visit(TypeParamList typeParamList);
  public void visit(TypeList typeList);
  public void visit(ClassBody classBody);
  public void visit(VariableDeclarators variableDeclarators);
  public void visit(ParameterList parameterList);
  public void visit(VariableInitializers variableInitializers);
  public void visit(StatementList statementList);
  public void visit(ExpressionList expressionList);
  public void visit(AndromedaFile andromedaFile);
  public void visit(PackageDecl packageDecl);
  public void visit(Literal literal);
  public void visit(Annotation annotation);
  public void visit(TypeParam typeParam);
  public void visit(VariableDeclaratorId variableDeclaratorId);
  public void visit(AccessorBody accessorBody);
  public void visit(MethodHeader methodHeader);
  public void visit(MethodDeclarator methodDeclarator);
  public void visit(Parameter parameter);
  public void visit(ArrayInitializer arrayInitializer);
  public void visit(LocalVariableDeclaration localVariableDeclaration);
  public void visit(Dims dims);
  public void visit(GlobalStructure globalStructure);
  public void visit(ClassDeclaration classDeclaration);
  public void visit(EnrichDeclaration enrichDeclaration);
  public void visit(FunctionDeclaration functionDeclaration);
  public void visit(GlobalVarDeclaration globalVarDeclaration);
  public void visit(GlobalInitDeclaration globalInitDeclaration);
  public void visit(InterfaceDeclaration interfaceDeclaration);
  public void visit(StructDeclaration structDeclaration);
  public void visit(IncludedFile includedFile);
  public void visit(TypeAlias typeAlias);
  public void visit(TypeExtension typeExtension);
  public void visit(InstanceLimitSetter instanceLimitSetter);
  public void visit(Type type);
  public void visit(BasicType basicType);
  public void visit(SimpleType simpleType);
  public void visit(QualifiedType qualifiedType);
  public void visit(ArrayType arrayType);
  public void visit(PointerType pointerType);
  public void visit(FuncPointer funcPointer);
  public void visit(ClassMemberDeclaration classMemberDeclaration);
  public void visit(MethodDeclaration methodDeclaration);
  public void visit(FieldDeclaration fieldDeclaration);
  public void visit(StaticInitDeclaration staticInitDeclaration);
  public void visit(AccessorDeclaration accessorDeclaration);
  public void visit(VariableDeclarator variableDeclarator);
  public void visit(VariableDecl variableDecl);
  public void visit(VariableAssignDecl variableAssignDecl);
  public void visit(VariableArrayInitDecl variableArrayInitDecl);
  public void visit(Statement statement);
  public void visit(BlockStatement blockStatement);
  public void visit(ExpressionStatement expressionStatement);
  public void visit(LocalTypeDeclarationStatement localTypeDeclarationStatement);
  public void visit(LocalVariableDeclarationStatement localVariableDeclarationStatement);
  public void visit(IfThenElseStatement ifThenElseStatement);
  public void visit(WhileStatement whileStatement);
  public void visit(DoWhileStatement doWhileStatement);
  public void visit(ForStatement forStatement);
  public void visit(ForCountStatement forCountStatement);
  public void visit(ForEachStatement forEachStatement);
  public void visit(BreakStatement breakStatement);
  public void visit(ContinueStatement continueStatement);
  public void visit(ReturnStatement returnStatement);
  public void visit(DeleteStatement deleteStatement);
  public void visit(ThrowStatement throwStatement);
  public void visit(ExplicitConstructorInvocationStatement explicitConstructorInvocationStatement);
  public void visit(EmptyStatement emptyStatement);
  public void visit(Expression expression);
  public void visit(Assignment assignment);
  public void visit(FieldAccess fieldAccess);
  public void visit(ArrayAccess arrayAccess);
  public void visit(ConditionalExpression conditionalExpression);
  public void visit(BinaryExpression binaryExpression);
  public void visit(UnaryExpression unaryExpression);
  public void visit(InstanceofExpression instanceofExpression);
  public void visit(CastExpression castExpression);
  public void visit(SuperExpression superExpression);
  public void visit(ThisExpression thisExpression);
  public void visit(MethodInvocation methodInvocation);
  public void visit(ArrayCreationExpression arrayCreationExpression);
  public void visit(ClassInstanceCreationExpression classInstanceCreationExpression);
  public void visit(LiteralExpression literalExpression);
  public void visit(MetaClassExpression metaClassExpression);
  public void visit(ParenthesisExpression parenthesisExpression);
  public void visit(KeyOfExpression keyOfExpression);

}
