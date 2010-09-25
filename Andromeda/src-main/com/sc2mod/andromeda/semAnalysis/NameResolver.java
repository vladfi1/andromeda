/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;


import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Destructor;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.FunctionSet;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.EnrichmentSet;
import com.sc2mod.andromeda.environment.types.FunctionPointer;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.StaticDecl;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarSet;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.variables.implicit.FuncPointerNameDecl;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.DeleteStatement;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocation;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Gets a field access or method invocation and resolves which field or method
 * was called by it. 
 * @author J. 'gex' Finis
 *
 */
public class NameResolver {
	
	private LocalVarStack localVars;
	private Environment env;
	private FunctionSet functions;
	private GlobalVarSet globalVars;
	private TypeProvider typeProvider;
	private VarDecl staticDecl = new VarDecl() {
		
		@Override
		public int getDeclType() {
			return TYPE_STATIC;
		}

		@Override
		public boolean isInitDecl() {
			return false;
		}
	};

	public NameResolver(LocalVarStack localVars, Environment env) {
		env.nameResolver = this;
		this.localVars = localVars;
		this.env = env;
		this.typeProvider = env.typeProvider;
		this.functions = env.getFunctions();
		this.globalVars = env.getGlobalVariables();
	}
	
	private VarDecl resolveFieldAccess(String uid, Scope scope, RecordType curClass,Expression leftSide, Type leftType, SyntaxNode syntax, boolean usesSuper){
		//Is the type correct?
		RecordType r;
		VarDecl field;
		boolean staticAccess = false;
		
		if(leftType instanceof StaticDecl){
			leftType = leftType.getWrappedType();
			staticAccess = true;
		} else if(leftType instanceof FunctionPointer){
			if(uid.equals("name")){
				FuncPointerNameDecl fpnd =  new FuncPointerNameDecl(leftSide,typeProvider);
				syntax.setSemantics(fpnd);
				return fpnd;
			}
		}
		
		boolean tryEnrichments = false;
		if(!leftType.canHaveFields()){
			tryEnrichments = true;
			field = null;
			r = null;
		} else {
			//The type can have fields
			r = (RecordType)leftType;
		
			//Get that field
			field = r.getFields().getFieldByName(uid);
			if(field == null){
				
				//Func pointer?
				AbstractFunction func = r.getMethods().resolveMethodInvocation(syntax, uid, null);
				if(func != null){
					field = func.getPointerDecl(typeProvider);
				} else {
					//No field or func pointer found in this type, try enrichments
					tryEnrichments = true;
				}
			}
		}
			
		//Enrichments
		if(tryEnrichments){
			//No type that can have fields, so check if an enrichment exists
			EnrichmentSet es = typeProvider.getEnrichmentSetForType(leftType);
			if(es==null){
				if(leftType.canHaveFields()){
						throw Problem.ofType(ProblemId.FIELD_NAME_NOT_FOUND).at(syntax).details(uid,leftType.getFullName())
							.raiseUnrecoverable();
					} else {
						throw Problem.ofType(ProblemId.TYPE_CANNOT_HAVE_FIELDS).at(syntax).details(leftType.getFullName())
							.raiseUnrecoverable();
					}
				}
				
			field = es.getFieldByName(uid, curClass, scope);
			if(field == null){
				
				//No field, try func pointer
				AbstractFunction func = es.getMethods().resolveInvocation(scope, syntax, uid, null);
				if(func != null){
					field = func.getPointerDecl(typeProvider);
				}
				
				//Also no function, fail!
				if(field == null){
					throw Problem.ofType(ProblemId.FIELD_NAME_NOT_FOUND).at(syntax).details(uid,leftType.getFullName())
						.raiseUnrecoverable();
				}
			}
			
			
			r = field.getContainingType();
		}
		
		//Accessor?
		if(field.isAccessor()){
//			AccessorDecl ad = (AccessorDecl)field;
//			if(write){
//				if(ad.getSetter() == null)
//					throw new CompilationError(syntax,"The accessor '" + uid + "' has no set method. Cannot set it." );
//			} else {
//				if(ad.getGetter() == null)
//					throw new CompilationError(syntax,"The accessor '" + uid + "' has no get method. Cannot get it." );
//			}
			//throw new Error("TODO: ACCESSOR VISIBILITY UND VERERBUNG!");
		}
		
		//Do visibility checks
		int vis = field.getVisibility();
		switch(vis){
		case Visibility.PRIVATE:
			if(r!=curClass) 
				throw Problem.ofType(ProblemId.FIELD_NOT_VISIBLE).at(syntax).details("private",uid)
					.raiseUnrecoverable();
		case Visibility.PROTECTED:
			if(r!=curClass&&!curClass.isInstanceof((Class)r))
				throw Problem.ofType(ProblemId.FIELD_NOT_VISIBLE).at(syntax).details("protected",uid)
					.raiseUnrecoverable();
		
		default:
		}
		
		//Do static checks
		if(!usesSuper && (staticAccess ^ field.isStatic())){
			Problem p;
			if(field.isStatic())
				p = Problem.ofType(ProblemId.STATIC_MEMBER_MISUSE);
			else 
				p = Problem.ofType(ProblemId.NONSTATIC_MEMBER_MISUSE);
			throw p.at(syntax).details(uid).
				raiseUnrecoverable();
		}
		
		syntax.setSemantics(field);
		return field;
	}
	

	
	public VarDecl resolveVariable(Scope scope,RecordType curClass,FieldAccess fieldAccess, boolean inMember){
		int accessType = fieldAccess.getAccessType();
		Expression leftSide = fieldAccess.getLeftExpression();
		String uid = fieldAccess.getName();
		Type leftType;
		boolean usesSuper = false;
		switch(accessType){
		case AccessType.EXPRESSION:
			leftType = leftSide.getInferedType();
			break;
		case AccessType.POINTER:
			throw new InternalError("Pointers currently not supported.");
//			leftType = leftSide.getInferedType();
//			if(!(leftType instanceof PointerType)) 
//				throw new CompilationError(fieldAccess,"The -> operator can only be used if the left side is a pointer, but it is '" + leftType.getUid() + "'");
//			leftType = ((PointerType)leftType).pointsTo();
//			break;
		case AccessType.SUPER:
			if(curClass == null  || curClass.getCategory()!= Type.CLASS)
				throw Problem.ofType(ProblemId.SUPER_OUTSIDE_OF_CLASS).at(fieldAccess)
					.raiseUnrecoverable();
			curClass = ((Class) curClass).getSuperClass();
			if(curClass == null)
				throw Problem.ofType(ProblemId.SUPER_IN_TOP_CLASS).at(fieldAccess)
					.raiseUnrecoverable();
			leftType = curClass;
			usesSuper = true;
			break;
		case AccessType.SIMPLE:
			return resolveSimpleVariable(scope,curClass,fieldAccess,inMember);
		default:
			throw new InternalError("Field access type " + accessType + " not supported.");
		}
		
		VarDecl vd = resolveFieldAccess(uid, scope, curClass,leftSide, leftType, fieldAccess,usesSuper);
		if(usesSuper&&!inMember&&vd.isMember()){
			throw Problem.ofType(ProblemId.NONSTATIC_MEMBER_MISUSE).at(fieldAccess).details(uid)
				.raiseUnrecoverable();
		}
		
		return vd;
	}	
	
