package com.poweruniverse.app.trans.impl;

import java.io.File;

import javax.activation.DataHandler;

import com.poweruniverse.app.trans.intface.TransInterface;
import com.poweruniverse.nim.base.message.JSONMessageResult;



//华电工程系统的数据接收实现类(与华电工程系统应用系统交互)
public class HDGCTransImpl extends TransInterface {
	
	public HDGCTransImpl(String serviceIp, String servicePort) {
		super(serviceIp, servicePort);
	}
	
	@Override
	public JSONMessageResult postRecord(String shiTiLeiDH, Integer targetZJZ,String jsonString) {
		return null;
	}
	
	@Override
	public JSONMessageResult postFile(Integer targetFJDM,File file) {
		return null;
	}

	@Override
	public JSONMessageResult postTask(String gongNengDH, String caoZuoDH,Integer targetZJZ, String jsonString) {
		return null;
	}

	@Override
	public DataHandler takeFile(Integer targetFJDM)  throws Exception{
		
		
		return null;
	}


	
}
