package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;

import mopaqlib.MoPaQ;

import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.IndexInformation;
import com.sc2mod.andromeda.codegen.NameGenerationVisitor;
import com.sc2mod.andromeda.environment.access.Invocation;

public class TransientCompilationData {

	private NameGenerationVisitor nameGenerator;
	private OutputMemoryStats outputStats;
	private CodeGenVisitor codeGenerator;
	private MoPaQ openedMap;
	private String outputName;
	private ArrayList<Invocation> virtualInvocations = new ArrayList<Invocation>();
	private int maxVCTSize;
	private IndexInformation indexInformation;
	
	public IndexInformation getIndexInformation() {
		return indexInformation;
	}

	public void setIndexInformation(IndexInformation indexInformation) {
		this.indexInformation = indexInformation;
	}

	public int getMaxVCTSize() {
		return maxVCTSize;
	}
	
	public void registerMaxVCTSize(int size) {
		if(maxVCTSize >= size) return;
		maxVCTSize = size;
	}

	public ArrayList<Invocation> getVirtualInvocations() {
		return virtualInvocations;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public CodeGenVisitor getCodeGenerator() {
		return codeGenerator;
	}

	public void setCodeGenerator(CodeGenVisitor codeGenerator) {
		this.codeGenerator = codeGenerator;
	}

	public OutputMemoryStats getOutputStats() {
		return outputStats;
	}

	public void setOutputStats(OutputMemoryStats outputStats) {
		this.outputStats = outputStats;
	}

	public NameGenerationVisitor getNameGenerator() {
		return nameGenerator;
	}

	public void setNameGenerator(NameGenerationVisitor nameGenerator) {
		this.nameGenerator = nameGenerator;
	}

	public void setOpenedMap(MoPaQ map) {
		this.openedMap = map;
	}
	
	
	public MoPaQ getOpenedMap() {
		return openedMap;
	}
}
