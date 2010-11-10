package com.sc2mod.andromeda.notifications;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.parsing.options.Configuration;

public enum ProblemId {
	
	//**** MISC ****
	INTERNAL_PROBLEM("An unexpected internal problem has occurred which indicates a bug. Please file a bug report!\n"+
					"Include a description of the situation where the bug happened.\n"+
					"A code sample that can be used to reproduce the bug would be best.\n"+
					"Post the bug at the bug tracking forum at www.sc2mod.com.\n"+
					"Include the following trace information into your bug report:\n"+
					"------------------------------------- TRACE BEGIN -------------------------------------\n"+
					"%s\n" +
					"------------------------------------- TRACE END -------------------------------------\n"+
					"Thank you for your help!"),
	
	//**** SYNTAX ERRORS ****
	SYNTAX_UNKNOWN ("Unknown Syntax Error: %s"),
	SYNTAX_UNEXPECTED_TOKEN ("Syntax Error: Unexpected token"),
	SYNTAX_UNTERMINATED_STRING("Unterminated string at end of line"),
	SYNTAX_ILLEGAL_ESCAPE_SEQUENCE("Illegal escape sequence %s"),
	SYNTAX_ILLEGAL_CHARACTER("Illegal character '%s'"),
	
	//**** EXTERNAL ERRORS ****
	EXTERNAL_MOPAQ_ERROR("The supplied map file could not be read properly:\n%s"),
	EXTERNAL_IO_EXCEPTION("An IOException occurred:\n%s"),
	EXTERNAL_EXCEPTION("An external exception occurred:\n%s: %s"),
	
	//**** INCLUSION ERRORS ****
	MALFORMED_INCLUDE ("Malformed include directive: %s"),
	MALFORMED_IMPORT ("Malformed import directive: %s"),
	INCLUDED_FILE_NOT_FOUND ("The included file '%s' was not found"),
	MISSING_NATIVE_LIB ("Missing native library file '%s'"),

	//**** NAME RESOLVING ****
	FIELD_NAME_NOT_FOUND ("Neither a field named '%s' nor a corresponding accessor method was found."),
	ACCESSOR_NOT_VISIBLE ("The accessor method for field '%s' is not visible"),
	FIELD_NOT_VISIBLE("The field '%s' is not visible"),
	ACCESSOR_WRONG_SIGNATURE ("The field '%s' cannot be found. A corresponding accessor method exists but has the wrong signature."),
	
	
	TYPE_CANNOT_HAVE_FIELDS ("The left side of a field access operation must be a class, struct or an " +
					"enriched basic type. However, no enrichment for the type '%s' exists.") , 

	STATIC_MEMBER_MISUSE ("Trying to access the static member '%s' in a non-static way."),
	NONSTATIC_MEMBER_MISUSE("Trying to access the non-static member '%s' in a static way."),
	NO_SUCH_METHOD("No visible method %s(%s) exists for type %s"),	//CHECK> DIFFERENT ERROR FOR NO METHOD AND WRONG SIG
	TYPE_CANNOT_HAVE_METHODS ("The left side of a method call must be a class, struct or an " +
					"enriched basic type. However, no enrichment for the type '%s' exists.") , 
	NO_SUCH_FUNCTION("No visible function or method %s(%s) found. Misspelled? Wrong types?"),

	SUPER_OUTSIDE_OF_CLASS("The 'super' expression can only be used in classes."),
	SUPER_IN_TOP_CLASS("Cannot use 'super' here because this class has no super class."),

	SUPER_CONSTRUCTOR_IN_TOP_CLASS("Cannot invoke a super constructor, because this class has no top class"),
	NO_IMPLICIT_SUPER_CONSTRUCTOR("The super class %s provides no parameterless constructor, so you must explicitly invoke a super constructor."),
	NO_CONSTRUCTOR_WITH_THIS_SIGNATURE("The class %s specifies no visible constructor with signature (%s)."),
	
