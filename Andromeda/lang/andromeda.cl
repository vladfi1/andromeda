// Andromeda: An extension for galaxy
// Copyright: geX 2010

package com.sc2mod.andromeda.syntaxNodes;

//Attributes
attr "com.sc2mod.andromeda.environment.types.Type" inferedType with Expression;
attr "com.sc2mod.andromeda.environment.types.Type" inferedType with VariableDeclaratorId;
attr "com.sc2mod.andromeda.environment.types.Type" inferedType with VariableDeclarator;
attr "com.sc2mod.andromeda.environment.Scope" scope with andromedaFile;
attr "com.sc2mod.andromeda.parsing.AndromedaFileInfo" fileInfo with andromedaFile;
attr "boolean" constant with Expression;
attr "boolean" simple with Expression;
attr "boolean" lValue with Expression;
attr statement successor with Statement;
attr "com.sc2mod.andromeda.semAnalysis.SuccessorList" successors with Statement;
attr "com.sc2mod.andromeda.environment.types.Type" leftExpectedType with binaryExpression;
attr "com.sc2mod.andromeda.environment.types.Type" rightExpectedType with binaryExpression;
attr "com.sc2mod.andromeda.vm.data.DataObject" value with Expression;


//Global file structure

andromedaFile ::= 
			packageDecl: packageDecl
			fileContent: imports
			fileContent: content
			

packageDecl ::=
			fieldAccess: packageName
			

fileContent ::= globalStructure*

globalStructure ::= 
				
					{classDeclaration} 	annotationList: annotations
										modifiers: modifiers
										string: name
										expression: instanceLimit
										type: superClass
										typeList: interfaces
										classBody: body
										typeParamList: typeParams
				|	{enrichDeclaration} annotationList: annotations
										modifiers: modifiers
										type: enrichedType
										classBody: body
				| 	{FunctionDeclaration} methodDeclaration: funcDecl
				|	{GlobalVarDeclaration} fieldDeclaration: fieldDecl
				|	{GlobalInitDeclaration} staticInitDeclaration: initDecl
				| 	{interfaceDeclaration}
										annotationList: annotations
										modifiers: modifiers
										string: name
										typeList: interfaces
										classBody: body
				|   {structDeclaration} 
										annotationList: annotations
										modifiers: modifiers
										string: name
										classBody: body
				|	{includedFile}		andromedaFile: includedContent
				|	{typeAlias}			annotationList: annotations
										modifiers: modifiers
										string: name
										type: enrichedType
				|	{typeExtension}		annotationList: annotations
										modifiers: modifiers
										string: name
										"boolean": isKey
										type: enrichedType
										"boolean": disjoint
				|	{instanceLimitSetter}
										type: enrichedType
										expression: instanceLimit
// Literals
literal ::= "com.sc2mod.andromeda.vm.data.DataObject": value
			"int": type
			
literalType ::= one of INT,FLOAT,STRING,NULL,CHAR,BOOL,TEXT


type	::=	{basicType} 
			"int" 	: category
			String	: name
	|  	{simpleType}	
			"int" 	: category
			String 	: name
			typeList: typeArguments
	|	{qualifiedType}
			"int" 	: category
			FieldAccess : qualifiedName
			typeList: typeArguments
	|	{arrayType} 
			"int" 	: category
			type	: wrappedType
			expressionList: dimensions
	|	{pointerType}
			"int" 	: category
			type	: wrappedType
	|	{funcPointer}
			"int"	: category
			type	: returnType
			typeList: typeArguments	
		

typeCategory ::= one of BASIC,SIMPLE,QUALIFIED,ARRAY,POINTER,FUNCTION
			
// Names



	
// Modifiers
modifiers ::= Integer* 



modifierType ::= one of PUBLIC,PROTECTED,PRIVATE,STATIC,ABSTRACT,FINAL,NATIVE,TRANSIENT,CONST,OVERRIDE

// Annotations
annotation ::= "String": name

annotationList ::= annotation*

// Generics
typeParam ::= 
			String: name
			Object: typeBound
			
typeParamList ::= typeParam*

// Struct declaration

// Class declarations

typeList ::= type*


classMemberDeclaration ::= {methodDeclaration} 
								"int": memberType								
								methodHeader: header
								statement: body
						| {fieldDeclaration}						
								"int": memberType
								annotationList: annotations
								modifiers: fieldModifiers
								type: type
								variableDeclarators: declaredVariables
						| {staticInitDeclaration}
								"int": memberType
								annotationList: annotations
								statement: body
						| {accessorDeclaration}
								"int": memberType
								annotationList: annotations
								modifiers: fieldModifiers
								type: type
								variableDeclaratorId: accessorName
								parameterList: accessorParameters
								methodDeclaration: getMethod
								methodDeclaration: setMethod
								"boolean": accessorUseThis

classBody ::= classMemberDeclaration*

classMemberType ::= one of ACCESSOR_DECLARATION,ACCESSOR_GET,ACCESSOR_SET,METHOD_DECLARATION, FIELD_DECLARATION, CONSTRUCTOR_DECLARATION, DESTRUCTOR_DECLARATION, STATIC_INIT

