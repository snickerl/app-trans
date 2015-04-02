package com.poweruniverse.app.trans.impl;

import java.io.File;

import javax.activation.DataHandler;

import net.sf.json.JSONObject;
import nim.data.wsclient.data.DataWebserviceImpl;
import nim.data.wsclient.data.JsonMessageResult;
import nim.data.wsclient.utils.NimDataClientUtils;

import com.poweruniverse.app.trans.intface.TransInterface;
import com.poweruniverse.nim.base.message.JSONMessageResult;



//数据接口实现类(与使用新平台（nim）的应用系统交互)
public class NIMTransImpl extends TransInterface {
	private DataWebserviceImpl dataServicePort = null;
	public NIMTransImpl(String serviceIp, String servicePort,String userName,String password) {
		super(serviceIp, servicePort,userName,password);
		try {
			dataServicePort = NimDataClientUtils.getDataServicePort(serviceIp, servicePort, userName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	//数据交换平台 调用此方法 向目标系统发送数据
	public JSONMessageResult postRecord(String shiTiLeiDH, String zhuJianZDDH, Integer targetZJZ,String jsonString) {
		JSONMessageResult ret = null;
		if(dataServicePort!=null){
			JSONObject json = JSONObject.fromObject(jsonString);
			json.put(zhuJianZDDH, targetZJZ);
			
			String saveRet = dataServicePort.save("xny", shiTiLeiDH, json.toString());
			JSONObject saveRetObj = JSONObject.fromObject(saveRet);
			if(saveRetObj.getBoolean("success")){
				JSONObject row = saveRetObj.getJSONArray("rows").getJSONObject(0);
				Integer pkValue = row.getInt(zhuJianZDDH);
//				
				ret = new JSONMessageResult();
				ret.put(zhuJianZDDH, pkValue);
			}else{
				ret = new JSONMessageResult(saveRetObj.getString("errorMsg"));
			}
		}else{
			ret = new JSONMessageResult("数据接收接口不存在");
		}
		return ret;
	}
	
	@Override
	public JSONMessageResult postFile(Integer targetFJDM,File file) {
		return null;
	}

	@Override
	public JSONMessageResult postTask(String gongNengDH, String caoZuoDH,String zhuJianZDDH,Integer targetZJZ, String jsonString) {
		return null;
	}

	@Override
	public DataHandler takeFile(Integer targetFJDM) throws Exception {
		//
		
		
		return null;
	}


	
}
