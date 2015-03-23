package com.poweruniverse.app.trans.impl;

import java.io.File;

import javax.activation.DataHandler;

import com.poweruniverse.app.trans.intface.TransInterface;



//华电工程系统的数据接收实现类(与华电工程系统应用系统交互)
public class HDGCTransImpl implements TransInterface {
	@Override
	public String postRecord(String shiTiLeiDH, Integer targetZJZ,String jsonString) {
		return null;
	}
	
	@Override
	public String postFile(Integer targetFJDM,File file) {
		return null;
	}

	@Override
	public String postTask(String gongNengDH, String caoZuoDH,Integer targetZJZ, String jsonString) {
		return null;
	}

	@Override
	public DataHandler takeFile(Integer targetFJDM) {
		
		
		return null;
	}


	
}
