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

public interface ParameterSemanticsVisitor<P,R> {

	public R visit(FileScope fileScope,P state);
	public R visit(GlobalScope globalScope,P state);
	public R visit(BlockScope blockScope,P state);
	public R visit(Package _package,P state);
	public R visit(Constructor constructor,P state);
	public R visit(ConstructorInvocation constructorInvocation,P state);
	public R visit(Deallocator deallocator,P state);
	public R visit(Destructor destructor,P state);
	public R visit(Function function,P state);
	public R visit(GenericFunctionProxy genericFunctionProxy,P state);
	public R visit(GenericMethodProxy genericMethodProxy,P state);
	public R visit(Invocation invocation,P state);
	public R visit(Method method,P state);
	public R visit(StaticInit staticInit,P state);
	public R visit(ArrayType arrayType,P state);
	public R visit(BasicType basicType,P state);
	public R visit(IClass _class,P state);
	public R visit(Enrichment enrichment,P state);
	public R visit(Extension extension,P state);
	public R visit(FunctionPointer functionPointer,P state);
	public R visit(GenericClass genericClass,P state);
	public R visit(GenericClassInstance genericClassInstance,P state);
	public R visit(IInterface _interface,P state);
	public R visit(NonReferentialType nonReferentialType,P state);
	public R visit(PointerType pointerType,P state);
	public R visit(SpecialType specialType,P state);
	public R visit(StaticDecl staticDecl,P state);
	public R visit(IStruct struct,P state);
	public R visit(TypeBool typeBool,P state);
	public R visit(TypeByte typeByte,P state);
	public R visit(TypeChar typeChar,P state);
	public R visit(TypeFixed typeFixed,P state);
	public R visit(TypeInt typeInt,P state);
	public R visit(TypeParameter typeParameter,P state);
	public R visit(TypeShort typeShort,P state);
	public R visit(TypeString typeString,P state);
	public R visit(TypeUnknown typeUnknown,P state);
	public R visit(AccessorDecl accessorDecl,P state);
	public R visit(FieldDecl fieldDecl,P state);
	public R visit(FuncPointerDecl funcPointerDecl,P state);
	public R visit(FuncPointerNameDecl funcPointerNameDecl,P state);
	public R visit(GenericVarProxy genericVarProxy,P state);
	public R visit(GlobalVarDecl globalVarDecl,P state);
	public R visit(ImplicitFieldDecl implicitFieldDecl,P state);
	public R visit(LocalVarDecl localVarDecl,P state);
	public R visit(ParamDecl paramDecl,P state);

}
