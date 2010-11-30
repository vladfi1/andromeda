// Andromeda: An extension for galaxy
// Copyright: geX 2010

package com.sc2mod.andromeda.syntaxNodes;

//Attributes
attr "com.sc2mod.andromeda.environment.types.IType" inferedType with ExprNode;
attr "com.sc2mod.andromeda.environment.types.IType" inferedType with IdentifierNode;
attr "com.sc2mod.andromeda.parsing.SourceInfo" sourceInfo with SourceFileNode;
attr "boolean" constant with ExprNode;
attr StmtNode successor with StmtNode;
attr "com.sc2mod.andromeda.semAnalysis.SuccessorList" successors with StmtNode;
attr "com.sc2mod.andromeda.environment.types.IType" leftExpectedType with BinOpExprNode;
attr "com.sc2mod.andromeda.environment.types.IType" rightExpectedType with BinOpExprNode;
attr "com.sc2mod.andromeda.vm.data.DataObject" value with ExprNode;


//Identifiers are pure strings but with location information
IdentifierNode ::= string : id

CompilationUnitIdentifierNode ::= 
					CompilationUnitIdentifierNode: prefix
					IdentifierNode: name
					
ImportNode ::=
		CompilationUnitIdentifierNode: importName
		"boolean": strict
		
ImportListNode ::=	ImportNode*
				 

//Global file structure

//Not emitted by the parser only built by the program
SourceListNode ::= SourceFileNode*

SourceFileNode ::= 
			PackageDeclNode: packageDecl
			ImportListNode: imports
			GlobalStructureListNode: content
			

PackageDeclNode ::=
			CompilationUnitIdentifierNode: packageName
			IdentifierNode: unitName
			

GlobalStructureListNode ::= GlobalStructureNode*

GlobalStructureNode ::= {ClassDeclNode} 	AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											ExprNode: instanceLimit
											TypeNode: superClass
											TypeListNode: interfaces
											MemberDeclListNode: body
											TypeParamListNode: typeParams
				|	{EnrichDeclNode} 		AnnotationListNode: annotations
											ModifierListNode: modifiers
											TypeNode: enrichedType
											MemberDeclListNode: body
				| 	{GlobalFuncDeclNode} 	MethodDeclNode: funcDecl
				|	{GlobalVarDeclNode} 	FieldDeclNode: fieldDecl
				|	{GlobalStaticInitDeclNode} StaticInitDeclNode: initDecl
				| 	{InterfaceDeclNode} 	AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											TypeListNode: interfaces
											TypeParamListNode: typeParams
											MemberDeclListNode: body
				|   {StructDeclNode}		AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											TypeParamListNode: typeParams
											MemberDeclListNode: body
				|	{IncludeNode}			string: name
											SourceFileNode: includeContent
				|	{TypeAliasDeclNode}		AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											TypeNode: enrichedType
				|	{TypeExtensionDeclNode}	AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											TypeParamListNode: typeParams
											"boolean": key
											TypeNode: enrichedType
											"boolean": disjoint
											MemberDeclListNode : body
				|	{InstanceLimitSetterNode} TypeNode: enrichedType
											ExprNode: instanceLimit
// Literals
LiteralNode ::= "com.sc2mod.andromeda.vm.data.DataObject": value
			LiteralTypeSE: type
			
LiteralTypeSE ::= one of INT,FLOAT,STRING,NULL,CHAR,BOOL,TEXT


TypeNode ::= {BasicTypeNode} 
			String	: name
	|  	{SimpleTypeNode}	
			String 	: name
			TypeListNode : typeArguments
	|	{QualifiedTypeNode}
			FieldAccessExprNode : qualifiedName
			TypeListNode: typeArguments
	|	{ArrayTypeNode} 
			TypeNode: wrappedType
			ExprNode: dimension
	|	{PointerTypeNode}
			TypeNode	: wrappedType
	|	{FuncPointerTypeNode}
			TypeNode	: returnType
			TypeListNode: typeArguments	
		

			
// Names



	
// Modifiers
ModifierListNode ::= ModifierSE* 



ModifierSE ::= one of PUBLIC,PROTECTED,PRIVATE,INTERNAL,STATIC,ABSTRACT,FINAL,NATIVE,TRANSIENT,CONST,OVERRIDE

// Annotations
AnnotationNode ::= "String": name

AnnotationListNode ::= AnnotationNode*

// Generics
TypeParamNode ::= 
			String: name
			TypeNode: typeBound
			
TypeParamListNode ::= TypeParamNode*

// Struct declaration

// Class declarations

TypeListNode ::= TypeNode*


MemberDeclNode 			::= {MethodDeclNode} 
								MethodTypeSE: methodType								
								MethodHeaderNode: header
								StmtNode: body
						| {FieldDeclNode}						
								AnnotationListNode: annotations
								ModifierListNode: fieldModifiers
								TypeNode: type
								VarDeclListNode: declaredVariables
								AccessorList : accessors
						| {StaticInitDeclNode}
								AnnotationListNode: annotations
								StmtNode: body

MemberDeclListNode ::= MemberDeclNode*

MethodTypeSE ::= one of METHOD, CONSTRUCTOR, DESTRUCTOR, ACCESSOR

// Field Declarations

VarDeclListNode ::= VarDeclNode*:declarators

