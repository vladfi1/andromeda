package com.sc2mod.andromeda.opt.dataflow;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.BreakStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ContinueStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.IfStmtNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.util.VisitorAdapter;

public class CFGBuilder {


	private Visitor visitor;
	private ICFGNodeSet sinks;
	
	public CFGBuilder(int numAnalysis){
		CFGNodeSetRecycleFactory setFactory = new CFGNodeSetRecycleFactory();
		visitor = new Visitor(numAnalysis,setFactory);
	}
	
	public CFGraph build(MethodDeclNode m){
		visitor.curNodeIndex = 1;
		CFGNode source = visitor.factory.create(0, m);
		sinks = visitor.setFactory.create();
		ICFGNodeSet last = m.getBody().accept(visitor,source);
		sinks.addSet(last);
		return new CFGraph(source, sinks, visitor.curNodeIndex);
	}
	
	private class Visitor extends VisitorAdapter<ICFGNodeSet, ICFGNodeSet>{
		
			
		private CFGNodeRecycleFactory factory;
		private CFGNodeSetRecycleFactory setFactory;
		private int curNodeIndex;
		private ICFGNodeSet continues;
		private ICFGNodeSet breaks;
	
		
		
		public Visitor(int numAnalysis, CFGNodeSetRecycleFactory setFactory) {
			factory = new CFGNodeRecycleFactory(numAnalysis);
			this.setFactory = setFactory;
		}
		
		
	
		@Override
		public ICFGNodeSet visit(StmtListNode stmtListNode, ICFGNodeSet last) {
			int size = stmtListNode.size();
			for(int i = 0 ; i < size ; i++){
				StmtNode stmt = stmtListNode.elementAt(i);
				
				//Visit the stmt and get its last set as next last set
				last = stmt.accept(this,last);
			}
			return last;
		}
		
		@Override
		public ICFGNodeSet visit(IfStmtNode ifStmtNode, ICFGNodeSet last) {
			//First the condition
			ICFGNodeSet cond = last = createNodeAndLinkToPredecessors(ifStmtNode.getCondition(), last);
			
			//If statement
			StmtNode stmt = ifStmtNode.getThenStatement();
			last = stmt.accept(this,last);
			
			//else part if there is one
			stmt = ifStmtNode.getElseStatement();
			ICFGNodeSet union = setFactory.create();
			union.addSet(last);
			
			if(stmt != null){
				//If we got an else, then the second branch is from the else
				last = stmt.accept(this,last);
				union.addSet(last);
				return union;
			} else {
				//If we got no else, the second branch is from the condition
				union.addSet(cond);
				return union;
			}
		}
		
		@Override
		public ICFGNodeSet visit(WhileStmtNode whileStmtNode, ICFGNodeSet last) {
			//First the condition
			CFGNode cond = createNodeAndLinkToPredecessors(whileStmtNode.getCondition(), last);
			
			//rescue continue and break statements and create new ones
			ICFGNodeSet continuesBefore = continues;
			ICFGNodeSet breaksBefore = breaks;
			continues = setFactory.create();
			breaks = setFactory.create();
			
			//process body (successor of condition)
			last = whileStmtNode.getThenStatement().accept(this,cond);
			
			//the last statements of the body go back to the condition
			cond.addPredecessorSet(last);
			
			//out set from condition
			ICFGNodeSet outset = setFactory.create(cond);
			
			//link breaks to outset
			outset.addSet(breaks);
			
			//link continues (go back to condition)
			for(CFGNode node : continues){
				node.addSuccessor(cond);
			}
			
			//continues and breaks are no longer needed, release them.
			setFactory.release(continues);
			setFactory.release(breaks);
			
			//restore continue and break statements
			continues = continuesBefore;
			breaks = breaksBefore;
			
			return outset;
		}
		
		@Override
		public ICFGNodeSet visit(DoWhileStmtNode doWhileStmtNode, ICFGNodeSet last) {
			
			//create the condition, but we cannot link it yet (predecessors not yet created)
			CFGNode cond = factory.create(curNodeIndex++, doWhileStmtNode.getCondition());
			
			//rescue continue and break statements and create new ones
			ICFGNodeSet continuesBefore = continues;
			ICFGNodeSet breaksBefore = breaks;
			continues = setFactory.create();
			breaks = setFactory.create();
			
			//create input set for the body
			ICFGNodeSet input = setFactory.create(cond);
			input.addSet(last);
			
			//process body
			last = doWhileStmtNode.getThenStatement().accept(this,input);
			
			//body goes to condition
			cond.addPredecessorSet(last);
			
			//out set from condition
			ICFGNodeSet outset = setFactory.create(cond);
			
			//link breaks to outset
			outset.addSet(breaks);
			
			//link continues (go to condition)
			for(CFGNode node : continues){
				node.addSuccessor(cond);
			}
			
			//continues and breaks are no longer needed, release them.
			setFactory.release(continues);
			setFactory.release(breaks);
			
			//restore continue and break statements
			continues = continuesBefore;
			breaks = breaksBefore;
			
			return outset;
		}
		
		@Override
		public ICFGNodeSet visit(BlockStmtNode blockStmtNode, ICFGNodeSet state) {
			return blockStmtNode.getStatements().accept(this,state);
		}
		
		@Override
		public ICFGNodeSet visit(BreakStmtNode breakStmtNode, ICFGNodeSet state) {
			CFGNode node = createNodeAndLinkToPredecessors(breakStmtNode, state);
			breaks.add(node);
			return null;
		}
		
		@Override
		public ICFGNodeSet visit(ContinueStmtNode continueStmtNode,
				ICFGNodeSet state) {
			CFGNode node = createNodeAndLinkToPredecessors(continueStmtNode, state);
			continues.add(node);
			return null;
		}
		
		@Override
		public ICFGNodeSet visit(ReturnStmtNode returnStmtNode, ICFGNodeSet state) {
			CFGNode returnNode = createNodeAndLinkToPredecessors(returnStmtNode, state);
			sinks.add(returnNode);
			return null;
		}
		

		
		@Override
		public ICFGNodeSet visit(ExprStmtNode exprStmtNode, ICFGNodeSet last) {
			return createNodeAndLinkToPredecessors(exprStmtNode, last);
		}
		
		@Override
		public ICFGNodeSet visit(LocalVarDeclStmtNode localVarDeclStmtNode,	ICFGNodeSet state) {
			return createNodeAndLinkToPredecessors(localVarDeclStmtNode, state);
		}
		
		/**
		 * By default, we just pass on the cfg node.
		 * This method is especially called for different types of statements
		 * that do not special control flow alteration.
		 */
		@Override
		public ICFGNodeSet visitDefault(SyntaxNode s, ICFGNodeSet last) {
			throw new InternalProgramError("Visit not implemented");
		}
		
		private CFGNode createNodeAndLinkToPredecessors(SyntaxNode snode, ICFGNodeSet last){
			CFGNode node = factory.create(curNodeIndex++, snode);
			node.addPredecessorSet(last);
			return node;
		}
	}
	
	
}
