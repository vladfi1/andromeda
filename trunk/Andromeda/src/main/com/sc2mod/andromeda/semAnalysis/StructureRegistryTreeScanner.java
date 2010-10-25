package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.ScopeUtil;
import com.sc2mod.andromeda.environment.scopes.ScopedElement;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetterNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.util.Pair;
import com.sc2mod.andromeda.util.visitors.NoResultTreeScanVisitor;

/**
 * Registers global structures like functions, fields and enrichments. Basically
 * everthing global that is not a type (Types have already been registered by
 * the TypeRegistryTreeScanner). This scanner works after types and their
 * hierarchy has been registered. This is important since it needs the
 * registered types to resolve the types and signatures of fields and functions.
 * 
 * @author gex
 * 
 */
public class StructureRegistryTreeScanner extends
		NoResultTreeScanVisitor<Pair<Scope, Type>> {

	private Environment env;
	private SemanticsCheckerAndResolver resolver;
	private TransientAnalysisData analysisData;

	public StructureRegistryTreeScanner(Environment env, TransientAnalysisData analysisData) {
		this.env = env;
		this.analysisData = analysisData;
		this.resolver = new SemanticsCheckerAndResolver(env);
	}

	// ***** Scope givers ****
	@Override
	public void visit(SourceFileNode andromedaFile, Pair<Scope, Type> scopes) {
		andromedaFile.childrenAccept(this, new Pair<Scope, Type>(andromedaFile
				.getSemantics(), null));
	}

	@Override
	public void visit(ClassDeclNode classDeclNode, Pair<Scope, Type> scopes) {
		RecordType t = classDeclNode.getSemantics();
		classDeclNode.childrenAccept(this, new Pair<Scope, Type>(t, t));
	}

	@Override
	public void visit(StructDeclNode structDeclNode, Pair<Scope, Type> scopes) {
		RecordType t = structDeclNode.getSemantics();
		structDeclNode.childrenAccept(this, new Pair<Scope, Type>(t, t));
	}

	@Override
	public void visit(InterfaceDeclNode interfaceDeclNode,
			Pair<Scope, Type> scopes) {
		RecordType t = interfaceDeclNode.getSemantics();
		interfaceDeclNode.childrenAccept(this, new Pair<Scope, Type>(t, t));
	}

	@Override
	public void visit(EnrichDeclNode enrichDeclaration, Pair<Scope, Type> scopes) {
		// Enrichments are created in this step, since there is no need to
		// create
		// them earlier. Since they are no own types, they do not need to be
		// registered in the type registry phase
		// By registering them this late, the type they enrich can be resolved
		// right away.
		Enrichment enrichment = new Enrichment(enrichDeclaration, scopes._1);
		enrichDeclaration.childrenAccept(this, new Pair<Scope, Type>(
				enrichment, enrichment.getEnrichedType()));
	}

	// ***************************
	// *** Registered elements ***
	// ***************************

	// *** Global elements ***

	@Override
	public void visit(GlobalStaticInitDeclNode globalInitDeclaration,
			Pair<Scope, Type> scopes) {
			StaticInitDeclNode sid = globalInitDeclaration.getInitDecl();
			StaticInit s = new StaticInit(sid,scopes._1);
			resolver.checkAndResolve(s);
			//global static inits are added to their scope (file)
			ScopeUtil.addStaticInit(scopes._1, s);
	}

	@Override
	public void visit(InstanceLimitSetterNode instanceLimitSetter,
			Pair<Scope, Type> scopes) {
		//Resolve type of instance limit setter and check that it may only be applied on classes
		Type t = env.typeProvider.resolveType(instanceLimitSetter.getEnrichedType(), scopes._1);
		if(t.getCategory() != TypeCategory.CLASS) {
			throw Problem.ofType(ProblemId.SETINSTANCELIMIT_ON_NONCLASS).at(instanceLimitSetter)
						.raiseUnrecoverable();
		}
	
		
		//Save for later usage by the InstanceLimitChecker
		analysisData.getInstanceLimits().add(new Pair<InstanceLimitSetterNode, Type>(instanceLimitSetter, t));
	}

	@Override
	public void visit(GlobalFuncDeclNode functionDeclaration,
			Pair<Scope, Type> scopes) {
		Function f = new Function(functionDeclaration, scopes._1);
		//resolve types and do other checks
		resolver.checkAndResolve(f);
		scopes._1.addContent(f.getName(), f);
	}

	@Override
	public void visit(GlobalVarDeclNode g, Pair<Scope, Type> scopes) {
		FieldDeclNode field = g.getFieldDecl();
		VarDeclListNode list = field.getDeclaredVariables();

		for (int i = 0, size = list.size(); i < size; i++) {
			VarDeclNode declNode = list.elementAt(i);
			GlobalVarDecl decl = new GlobalVarDecl(field, declNode, scopes._1);
			resolver.checkAndResolve(decl);
			scopes._1.addContent(decl.getUid(), decl);
		}
	}

	// *** Elements in blocks (members) ***

	/**
	 * Entries a member into the scope in which it is defined and into the
	 * enriched type if we are in an enrichment.
	 */
	private void entry(Pair<Scope, Type> scopes, ScopedElement elem) {
		String uid = elem.getUid();
		Scope scope = scopes._1;
		Type type = scopes._2;
		scope.addContent(uid, elem);
		if (type != scope) {
			type.addContent(uid, elem);
		}
	}

	@Override
	public void visit(FieldDeclNode field, Pair<Scope, Type> scopes) {
		VarDeclListNode list = field.getDeclaredVariables();

		for (int i = 0, size = list.size(); i < size; i++) {
			VarDeclNode declNode = list.elementAt(i);
			FieldDecl decl = new FieldDecl(field, declNode, scopes._2,
					scopes._1);
			resolver.checkAndResolve(decl);
			// Add field into scope (and type if we are in an enrichment)
			entry(scopes, decl);
		}
	}

	@Override
	public void visit(AccessorDeclNode accessorDeclNode, Pair<Scope, Type> state) {
		AccessorDecl ad = new AccessorDecl(accessorDeclNode, state._2, state._1);
		resolver.checkAndResolve(ad);
		entry(state, ad);
	}

	@Override
	public void visit(MethodDeclNode methodDeclNode, Pair<Scope, Type> state) {

		switch(methodDeclNode.getMethodType()){
		case CONSTRUCTOR:
		{
			Class c = (Class)state._2;
			Constructor con = new Constructor(methodDeclNode,c,state._1);
			resolver.checkAndResolve(con);
			c.addConstructor(con);
		}	break;
		case DESTRUCTOR:
		{	Class c = (Class)state._2;
			Destructor destr = new Destructor(methodDeclNode,c,state._1);
			resolver.checkAndResolve(destr);
			c.setDestructor(destr);
		}	break;
		case METHOD:
			Method op = new Method(methodDeclNode,state._2,state._1);
			resolver.checkAndResolve(op);
			entry(state, op);
			break;
		}
		
	}

	@Override
	public void visit(StaticInitDeclNode staticInitDeclNode,
			Pair<Scope, Type> state) {
		StaticInit s = new StaticInit(staticInitDeclNode,state._1);
		resolver.checkAndResolve(s);
		
		//non-global static inits are always only added to their type.
		//Since we must be in a typed block, we can safely assume that type != null
		ScopeUtil.addStaticInit(state._2, s);
	}

}
