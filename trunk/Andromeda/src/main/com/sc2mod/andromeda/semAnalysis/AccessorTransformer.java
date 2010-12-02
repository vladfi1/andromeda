package com.sc2mod.andromeda.semAnalysis;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AccessorList;
import com.sc2mod.andromeda.syntaxNodes.AssignOpSE;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodHeaderNode;
import com.sc2mod.andromeda.syntaxNodes.MethodTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierSE;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParameterListNode;
import com.sc2mod.andromeda.syntaxNodes.ParameterNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.SimpleTypeNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

//TODO Comment this class
public class AccessorTransformer {
	
	private SimpleTypeNode VOID = new SimpleTypeNode("void", null);
	private ParameterListNode EMPTY_PARAMS = new ParameterListNode();
	
	private List<MethodDeclNode> accessorMethodsToAdd = new ArrayList<MethodDeclNode>();
	private List<GlobalFuncDeclNode> accessorFuncsToAdd = new ArrayList<GlobalFuncDeclNode>();
	private List<FieldDeclNode> fieldsToRemove = new ArrayList<FieldDeclNode>();
	private boolean wantRemove;
	
	public void doPostProcessing(){
		
		for(FieldDeclNode f : fieldsToRemove){
			//Remove abstract fields
			SyntaxNode s = f.getParent();
			if(s instanceof GlobalVarDeclNode){
				SyntaxNode list = s.getParent();
				if(list instanceof GlobalStructureListNode){
					if(! ((GlobalStructureListNode)list).remove((GlobalStructureNode) s) ){
						throw new InternalProgramError("!");
					}
				} else throw new InternalProgramError("!");
			} else if(s instanceof MemberDeclListNode){
				if(! ((MemberDeclListNode)s).remove(f) ){
					throw new InternalProgramError("!");
				}
			} else throw new InternalProgramError("!");
			
		}
		
		for(MethodDeclNode m : accessorMethodsToAdd){
			//Add accessor methods
			SyntaxNode s = m.getParent();
			if(s instanceof MemberDeclListNode){
				((MemberDeclListNode)s).add(m);
			} else throw new InternalProgramError("!");
		}
		
		for(GlobalFuncDeclNode m : accessorFuncsToAdd){
			//Add accessor methods
			SyntaxNode s = m.getParent();
			if(s instanceof GlobalStructureListNode){
				((GlobalStructureListNode)s).add(m);
			} else throw new InternalProgramError("!");
		}
		
		fieldsToRemove.clear();
		accessorMethodsToAdd.clear();
		accessorFuncsToAdd.clear();
	}

	public MethodDeclNode[] transformClassField(FieldDeclNode field){
		VarDeclNode var = checkSizeAndGetVarDecl(field);
		
		checkRemoveFieldIfAbstract(field);
		
		AccessorList accessors = getAndRemoveAccessors(field);
		MethodDeclNode[] result = new MethodDeclNode[accessors.size()];
		for(int i=0,size=accessors.size(); i<size ; i++){
			MethodDeclNode md = transformAccessor(field,var,accessors.get(i));
			result[i] = md;
			md.setParent(field.getParent());
			accessorMethodsToAdd.add(md);
		}
		return result;

	}

	public GlobalFuncDeclNode[] transformGlobalField(GlobalVarDeclNode globalDecl) {
		FieldDeclNode field = globalDecl.getFieldDecl();
		VarDeclNode var = checkSizeAndGetVarDecl(field);
		
		checkRemoveFieldIfAbstract(field);
		
		AccessorList accessors = getAndRemoveAccessors(field);
		GlobalFuncDeclNode[] result = new GlobalFuncDeclNode[accessors.size()];
		for(int i=0,size=accessors.size(); i<size ; i++){
			MethodDeclNode md = transformAccessor(field,var,accessors.get(i));
			GlobalFuncDeclNode gvd = new GlobalFuncDeclNode(md);
			gvd.setPos(md.getLeftPos(), md.getRightPos());
			result[i] = gvd;
			gvd.setParent(globalDecl.getParent());
			accessorFuncsToAdd.add(gvd);
		}
		return result;

	}
	