// Field Declarations

variableDeclarators ::= variableDeclarator*:declarators

variableDeclarator ::= {variableDecl} 
						variableDeclaratorId: name 
					| {variableAssignDecl} 
						variableDeclaratorId: name expression: initializer
					| {variableArrayInitDecl} 
					variableDeclaratorId: name arrayInitializer: arrayInit


variableDeclaratorId ::= String: name variableDeclaratorId: arrayChild		

//Accessor declarations

accessorBody ::= 
			methodDeclaration: getMethod
			methodDeclaration: setMethod

//Method declarations

methodHeader ::= annotationList: annotations 
				modifiers: modifiers
				type: returnType
				String: name
				parameterList: parameters
				Object: throwDeclaration

methodDeclarator ::= String: name
					parameterList: parameters

parameterList ::= parameter*

parameter ::= type: type 
			variableDeclaratorId: name

			
			//Constructor declarations
				


//Array init
arrayInitializer ::= variableInitializers: inits "boolean": hasComma

variableInitializers ::= object* 

//Statements

localVariableDeclaration ::= 
				modifiers: modifiers
				type: type
				variableDeclarators: declarators

statementList ::= statement*


statement ::= {blockStatement}
				statementList: statements
			| {expressionStatement}
				expression: expression
			| {localTypeDeclarationStatement}
				classDeclaration: classDeclaration
			| {localVariableDeclarationStatement}
				localVariableDeclaration: varDeclaration
			| {ifThenElseStatement}
				expression: condition
				statement: thenStatement
				statement: elseStatement
			| {whileStatement}
				expression: condition
				statement: thenStatement
			| {doWhileStatement}
				expression: condition
				statement: thenStatement
			| {forStatement}
				statement: forInit
				expression: condition
				blockStatement: forUpdate
				statement: thenStatement
			| {forCountStatement}
				type: iteratorType
				variableDecl: iterator
				expression : fromExpr
				expression : toExpr
				statement : thenStatement
			| {forEachStatement}
				type: iteratorType
				variableDecl: iterator
				expression: expression
				statement: thenStatement
			| {breakStatement}
				string: label
			| {continueStatement}
				string: label
			| {returnStatement}
				expression: result
			| {deleteStatement}
				expression: expression
			| {throwStatement}
				expression: result
			| {explicitConstructorInvocationStatement}
				expression: expression
				"boolean": useSuper
				expressionList: arguments
			| {emptyStatement}
			

//Array dimensions
dims ::= "int":numDimension

// Expressions (ouch)
expression ::= {assignment}
				"int": assignmentType
				expression: leftExpression
				"int": operator
				expression: rightExpression
			| {fieldAccess}
				expression: leftExpression  
				"int": accessType
				String: name
			| {arrayAccess}
				expression: leftExpression
				expression: rightExpression
			| {conditionalExpression}
				expression: leftExpression
				expression: rightExpression
				expression: rightExpression2
			| {binaryExpression}
				expression: leftExpression
				expression: rightExpression
				"int": operator
			| {unaryExpression}
				expression: expression
				"int": operator				
			| {instanceofExpression}
				expression: leftExpression
				type: type
			| {castExpression}
				type: type
				expression: rightExpression
			| {superExpression}
				expression: superClassName
			| {thisExpression}
				expression: thisClassName
			| {methodInvocation}
				"int": invocationType
				expression: prefix
				String: funcName
				expressionList: arguments
				"boolean": inline
			| {arrayCreationExpression}
				type: type
				expressionList: definedDimensions
				"int": additionalDimensions
				arrayInitializer: arrayInitializer
			| {classInstanceCreationExpression}
				type: type				
				expressionList: arguments
				classBody: classBody
			| {literalExpression}
				literal: literal
			| {metaClassExpression}
				type: type		
			| {parenthesisExpression}
				expression: expression
			| {KeyOfExpression}
				type: type
								
//Types of method invocations
accessType ::= one of SIMPLE,SUPER,NATIVE,NAMED_SUPER,EXPRESSION,POINTER

//Argument list
expressionList ::= expression*

assignmentType ::= one of POINTER, FIELD, ARRAY

unaryOperator ::= one of COMP,MINUS,NOT,PREPLUSPLUS,PREMINUSMINUS,POSTPLUSPLUS,POSTMINUSMINUS,DEREFERENCE,ADDRESSOF

binaryOperator ::= one of OROR,ANDAND,OR,AND,XOR,EQEQ,NOTEQ,GT,LT,GTEQ,LTEQ,LSHIFT,RSHIFT,URSHIFT,PLUS,MINUS,MULT,DIV,MOD
				

assignmentOperatorType ::= one of EQ,MULTEQ,DIVEQ,MODEQ,PLUSEQ,MINUSEQ,LSHIFTEQ,RSHIFTEQ,URSHIFTEQ,ANDEQ,XOREQ,OREQ