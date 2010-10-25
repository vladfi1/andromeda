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
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.Extension;
import com.sc2mod.andromeda.environment.types.FunctionPointer;
import com.sc2mod.andromeda.environment.types.GenericClass;
import com.sc2mod.andromeda.environment.types.GenericClassInstance;
import com.sc2mod.andromeda.environment.types.Interface;
import com.sc2mod.andromeda.environment.types.NonReferentialType;
import com.sc2mod.andromeda.environment.types.PointerType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.StaticDecl;
import com.sc2mod.andromeda.environment.types.Struct;
import com.sc2mod.andromeda.environment.types.TypeBool;
import com.sc2mod.andromeda.environment.types.TypeByte;
import com.sc2mod.andromeda.environment.types.TypeChar;
import com.sc2mod.andromeda.environment.types.TypeFixed;
import com.sc2mod.andromeda.environment.types.TypeInt;
import com.sc2mod.andromeda.environment.types.TypeParameter;
import com.sc2mod.andromeda.environment.types.TypeShort;
import com.sc2mod.andromeda.environment.types.TypeString;
import com.sc2mod.andromeda.environment.types.TypeUnknown;
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

public abstract class ParameterSemanticsVisitorAdapter<P,R> implements ParameterSemanticsVisitor<P,R> {

	public R visit(FileScope fileScope,P state) { return visitDefault(fileScope,state); }
	public R visit(GlobalScope globalScope,P state) { return visitDefault(globalScope,state); }
	public R visit(BlockScope blockScope,P state) { return visitDefault(blockScope,state); }
	public R visit(Package _package,P state) { return visitDefault(_package,state); }
	public R visit(Constructor constructor,P state) { return visitDefault(constructor,state); }
	public R visit(ConstructorInvocation constructorInvocation,P state) { return visitDefault(constructorInvocation,state); }
	public R visit(Deallocator deallocator,P state) { return visitDefault(deallocator,state); }
	public R visit(Destructor destructor,P state) { return visitDefault(destructor,state); }
	public R visit(Function function,P state) { return visitDefault(function,state); }
	public R visit(GenericFunctionProxy genericFunctionProxy,P state) { return visitDefault(genericFunctionProxy,state); }
	public R visit(GenericMethodProxy genericMethodProxy,P state) { return visitDefault(genericMethodProxy,state); }
	public R visit(Invocation invocation,P state) { return visitDefault(invocation,state); }
	public R visit(Method method,P state) { return visitDefault(method,state); }
	public R visit(StaticInit staticInit,P state) { return visitDefault(staticInit,state); }
	public R visit(ArrayType arrayType,P state) { return visitDefault(arrayType,state); }
	public R visit(BasicType basicType,P state) { return visitDefault(basicType,state); }
	public R visit(Class _class,P state) { return visitDefault(_class,state); }
	public R visit(Enrichment enrichment,P state) { return visitDefault(enrichment,state); }
	public R visit(Extension extension,P state) { return visitDefault(extension,state); }
	public R visit(FunctionPointer functionPointer,P state) { return visitDefault(functionPointer,state); }
	public R visit(GenericClass genericClass,P state) { return visitDefault(genericClass,state); }
	public R visit(GenericClassInstance genericClassInstance,P state) { return visitDefault(genericClassInstance,state); }
	public R visit(Interface _interface,P state) { return visitDefault(_interface,state); }
	public R visit(NonReferentialType nonReferentialType,P state) { return visitDefault(nonReferentialType,state); }
	public R visit(PointerType pointerType,P state) { return visitDefault(pointerType,state); }
	public R visit(SpecialType specialType,P state) { return visitDefault(specialType,state); }
	public R visit(StaticDecl staticDecl,P state) { return visitDefault(staticDecl,state); }
	public R visit(Struct struct,P state) { return visitDefault(struct,state); }
	public R visit(TypeBool typeBool,P state) { return visitDefault(typeBool,state); }
	public R visit(TypeByte typeByte,P state) { return visitDefault(typeByte,state); }
	public R visit(TypeChar typeChar,P state) { return visitDefault(typeChar,state); }
	public R visit(TypeFixed typeFixed,P state) { return visitDefault(typeFixed,state); }
	public R visit(TypeInt typeInt,P state) { return visitDefault(typeInt,state); }
	public R visit(TypeParameter typeParameter,P state) { return visitDefault(typeParameter,state); }
	public R visit(TypeShort typeShort,P state) { return visitDefault(typeShort,state); }
	public R visit(TypeString typeString,P state) { return visitDefault(typeString,state); }
	public R visit(TypeUnknown typeUnknown,P state) { return visitDefault(typeUnknown,state); }
	public R visit(AccessorDecl accessorDecl,P state) { return visitDefault(accessorDecl,state); }
	public R visit(FieldDecl fieldDecl,P state) { return visitDefault(fieldDecl,state); }
	public R visit(FuncPointerDecl funcPointerDecl,P state) { return visitDefault(funcPointerDecl,state); }
	public R visit(FuncPointerNameDecl funcPointerNameDecl,P state) { return visitDefault(funcPointerNameDecl,state); }
	public R visit(GenericVarProxy genericVarProxy,P state) { return visitDefault(genericVarProxy,state); }
	public R visit(GlobalVarDecl globalVarDecl,P state) { return visitDefault(globalVarDecl,state); }
	public R visit(ImplicitFieldDecl implicitFieldDecl,P state) { return visitDefault(implicitFieldDecl,state); }
	public R visit(LocalVarDecl localVarDecl,P state) { return visitDefault(localVarDecl,state); }
	public R visit(ParamDecl paramDecl,P state) { return visitDefault(paramDecl,state); }
	public R visitDefault(SemanticsElement semanticsElement,P state){ return null; }

}
