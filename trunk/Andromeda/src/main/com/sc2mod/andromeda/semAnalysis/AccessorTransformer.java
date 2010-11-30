package com.sc2mod.andromeda.semAnalysis;

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
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;

public class AccessorTransformer {
	
	private SimpleTypeNode VOID = new SimpleTypeNode("void", null);
	private ParameterListNode EMPTY_PARAMS = new ParameterListNode();
	
	public MethodDeclNode[] transformClassField(FieldDeclNode field){
		VarDeclNode var = checkSizeAndGetVarDecl(field);
		
		//calculate parent
		MemberDeclListNode parent = (MemberDeclListNode) field.getParent();
		
		AccessorList accessors = getAndRemoveAccessors(field);
		MethodDeclNode[] result = new MethodDeclNode[accessors.size()];
		for(int i=0,size=accessors.size(); i<size ; i++){
			MethodDeclNode md = transformAccessor(field,var,accessors.get(i));
			//FIXME parent.add considered harmful...
			parent.add(md);
			result[i] = md;
		}
		return result;

	}
	
	public GlobalFuncDeclNode[] transformGlobalField(GlobalVarDeclNode globalDecl) {
		FieldDeclNode field = globalDecl.getFieldDecl();
		VarDeclNode var = checkSizeAndGetVarDecl(field);
		
		//calculate parent
		GlobalStructureListNode parent = (GlobalStructureListNode) globalDecl.getParent();
		
		AccessorList accessors = getAndRemoveAccessors(field);
		GlobalFuncDeclNode[] result = new GlobalFuncDeclNode[accessors.size()];
		for(int i=0,size=accessors.size(); i<size ; i++){
			MethodDeclNode md = transformAccessor(field,var,accessors.get(i));
			GlobalFuncDeclNode gvd = new GlobalFuncDeclNode(md);
			gvd.setPos(md.getLeftPos(), md.getRightPos());
			parent.add(gvd);
			result[i] = gvd;
		}
		return result;

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

}
