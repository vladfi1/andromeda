package com.sc2mod.andromeda.environment.visitors;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.GlobalScope;
import com.sc2mod.andromeda.environment.scopes.BlockScope;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.ConstructorInvocation;
import com.sc2mod.andromeda.environment.operations.Deallocator;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.GenericFunctionProxy;
import com.sc2mod.andromeda.environment.operations.GenericMethodProxy;
import com.sc2mod.andromeda.environment.operations.Invocation;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.types.ArrayType;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.Extension;
import com.sc2mod.andromeda.environment.types.FunctionPointer;
import com.sc2mod.andromeda.environment.types.GenericClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.NonReferentialType;
import com.sc2mod.andromeda.environment.types.PointerType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.StaticDecl;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.TypeBool;
import com.sc2mod.andromeda.environment.types.TypeByte;
import com.sc2mod.andromeda.environment.types.TypeChar;
import com.sc2mod.andromeda.environment.types.TypeFixed;
import com.sc2mod.andromeda.environment.types.TypeInt;
import com.sc2mod.andromeda.environment.types.TypeShort;
import com.sc2mod.andromeda.environment.types.TypeString;
import com.sc2mod.andromeda.environment.types.TypeUnknown;
import com.sc2mod.andromeda.environment.types.generic.GenericClassInstance;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.FuncPointerNameDecl;
import com.sc2mod.andromeda.environment.variables.GenericVarProxy;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.ImplicitFieldDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.SemanticsElement;

public abstract class NoResultSemanticsVisitorAdapter<P> implements NoResultSemanticsVisitor<P> {

	public void visit(FileScope fileScope,P state) { visitDefault(fileScope,state); }
	public void visit(GlobalScope globalScope,P state) { visitDefault(globalScope,state); }
	public void visit(BlockScope blockScope,P state) { visitDefault(blockScope,state); }
	public void visit(Package _package,P state) { visitDefault(_package,state); }
	public void visit(Constructor constructor,P state) { visitDefault(constructor,state); }
	public void visit(ConstructorInvocation constructorInvocation,P state) { visitDefault(constructorInvocation,state); }
	public void visit(Deallocator deallocator,P state) { visitDefault(deallocator,state); }
	public void visit(Destructor destructor,P state) { visitDefault(destructor,state); }
	public void visit(Function function,P state) { visitDefault(function,state); }
	public void visit(GenericFunctionProxy genericFunctionProxy,P state) { visitDefault(genericFunctionProxy,state); }
	public void visit(GenericMethodProxy genericMethodProxy,P state) { visitDefault(genericMethodProxy,state); }
	public void visit(Invocation invocation,P state) { visitDefault(invocation,state); }
	public void visit(Method method,P state) { visitDefault(method,state); }
	public void visit(StaticInit staticInit,P state) { visitDefault(staticInit,state); }
	public void visit(ArrayType arrayType,P state) { visitDefault(arrayType,state); }
	public void visit(BasicType basicType,P state) { visitDefault(basicType,state); }
	public void visit(IClass _class,P state) { visitDefault(_class,state); }
	public void visit(Enrichment enrichment,P state) { visitDefault(enrichment,state); }
	public void visit(Extension extension,P state) { visitDefault(extension,state); }
	public void visit(FunctionPointer functionPointer,P state) { visitDefault(functionPointer,state); }
	public void visit(GenericClass genericClass,P state) { visitDefault(genericClass,state); }
	public void visit(GenericClassInstance genericClassInstance,P state) { visitDefault(genericClassInstance,state); }
	public void visit(IInterface _interface,P state) { visitDefault(_interface,state); }
	public void visit(NonReferentialType nonReferentialType,P state) { visitDefault(nonReferentialType,state); }
	public void visit(PointerType pointerType,P state) { visitDefault(pointerType,state); }
	public void visit(SpecialType specialType,P state) { visitDefault(specialType,state); }
	public void visit(StaticDecl staticDecl,P state) { visitDefault(staticDecl,state); }
	public void visit(IStruct struct,P state) { visitDefault(struct,state); }
	public void visit(TypeBool typeBool,P state) { visitDefault(typeBool,state); }
	public void visit(TypeByte typeByte,P state) { visitDefault(typeByte,state); }
	public void visit(TypeChar typeChar,P state) { visitDefault(typeChar,state); }
	public void visit(TypeFixed typeFixed,P state) { visitDefault(typeFixed,state); }
	public void visit(TypeInt typeInt,P state) { visitDefault(typeInt,state); }
	public void visit(TypeParameter typeParameter,P state) { visitDefault(typeParameter,state); }
	public void visit(TypeShort typeShort,P state) { visitDefault(typeShort,state); }
	public void visit(TypeString typeString,P state) { visitDefault(typeString,state); }
	public void visit(TypeUnknown typeUnknown,P state) { visitDefault(typeUnknown,state); }
	public void visit(AccessorDecl accessorDecl,P state) { visitDefault(accessorDecl,state); }
	public void visit(FieldDecl fieldDecl,P state) { visitDefault(fieldDecl,state); }
	public void visit(FuncPointerDecl funcPointerDecl,P state) { visitDefault(funcPointerDecl,state); }
	public void visit(FuncPointerNameDecl funcPointerNameDecl,P state) { visitDefault(funcPointerNameDecl,state); }
	public void visit(GenericVarProxy genericVarProxy,P state) { visitDefault(genericVarProxy,state); }
	public void visit(GlobalVarDecl globalVarDecl,P state) { visitDefault(globalVarDecl,state); }
	public void visit(ImplicitFieldDecl implicitFieldDecl,P state) { visitDefault(implicitFieldDecl,state); }
	public void visit(LocalVarDecl localVarDecl,P state) { visitDefault(localVarDecl,state); }
	public void visit(ParamDecl paramDecl,P state) { visitDefault(paramDecl,state); }
	public void visitDefault(SemanticsElement semanticsElement,P state){}

}
