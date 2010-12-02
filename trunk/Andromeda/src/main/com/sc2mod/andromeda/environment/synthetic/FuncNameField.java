package com.sc2mod.andromeda.environment.synthetic;

import com.sc2mod.andromeda.codegen.buffers.AdvancedBuffer;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.SyntheticFieldDecl;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.FunctionObject;

public class FuncNameField extends SyntheticFieldDecl{

	public FuncNameField(TypeProvider tp, IScope scope) {
		super(new ModifierListNode(), tp.getSystemType("funcName"), new IdentifierNode("name"), scope);
	}
	
	//TODO This is by far the dirtiest hack ever
	public void genCode(AdvancedBuffer codeGenerator, ExprNode node){
		if(node instanceof FieldAccessExprNode){
			ExprNode op = node.getLeftExpression();
			DataObject val = op.getValue();
			if(val == null || !(val instanceof FunctionObject)){
				throw new InternalError("Only allowed for constant functions at the moment");
			}
			String opName = ((FunctionObject)val).getOperation().getGeneratedName();
			
			codeGenerator.append("\"").append(opName).append("\"");
			
		} else throw new InternalError("!");
	}

}
