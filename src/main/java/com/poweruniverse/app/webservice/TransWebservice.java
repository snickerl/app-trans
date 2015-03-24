package com.poweruniverse.app.webservice;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import com.poweruniverse.nim.base.bean.UserInfo;
import com.poweruniverse.nim.base.webservice.BasePlateformWebservice;

@WebService
public class TransWebservice extends BasePlateformWebservice{
	
	@Resource
	private WebServiceContext wsContext;
	
	public TransWebservice(UserInfo userInfo) {
		super();
		this.userInfo = userInfo;
	}
	
	
	//
	public void sendRecord(String sourceXTDH,String sourceSTLDH,Integer sourceId,String sourceData,String targetXTDH){
		
	}
	
	//
	public void sendFile(){
		
	}
	
	//
	public void sendTask(){
		
	}
	
	//查询 本系统需要得到的数据记录
	public void getRecord(){
		
	}
	
	//查询 本系统需要得到的文件记录
	public void getFile(){
		
	}

	//查询 本系统需要得到的任务记录
	public void getTask(){
		
	}

}