	THIS_OUTSIDE_CLASS_OR_ENRICHMENT("'this' can only be used inside of classes, enrichments or interfaces"),
	THIS_IN_STATIC_MEMBER("'this' cannot be used in static methods and attributes"),
	
	DUPLICATE_LOCAL_VARIABLE ("Duplicate local variable '%s'"),
	GLOBAL_VAR_ACCESS_BEFORE_DECL("Accessing a global variable from above its declaration is forbidden"),
	
	UNKNOWN_TYPE("Type %s does not exist"),
	
	NAME_EXPR_IS_NOT_A_VAR_OR_OP("A name expression must be a variable or operation name but it is a %s"),
	
	//**** MODIFIERS & ANNOTATIONS ***
	DUPLICATE_MODIFIER("Duplicate modifier '%s'"),
	DUPLICATE_VISIBILITY_MODIFIER("Duplicate visibility modifier"),
	INVALID_MODIFIER("%s cannot be declared '%s'"),
	INVALID_VISIBILITY_MODIFIER("%s cannot have a visibility modifier"),
	UNKNOWN_ANNOTATION("Unknown annotation '%s' for %s"),
	
	KEYOF_USED_ON_NONKEY ("The keyof operator can only be used on key type extensions (extensions with the iskey modifier). The type %s is no key type."),
	
	//**** CLASSES & OTHER RECORDS ****
	CLASS_EXTENDS_NON_CLASS("Classes may only extend other classes."),
	CLASS_IMPLEMENTS_NON_INTERFACE("The types in an implements clause must be an interface."),
	DUPLICATE_IMPLEMENTS("Duplicate interface in implements clause."),
	STATIC_CLASS_HAS_IMPLEMENTS("Static classes cannot implement interfaces"),
	FINAL_CLASS_EXTENDED("Cannot extend class %s because it is final"),
	STATIC_CLASS_HAS_EXTENDS("Static classes cannot extend other classes"),
	STATIC_CLASS_EXTENDED("Cannot extend class %s because it is static"),
	STATIC_CLASS_HAS_CONSTRUCTOR("Static classes cannot have constructors."),
	STATIC_CLASS_HAS_NON_STATIC_MEMBER("Static classes cannot have non-static members"),
	ABSTRACT_METHOD_IN_NON_ABSTRACT_CLASS("Cannot define an abstract method in a non-abstract class"),
	NON_ABSTRACT_CLASS_MISSES_IMPLEMENTATIONS("The non-abstract class %s must implement the abstract method '%s' defined in type %s"),
	CONSTRUCTOR_REQUIRED("The super class %s has no parameterless, visible constructor, so the class % must specify a constructor"),
	DUPLICATE_DESTRUCTOR("Duplicate destructor."),
	DUPLICATE_CONSTRUCTOR("Duplicate constructor %s"),
	
	DELETE_NON_CLASS("The argument of a delete statement must be a class, but it is %s (%s)"),
	NEW_NON_CLASS("Only classes can be instanciated by a new-expresion"),
	
	INHERITANCE_CYCLE("Cycle in inheritance hierarchy involving type %s"),
	
	STRUCT_MEMBER_WITH_MODIFIER("Struct fields cannot have modifiers."),
	
	DUPLICATE_TYPE_DEFINITION("Duplicate definition of type %s."),
	NON_GENERIC_TYPE_HAS_TYPE_ARGUMENTS("The non-generic type %s cannot have type arguments"),
	INVALID_TYPE_BOUND("This type cannot be used as type bound. Only types based on int (classes, interfaces, int-extensions) are allowed."),
	
	INTERFACE_EXTENDING_NON_INTERFACE("Interfaces can only extend other interfaces"),
	DUPLICATE_EXTENDS("Duplicate interface in extends clause"),
	
	//**** TYPE EXTENSIONS & ENRICHMENTS ****
	DISJOINT_EXTENSION_BASED_ON_EXTENSION("Disjoint type extensions cannot be based on other extensions, they must be based on native types."),
	EXTENSION_TYPE_INVALID("The type %s cannot be extended. Only basic types and other extensions can."),
	EXTENSION_OF_KEY_TYPE("Key types cannot be extended."),
	INVALID_BASE_TYPE_FOR_KEY_TYPE("Only int and string are allowed to be used as base types for key types."),
	