	private void checkRemoveFieldIfAbstract(FieldDeclNode field) {
		wantRemove = false;
		if(field.getFieldModifiers().contains(ModifierSE.ABSTRACT)){
			fieldsToRemove.add(field);
			wantRemove = true;
			
			//Check that abstract fields do not have an initialization
			VarDeclNode varDecl = field.getDeclaredVariables().get(0); 
			if(!(varDecl instanceof UninitedVarDeclNode)){
				Problem.ofType(ProblemId.ABSTRACT_FIELD_WITH_INIT).at(varDecl.getInitializer())
					.raise();
			}
		}
	}
	
	private VarDeclNode checkSizeAndGetVarDecl(FieldDeclNode field){
		VarDeclListNode vars = field.getDeclaredVariables();
		if(vars.size()>1){
			throw Problem.ofType(ProblemId.ACCESSOR_ON_MULTIPLE_FIELDS).at(field.getAccessors())
				.raiseUnrecoverable();
		}
		VarDeclNode var = vars.get(0);
		return var;
	}



	private AccessorList getAndRemoveAccessors(FieldDeclNode field) {
		AccessorList accessors = field.getAccessors();
		
		//field no longer has accessors
		field.setAccessors(null);
		accessors.setParent(null);
		return accessors;
	}

	private MethodDeclNode transformAccessor(FieldDeclNode field, VarDeclNode var, MethodDeclNode method) {
		MethodHeaderNode header = method.getHeader();
		String name = header.getName();
		header.setName(name + upperFirst(var.getName().getId()));
		method.setMethodType(MethodTypeSE.METHOD);
		
		addStaticIfFieldIsStatic(field,method);
	
		
		if("set".equals(name)){
			transformSetter(field,var,method);
		} else {
			transformGetter(field,var,method);
		}
		
		return method;
	}

	private void addStaticIfFieldIsStatic(FieldDeclNode field,
			MethodDeclNode method) {
		ModifierListNode mods = method.getHeader().getModifiers();

		if(mods.contains(ModifierSE.STATIC)){
			throw Problem.ofType(ProblemId.ACCESSOR_DECLARED_STATIC).at(mods)
					.raiseUnrecoverable();
		}
		
		boolean isStatic = false;
		mods = field.getFieldModifiers();
		if(mods.contains(ModifierSE.STATIC)){
			isStatic = true;
		}
	
		if(!isStatic)
			return;
		
		//Add static if field is static
		method.getHeader().getModifiers().add(ModifierSE.STATIC);
	}

	private void transformSetter(FieldDeclNode field, VarDeclNode var,
			MethodDeclNode method) {
		MethodHeaderNode header = method.getHeader();
		if(header.getReturnType() == null){
			header.setReturnType(VOID);
		}
		if(header.getParameters() == null){
			header.setParameters(new ParameterListNode(new ParameterNode(field.getType(), new IdentifierNode("value"))));
		}
		if(needsBody(method)){
			method.setBody(new BlockStmtNode(new StmtListNode(
					new ExprStmtNode(new AssignmentExprNode(new NameExprNode(var.getName().getId()), AssignOpSE.EQ, new NameExprNode("value")))
				)));
		}
	}

	private void transformGetter(FieldDeclNode field, VarDeclNode var,
			MethodDeclNode method) {
		MethodHeaderNode header = method.getHeader();
		if(header.getReturnType() == null){
			header.setReturnType(field.getType());
		}
		if(header.getParameters() == null){
			header.setParameters(EMPTY_PARAMS);
		}
		if(needsBody(method)){
			method.setBody(new BlockStmtNode(new StmtListNode(new ReturnStmtNode(new NameExprNode(var.getName().getId())))));
		}
	}

	private boolean needsBody(MethodDeclNode method) {
		if(method.getBody() != null)
			return false;
		ModifierListNode mods = method.getHeader().getModifiers();
		if(mods.contains(ModifierSE.ABSTRACT)){
			return false;
		}
		return true;
	}

	private static String upperFirst(String id) {
		char c = id.charAt(0);
		
		if(Character.getType(c) == Character.LOWERCASE_LETTER){
			if(id.length() == 1){
				return String.valueOf(Character.toUpperCase(c));
			}
			return Character.toUpperCase(c) + id.substring(1);
		} else {
			return id;
		}
	}
	
	
	public static String createAccessMethodName(String prefix, String name){
		return prefix + upperFirst(name);
	}

	public boolean wantRemoveAbstractField() {
		return wantRemove;
	}

}
