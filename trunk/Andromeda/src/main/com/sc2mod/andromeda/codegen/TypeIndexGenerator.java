package com.sc2mod.andromeda.codegen;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;

/**
 * Generates indices which are necessary for the generated code:
 * 
 * - the class index which is used for assigning class ids and for instanceof calculations
 * @author gex
 *
 */
public class TypeIndexGenerator extends VoidSemanticsVisitorAdapter{

	
	private IndexGenerationVisitor visitor = new IndexGenerationVisitor();
	public IndexInformation createIndexInformation(Environment env){
		visitor.info = new IndexInformation();
		for(IDeclaredType t : env.typeProvider.getDeclaredTypes()){
			switch(t.getCategory()){
			case CLASS:
				if(t.isTopType())
					t.accept(visitor);
				break;
			case INTERFACE:
				
			}
		}
		return visitor.info;
	}
	
private class IndexGenerationVisitor extends VoidSemanticsVisitorAdapter{
		
	private IndexInformation info;

	private int curClassIndex = 1;	
	
	@Override
	public void visit(ClassImpl classImpl) {
		int minInstanceofIndex = curClassIndex;
		
		for(IDeclaredType r: classImpl.getDescendants()){
			((IClass)r).accept(this);
		}
		
		int classIndex = curClassIndex++;
		info.addClassInformation(classImpl, classIndex, minInstanceofIndex);
	}
	
	
}

}