	NATIVE_ENRICHMENT_HAS_FIELD("Enrichments of native types cannot contain fields"),
	NATIVE_ENRICHMENT_METHOD_DECLARED_OVERRIDE("Methods of enrichments of native types cannot be declared 'override'"),
	
	//**** INSTANCE LIMIT ****
	CHILD_CLASS_HAS_INSTANCELIMIT ("Instance limits may only be specified for top classes"),
	STATIC_CLASS_HAS_INSTANCELIMIT ("Static classes may not specify an instancelimit"),
	WRONGLY_TYPED_INSTANCELIMIT ("The instancelimit of a class must be of type int, but it is of type %s"),
	NONCONSTANT_INSTANCELIMIT ("Could not determine the instancelimit. It must be a constant value."),
	NEGATIVE_INSTANCELIMIT ("Negative instance limit: %s"),
	ZERO_INSTANCELIMIT ("Zero instancelimit. If you want a class without any instance, declare the class 'static'."),
	SETINSTANCELIMIT_ON_NONCLASS("The type in a setinstancelimit clause must be a class."),
	
	//***** ARRAYS *****
	ARRAY_ACCESS_ON_NONARRAY("Cannot use array access, because the operand is no array type, but %s."),
	UNKNOWN_ARRAY_DIMENSION_TYPE("Could not determine the type of an array dimension."),
	INVALID_ARRAY_DIMENSION_TYPE("The dimension of an array must be of type int, but it is of type %s."),
	NON_CONSTANT_ARRAY_DIMENSION("Could not determine the dimensions of an array. The expression must be constant. If it contains constant variables, these must be defined before the usage."),
	NEGATIVE_ARRAY_DIMENSION("The dimension of an array cannot be negative, but it is %s."),
	
	//**** CONSTANTS ****
	CONST_VAR_NOT_INITED ("Constant variables must be initialized in their declaration"),
	CONST_VAR_UNCONST_INIT ("Constant variables must be initialized with constant values."),
	CONST_VAR_REASSIGNED("Cannot assign to this variable, because it is declared const!"),
	
	//**** TYPE ERRORS ****
	TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT("Cannot assign a value of type %s to a variable of type %s"),
	TYPE_ERROR_NONBOOL_LOOP_CONDITION("The condition of a loop must be of type bool, but it is of type %s"),
	TYPE_ERROR_NONBOOL_IF_CONDITION("The condition of an if-statement must be of type bool or of a referential type, but it is of type %s"),
	TYPE_ERROR_NONBOOL_CONDITIONAL("The condition of a conditional expression must be of type bool, but it is of type %s"),
	TYPE_ERROR_INCOMPATIBLE_CONDITIONAL("The types of the two alternatives in a conditional expression do not match. They must either be the same type or the 'else' type must be implicitly castable to the 'if' one.\n'if' type: %s\n'else' type: %s"),
	TYPE_ERROR_FORBIDDEN_CAST("Cannot cast from %s to %s"),
	TYPE_ERROR_NEED_UNCHECKED_CAST("Cannot cast from %s to %s with a checked cast. Use an unchecked cast instead."),
	
	
	TYPE_ERROR_INCOMPATIBLE_RETURN_TYPE("Returned type incompatible with declared return type.\nReturn type: %s\nReturned type: %s"),
	MISSING_RETURN("Missing return. (the control flow of non-void functions may not reach the end of the function body)"),
	RETURNING_VALUE_IN_VOID_METHOD ("Returning a value in a function that should return void."),
	RETURN_WITHOUT_VALUE ("Returning no value is only allowed in void functions."),
	ARRAY_OR_STRUCT_RETURNED ("Structs and arrays cannot be used as return types, use pointers instead."),
	ARRAY_OR_STRUCT_AS_PARAMETER ("Structs and arrays cannot be used as parameters, use pointers instead."),
	
	
	BINOP_ON_VOID("Cannot use binary operators on void values"),
	UNOP_ON_VOID("Cannot use unary operators on void values"),
	INCDEC_ON_NONLVALUE("Can only apply increment/decrement to valid lValues (variables,fields,accessors)."),
	INCDEC_ON_CONST("Cannot use increment/decrement on 'const' variables"),
	
