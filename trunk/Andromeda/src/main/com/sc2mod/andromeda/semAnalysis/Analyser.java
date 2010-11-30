package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public interface Analyser {

	void analyse(Environment env, SyntaxNode ast);
}
