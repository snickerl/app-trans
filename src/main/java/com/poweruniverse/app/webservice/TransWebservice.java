package com.poweruniverse.app.webservice;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import com.poweruniverse.app.entity.trans.ChuanShuJL;
import com.poweruniverse.app.entity.trans.ChuanShuJLYL;
import com.poweruniverse.app.entity.trans.GongNengCZYS;
import com.poweruniverse.app.entity.trans.ShiTiLeiYS;
import com.poweruniverse.app.entity.trans.YingYongXT;
import com.poweruniverse.app.entity.trans.YingYongXTPTYH;
import com.poweruniverse.app.utils.TransUtils;
import com.poweruniverse.nim.base.bean.UserInfo;
import com.poweruniverse.nim.base.description.Application;
import com.poweruniverse.nim.base.message.JSONMessageResult;
import com.poweruniverse.nim.base.utils.FreemarkerUtils;
import com.poweruniverse.nim.base.webservice.AbstractWebservice;
import com.poweruniverse.nim.data.entity.sys.YongHu;
import com.poweruniverse.nim.data.entity.sys.YongHuZT;
import com.poweruniverse.nim.data.service.utils.HibernateSessionFactory;

@WebService
public class TransWebservice extends AbstractWebservice{
	
	@Resource
	private WebServiceContext wsContext;
	