	TYPE_ERROR_BINOP_OPERAND_TYPE("The %s side of a %s binary operation must be of %s, but it is of type %s"),
	TYPE_ERROR_INCOMPATIBLE_BINOP_OPERANDS("Incompatible operand types for operator '%s'.\nLeft type: %s\nRight type: %s"),
	TYPE_ERROR_INCOMPATIBLE_COMPARISON("Cannot compare %s with %s"),
	TYPE_ERROR_UNOP_OPERAND_TYPE("The operand of a %s must be of %s, but it is of type %s"),
	
	
	//**** MISC ****
	UNREACHABLE_CODE("Unreachable code."),
	
	BREAK_OUTSIDE_LOOP("'break' may only be used in loops"),
	CONTINUE_OUTSIDE_LOOP("'continue' may only be used in loops"),
	SYSTEM_CLASS_NOT_DEFINED_AS_CLASS("The internal system class %s was not defined as a class"),
	SYSTEM_TYPE_MISSING("Missing system type '%s'."),
	SYSTEM_TYPE_MISSING_MEMBER("The system type %s misses the %s %s"),
	
	//**** FOREACH ****
	FOREACH_NO_GET_ITERATOR_METHOD("The expression over which a for each loop iterates must provide an accessible method .getIterator(). No such method found for type %s"),
	FOREACH_NO_HAS_NEXT_METHOD("The iterator returned by the getIterator() method in a for each loop must provide the method bool hasNext().\nThis method is not provided by the iterator type  %s"),
	FOREACH_HAS_NEXT_DOES_NOT_RETURN_BOOL("The hasNext() method of the iterator in a for each loop must return a value of type 'bool' but it returns a value of type %s"),
	FOREACH_NO_NEXT_METHOD("The iterator returned by the getIterator() method in a for each loop must provide the method next().\nThis method is not provided by the iterator type %s"),
	FOREACH_INCOMPATIBLE_ITERATION_TYPE("The return type of the next() method of the iterator in a for each loop must be compatible with the type of the iterating variable, but it isn't. next() return type: %s\niterating variable type: %s"),
	
	//****** FIELDS ******
	DUPLICATE_NAME("Duplicate name '%s'"),
	DUPLICAT_GLOBAL_VARIABLE("Duplicate global variable '%s'"),
	ACCESSOR_ON_MULTIPLE_FIELDS("Cannot define accessors for declarations with multiple fields"),
	ACCESSOR_DECLARED_STATIC("Accessor methods may not be declared static. They are implicitly static if their corresponding field is static"),
	ACCESSOR_GET_OR_SET_MISSING("This accessor value cannot be used on the left side of a get/set assignment.\nThere is a %s method '%s' but no %s method in the same scope."),
	ACCESSOR_GET_OR_SET_NOT_VISIBLE("This accessor value cannot be used on the left side of a get/set assignment.\nThere is a visible %s method '%s' but the corresponding %s method is not visible."),
	ACCESSOR_GET_OR_SET_WRONG_SIGNATURE("This accessor value cannot be used on the left side of a get/set assignment.\nThere is a valid %s method '%s' but the corresponding %s method has the wrong signature."),
	ACCESSOR_STATIC_NON_STATIC_GET_SET("This accessor value cannot be used on the left side of a get/set assignment.\nThe get and set method must both be static or not static, but one is static and the other one is not"),
	
	
	//****** METHODS ******
	OVERRIDE_DECL_DOES_NOT_OVERRIDE("Method was declared 'override' but no visible overridden method exists in the superclass."),
	OVERRIDE_RETURN_TYPE_MISMATCH("The overriding method's returntype must be the same type or a subtype of the return type of the overridden method.\nConflicting return types (and methods):%s (%s) is no subtype of %s (%s)"),
	OVERRIDE_STATIC_NON_STATIC("Overwriting a static method with a non-static method or vice versa is not possible."),
	OVERRIDE_FINAL_METHOD("Cannot override a final method."),
	OVERRIDE_REDUCED_VISIBILITY("Cannot reduce visibility of overridden method."),
	OVERRIDE_WITHOUT_OVERRIDE_MODIFIER("The method %s overrides %s but has no 'override' modifier"),
	AMBIGUOUS_METHOD_ACCESS("Ambiguous access! There is more than one operation named %s:\n%s"),
	AMBIGUOUS_METHOD_CALL("This method invocation is ambiguous.\nPossible calls:\n%s\n%s"),
	CONSTRUCTOR_WITH_RETURN_TYPE("Constructors may not specify a return type"),
	DUPLICATE_METHOD("Duplicate method."),
	
