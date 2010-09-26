// Andromeda: An extension for galaxy
// Copyright: geX 2010

package com.sc2mod.andromeda.syntaxNodes;

//Attributes
attr "com.sc2mod.andromeda.environment.types.Type" inferedType with ExprNode;
attr "com.sc2mod.andromeda.environment.types.Type" inferedType with VarDeclNode;
attr "com.sc2mod.andromeda.environment.Scope" scope with SourceFileNode;
attr "com.sc2mod.andromeda.parsing.SourceFileInfo" fileInfo with SourceFileNode;
attr "boolean" constant with ExprNode;
attr "boolean" simple with ExprNode;
attr "boolean" lValue with ExprNode;
attr StmtNode successor with StmtNode;
attr "com.sc2mod.andromeda.semAnalysis.SuccessorList" successors with StmtNode;
attr "com.sc2mod.andromeda.environment.types.Type" leftExpectedType with BinOpExprNode;
attr "com.sc2mod.andromeda.environment.types.Type" rightExpectedType with BinOpExprNode;
attr "com.sc2mod.andromeda.vm.data.DataObject" value with ExprNode;


//Identifiers are pure strings but with location information
IdentifierNode ::= string : id

//Global file structure

SourceFileNode ::= 
			PackageDeclNode: packageDecl
			GlobalStructureListNode: imports
			GlobalStructureListNode: content
			

PackageDeclNode ::=
			FieldAccessExprNode: packageName
			

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
											MemberDeclListNode: body
				|   {StructDeclNode}		AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											MemberDeclListNode: body
				|	{IncludeNode}			SourceFileNode: includedContent
				|	{TypeAliasDeclNode}		AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											TypeNode: enrichedType
				|	{TypeExtensionDeclNode}	AnnotationListNode: annotations
											ModifierListNode: modifiers
											string: name
											"boolean": isKey
											TypeNode: enrichedType
											"boolean": disjoint
				|	{InstanceLimitSetterNode} TypeNode: enrichedType
											ExprNode: instanceLimit
// Literals
LiteralNode ::= "com.sc2mod.andromeda.vm.data.DataObject": value
			"int": type
			
LiteralTypeSE ::= one of INT,FLOAT,STRING,NULL,CHAR,BOOL,TEXT


TypeNode ::= {BasicTypeNode} 
			"int" 	: category
			String	: name
	|  	{SimpleTypeNode}	
			"int" 	: category
			String 	: name
			TypeListNode : typeArguments
	|	{QualifiedTypeNode}
			"int" 	: category
			FieldAccessExprNode : qualifiedName
			TypeListNode: typeArguments
	|	{ArrayTypeNode} 
			"int" 	: category
			TypeNode: wrappedType
			ExprListNode: dimensions
	|	{PointerTypeNode}
			"int" 	: category
			TypeNode	: wrappedType
	|	{FuncPointerTypeNode}
			"int"		: category
			TypeNode	: returnType
			TypeListNode: typeArguments	
		

TypeCategorySE ::= one of BASIC,SIMPLE,QUALIFIED,ARRAY,POINTER,FUNCTION
			
// Names



	
// Modifiers
ModifierListNode ::= Integer* 



ModifierTypeSE ::= one of PUBLIC,PROTECTED,PRIVATE,STATIC,ABSTRACT,FINAL,NATIVE,TRANSIENT,CONST,OVERRIDE

// Annotations
AnnotationNode ::= "String": name

AnnotationListNode ::= AnnotationNode*

// Generics
TypeParamNode ::= 
			String: name
			Object: typeBound
			
TypeParamListNode ::= TypeParamNode*

// Struct declaration

// Class declarations

TypeListNode ::= TypeNode*


MemberDeclNode 			::= {MethodDeclNode} 
								"int": memberType								
								MethodHeaderNode: header
								StmtNode: body
						| {FieldDeclNode}						
								"int": memberType
								AnnotationListNode: annotations
								ModifierListNode: fieldModifiers
								TypeNode: type
								VarDeclListNode: declaredVariables
						| {StaticInitDeclNode}
								"int": memberType
								AnnotationListNode: annotations
								StmtNode: body
						| {AccessorDeclNode}
								"int": memberType
								AnnotationListNode: annotations
								ModifierListNode: fieldModifiers
								TypeNode: type
								IdentifierNode: accessorName
								ParameterListNode: accessorParameters
								MethodDeclNode: getMethod
								MethodDeclNode: setMethod
								"boolean": accessorUseThis

MemberDeclListNode ::= MemberDeclNode*

MemberTypeSE ::= one of ACCESSOR_DECLARATION,ACCESSOR_GET,ACCESSOR_SET,METHOD_DECLARATION, FIELD_DECLARATION, CONSTRUCTOR_DECLARATION, DESTRUCTOR_DECLARATION, STATIC_INIT

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

AccessorBodyTransNode ::= 
			MethodDeclNode: getMethod
			MethodDeclNode: setMethod

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
				StmtNode: thenStatement
				StmtNode: elseStatement
			| {WhileStmtNode}
				ExprNode: condition
				StmtNode: thenStatement
			| {DoWhileStmtNode}
				ExprNode: condition
				StmtNode: thenStatement
			| {ForStmtNode}
				StmtNode: forInit
				ExprNode: condition
				BlockStmtNode: forUpdate
				StmtNode: thenStatement
			| {ForCountStmtNode}
				TypeNode: iteratorType
				IdentifierNode: iterator
				ExprNode : fromExpr
				ExprNode : toExpr
				StmtNode : thenStatement
			| {ForEachStmtNode}
				TypeNode: iteratorType
				IdentifierNode: iterator
				ExprNode: expression
				StmtNode: thenStatement
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
				"int": assignmentType
				ExprNode: leftExpression
				"int": operator
				ExprNode: rightExpression
			| {FieldAccessExprNode}
				ExprNode: leftExpression  
				"int": accessType
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
				"int": operator
			| {UnOpExprNode}
				ExprNode: expression
				"int": operator				
			| {InstanceofExprNode}
				ExprNode: leftExpression
				TypeNode: type
			| {CastExprNode}
				TypeNode: type
				ExprNode: rightExpression
			| {SuperExprNode}
				ExprNode: superClassName
			| {ThisExprNode}
				ExprNode: thisClassName
			| {MethodInvocationExprNode}
				"int": invocationType
				ExprNode: prefix
				String: funcName
				ExprListNode: arguments
				"boolean": inline
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
AccessTypeSE ::= one of SIMPLE,SUPER,NATIVE,NAMED_SUPER,EXPRESSION,POINTER

//Argument list
ExprListNode ::= ExprNode*

AssignmentTypeSE ::= one of POINTER, FIELD, ARRAY

UnOpTypeSE ::= one of COMP,MINUS,NOT,PREPLUSPLUS,PREMINUSMINUS,POSTPLUSPLUS,POSTMINUSMINUS,DEREFERENCE,ADDRESSOF

BinOpTypeSE ::= one of OROR,ANDAND,OR,AND,XOR,EQEQ,NOTEQ,GT,LT,GTEQ,LTEQ,LSHIFT,RSHIFT,URSHIFT,PLUS,MINUS,MULT,DIV,MOD
				

AssignOpTypeSE ::= one of EQ,MULTEQ,DIVEQ,MODEQ,PLUSEQ,MINUSEQ,LSHIFTEQ,RSHIFTEQ,URSHIFTEQ,ANDEQ,XOREQ,OREQ