	private VarDecl resolveSimpleVariable(Scope scope,RecordType curClass,FieldAccess name, boolean inMember){
		String uid = name.getName();
		VarDecl v;
		
		//** Simple name **
		
		//First, look in the local variables
		v = localVars.resolveVar(uid);
		if(v != null){
			name.setSemantics(v);
			
			return v;
		}
		
		//Next, check if it is a class member
		if(curClass != null){
			VarDecl f = curClass.getFields().getFieldByName(uid);
			if(f != null){
				//Nonstatic? Only allowed if in a member
				if(!inMember&&!f.isStatic()){
					throw Problem.ofType(ProblemId.NONSTATIC_MEMBER_MISUSE).at(name)
						.details(uid)
						.raiseUnrecoverable();
				
				}
				
				name.setSemantics(f);
				return f;
			}
			
			//Method?
			AbstractFunction func = curClass.getMethods().resolveMethodInvocation(name, uid, null);
			if(func != null){
				return func.getPointerDecl(typeProvider);
			}
		}
		
		//Last, check global variables
		GlobalVarDecl g = globalVars.getVarByName(uid, scope);
		if(g != null){
			name.setSemantics(g);
			return g;
		}
		
		//Is this a type name?
		Type t = typeProvider.resolveTypeName(uid);
		if(t != null){
			staticDecl.setType(new StaticDecl(t));
			name.setSemantics(staticDecl);
			return staticDecl;
		}
		
		//Is this a global function?
		Function func = functions.resolveInvocation(scope, name, uid, null, false);
		if(func != null){
			return func.getPointerDecl(typeProvider);
		}
		
		throw Problem.ofType(ProblemId.VAR_NAME_NOT_FOUND).at(name).details(name.getName())
				.raiseUnrecoverable();
		
	}
	
