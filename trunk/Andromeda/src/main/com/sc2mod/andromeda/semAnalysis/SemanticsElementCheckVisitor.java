package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;

/**
 * This visitor performs checks on newly created semantics elements.
 * 
 * For operations and variables, it is called directly after they are
 * constructed by the StructureRegistryTreeScanner.
 * 
 * For types like classes, it is called after members are resolved and
 * copied down (because it needs some of the information from these steps).
 * 
 * @author gex
 *
 */
public class SemanticsElementCheckVisitor extends VoidSemanticsVisitorAdapter {

	private TypeProvider tprov;
	private StructIndexCalculationVisitor structIndexCalculator = new StructIndexCalculationVisitor();
	
	public SemanticsElementCheckVisitor(Environment env) {
		this.tprov = env.typeProvider;
	}
	
	//*************************************************************
	//***														***
	//***						FIELDS							***
	//***														***
	//*************************************************************
	@Override
	public void visit(FieldDecl fieldDecl) {
		IType t = fieldDecl.getContainingType();
		if(t != null && !fieldDecl.isStatic()){
			switch(t.getCategory()){
			case CLASS:
			case STRUCT:
				break;
			default:
				throw Problem.ofType(ProblemId.NATIVE_ENRICHMENT_HAS_FIELD).at(fieldDecl.getDefinition())
					.raiseUnrecoverable();
			}
		}
		
	}
	
	
	//*************************************************************
	//***														***
	//***						OPERATIONS						***
	//***														***
	//*************************************************************
	
	@Override
	public void visit(Function function) {
		if(function.isNative()){
			if(function.isFinal()) 
				throw Problem.ofType(ProblemId.NATIVE_FUNCTION_FINAL).at(function.getHeader().getModifiers())
							.raiseUnrecoverable();
			if(function.hasBody())
				throw Problem.ofType(ProblemId.NATIVE_FUNCTION_WITH_BODY).at(function.getHeader().getModifiers())
							.raiseUnrecoverable();
		}	
	}
	
	@Override
	public void visit(Method method) {
		IType containingType = method.getContainingType();
		if(!method.hasBody()) {
			if(containingType instanceof IInterface) {
				if(method.isAbstract()) {
					throw Problem.ofType(ProblemId.ABSTRACT_INTERFACE_METHOD).at(method.getHeader())
								.raiseUnrecoverable();
				}
			} else
			if(!method.isAbstract()) {
				throw Problem.ofType(ProblemId.NON_ABSTRACT_METHOD_WITHOUT_BODY).at(method.getHeader())
								.raiseUnrecoverable();
			}
		} else {
			if(method.isAbstract()) {
				throw Problem.ofType(ProblemId.ABSTRACT_METHOD_WITH_BODY).at(method.getHeader())
								.raiseUnrecoverable();
			}
		}
		if(method.isStatic()) {
			if(method.isFinal()) 
				throw Problem.ofType(ProblemId.STATIC_METHOD_FINAL).at(method.getHeader().getModifiers())
						.raiseUnrecoverable();
			if(method.isAbstract())
				throw Problem.ofType(ProblemId.STATIC_METHOD_ABSTRACT).at(method.getHeader().getModifiers())
						.raiseUnrecoverable();
			if(!method.hasBody())
				throw Problem.ofType(ProblemId.STATIC_METHOD_WITHOUT_BODY).at(method.getHeader())
						.raiseUnrecoverable();
		}
	}
	
	@Override
	public void visit(Constructor constructor) {
		//Is this really a constructor or just a method without return type
		if(!constructor.getName().equals(constructor.getContainingType().getUid())){
			throw Problem.ofType(ProblemId.MISSING_RETURN_TYPE).at(constructor.getHeader())
					.raiseUnrecoverable();
		}
	}
	
	@Override
	public void visit(Destructor destructor) {
		//Is this really a destructor?
		if(!destructor.getName().equals(destructor.getContainingType().getUid())){
			throw Problem.ofType(ProblemId.MISSPELLED_DESTRUCTOR).at(destructor.getHeader())
					.raiseUnrecoverable();
		}
	}
	
	
	//*************************************************************
	//***														***
	//***						CLASSES							***
	//***														***
	//*************************************************************
	
	/**
	 * Note that for classes, the semantics check visitor
	 * should not contain any checks that assume that
	 * top classes are visited before their decendants.
	 * For those checks, use the CopyDownVisitor which is
	 * invoked right before this visitor.
	 */
	@Override
	public void visit(ClassImpl class1) {
		checkImplicitConstructor(class1);
		
		if(class1.isStatic())
			checkStaticClass(class1);
		
		classScopeChecks(class1);
	}
	
