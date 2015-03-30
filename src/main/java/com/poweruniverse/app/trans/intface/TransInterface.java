package com.poweruniverse.app.trans.intface;

import java.io.File;

import javax.activation.DataHandler;

import com.poweruniverse.nim.base.message.JSONMessageResult;




//数据传输的接口定义
//需要为每个应用系统 提供一个实现类用于接收本系统发出的消息（数据、附件、任务）
public abstract class TransInterface {
	private String serviceIp = null;
	private String servicePort = null;
	
	public TransInterface(String serviceIp, String servicePort) {
		super();
		this.serviceIp = serviceIp;
		this.servicePort = servicePort;
	}

	//返回消息为json格式 的字符串:
	//成功:{success:true,id:XXXXX}  返回本系统的id
	//失败:{success:false,errorMsg:'错误内容'}  
	public abstract JSONMessageResult postRecord(String shiTiLeiDH,Integer targetZJZ,String jsonString);
	
	//返回消息为json格式 的字符串:
	//成功:{success:true,id:XXXXX}  返回本系统的id
	//失败:{success:false,errorMsg:'错误内容'}  
	public abstract JSONMessageResult postFile(Integer targetFJDM,File file);
	
	//返回消息为json格式 的字符串:
	//成功:{success:true,id:XXXXX}  返回本系统的id
	//失败:{success:false,errorMsg:'错误内容'}  
	public abstract JSONMessageResult postTask(String gongNengDH,String caoZuoDH,Integer targetZJZ,String jsonString);
	
	
	//向目标系统请求文件( 保存到本地 准备发送给目标系统)
	//成功:{success:true,id:XXXXX}  返回本系统的id
	//失败:{success:false,errorMsg:'错误内容'}  
	public abstract DataHandler takeFile(Integer targetFJDM) throws Exception;

	protected String getServiceIp() {
		return serviceIp;
	}

	protected String getServicePort() {
		return servicePort;
	}
	
}