	public TransWebservice(UserInfo userInfo) {
		super();
		this.userInfo = userInfo;
	}
	
	
	//接受应用系统传出数据的请求 必须是纯粹的单表数据 不会解析其中的子表信息（关联的对象和附件必须提前传输）
	public JSONMessageResult sendRecord(
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="sourceSTLDH") String sourceSTLDH,
			@WebParam(name="sourceId") Integer sourceId,
			@WebParam(name="sourceJSONData") String sourceJSONData,
			@WebParam(name="targetXTDH") String targetXTDH){
		//检查是否提供了主键值
		if(sourceId == null){
			return new JSONMessageResult("源系统数据主键值不允许为空!");
		}
		JSONMessageResult result = null;
		Session sess = null;
		try {
			//当前登录用户的id
			Integer yhdm = getYongHuDM(wsContext,false);
			if( yhdm == null){
				return new JSONMessageResult("请提供有效的登录信息!");
			}
			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出数据请求
			if(!sourceYYXT.getShiFouCCSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送数据");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			//检查是否为应用系统指定数据发送用户 
			YongHu yh = null;
			for(YingYongXTPTYH yyxtyh:sourceYYXT.getShuJuPTYHs()){
				if(yyxtyh.getYongHu().getYongHuZT().getYongHuZTDM() == YongHuZT.ZhengChang && yhdm.equals(yyxtyh.getYongHu().getYongHuDM())){
					yh = yyxtyh.getYongHu();
				}
			}
			if(yh==null){
				return new JSONMessageResult("请提供有效的登录信息!");
			}
			//检查实体类映射关系是否存在
			ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
					.add(Restrictions.eq("sourceYYXT.id", sourceYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("targetYYXT.id", targetYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("sourceSTLDH", sourceSTLDH))
					.uniqueResult();
			if(transSTLMap==null){
				return new JSONMessageResult("系统("+sourceXTDH+")的实体类("+sourceSTLDH+")未与目标系统("+targetXTDH+")建立映射!");
			}
			//将数据字符串转换为json对象
			JSONObject sourceJSONObj = JSONObject.fromObject(sourceJSONData);
			//创建传输记录
			ChuanShuJL transInfo = TransUtils.createRecordTrans(transSTLMap, sourceId,sourceJSONObj,yh,this.getClientIP(wsContext));
			sess.save(transInfo);
			
			result = new JSONMessageResult();
		}catch (Exception e){
			result = new JSONMessageResult(e.getMessage());
			e.printStackTrace();
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		
		return result;
	}
	
	//接受应用系统发出的附件传输请求 依赖于隐含的一个附件获取请求
	public JSONMessageResult sendFile(
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="sourceId") Integer sourceId,
			@WebParam(name="targetXTDH") String targetXTDH){
		
		if(sourceId == null){
			return new JSONMessageResult("附件传输失败：必须提供附件的主键值");
		}
		//
		JSONMessageResult result = null;
		Session sess = null;
		try {
			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出附件
			if(!sourceYYXT.getShiFouCCSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送附件");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			
			//检查是否为应用系统指定数据发送用户 
			Integer yhdm = getYongHuDM(wsContext,false);
			YongHu yh = null;
			if( yhdm == null){
				return new JSONMessageResult("请提供有效的登录信息!");
			}else{
				for(YingYongXTPTYH yyxtyh:sourceYYXT.getShuJuPTYHs()){
					if(yyxtyh.getYongHu().getYongHuZT().getYongHuZTDM() == YongHuZT.ZhengChang && yhdm.equals(yyxtyh.getYongHu().getYongHuDM())){
						yh = yyxtyh.getYongHu();
					}
				}
				if(yh==null){
					return new JSONMessageResult("请提供有效的登录信息!");
				}
			}
			//先创建一个数据交换平台接收附件的请求
			ChuanShuJL fuJianGetTrans = TransUtils.createFuJianTrans(sourceXTDH, sourceId, "_system",yh,this.getClientIP(wsContext));
			sess.saveOrUpdate(fuJianGetTrans);
			//再创建从数据交换平台发送附件的传输请求
			ChuanShuJL fuJianSendTrans = TransUtils.createFuJianTrans("_system", sourceId, targetXTDH,yh,"127.0.0.1");//
			fuJianSendTrans.addToyls(fuJianSendTrans, fuJianGetTrans);
			sess.saveOrUpdate(fuJianGetTrans);
			
			result = new JSONMessageResult();
		}catch (Exception e){
			result = new JSONMessageResult(e.getMessage());
			e.printStackTrace();
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		
		return result;
		
	}
	
	//接受应用系统发出的依赖于数据的任务传输请求 可以同时发送一份数据 作为任务变量
	public JSONMessageResult sendTask(
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="sourceGNDH") String sourceGNDH,
			@WebParam(name="sourceCZDH") String sourceCZDH,
			@WebParam(name="sourceId") Integer sourceId,
			@WebParam(name="sourceJSONData") String sourceJSONData,
			@WebParam(name="targetXTDH") String targetXTDH){
		//检查是否提供了主键值
		if(sourceId == null){
			return new JSONMessageResult("源系统数据主键值不允许为空!");
		}
		
		JSONMessageResult result = null;
		Session sess = null;
		try {
			//将数据字符串转换为json对象
			JSONObject sourceJSONObj = JSONObject.fromObject(sourceJSONData);

			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			if(!sourceYYXT.getShiFouCCSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送任务");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			
			//检查是否为应用系统指定数据发送用户 
			Integer yhdm = getYongHuDM(wsContext,false);
			YongHu yh = null;
			if( yhdm == null){
				return new JSONMessageResult("请提供有效的登录信息!");
			}else{
				for(YingYongXTPTYH yyxtyh:sourceYYXT.getShuJuPTYHs()){
					if(yyxtyh.getYongHu().getYongHuZT().getYongHuZTDM() == YongHuZT.ZhengChang && yhdm.equals(yyxtyh.getYongHu().getYongHuDM())){
						yh = yyxtyh.getYongHu();
					}
				}
				if(yh == null){
					return new JSONMessageResult("请提供有效的登录信息!");
				}
			}
			//检查功能操作映射关系是否存在
			GongNengCZYS transGNCZMap = (GongNengCZYS)sess.createCriteria(GongNengCZYS.class)
					.createAlias("gongNengYS", "gnczys_gnys")
					.add(Restrictions.eq("gnczys_gnys.sourceYYXT.id", sourceYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("gnczys_gnys.targetYYXT.id", targetYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("gnczys_gnys.sourceGNDH", sourceGNDH))
					.add(Restrictions.eq("sourceCZDH", sourceCZDH))
					.uniqueResult();
			if(transGNCZMap==null){
				return new JSONMessageResult("系统("+sourceXTDH+")的功能("+sourceGNDH+")，操作("+sourceCZDH+")未与目标系统("+targetXTDH+")建立映射!");
			}
			
			//创建传输记录
			ChuanShuJL transInfo = TransUtils.createTaskTrans(transGNCZMap, sourceId,sourceJSONObj,yh,this.getClientIP(wsContext));
			sess.save(transInfo);
			
			result = new JSONMessageResult();
		}catch (Exception e){
			result = new JSONMessageResult(e.getMessage());
			e.printStackTrace();
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		
		return result;
	}
	
	//取得发送给本系统的数据记录 
	public JSONMessageResult getRecord(
			@WebParam(name="transId") Integer transId,
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="targetXTDH") String targetXTDH,
			@WebParam(name="targetSTLDH") String targetSTLDH,
			@WebParam(name="targetZJZ") Integer targetZJZ){

		//检查是否提供了主键值
		if(transId == null){
			return new JSONMessageResult("数据传输记录id不允许为空!");
		}
		//检查是否提供了主键值
		if(targetZJZ == null){
			return new JSONMessageResult("目标系统数据主键值不允许为空!");
		}
		
		JSONMessageResult result = null;
		Session sess = null;
		try {
			sess = HibernateSessionFactory.getSession();
			//
			ChuanShuJL transInfo = (ChuanShuJL)sess.load(ChuanShuJL.class,transId);
			if(transInfo==null){
				return new JSONMessageResult("数据传输记录("+transId+")不存在!");
			}
			if(!sourceXTDH.equals(transInfo.getSourceXTDH())){
				return new JSONMessageResult("数据传输记录("+transId+")的源系统代号("+transInfo.getSourceXTDH()+")与请求的参数("+sourceXTDH+")不一致!");
			}
			if(!targetXTDH.equals(transInfo.getTargetXTDH())){
				return new JSONMessageResult("数据传输记录("+transId+")的目标系统代号("+transInfo.getTargetXTDH()+")与请求的参数("+targetXTDH+")不一致!");
			}
			if(!targetSTLDH.equals(transInfo.getTargetSTLDH())){
				return new JSONMessageResult("数据传输记录("+transId+")的实体类代号("+transInfo.getTargetSTLDH()+")与请求的参数("+targetSTLDH+")不一致!");
			}
			if(transInfo.getShiFouCSWC()){
				return new JSONMessageResult("数据传输记录("+transId+")已传输完成，不允许重新获取!");
			}
			//依赖数据是否都传输完成
			for(ChuanShuJLYL yl:transInfo.getYls()){
				if(!yl.getYiLaiCSJL().getShiFouCSWC()){
					return new JSONMessageResult("数据传输记录("+transId+")的依赖信息尚未传输完成!");
				}
			}

			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出数据
			if(!sourceYYXT.getShiFouCCSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送数据");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			//检查目标系统是否为自动接收附件
			if(targetYYXT.getShiFouJSSJ()){
				return new JSONMessageResult("目标系统("+sourceXTDH+")配置为自动接收数据");
			}
			//检查实体类映射关系是否存在
			ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
					.add(Restrictions.eq("sourceYYXT.id", sourceYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("targetYYXT.id", sourceYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("targetSTLDH", targetSTLDH))
					.uniqueResult();
			if(transSTLMap==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")未与目标系统("+targetXTDH+")的实体类("+targetSTLDH+")建立映射!");
			}
			//继续检查是否曾经传输过 主键值是否相符
			Integer existsTargetId = transInfo.getTargetZJZ();
			if(existsTargetId != null && !existsTargetId.equals(targetZJZ)){
				return new JSONMessageResult("数据传输记录的目标系统主键值("+existsTargetId+")已存在且与本次提供的主键值("+targetZJZ+")不符!");
			}
			//查找本系统是否曾经向目标系统传输过此条数据(查找已完成的记录 获取目标代码) （可能有多次传输 多条记录 取任意一条已完成的即可）
			ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.eq("sourceXTDH", transInfo.getSourceXTDH()))
					.add(Restrictions.eq("sourceSTLDH",transInfo.getSourceSTLDH()))
					.add(Restrictions.eq("sourceZJZ", transInfo.getSourceZJZ()))
					.add(Restrictions.eq("targetXTDH", transInfo.getTargetXTDH()))
					.add(Restrictions.eq("shiFouCSWC", true))
					.add(Restrictions.isNotNull("targetZJZ"))
					.add(Restrictions.ne("chuanShuJLDM", transId))
					.setMaxResults(1)
					.uniqueResult();
			//已传输完成的数据 一定是有主键值的
			if(hisTrans!=null && !targetZJZ.equals(hisTrans.getTargetZJZ()) ){
				return new JSONMessageResult("存在传输完成的数据传输记录，且与本次提供的主键值("+targetZJZ+")不符!");
			}
			//替换数据中 关联对象的主键值为依赖的数据传输中 得到的目标系统主键值
			Map<String, Object> root = new HashMap<String, Object>();
			for(ChuanShuJLYL depend:transInfo.getYls()){
				ChuanShuJL yl = depend.getChuanShuJL();
				root.put(yl.getSourceSTLDH()+"_"+yl.getSourceZJZ(), yl.getTargetZJZ());
			}
			String jsonString =  transInfo.getJsonData();
			if(jsonString.indexOf("${")>=0){
				jsonString = FreemarkerUtils.processTemplate(jsonString, root);
			}
			
			//记录传输成功状态及目标系统主键值
			transInfo.setTargetZJZ(targetZJZ);
			transInfo.setShiFouCSWC(true);
			transInfo.setCuoWuXXX(null);
			sess.update(transInfo);
			//同时更新所有待传输的相同记录（不要在这里处理 有可能处理完又新增了待传输的记录）
//			@SuppressWarnings("unchecked")
//			List<ChuanShuJL> todoTransList = (List<ChuanShuJL>)sess.createCriteria(ChuanShuJL.class)
//					.add(Restrictions.eq("sourceXTDH", transInfo.getSourceXTDH()))
//					.add(Restrictions.eq("sourceSTLDH",transInfo.getSourceSTLDH()))
//					.add(Restrictions.eq("sourceZJZ", transInfo.getSourceZJZ()))
//					.add(Restrictions.eq("targetXTDH", transInfo.getTargetXTDH()))
//					.add(Restrictions.eq("shiFouCSWC", false))
//					.add(Restrictions.isNull("targetZJZ"))
//					.add(Restrictions.ne("chuanShuJLDM", transInfo.getChuanShuJLDM()))
//					.list();
//			for(ChuanShuJL todoTrans:todoTransList){
//				todoTrans.setTargetZJZ(targetZJZ);
//				sess.update(transInfo);
//			}
			//创建返回信息
			result = new JSONMessageResult();
			result.put("targetSTLDH", transSTLMap.getTargetSTLDH());
			result.put("targetZJZ", targetZJZ);
			result.put("data", jsonString);
		
		}catch (Exception e){
			result = new JSONMessageResult(e.getMessage());
			e.printStackTrace();
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		return result;
	}
	
	
	
	//查询 本系统需要得到的文件记录
	@WebResult
	@XmlMimeType("*/*")
	public DataHandler getFile(
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="targetXTDH") String targetXTDH,
			@WebParam(name="transId") Integer transId,
			@WebParam(name="targetZJZ") Integer targetZJZ) throws Exception{
		
		//检查是否提供了主键值
		if(transId == null){
			throw new Exception("附件传输记录id不允许为空!");
		}
		//检查是否提供了主键值
		if(targetZJZ == null){
			throw new Exception("目标系统附件主键值不允许为空!");
		}
		DataHandler fileHandler = null;
		Session sess = null;
		try {
			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				throw new Exception("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出数据请求
			if(!sourceYYXT.getShiFouCCFJ()){
				throw new Exception("源系统("+sourceXTDH+")配置为不发送附件");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				throw new Exception("目标系统("+sourceXTDH+")不存在!");
			}
			//检查目标系统是否为自动接收附件
			if(targetYYXT.getShiFouJSFJ()){
				throw new Exception("目标系统("+sourceXTDH+")配置为自动接收附件");
			}
			//取得记录
			ChuanShuJL transInfo = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.eq("chuanShuJLDM", transId))
					.add(Restrictions.eq("sourceXTDH", sourceXTDH))
					.add(Restrictions.eq("xinXiLBMC", "file"))
					.add(Restrictions.eq("targetXTDH", targetXTDH))
					.uniqueResult();
			if(transInfo==null){
				throw new Exception("文件传输记录("+transId+")不存在!");
			}else if(transInfo.getShiFouCSWC()){
				throw new Exception("文件传输记录("+transId+")已传输完成，不允许重新获取!");
			}else{
				//依赖数据是否都传输完成
				for(ChuanShuJLYL yl:transInfo.getYls()){
					if(!yl.getYiLaiCSJL().getShiFouCSWC()){
						throw new Exception("文件传输记录("+transId+")的依赖信息尚未传输完成!");
					}
				}
			}
			//继续检查是否曾经传输过 主键值是否相符
			Integer existsTargetId = transInfo.getTargetZJZ();
			if(existsTargetId != null && !existsTargetId.equals(targetZJZ)){
				throw new Exception("文件传输记录的目标系统主键值("+existsTargetId+")已存在且与本次提供的主键值("+targetZJZ+")不符!");
			}else{
				//查找本系统是否曾经向目标系统传输过此条附件(查找已完成的记录 获取目标代码) （可能有多次传输 多条记录 取任意一条已完成的即可）
				ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
						.add(Restrictions.eq("sourceXTDH", transInfo.getSourceXTDH()))
						.add(Restrictions.eq("xinXiLBMC", "file"))
						.add(Restrictions.eq("sourceZJZ", transInfo.getSourceZJZ()))
						.add(Restrictions.eq("targetXTDH", transInfo.getTargetXTDH()))
						.add(Restrictions.eq("shiFouCSWC", true))
						.add(Restrictions.isNotNull("targetZJZ"))
						.setMaxResults(1)
						.uniqueResult();
				//已传输完成的数据 一定是有主键值的
				if(hisTrans!=null && !targetZJZ.equals(hisTrans.getTargetZJZ()) ){
					throw new Exception("存在传输完成的文件传输记录，且与本次提供的主键值("+targetZJZ+")不符!");
				}
			}
			//取得附件
			File localFile = new File(Application.getInstance().getContextPath()+"WEB-INF/file/"+sourceXTDH+"/"+transInfo.getSourceZJZ());
			if(!localFile.exists()){
				throw new Exception("文件不存在："+localFile.getPath());
			}
			fileHandler = new DataHandler(
				new FileDataSource(localFile){
					public String getContentType() {
						return "application/octet-stream";
					}
				}
			);
			
			//记录传输成功标志
			transInfo.setTargetZJZ(targetZJZ);
			transInfo.setShiFouCSWC(true);
			transInfo.setCuoWuXXX(null);
			sess.update(transInfo);
			//同时更新所有待传输的相同记录（不要在这里处理 有可能处理完又新增了待传输的记录）
//			@SuppressWarnings("unchecked")
//			List<ChuanShuJL> todoTransList = (List<ChuanShuJL>)sess.createCriteria(ChuanShuJL.class)
//					.add(Restrictions.eq("sourceXTDH", transInfo.getSourceXTDH()))
//					.add(Restrictions.eq("xinXiLBMC", "file"))
//					.add(Restrictions.eq("sourceZJZ", transInfo.getSourceZJZ()))
//					.add(Restrictions.eq("targetXTDH", transInfo.getTargetXTDH()))
//					.add(Restrictions.eq("shiFouCSWC", false))
//					.add(Restrictions.isNull("targetZJZ"))
//					.add(Restrictions.ne("chuanShuJLDM", transInfo.getChuanShuJLDM()))
//					.list();
//			for(ChuanShuJL todoTrans:todoTransList){
//				todoTrans.setTargetZJZ(targetZJZ);
//				sess.update(transInfo);
//			}
		}catch (Exception e){
			e.printStackTrace();
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
			throw e;
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		return fileHandler;
	}

	//取得发送目标为本系统的任务记录
	public JSONMessageResult getTask(
			@WebParam(name="transId") Integer transId,
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="targetXTDH") String targetXTDH,
			@WebParam(name="targetGNDH") String targetGNDH,
			@WebParam(name="targetCZDH") String targetCZDH,
			@WebParam(name="targetZJZ") Integer targetZJZ){

		//检查是否提供了主键值
		if(transId == null){
			return new JSONMessageResult("数据传输记录id不允许为空!");
		}
		//检查是否提供了主键值
		if(targetZJZ == null){
			return new JSONMessageResult("目标系统数据主键值不允许为空!");
		}
		
		JSONMessageResult result = null;
		Session sess = null;
		try {
			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出任务
			if(!sourceYYXT.getShiFouCCRW()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送任务");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			//检查目标系统是否为自动接收任务
			if(targetYYXT.getShiFouJSRW()){
				return new JSONMessageResult("目标系统("+sourceXTDH+")配置为自动接收任务");
			}
			//检查功能操作映射关系是否存在
			GongNengCZYS transGNCZMap = (GongNengCZYS)sess.createCriteria(GongNengCZYS.class)
					.createAlias("gongNengYS", "gnczys_gnys")
					.add(Restrictions.eq("gnczys_gnys.sourceYYXT.id", sourceYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("gnczys_gnys.targetYYXT.id", targetYYXT.getYingYongXTDM()))
					.add(Restrictions.eq("gnczys_gnys.targetGNDH", targetGNDH))
					.add(Restrictions.eq("targetCZDH", targetCZDH))
					.uniqueResult();
			if(transGNCZMap==null){
				return new JSONMessageResult("系统("+sourceXTDH+")未与目标系统("+targetXTDH+")的功能("+targetGNDH+")，操作("+targetCZDH+")建立映射!");
			}
			//取得记录
			ChuanShuJL transInfo = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.eq("chuanShuJLDM", transId))
					.add(Restrictions.eq("sourceXTDH", sourceXTDH))
					.add(Restrictions.eq("targetXTDH", targetXTDH))
					.add(Restrictions.eq("targetGNDH", targetGNDH))
					.add(Restrictions.eq("targetCZDH", targetCZDH))
					.uniqueResult();
			if(transInfo==null){
				return new JSONMessageResult("任务传输记录("+transId+")不存在!");
			}else if(transInfo.getShiFouCSWC()){
				return new JSONMessageResult("任务传输记录("+transId+")已传输完成，不允许重新获取!");
			}else{
				//依赖数据是否都传输完成
				for(ChuanShuJLYL yl:transInfo.getYls()){
					if(!yl.getYiLaiCSJL().getShiFouCSWC()){
						return new JSONMessageResult("任务传输记录("+transId+")的依赖信息尚未传输完成!");
					}
				}
			}
			//一定存在一个检查是否曾经传输过 主键值是否相符
			Integer existsTargetZJZ = transInfo.getTargetZJZ();
			if(existsTargetZJZ != null && !existsTargetZJZ.equals(targetZJZ)){
				return new JSONMessageResult("数据传输记录的目标系统主键值("+existsTargetZJZ+")已存在且与本次提供的主键值("+targetZJZ+")不符!");
			}else{
				//查找本系统是否曾经向目标系统传输过此条数据(查找已完成的记录 获取目标代码) （可能有多次传输 多条记录 取任意一条已完成的即可）
				ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
						.add(Restrictions.eq("sourceXTDH", transInfo.getSourceXTDH()))
						.add(Restrictions.eq("sourceSTLDH",transInfo.getSourceSTLDH()))
						.add(Restrictions.eq("sourceZJZ", transInfo.getSourceZJZ()))
						.add(Restrictions.eq("targetXTDH", transInfo.getTargetXTDH()))
						.add(Restrictions.eq("shiFouCSWC", true))
						.add(Restrictions.isNotNull("targetZJZ"))
						.setMaxResults(1)
						.uniqueResult();
				//已传输完成的数据 一定是有主键值的
				if(hisTrans!=null && !targetZJZ.equals(hisTrans.getTargetZJZ()) ){
					return new JSONMessageResult("存在传输完成的数据传输记录，且与本次提供的主键值("+targetZJZ+")不符!");
				}
			}
			//替换数据中 关联对象的主键值为依赖的数据传输中 得到的目标系统主键值
			Map<String, Object> root = new HashMap<String, Object>();
			for(ChuanShuJLYL depend:transInfo.getYls()){
				ChuanShuJL yl = depend.getChuanShuJL();
				root.put(yl.getSourceSTLDH()+"_"+yl.getSourceZJZ(), yl.getTargetZJZ());
			}
			String jsonString =  transInfo.getJsonData();
			if(jsonString.indexOf("${")>=0){
				jsonString = FreemarkerUtils.processTemplate(jsonString, root);
			}
			
			//记录传输成功状态信息
			transInfo.setTargetZJZ(targetZJZ);
			transInfo.setShiFouCSWC(true);
			transInfo.setCuoWuXXX(null);
			sess.update(transInfo);
			//同时更新所有待传输的相同记录（不要在这里处理 有可能处理完又新增了待传输的记录）
//			@SuppressWarnings("unchecked")
//			List<ChuanShuJL> todoTransList = (List<ChuanShuJL>)sess.createCriteria(ChuanShuJL.class)
//					.add(Restrictions.eq("sourceXTDH", transInfo.getSourceXTDH()))
//					.add(Restrictions.eq("sourceSTLDH",transInfo.getSourceSTLDH()))
//					.add(Restrictions.eq("sourceZJZ", transInfo.getSourceZJZ()))
//					.add(Restrictions.eq("targetXTDH", transInfo.getTargetXTDH()))
//					.add(Restrictions.eq("shiFouCSWC", false))
//					.add(Restrictions.isNull("targetZJZ"))
//					.add(Restrictions.ne("chuanShuJLDM", transInfo.getChuanShuJLDM()))
//					.list();
//			for(ChuanShuJL todoTrans:todoTransList){
//				todoTrans.setTargetZJZ(targetZJZ);
//				sess.update(transInfo);
//			}
			//返回信息
			result = new JSONMessageResult();
			result.put("targetGNDH", targetGNDH);
			result.put("targetCZDH", targetCZDH);
			result.put("targetSTLDH", transGNCZMap.getGongNengYS().getShiTiLeiYS().getTargetSTLDH());
			result.put("targetZJZ", targetZJZ);
			result.put("data", jsonString);
		
		}catch (Exception e){
			result = new JSONMessageResult(e.getMessage());
			e.printStackTrace();
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		return result;
	}
	
	//查询是否有发送给本系统的数据（一次最多返回20条）
	public JSONMessageResult queryRecords(
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="targetXTDH") String targetXTDH){
		JSONMessageResult result = null;
		Session sess = null;
		try {
			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出数据请求
			if(!sourceYYXT.getShiFouCCSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送数据");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			//检查目标系统是否为自动接收数据
			if(sourceYYXT.getShiFouJSSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为自动接收数据");
			}
			//所有未完成的下级记录(子查询)
			DetachedCriteria subselect = DetachedCriteria.forClass(ChuanShuJLYL.class);
				subselect.createAlias("yiLaiCSJL", "mx_yl");
				subselect.add(Restrictions.eq("mx_yl.shiFouCSWC", false));
				subselect.setProjection(Projections.property("xinXiCSJL.id"));
			//取得数据传输记录
			Criteria dataTransCriteria = sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.eq("sourceXTDH", sourceXTDH))
					.add(Restrictions.eq("targetXTDH", targetXTDH))
					.add(Restrictions.eq("xinXiLBMC", "data"))
					.add(Restrictions.eq("shiFouCSWC", false))//未传输完成 
					.add(Property.forName("chuanShuJLDM").notIn(subselect))//不存在未完成的依赖关系
					.addOrder(Order.asc("chuanShuJLDM"));
			@SuppressWarnings("unchecked")
			List<ChuanShuJL> dataChuanShuJLs = (List<ChuanShuJL>)dataTransCriteria.setMaxResults(20).list();
			JSONArray rows = new JSONArray();
			for(ChuanShuJL csjl:dataChuanShuJLs){
				JSONObject row = new JSONObject();
				
				row.put("transId", csjl.getChuanShuJLDM());//传输记录的主键值
				row.put("targetSTLDH", csjl.getTargetSTLDH());//源系统的实体类代号
				//检查是否有曾经传输成功的记录
				if(csjl.getTargetZJZ()==null){
					ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", csjl.getSourceXTDH()))
							.add(Restrictions.eq("sourceSTLDH",csjl.getSourceSTLDH()))
							.add(Restrictions.eq("sourceZJZ", csjl.getSourceZJZ()))
							.add(Restrictions.eq("targetXTDH", csjl.getTargetXTDH()))
							.add(Restrictions.eq("shiFouCSWC", true))
							.add(Restrictions.isNotNull("targetZJZ"))
							.add(Restrictions.ne("chuanShuJLDM", csjl.getChuanShuJLDM()))
							.setMaxResults(1)
							.uniqueResult();
					if(hisTrans!=null){
						csjl.setTargetZJZ(hisTrans.getTargetZJZ());
						sess.update(csjl);
					}
				}
				row.put("targetZJZ", csjl.getTargetZJZ());
				
				rows.add(row);
			}
			
			result = new JSONMessageResult();
			result.put("data", rows);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONMessageResult(e.getMessage());
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		
		return result;
	}
	
	//查询是否有发送给本系统的数据（一次最多返回20条）
	public JSONMessageResult queryTasks(
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="targetXTDH") String targetXTDH){
		JSONMessageResult result = null;
		Session sess = null;
		try {
			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出数据请求
			if(!sourceYYXT.getShiFouCCRW()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送任务");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			//检查目标系统是否为自动接收数据
			if(sourceYYXT.getShiFouJSRW()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为自动接收任务");
			}
			//所有未完成的下级记录(子查询)
			DetachedCriteria subselect = DetachedCriteria.forClass(ChuanShuJLYL.class);
				subselect.createAlias("yiLaiCSJL", "mx_yl");
				subselect.add(Restrictions.eq("mx_yl.shiFouCSWC", false));
				subselect.setProjection(Projections.property("xinXiCSJL.id"));
			//取得数据传输记录
			Criteria dataTransCriteria = sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.eq("sourceXTDH", sourceXTDH))
					.add(Restrictions.eq("targetXTDH", targetXTDH))
					.add(Restrictions.eq("xinXiLBMC", "task"))
					.add(Restrictions.eq("shiFouCSWC", false))//未传输完成 
					.add(Property.forName("chuanShuJLDM").notIn(subselect))//不存在未完成的依赖关系
					.addOrder(Order.asc("chuanShuJLDM"));
			@SuppressWarnings("unchecked")
			List<ChuanShuJL> dataChuanShuJLs = (List<ChuanShuJL>)dataTransCriteria.setMaxResults(20).list();
			JSONArray rows = new JSONArray();
			for(ChuanShuJL csjl:dataChuanShuJLs){
				JSONObject row = new JSONObject();
				
				row.put("transId", csjl.getChuanShuJLDM());//传输记录的主键值
				row.put("targetGNDH", csjl.getTargetGNDH());//目标系统的功能代号
				row.put("targetCZDH", csjl.getTargetCZDH());//目标系统的功能代号
				
				//检查是否有曾经传输成功的记录
				if(csjl.getTargetZJZ()==null){
					ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", csjl.getSourceXTDH()))
							.add(Restrictions.eq("sourceSTLDH",csjl.getSourceSTLDH()))
							.add(Restrictions.eq("sourceZJZ", csjl.getSourceZJZ()))
							.add(Restrictions.eq("targetXTDH", csjl.getTargetXTDH()))
							.add(Restrictions.eq("shiFouCSWC", true))
							.add(Restrictions.isNotNull("targetZJZ"))
							.add(Restrictions.ne("chuanShuJLDM", csjl.getChuanShuJLDM()))
							.setMaxResults(1)
							.uniqueResult();
					if(hisTrans!=null){
						csjl.setTargetZJZ(hisTrans.getTargetZJZ());
						sess.update(csjl);
					}
				}
				row.put("targetZJZ", csjl.getTargetZJZ());
				rows.add(row);
			}
			
			result = new JSONMessageResult();
			result.put("data", rows);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONMessageResult(e.getMessage());
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		
		return result;
	}
		
	//查询是否有发送给本系统的附件（一次最多返回20条）
	public JSONMessageResult queryFiles(
			@WebParam(name="sourceXTDH") String sourceXTDH,
			@WebParam(name="targetXTDH") String targetXTDH){
		JSONMessageResult result = null;
		Session sess = null;
		try {
			sess = HibernateSessionFactory.getSession();
			//检查应用系统定义
			YingYongXT sourceYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", sourceXTDH))
					.uniqueResult();
			if(sourceYYXT==null){
				return new JSONMessageResult("源系统("+sourceXTDH+")不存在!");
			}
			//检查源系统是否可以发出数据请求
			if(!sourceYYXT.getShiFouCCSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为不发送数据");
			}
			YingYongXT targetYYXT = (YingYongXT)sess.createCriteria(YingYongXT.class)
					.add(Restrictions.eq("yingYongXTDH", targetXTDH))
					.uniqueResult();
			if(targetYYXT==null){
				return new JSONMessageResult("目标系统("+sourceXTDH+")不存在!");
			}
			//检查目标系统是否为自动接收数据
			if(sourceYYXT.getShiFouJSSJ()){
				return new JSONMessageResult("源系统("+sourceXTDH+")配置为自动接收数据");
			}
			//所有未完成的下级记录(子查询)
			DetachedCriteria subselect = DetachedCriteria.forClass(ChuanShuJLYL.class);
				subselect.createAlias("yiLaiCSJL", "mx_yl");
				subselect.add(Restrictions.eq("mx_yl.shiFouCSWC", false));
				subselect.setProjection(Projections.property("xinXiCSJL.id"));
			//取得数据传输记录
			Criteria dataTransCriteria = sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.eq("sourceXTDH", sourceXTDH))
					.add(Restrictions.eq("targetXTDH", targetXTDH))
					.add(Restrictions.eq("xinXiLBMC", "file"))
					.add(Restrictions.eq("shiFouCSWC", false))//未传输完成 
					.add(Property.forName("chuanShuJLDM").notIn(subselect))//不存在未完成的依赖关系
					.addOrder(Order.asc("chuanShuJLDM"));
			@SuppressWarnings("unchecked")
			List<ChuanShuJL> dataChuanShuJLs = (List<ChuanShuJL>)dataTransCriteria.setMaxResults(20).list();
			JSONArray rows = new JSONArray();
			for(ChuanShuJL csjl:dataChuanShuJLs){
				JSONObject row = new JSONObject();
				
				row.put("transId", csjl.getChuanShuJLDM());//传输记录的主键值
				
				//检查是否有曾经传输成功的记录
				if(csjl.getTargetZJZ()==null){
					ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", csjl.getSourceXTDH()))
							.add(Restrictions.eq("xinXiLBMC", "file"))
							.add(Restrictions.eq("sourceZJZ", csjl.getSourceZJZ()))
							.add(Restrictions.eq("targetXTDH", csjl.getTargetXTDH()))
							.add(Restrictions.eq("shiFouCSWC", true))
							.add(Restrictions.isNotNull("targetZJZ"))
							.add(Restrictions.ne("chuanShuJLDM", csjl.getChuanShuJLDM()))
							.setMaxResults(1)
							.uniqueResult();
					if(hisTrans!=null){
						csjl.setTargetZJZ(hisTrans.getTargetZJZ());
						sess.update(csjl);
					}
				}
				row.put("targetZJZ", csjl.getTargetZJZ());//目标系统主键值

				rows.add(row);
			}
			
			result = new JSONMessageResult();
			result.put("data", rows);
		}catch (Exception e){
			e.printStackTrace();
			result = new JSONMessageResult(e.getMessage());
			if (sess != null) {
				HibernateSessionFactory.closeSession(false);
			}
		}finally{
			if (sess != null) {
				HibernateSessionFactory.closeSession(true);
			}
		}
		
		return result;
	}

}