	ABSTRACT_INTERFACE_METHOD("Interface methods may not be declared abstract (they are implicitly)."),
	NON_ABSTRACT_METHOD_WITHOUT_BODY("A method that is not declared 'abstract' must have a body."),
	ABSTRACT_METHOD_WITH_BODY("A method declared 'abstract' may not have a body."),
	STATIC_METHOD_ABSTRACT("Static methods cannot be abstract."),
	STATIC_METHOD_FINAL("Static methods cannot be final."),
	STATIC_METHOD_WITHOUT_BODY("Static methods must have a body."),
	METHOD_DECLARED_NATIVE("Methods cannot be declared native."),
	NATIVE_FUNCTION_FINAL("Native functions cannot be declared final."),
	NATIVE_FUNCTION_WITH_BODY("Native functions may not have a body."),
	
	MISSING_RETURN_TYPE("Missing return type.\n(or class name misspelled if this method should be a constructor)"),
	MISSPELLED_DESTRUCTOR("Destructors must have the name of their class."),
	
	DUPLICATE_FUNCTION("Duplicate definition of function %s(%s)"),
	DUPLICATE_OVERRIDE_FUNCTION("Duplicate override definition of function %s(%s)"),
	DANGLING_FORWARD_DECLARATION("No definition found for this forward-declaration"),
	
	
	//****** UNUSED STUFF ******
	UNCALLED_FUNCTION("The %s is never called."),
	UNREAD_VARIABLE("The %s %s is never read"),
	UNWRITTEN_VARIABLE("The %s %s is never written"),
	UNINITIALIZED_VARIABLE("The %s %s is read but never initialized");
	
	String message;
	ProblemId(String message){
		this.message = message;
	}
	
	/**
	 * Internal factory method to create Problems.
	 * Depending on the problemId, this method will check the options of the
	 * given srcEnv, to look which severity to create.
	 * 
	 * @param problemId
	 * @param message 
	 * @param srcEnv
	 * @return
	 */
	Problem createProblem(CompilationEnvironment srcEnv){
		
		//TODO: SET WARNINGS IN Problems
				
		//Here, the severity should be looked up and the correct class should be returned.
		return new Problem(this, srcEnv, ProblemSeverity.ERROR);
	}
	
	private static final Pattern TOKEN_PATTERN = Pattern.compile("\\%\\w");
	String createMessage(Object[] messageTokens){
		String msgTemplate = this.message;
		if(messageTokens == null) return msgTemplate;
		
		//ssprintf
		StringBuffer builder = new StringBuffer();
		Matcher m = TOKEN_PATTERN.matcher(msgTemplate);
		for(int i = 0;i < messageTokens.length; i++){
			m.find();
			m.appendReplacement(builder, escapeStr(String.valueOf(messageTokens[i])));

		}
		m.appendTail(builder);
		return builder.toString();
	}

	private String escapeStr(String valueOf) {
		return valueOf.replace("\\","\\\\").replace("$", "\\$");
	}

}
