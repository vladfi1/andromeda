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
import com.sc2mod.andromeda.environment.types.PointerType;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.StaticDecl;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarSet;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.variables.implicit.FuncPointerNameDecl;
import com.sc2mod.andromeda.notifications.CompilationError;
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
						throw new CompilationError(syntax,"No visible field named '" + uid + "' exists for type " + leftType.getUid() + ".");
					} else {
						throw new CompilationError(syntax,"The left side of a field access operation must be a class, struct or an enriched basic type. However, no enrichment for the type '" + leftType.getUid() + "' exists.");
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
					if(leftType.canHaveFields()){
						throw new CompilationError(syntax,"No visible field named '" + uid + "' exists for type " + leftType.getUid() + ".");
					} else {
						throw new CompilationError(syntax,"No visible accessor named '" + uid + "' exists in enrichments for type " + leftType.getUid() + ".");
					}
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
			if(r!=curClass) throw new CompilationError(syntax,"The private field '" + uid + "' is not visible.");
		case Visibility.PROTECTED:
			if(r!=curClass&&!curClass.isInstanceof((Class)r)) throw new CompilationError(syntax,"The protected field '" + uid + "' is not visible.");
		default:
		}
		
		//Do accessor checks
		if(!usesSuper && (staticAccess ^ field.isStatic())){
			throw new CompilationError(syntax,"Trying to access a static member in a non-static way or vice versa.");
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
			leftType = leftSide.getInferedType();
			if(!(leftType instanceof PointerType)) 
				throw new CompilationError(fieldAccess,"The -> operator can only be used if the left side is a pointer, but it is '" + leftType.getUid() + "'");
			leftType = ((PointerType)leftType).pointsTo();
			break;
		case AccessType.SUPER:
			if(curClass == null  || curClass.getCategory()!= Type.CLASS)
				throw new CompilationError(fieldAccess,"Super can only be used inside of classes.");
			curClass = ((Class) curClass).getSuperClass();
			if(curClass == null)
				throw new CompilationError(fieldAccess,"Cannot use super here, since the class has no super class.");
			leftType = curClass;
			usesSuper = true;
			break;
		case AccessType.SIMPLE:
			return resolveSimpleVariable(scope,curClass,fieldAccess,inMember);
		default:
			throw new CompilationError(fieldAccess,"Field access type " + accessType + " not supported.");
		}
		
		VarDecl vd = resolveFieldAccess(uid, scope, curClass,leftSide, leftType, fieldAccess,usesSuper);
		if(usesSuper&&!inMember&&vd.isMember()){
			throw new CompilationError(fieldAccess,"Cannot access a non-static member from a static contect");
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
					throw new CompilationError(name,"Cannot access a non-static member from a static context.");
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
		
		throw new CompilationError(name,"No visible variable or field of the name '" + name.getName() + "' exists.");
		
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
						throw new CompilationError(where,"No visible method " + uid +"(" + sig.getFullName() + ") exists for type " + leftType.getFullName() + ".");
					} else {
						throw new CompilationError(where,"The left side of a field access operation must be a class, struct or an enriched basic type. However, no enrichment for the type '" + leftType.getUid() + "' exists.");
					}
				}
				
			if(meth == null){
				if(oldType.canHaveFields()){
					throw new CompilationError(where,"No visible field named '" + uid + "' exists for type " + leftType.getUid() + ".");
				} else {
					throw new CompilationError(where,"No visible accessor named '" + uid + "' exists in enrichments for type " + leftType.getUid() + ".");
				}
				
			}
			r = meth.getContainingType();
		}
		
		//Do visibility checks
		int vis = meth.getVisibility();
		switch(vis){
		case Visibility.PRIVATE:
			if(r!=curClass) throw new CompilationError(where,"The private field '" + uid + "' is not visible.");
		case Visibility.PROTECTED:
			if(r!=curClass&&!curClass.isInstanceof((Class)r)) throw new CompilationError(where,"The protected field '" + uid + "' is not visible.");
		default:
		}
	
		if(meth.isStatic()){
			if(isStatic){
				return new Invocation(meth,Invocation.TYPE_STATIC);
			}
			throw new CompilationError(where, "Static methods must be accessed in a static way (by prefixing them with their type name).");
		}
		if(isStatic){
			throw new CompilationError(where, "Cannot access a non-static method in a static way.");
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
			if(curClass == null  || curClass.getCategory()!= Type.CLASS)
				throw new CompilationError(methodInvocation,"Super can only be used inside of classes.");
			curClass = ((Class) curClass).getSuperClass();
			if(curClass == null)
				throw new CompilationError(methodInvocation,"Cannot use super here, since the class has no super class.");
			canBeFunction = false;
			
			//Super calls are never virtual
			virtualAllowed = false;
			break;
		case AccessType.NATIVE:
			canBeMethod = false;
			break;
		default: throw new CompilationError(methodInvocation, "Unknown invocation type " + invocationType);
		}

		//Method call?
		if(canBeMethod&&curClass != null){
			AbstractFunction m = curClass.getMethods().resolveMethodInvocation(methodInvocation, name, s);
			if(m!=null){
				if(m.isStatic())
					return new Invocation(m,Invocation.TYPE_STATIC);
				
				//Non static method. Can we access it from here?
				if(!inMember)
					throw new CompilationError(methodInvocation,"Cannot call a non-static method from a static context.");
				
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
		
		throw new CompilationError(methodInvocation,"Could not resolve this function call. No visible function/method with this name and signature exists.\nInferred Signature: (" + s.toString() + ")");
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
