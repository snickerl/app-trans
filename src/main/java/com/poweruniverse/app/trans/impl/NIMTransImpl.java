package com.poweruniverse.app.trans.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.DataHandler;

import nim.data.wsclient.data.DataWebserviceImpl;
import nim.data.wsclient.data.DataWebserviceImplService;
import nim.data.wsclient.data.JsonDataResult;

import com.poweruniverse.app.trans.intface.TransInterface;
import com.poweruniverse.nim.base.message.JSONMessageResult;



//数据接口实现类(与使用新平台（nim）的应用系统交互)
public class NIMTransImpl extends TransInterface {
	private DataWebserviceImpl dataServicePort = null;
	public NIMTransImpl(String serviceIp, String servicePort) {
		super(serviceIp, servicePort);
		
		try {
			DataWebserviceImplService dataService = new DataWebserviceImplService(new URL("http://"+this.getServiceIp()+":"+this.getServicePort()+"/ws/nim-data/data?wsdl"));
			dataServicePort = dataService.getDataWebserviceImplPort();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	//数据交换平台 调用此方法 向目标系统发送数据
	public JSONMessageResult postRecord(String shiTiLeiDH, Integer targetZJZ,String jsonString) {
		if(dataServicePort!=null){
			JsonDataResult saveRet = dataServicePort.save("", "", "", jsonString, false);
		}else{
			
		}
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
	public DataHandler takeFile(Integer targetFJDM) throws Exception {
		//
		
		
		return null;
	}


	
}