VarDeclNode ::= {UninitedVarDeclNode} 
						IdentifierNode: name 
					| {VarAssignDeclNode} 
						IdentifierNode: name
						ExprNode: initializer
					| {VarArrayInitDeclNode} 
						IdentifierNode: name
						ArrayInitNode: arrayInit
	

//Accessor declarations

AccessorList ::= MethodDeclNode*

//Method declarations

MethodHeaderNode ::= AnnotationListNode: annotations 
				ModifierListNode: modifiers
				TypeNode: returnType
				String: name
				ParameterListNode: parameters
				Object: throwDeclaration

MethodDeclaratorTransNode ::= String: name
								ParameterListNode: parameters

ParameterListNode ::= ParameterNode*

ParameterNode ::= 	TypeNode: type 
					IdentifierNode: name

			
//Constructor declarations
				


//Array init
ArrayInitNode ::= VarInitializerListNode: inits
					"boolean": hasComma

VarInitializerListNode ::= object* 

//Statements

LocalVarDeclNode ::= 
				ModifierListNode: modifiers
				TypeNode: type
				VarDeclListNode: declarators

StmtListNode ::= StmtNode*


StmtNode ::= {BlockStmtNode}
				StmtListNode: statements
			| {ExprStmtNode}
				ExprNode: expression
			| {LocalTypeDeclStmtNode}
				ClassDeclNode: classDeclaration
			| {LocalVarDeclStmtNode}
				LocalVarDeclNode: varDeclaration
			| {IfStmtNode}
				ExprNode: condition
				BlockStmtNode: thenStatement
				BlockStmtNode: elseStatement
			| {WhileStmtNode}
				ExprNode: condition
				BlockStmtNode: thenStatement
			| {DoWhileStmtNode}
				ExprNode: condition
				BlockStmtNode: thenStatement
			| {ForStmtNode}
				StmtNode: forInit
				ExprNode: condition
				BlockStmtNode: forUpdate
				BlockStmtNode: thenStatement
			| {ForCountStmtNode}
				TypeNode: iteratorType
				IdentifierNode: iterator
				ExprNode : fromExpr
				ExprNode : toExpr
				BlockStmtNode : thenStatement
			| {ForEachStmtNode}
				TypeNode: iteratorType
				IdentifierNode: iterator
				ExprNode: expression
				BlockStmtNode: thenStatement
			| {BreakStmtNode}
				string: label
			| {ContinueStmtNode}
				string: label
			| {ReturnStmtNode}
				ExprNode: result
			| {DeleteStmtNode}
				ExprNode: expression
			| {ThrowStmtNode}
				ExprNode: result
			| {ExplicitConsCallStmtNode}
				ExprNode: expression
				"boolean": useSuper
				ExprListNode: arguments
			| {EmptyStmtNode}
			

//Array dimensions
ArrayDimensionsNode ::= "int":numDimension

// Expressions (ouch)
ExprNode ::= {AssignmentExprNode}
				ExprNode: leftExpression
				AssignOpSE: assignOp
				ExprNode: rightExpression
			| {NameExprNode}
				String: name
			| {FieldAccessExprNode}
				ExprNode: leftExpression  
				String: name
			| {ArrayAccessExprNode}
				ExprNode: leftExpression
				ExprNode: rightExpression
			| {ConditionalExprNode}
				ExprNode: leftExpression
				ExprNode: rightExpression
				ExprNode: rightExpression2
			| {BinOpExprNode}
				ExprNode: leftExpression
				ExprNode: rightExpression
				BinOpSE: binOp
			| {UnOpExprNode}
				ExprNode: expression
				UnOpSE: unOp				
			| {InstanceofExprNode}
				ExprNode: leftExpression
				TypeNode: type
			| {CastExprNode}
				TypeNode: type
				ExprNode: rightExpression
				"boolean": unchecked
			| {SuperExprNode}
				ExprNode: superClassName
			| {ThisExprNode}
				ExprNode: thisClassName
			| {MethodInvocationExprNode}
				ExprNode: prefix
				String: funcName
				ExprListNode: arguments
				SpecialInvocationSE: special
			| {ArrayCreationExprNode}
				TypeNode: type
				ExprListNode: definedDimensions
				"int": additionalDimensions
				ArrayInitNode: arrayInitializer
			| {NewExprNode}
				TypeNode: type				
				ExprListNode: arguments
				MemberDeclListNode: classBody
			| {LiteralExprNode}
				LiteralNode: literal
			| {MetaClassExprNode}
				TypeNode: type		
			| {ParenthesisExprNode}
				ExprNode: expression
			| {KeyOfExprNode}
				TypeNode: type
								
//Types of method invocations
SpecialInvocationSE ::= one of INLINE,EXECUTE,EVALUATE,NATIVE

//Argument list
ExprListNode ::= ExprNode*


UnOpSE ::= one of COMP,MINUS,NOT,PREPLUSPLUS,PREMINUSMINUS,POSTPLUSPLUS,POSTMINUSMINUS,DEREFERENCE,ADDRESSOF

BinOpSE ::= one of OROR,ANDAND,OR,AND,XOR,EQEQ,NOTEQ,GT,LT,GTEQ,LTEQ,LSHIFT,RSHIFT,URSHIFT,PLUS,MINUS,MULT,DIV,MOD
				

AssignOpSE ::= one of EQ,MULTEQ,DIVEQ,MODEQ,PLUSEQ,MINUSEQ,LSHIFTEQ,RSHIFTEQ,URSHIFTEQ,ANDEQ,XOREQ,OREQ