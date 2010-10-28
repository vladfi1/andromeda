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

public abstract class VoidSemanticsVisitorAdapter implements VoidSemanticsVisitor {

	public void visit(FileScope fileScope) { visitDefault(fileScope); }
	public void visit(GlobalScope globalScope) { visitDefault(globalScope); }
	public void visit(BlockScope blockScope) { visitDefault(blockScope); }
	public void visit(Package _package) { visitDefault(_package); }
	public void visit(Constructor constructor) { visitDefault(constructor); }
	public void visit(ConstructorInvocation constructorInvocation) { visitDefault(constructorInvocation); }
	public void visit(Deallocator deallocator) { visitDefault(deallocator); }
	public void visit(Destructor destructor) { visitDefault(destructor); }
	public void visit(Function function) { visitDefault(function); }
	public void visit(GenericFunctionProxy genericFunctionProxy) { visitDefault(genericFunctionProxy); }
	public void visit(GenericMethodProxy genericMethodProxy) { visitDefault(genericMethodProxy); }
	public void visit(Invocation invocation) { visitDefault(invocation); }
	public void visit(Method method) { visitDefault(method); }
	public void visit(StaticInit staticInit) { visitDefault(staticInit); }
	public void visit(ArrayType arrayType) { visitDefault(arrayType); }
	public void visit(BasicType basicType) { visitDefault(basicType); }
	public void visit(Class _class) { visitDefault(_class); }
	public void visit(Enrichment enrichment) { visitDefault(enrichment); }
	public void visit(Extension extension) { visitDefault(extension); }
	public void visit(FunctionPointer functionPointer) { visitDefault(functionPointer); }
	public void visit(GenericClass genericClass) { visitDefault(genericClass); }
	public void visit(GenericClassInstance genericClassInstance) { visitDefault(genericClassInstance); }
	public void visit(Interface _interface) { visitDefault(_interface); }
	public void visit(NonReferentialType nonReferentialType) { visitDefault(nonReferentialType); }
	public void visit(PointerType pointerType) { visitDefault(pointerType); }
	public void visit(SpecialType specialType) { visitDefault(specialType); }
	public void visit(StaticDecl staticDecl) { visitDefault(staticDecl); }
	public void visit(Struct struct) { visitDefault(struct); }
	public void visit(TypeBool typeBool) { visitDefault(typeBool); }
	public void visit(TypeByte typeByte) { visitDefault(typeByte); }
	public void visit(TypeChar typeChar) { visitDefault(typeChar); }
	public void visit(TypeFixed typeFixed) { visitDefault(typeFixed); }
	public void visit(TypeInt typeInt) { visitDefault(typeInt); }
	public void visit(TypeParameter typeParameter) { visitDefault(typeParameter); }
	public void visit(TypeShort typeShort) { visitDefault(typeShort); }
	public void visit(TypeString typeString) { visitDefault(typeString); }
	public void visit(TypeUnknown typeUnknown) { visitDefault(typeUnknown); }
	public void visit(AccessorDecl accessorDecl) { visitDefault(accessorDecl); }
	public void visit(FieldDecl fieldDecl) { visitDefault(fieldDecl); }
	public void visit(FuncPointerDecl funcPointerDecl) { visitDefault(funcPointerDecl); }
	public void visit(FuncPointerNameDecl funcPointerNameDecl) { visitDefault(funcPointerNameDecl); }
	public void visit(GenericVarProxy genericVarProxy) { visitDefault(genericVarProxy); }
	public void visit(GlobalVarDecl globalVarDecl) { visitDefault(globalVarDecl); }
	public void visit(ImplicitFieldDecl implicitFieldDecl) { visitDefault(implicitFieldDecl); }
	public void visit(LocalVarDecl localVarDecl) { visitDefault(localVarDecl); }
	public void visit(ParamDecl paramDecl) { visitDefault(paramDecl); }
	public void visitDefault(SemanticsElement semanticsElement){}

}
