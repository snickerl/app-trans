package com.poweruniverse.app.trans.impl;

import java.io.File;
import java.util.Calendar;

import javax.activation.DataHandler;

import net.sf.json.JSONObject;
import oim.xny.wsclient.dataTrans.DataTransWS;
import oim.xny.wsclient.utils.OimXnyClientUtils;

import com.poweruniverse.app.trans.intface.TransInterface;
import com.poweruniverse.nim.base.message.JSONMessageResult;



//新能源的数据接收实现类(与新能源应用系统交互)
public class XNYTransImpl extends TransInterface {
	private DataTransWS dataServicePort = null;
	public XNYTransImpl(String serviceIp, String servicePort,String userName,String password) {
		super(serviceIp, servicePort,userName,password);
		try {
			dataServicePort = OimXnyClientUtils.getDataTransWSPort(this.getServiceIp(), this.getServicePort(), userName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public JSONMessageResult postRecord(String shiTiLeiDH,String zhuJianZDDH, Integer targetZJZ,String jsonString) {
		JSONMessageResult ret = null;
		try {
			if(dataServicePort!=null){
				JSONObject json = JSONObject.fromObject(jsonString);
				json.put(zhuJianZDDH, targetZJZ);
				
				String key = ""+Calendar.getInstance().getTimeInMillis();
				String encryptUserCode = OimXnyClientUtils.desEncrypt(this.getUserName(),key);
				String encryptUserPwd = OimXnyClientUtils.desEncrypt(this.getPassword(),key);
				
				String saveRet = dataServicePort.saveByUser(shiTiLeiDH, targetZJZ, jsonString, encryptUserCode, encryptUserPwd, key);
				JSONObject saveRetObj = JSONObject.fromObject(saveRet);
				if(saveRetObj.getBoolean("success")){
					Integer pkValue = saveRetObj.getJSONObject("data").getInt(zhuJianZDDH);
					
					ret = new JSONMessageResult();
					ret.put(zhuJianZDDH, pkValue);
				}else{
					ret = new JSONMessageResult(saveRetObj.getString("errorMsg"));
				}
			}else{
				ret = new JSONMessageResult("数据接收接口不存在");
			}
		} catch (Exception e) {
			ret = new JSONMessageResult(e.getMessage());
			e.printStackTrace();
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
	public DataHandler takeFile(Integer targetFJDM)  throws Exception{
		//
		
		
		return null;
	}


	
}
