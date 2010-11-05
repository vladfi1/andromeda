package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;

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
	public SemanticsElementCheckVisitor(Environment env) {
		this.tprov = env.typeProvider;
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
		
		if(!class1.isAbstract())
			checkForUnimplementedMethods(class1);
	}
	
	/**
	 * Checks that non abstract classes contain no unimplemented methods
	 * @param class1
	 */
	private void checkForUnimplementedMethods(IClass class1) {
		//FIXME: Check for unimplemented methods again
//		if(methods.containsUnimplementedMethods()&&!this.isAbstract()){
//			List<Operation> meths = methods.getUnimplementedMethods();
//			StringBuffer sb = new StringBuffer(256);
//			for(Operation m : meths){
//				sb.append("\n").append(m.getContainingType().getUid()).append(".").append(m.getNameAndSignature()).append("\n");
//			}
//			sb.setLength(sb.length()-1);
//			throw Problem.ofType(ProblemId.ABSTRACT_CLASS_MISSES_IMPLEMENTATIONS).at(this.declaration)
//							.details(this.getUid(),sb.toString())
//							.raiseUnrecoverable();
//			
//		}
	}

	/**
	 * Checks about static classes
	 * @param class1
	 */
	private void checkStaticClass(IClass class1){
		if(!class1.getConstructors().isEmpty())
			throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_CONSTRUCTOR).at(class1.getConstructors().getAny().getDefinition())
						.raiseUnrecoverable();
		
		//FIXME: Numstatics count in typeUtil machen und hier usen
//		if(class1.getNumNonStatics()!=0)
//			throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_NON_STATIC_MEMBER).at(class1.getDefinition())
//						.raiseUnrecoverable();
	}
	

	/**
	 * Checks that a class which has a parent class without a parameterless
	 * constructor acutally defines a constructor.
	 * @param c
	 */
	private void checkImplicitConstructor(IClass c) {
		//If we are in a top class or we have constructors, everything is fine
		if(c.isTopClass()) return;
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
	 * Struct members are checked so that they contain no modifiers.
	 */
	@Override
	public void visit(StructImpl struct) {
		for(IScopedElement s : struct.getContent().viewValues()){
			//Since the parser anything but fields in structs, we can safely cast to fieldDecl here
			FieldDecl field = (FieldDecl)s;
			if(!field.getFieldDeclaration().getFieldModifiers().isEmpty()){
				throw Problem.ofType(ProblemId.STRUCT_MEMBER_WITH_MODIFIER).at(field.getFieldDeclaration().getFieldModifiers())
								.raiseUnrecoverable();
			}
		}
	}
}
