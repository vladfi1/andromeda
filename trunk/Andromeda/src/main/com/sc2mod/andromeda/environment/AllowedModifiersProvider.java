package com.sc2mod.andromeda.environment;

import java.util.EnumSet;
import java.util.Set;

import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Deallocator;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.SyntheticFieldDecl;
import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;
import com.sc2mod.andromeda.syntaxNodes.ModifierSE;

public class AllowedModifiersProvider {

	//Misc
	private static final EnumSet<ModifierSE> NO_MODIFIERS = EnumSet.noneOf(ModifierSE.class);
	
	//Operations
	private static final EnumSet<ModifierSE> FUNC_MODIFIERS = EnumSet.of(ModifierSE.NATIVE);
	private static final EnumSet<ModifierSE> METHOD_MODIFIERS = EnumSet.of(ModifierSE.ABSTRACT,ModifierSE.FINAL,ModifierSE.STATIC,ModifierSE.OVERRIDE);
	private static final EnumSet<ModifierSE> CONSTRUCTOR_MODIFIERS = NO_MODIFIERS;
	private static final EnumSet<ModifierSE> DESTRUCTOR_MODIFIERS = NO_MODIFIERS;
	private static final EnumSet<ModifierSE> DEALLOCATOR_MODIFIERS = NO_MODIFIERS;
	
	//Types
	private static final EnumSet<ModifierSE> CLASS_MODIFIERS = EnumSet.of(ModifierSE.ABSTRACT, ModifierSE.FINAL, ModifierSE.STATIC);
	private static final EnumSet<ModifierSE> INTERFACE_MODIFIERS = NO_MODIFIERS;
	private static final EnumSet<ModifierSE> STRUCT_MODIFIERS = NO_MODIFIERS;
	private static final EnumSet<ModifierSE> EXTENSION_MODIFIERS = EnumSet.of(ModifierSE.FINAL);
	
	//Variables
	private static final EnumSet<ModifierSE> FIELD_MODIFIERS = EnumSet.of(ModifierSE.STATIC, ModifierSE.CONST);
	private static final EnumSet<ModifierSE> GLOBAL_VAR_MODIFIERS = EnumSet.of(ModifierSE.CONST);
	//FIXME testcases that params and locals cannot have visibility modifiers
	private static final EnumSet<ModifierSE> PARAM_MODIFIERS = EnumSet.of(ModifierSE.CONST);
	private static final EnumSet<ModifierSE> LOCAL_VAR_MODIFIERS = EnumSet.of(ModifierSE.CONST);
	
	private final Visitor visitor = new Visitor();
	
	private class Visitor extends ParameterSemanticsVisitorAdapter<Void, Set<ModifierSE>>{
		
		//*********** OPERATIONS ************
		@Override
		public Set<ModifierSE> visit(Function function, Void state) {
			return FUNC_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(Method method, Void state) {
			return METHOD_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(Constructor constructor, Void state) {
			return CONSTRUCTOR_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(Destructor destructor, Void state) {
			return DESTRUCTOR_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(Deallocator deallocator, Void state) {
			return DEALLOCATOR_MODIFIERS;
		}
		
		//*********** TYPES ************
		@Override
		public Set<ModifierSE> visit(ClassImpl classImpl, Void state) {
			return CLASS_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(ExtensionImpl extensionImpl, Void state) {
			return EXTENSION_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(InterfaceImpl interfaceImpl, Void state) {
			return INTERFACE_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(StructImpl structImpl, Void state) {
			return STRUCT_MODIFIERS;
		}
		
		//*********** VARIABLES ************
		@Override
		public Set<ModifierSE> visit(FieldDecl fieldDecl, Void state) {
			return FIELD_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(GlobalVarDecl globalVarDecl, Void state) {
			return GLOBAL_VAR_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(SyntheticFieldDecl implicitFieldDecl,
				Void state) {
			return FIELD_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(ImplicitParamDecl implicitParamDecl,
				Void state) {
			return PARAM_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(ParamDecl paramDecl, Void state) {
			return PARAM_MODIFIERS;
		}
		@Override
		public Set<ModifierSE> visit(LocalVarDecl localVarDecl, Void state) {
			return LOCAL_VAR_MODIFIERS;
		}
		
		@Override
		public Set<ModifierSE> visitDefault(SemanticsElement semanticsElement,
				Void state) {
			throw new InternalError("Unknown modifiable element " + semanticsElement.getClass().getSimpleName());
		}
		
		
	}


	public Set<ModifierSE> provide(SemanticsElement se) {
		return se.accept(visitor,null);
	}
}