	private void classScopeChecks(ClassImpl clazz){
		boolean staticClass = clazz.isStatic();
		boolean nonAbstract = !clazz.isAbstract();
		ScopeContentSet content = clazz.getContent();
		for(IScopedElement elem : clazz.getContent().iterateDeep(true, false,false)){
			switch(elem.getElementType()){
			case OPERATION:
				Operation op = (Operation) elem;
				
				//check that override methods do override
				if(op.isOverride() && op.getOverrideInformation().getOverridenMethod() == null){
					throw Problem.ofType(ProblemId.OVERRIDE_DECL_DOES_NOT_OVERRIDE).at(op.getDefinition())
						.raiseUnrecoverable();
				}
				
				//check that a non abstract class has no abstract members
				if(nonAbstract && op.isAbstract()){
					if(content.isElementInherited(op)){

						throw Problem.ofType(ProblemId.NON_ABSTRACT_CLASS_MISSES_IMPLEMENTATIONS)
							.at(clazz.getDefinition())
							.details(clazz.getName(),op.getReturnType().getFullName() + " " + OperationUtil.getNameAndSignature(op),op.getContainingType().getUid())
							.raiseUnrecoverable();
					} else {
						throw Problem.ofType(ProblemId.ABSTRACT_METHOD_IN_NON_ABSTRACT_CLASS)
							.at(op.getDefinition())
							.raiseUnrecoverable();
					}
					
				}
				
			case VAR:
				
				//check that static classes contain only static members
				if(staticClass){
					if(!elem.isStatic()){
						throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_NON_STATIC_MEMBER)
							.at(elem.getDefinition())
							.raiseUnrecoverable();
					}
				}
			}
		}
		
	}


	/**
	 * Checks about static classes
	 * @param class1
	 */
	private void checkStaticClass(IClass class1){
		if(!class1.getConstructors().isEmpty())
			throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_CONSTRUCTOR).at(class1.getConstructors().getAny().getDefinition())
						.raiseUnrecoverable();
	}
	

	/**
	 * Checks that a class which has a parent class without a parameterless
	 * constructor acutally defines a constructor.
	 * @param c
	 */
	private void checkImplicitConstructor(IClass c) {
		//If we are in a top class or we have constructors, everything is fine
		if(c.isTopType()) return;
		if(c.hasConstructors()) return;
		
		IClass superClass = c.getSuperClass();
		
		//If the super class has no constructors, then it has the 
		//implicit default constructor, which is fine, too.
		if(!superClass.hasConstructors()) return;
		
		//Check if it has a parameterless, visible constructor.
		Operation cons = superClass.getConstructors().get(Signature.EMPTY_SIGNATURE, null,c);
		
		if(cons == null)
			throw Problem.ofType(ProblemId.CONSTRUCTOR_REQUIRED).at(c.getDefinition())
			.details(superClass.getName(),c.getName())
			.raiseUnrecoverable();
		
	}
	
	
	/**
	 * Struct members are checked so that they contain no modifiers
	 * and no initialization and are not from the type itself
	 */
	@Override
	public void visit(StructImpl struct) {
		for(IScopedElement s : struct.getContent().viewValues()){
			if(s.getElementType() != ScopedElementType.VAR)
				continue;
			
			FieldDecl field = (FieldDecl)s;
			
			//No modifiers!
			if(!field.getFieldDeclaration().getFieldModifiers().isEmpty()){
				throw Problem.ofType(ProblemId.STRUCT_MEMBER_WITH_MODIFIER).at(field.getFieldDeclaration().getFieldModifiers())
								.raiseUnrecoverable();
			}
			
			//No inits!
			if(field.isInitedInDecl()){
				Problem.ofType(ProblemId.STRUCT_MEMBER_WITH_INIT).at(field.getFieldDeclaration())
					.raise();
			}
			
			//Checks for struct members that are also a struct type
			IType type = field.getType();
			int structIndex = type.accept(structIndexCalculator,null);
			if(structIndex > -1){
				int myIndex = struct.getStructId();
				//Struct defined below?
				if(structIndex>myIndex){
					Problem.ofType(ProblemId.STRUCT_MEMBER_REFERENCING_STRUCT_BELOW).at(type.getDefinition())
						.raise();
				//The struct itself?
				} else if(structIndex==myIndex){
					Problem.ofType(ProblemId.STRUCT_MEMBER_REFERENCING_SELF).at(type.getDefinition())
						.raise();
				}
			}
		}
	}
}