	public Invocation resolvePrefixedFunctionCall(Scope scope, RecordType curClass, SyntaxNode where, Type leftType, Signature sig, String uid, boolean isStatic){
		//Is the type correct?
		RecordType r;
		AbstractFunction meth;
		
		if(leftType instanceof StaticDecl){
			leftType = leftType.getWrappedType();
		}
		
		boolean tryEnrichments = false;
		if(!leftType.canHaveMethods()){
			tryEnrichments = true;
			meth = null;
			r = null;
		} else {
			//The type can have fields
			r = (RecordType)leftType;
		
			//Get that method
			meth = r.getMethods().resolveMethodInvocation(where, uid, sig);
			if(meth == null){
				tryEnrichments = true;
			}
		}
			
		//Enrichments
		if(tryEnrichments){
			//No type that can have fields, so check if an enrichment exists
			boolean enrichmentSetFound = false;
			Type newType = leftType;
			Type oldType = leftType;
			meth = null;
			do{
				leftType = newType;
				EnrichmentSet es = typeProvider.getEnrichmentSetForType(leftType);
				if(es == null) continue;
				enrichmentSetFound = true;
				meth = es.resolveMethodInvocation(scope, where, uid, sig, curClass);
				if(meth != null) break;
				
			} while((newType=leftType.getSuperType())!=leftType);
			
			if(!enrichmentSetFound){
				if(oldType.canHaveFields()){
						throw Problem.ofType(ProblemId.NO_SUCH_METHOD).at(where)
								.details(uid,sig.getFullName(),leftType)
								.raiseUnrecoverable();
				} else {
						throw Problem.ofType(ProblemId.TYPE_CANNOT_HAVE_METHODS).at(where)
								.details(leftType)
								.raiseUnrecoverable();
					}
				}
				
			if(meth == null){
				throw Problem.ofType(ProblemId.NO_SUCH_METHOD).at(where)
				.details(uid,sig.getFullName(),leftType)
				.raiseUnrecoverable();
			}
			r = meth.getContainingType();
		}
		
		//Do visibility checks
		int vis = meth.getVisibility();
		switch(vis){
		case Visibility.PRIVATE:
			if(r!=curClass) 
				throw Problem.ofType(ProblemId.METHOD_NOT_VISIBLE).at(where)
						.details("private",uid)
						.raiseUnrecoverable();
		case Visibility.PROTECTED:
			if(r!=curClass&&!curClass.isInstanceof((Class)r))
				throw Problem.ofType(ProblemId.METHOD_NOT_VISIBLE).at(where)
				.details("protected",uid)
				.raiseUnrecoverable();
		default:
		}
	
		if(meth.isStatic()){
			if(isStatic){
				return new Invocation(meth,Invocation.TYPE_STATIC);
			}
			throw Problem.ofType(ProblemId.STATIC_MEMBER_MISUSE).at(where)
				.details(uid)
				.raiseUnrecoverable();
		}
		if(isStatic){
			throw Problem.ofType(ProblemId.NONSTATIC_MEMBER_MISUSE).at(where)
				.details(uid)
				.raiseUnrecoverable();
		}
		if(meth.isOverridden())
			return Invocation.createVirtualInvocation(meth, env);
		return new Invocation(meth,Invocation.TYPE_METHOD);

		
	}
	
	
	public Invocation resolveFunctionCall(Scope scope,RecordType curClass,MethodInvocation methodInvocation, boolean inMember){
		//Assemble call signature
		Signature s = new Signature(methodInvocation.getArguments());
		String name = methodInvocation.getFuncName();
		
		int invocationType = methodInvocation.getInvocationType();
		boolean canBeMethod = true;
		boolean canBeFunction = true;
		boolean virtualAllowed = true;
		switch(invocationType){
		case AccessType.SIMPLE:
			break;
		case AccessType.EXPRESSION:
			boolean isStatic;
			if(methodInvocation.getPrefix().getSemantics()==staticDecl){
				isStatic = true;
			} else {
				isStatic = false;
			}
			return resolvePrefixedFunctionCall(scope, curClass, methodInvocation, methodInvocation.getPrefix().getInferedType(), s, name,isStatic);
		case AccessType.NAMED_SUPER:
		case AccessType.SUPER:
			if(curClass == null  || !curClass.isClass())
				throw Problem.ofType(ProblemId.SUPER_OUTSIDE_OF_CLASS).at(methodInvocation)
					.raiseUnrecoverable();
			curClass = ((Class) curClass).getSuperClass();
			if(curClass == null)
				throw Problem.ofType(ProblemId.SUPER_IN_TOP_CLASS).at(methodInvocation)
					.raiseUnrecoverable();
			canBeFunction = false;
			
			//Super calls are never virtual
			virtualAllowed = false;
			break;
		case AccessType.NATIVE:
			canBeMethod = false;
			break;
		default: 
			throw new InternalProgramError(methodInvocation, "Unknown invocation type " + invocationType);
		}

		//Method call?
		if(canBeMethod&&curClass != null){
			AbstractFunction m = curClass.getMethods().resolveMethodInvocation(methodInvocation, name, s);
			if(m!=null){
				if(m.isStatic())
					return new Invocation(m,Invocation.TYPE_STATIC);
				
				//Non static method. Can we access it from here?
				if(!inMember)
					throw Problem.ofType(ProblemId.NONSTATIC_MEMBER_MISUSE).at(methodInvocation)
						.details(name)
						.raiseUnrecoverable();
				
				if(virtualAllowed&&m.isOverridden())
					return Invocation.createVirtualInvocation(m, env);
				return new Invocation(m,Invocation.TYPE_METHOD);
			}
		}		
		
		//Function call?
		if(canBeFunction){
			Function f = functions.resolveInvocation(scope,methodInvocation,name,s,invocationType==AccessType.NATIVE);
			if(f != null){
				if(f.isNative())
					return new Invocation(f, Invocation.TYPE_NATIVE);
				return new Invocation(f, Invocation.TYPE_FUNCTION);
			}
		}
		
		throw Problem.ofType(ProblemId.NO_SUCH_FUNCTION).at(methodInvocation)
					.details(name,s.getFullName())
					.raiseUnrecoverable();
	}
	

	
	public void registerLocalVar(VarDecl decl){
		localVars.addLocalVar(decl);
	}
	
	public void pushLocalBlock(){
		localVars.pushLocalBlock();
	}
	
	public void popLocalBlock(){
		localVars.popLocalBlock();
	}

	public LocalVarDecl[] methodFinished(int numParams) {
		return localVars.methodFinished(numParams);
	}

	public Invocation registerDelete(Class class1, DeleteStatement deleteStatement) {
		Destructor destructor = class1.getDestructor();
		if(destructor.isOverridden())
			return Invocation.createVirtualInvocation(destructor, env);
		return new Invocation(destructor,Invocation.TYPE_METHOD);
	}

}
