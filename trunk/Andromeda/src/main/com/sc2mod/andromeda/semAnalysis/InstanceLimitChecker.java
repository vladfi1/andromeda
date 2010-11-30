package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetterNode;
import com.sc2mod.andromeda.util.Pair;
import com.sc2mod.andromeda.vm.data.DataObject;

public class InstanceLimitChecker {

	private TransientAnalysisData analysisData;
	private Environment env;
	private IType INT;
	
	public InstanceLimitChecker(TransientAnalysisData analysisData, Environment env) {
		this.analysisData = analysisData;
		this.env = env;
		this.INT = env.typeProvider.BASIC.INT;
	}
	
	public void doChecks(){
		//First check instance limits in classes
		for(IClass c : env.typeProvider.getClasses()){
			checkLimit(c);
		}
		
		//Then do explicit setters
		for(Pair<InstanceLimitSetterNode, IType> tuple : analysisData.getInstanceLimits()){
			checkLimit(tuple._1,tuple._2);
		}
		
		//Check array limits
		for(ArrayTypeNode array : analysisData.getArrayTypes()){
			ExprNode dim = array.getDimension();
			checkArrayLimit(dim);
		}
		
	}
	
	private int checkArrayLimit(ExprNode dimension) {
		
		IType ty = dimension.getInferedType();
		if(ty == null)
			throw Problem.ofType(ProblemId.UNKNOWN_ARRAY_DIMENSION_TYPE).at(dimension)
						.raiseUnrecoverable();
		
		if(ty.getReachableBaseType() != INT)
			throw Problem.ofType(ProblemId.INVALID_ARRAY_DIMENSION_TYPE).at(dimension)
						.details(ty.getUid())
						.raiseUnrecoverable();
		
		DataObject value = dimension.getValue();
		if(value == null)
			throw Problem.ofType(ProblemId.NON_CONSTANT_ARRAY_DIMENSION).at(dimension)
						.raiseUnrecoverable();
		int dim = value.getIntValue();
		if(dim <= 0)
			throw Problem.ofType(ProblemId.NEGATIVE_ARRAY_DIMENSION).at(dimension)
						.details(dim)
						.raiseUnrecoverable();

		return dim;
	}

	/**
	 * Does checks that are the same for direct instance limit in a class decl and an
	 * instance limit setter. Returns the infered instance limit.
	 * @param c the class
	 * @param instanceLimit the instance limit expression
	 * @return
	 */
	private int doCommonChecks(IClass c, ExprNode instanceLimit){
		if(c.isStaticElement())
			throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_INSTANCELIMIT).at(instanceLimit)
				.raiseUnrecoverable();
		if(instanceLimit.getInferedType()!= INT)
			throw Problem.ofType(ProblemId.WRONGLY_TYPED_INSTANCELIMIT).at(instanceLimit).details(instanceLimit.getInferedType().getUid())
				.raiseUnrecoverable();
		DataObject val = instanceLimit.getValue();
		if(val == null)
			throw Problem.ofType(ProblemId.NONCONSTANT_INSTANCELIMIT).at(instanceLimit)
				.raiseUnrecoverable();
		int v = val.getIntValue();
		if(v < 0)
			throw Problem.ofType(ProblemId.NEGATIVE_INSTANCELIMIT).at(instanceLimit).details(v)
				.raiseUnrecoverable();
		if(v == 0)
			throw Problem.ofType(ProblemId.ZERO_INSTANCELIMIT).at(instanceLimit)
				.raiseUnrecoverable();
		return v;
	}

	private void checkLimit(IClass c){
		ClassDeclNode classDeclaration = c.getDefinition();
		ExprNode instanceLimit = classDeclaration.getInstanceLimit();
		if(instanceLimit!=null){
			int limit = doCommonChecks(c, instanceLimit);
			c.setInstanceLimit(limit);
		}
		
	}
	
	private void checkLimit(InstanceLimitSetterNode ils, IType t){
		ExprNode instanceLimitExpr = ils.getInstanceLimit();
		
		IClass c = (IClass)t;
		if(!c.isTopType()) {
			throw Problem.ofType(ProblemId.CHILD_CLASS_HAS_INSTANCELIMIT).at(ils)
				.raiseUnrecoverable();
		}
		int limit = doCommonChecks(c, instanceLimitExpr);
		c.setInstanceLimit(limit);
	}
}
