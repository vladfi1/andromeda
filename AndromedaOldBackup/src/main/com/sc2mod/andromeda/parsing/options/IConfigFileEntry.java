package com.sc2mod.andromeda.parsing.options;

public interface IConfigFileEntry extends IParam {

	public String getSection(String runConfig);

	public String getKey();

}
