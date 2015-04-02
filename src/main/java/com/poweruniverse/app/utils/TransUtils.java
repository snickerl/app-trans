package com.poweruniverse.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.poweruniverse.app.entity.trans.ChuanShuJL;
import com.poweruniverse.app.entity.trans.ChuanShuJLYL;
import com.poweruniverse.app.entity.trans.GongNengCZYS;
import com.poweruniverse.app.entity.trans.ShiTiLeiYS;
import com.poweruniverse.app.entity.trans.ZiDuanYS;
import com.poweruniverse.nim.data.entity.sys.YongHu;
import com.poweruniverse.nim.data.entity.sys.ZiDuanLX;
import com.poweruniverse.nim.data.service.utils.HibernateSessionFactory;

/**
 * 解析客户端保存的json数据 插入到数据接口表中
 *
 */
public class TransUtils {
	public static SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 创建一个任务传输记录 
	 * @param yongHuDM
	 * @param sourceSTLDH
	 * @param sourceId
	 * @param targetXTDH
	 * @param targetSTLDH
	 * @param targetId
	 * @param jsonData
	 * @return
	 */
	public static ChuanShuJL createTaskTrans(GongNengCZYS transGNCZMap,Integer sourceId,JSONObject sourceData,YongHu yh,String ip) throws Exception{
		Session sess = HibernateSessionFactory.getSession();
		//此实体类的映射关系
		ShiTiLeiYS transSTLMap = transGNCZMap.getGongNengYS().getShiTiLeiYS();

		ChuanShuJL transInfo = new ChuanShuJL();
		transInfo.setSourceXTDH(transGNCZMap.getGongNengYS().getSourceYYXT().getYingYongXTDH());
		transInfo.setSourceGNDH(transGNCZMap.getGongNengYS().getSourceGNDH());
		transInfo.setSourceCZDH(transGNCZMap.getSourceCZDH());
		transInfo.setSourceSTLDH(transSTLMap.getSourceSTLDH());
		transInfo.setSourceZJZ(sourceId);
		transInfo.setChuangJianSJ(Calendar.getInstance().getTime());//创建时间
		transInfo.setChuangJianRen(yh.getYongHuMC());//创建人
		transInfo.setChuangJianIp(ip);//创建人ip
		
		transInfo.setShiFouCSWC(false);//未传输完成
		transInfo.setShuJuLBMC("json");//传输内容是json 只有 json|xml
		transInfo.setXinXiLBMC("task");//传输的类别是task  data|file|task

		transInfo.setTargetXTDH(transGNCZMap.getGongNengYS().getTargetYYXT().getYingYongXTDH());
		transInfo.setTargetGNDH(transGNCZMap.getGongNengYS().getTargetGNDH());
		transInfo.setTargetCZDH(transGNCZMap.getTargetCZDH());
		transInfo.setTargetSTLDH(transSTLMap.getTargetSTLDH());
		transInfo.setTargetZJZ(null);
		
		//数据转换
		JSONObject newJsonObj = new JSONObject();
		for(ZiDuanYS zdMap:transSTLMap.getZds()){
			Object value = null;
			if(sourceData.containsKey(zdMap.getSourceZDDH())){
				value = sourceData.get(zdMap.getSourceZDDH());
			}
			if(value==null || value instanceof JSONNull){
				//空值也要传递
				newJsonObj.put(zdMap.getTargetZDDH(), JSONNull.getInstance());
			}else{
				//根据源字段类型 确定如何传递此数据
				Integer zdlxdm = zdMap.getSourceZDLX().getZiDuanLXDM();
				if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_OBJECT)){
					//对象类型的数据 （不会自动级联传输关联的对象 此对象必须已存在）
					ShiTiLeiYS zdGLSTLYS = zdMap.getGuanLianSTLYS();
					//子对象的主键值
					JSONObject subObj = (JSONObject)value;
					Integer subPKValue = subObj.getInt(zdGLSTLYS.getSourceZJZDDH());
					//在数据传输表中 查找对应的子表传输记录(取最后一条)
					ChuanShuJL dependTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", transSTLMap.getSourceYYXT().getYingYongXTDH()))
							.add(Restrictions.eq("sourceSTLDH", zdGLSTLYS.getSourceSTLDH()))
							.add(Restrictions.eq("sourceZJZ", subPKValue))
							.add(Restrictions.eq("targetXTDH", transSTLMap.getTargetYYXT().getYingYongXTDH()))
							.addOrder(Order.desc("chuanShuJLDM"))
							.setMaxResults(1)
							.uniqueResult();
					if(dependTrans!=null){
						//如果关联对象已通过接口传输 无论是否附件、是否传输完成，仅记录依赖关系即可
						ChuanShuJLYL subChuanShuJLYL = new ChuanShuJLYL();
						subChuanShuJLYL.setYiLaiCSJL(dependTrans);
						transInfo.addToyls(transInfo, subChuanShuJLYL);
						//记录对象类型字段的数据
						newJsonObj.put(
							zdMap.getTargetZDDH(), 
							JSONObject.fromObject("{"+
									zdGLSTLYS.getTargetZJZDDH()+":'"+encodeOutputPKValue(zdGLSTLYS.getSourceSTLDH(),subPKValue)+"'"
							+ "}")
						);
					}else {
						//对象类型字段的关联对象 必须已经先通过接口传输了
						throw new Exception("生成待传输JSON数据失败：源数据("+zdMap.getShiTiLeiYS().getSourceSTLDH()+")中字段("+zdMap.getSourceZDDH()+")依赖的子表数据(id:"+subPKValue+")尚未传输");
					}
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_File)){
					//附件类型的数据 （不会自动级联传输关联的对象 此对象必须已存在）
					ShiTiLeiYS zdGLSTLYS = zdMap.getGuanLianSTLYS();
					//子对象的主键值
					JSONObject subObj = (JSONObject)value;
					Integer subPKValue = subObj.getInt(zdGLSTLYS.getSourceZJZDDH());
					//在数据传输表中 查找对应的附件传输记录(取最后一条)
					ChuanShuJL dependTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", transSTLMap.getSourceYYXT().getYingYongXTDH()))
							.add(Restrictions.eq("xinXiLBMC", "file"))
							.add(Restrictions.eq("sourceZJZ", subPKValue))
							.add(Restrictions.eq("targetXTDH", transSTLMap.getTargetYYXT().getYingYongXTDH()))
							.addOrder(Order.desc("chuanShuJLDM"))
							.setMaxResults(1)
							.uniqueResult();
					if(dependTrans!=null){
						//如果关联附件已通过接口传输 无论是否附件、是否传输完成，仅记录依赖关系即可
						ChuanShuJLYL subChuanShuJLYL = new ChuanShuJLYL();
						subChuanShuJLYL.setYiLaiCSJL(dependTrans);
						transInfo.addToyls(transInfo, subChuanShuJLYL);
						//记录对象类型字段的数据
						newJsonObj.put(
							zdMap.getTargetZDDH(), 
							JSONObject.fromObject("{"+
									zdGLSTLYS.getTargetZJZDDH()+":'"+encodeOutputPKValue(zdGLSTLYS.getSourceSTLDH(),subPKValue)+"'"
							+ "}")
						);
					}else {
						//对象类型字段的关联对象 必须已经先通过接口传输了
						throw new Exception("生成待传输JSON数据失败：源数据("+zdMap.getShiTiLeiYS().getSourceSTLDH()+")中字段("+zdMap.getSourceZDDH()+")依赖的附件(id:"+subPKValue+")尚未传输");
					}
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_SET) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_Fileset) ){
					//集合类型的数据 （忽略）
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_STRING) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_TEXT)){
					newJsonObj.put(zdMap.getTargetZDDH(), value.toString());
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_DOUBLE) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_INT) ){
					newJsonObj.put(zdMap.getTargetZDDH(), value);
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_DATE) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_MONTH)){
					newJsonObj.put(zdMap.getTargetZDDH(),dtf.format((Date)value));
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_BOOLEAN)){
					newJsonObj.put(zdMap.getTargetZDDH(), value);
				}else{
					throw new Exception("未处理的字段类型！"+zdlxdm);
				}
			}
		}
		transInfo.setJsonData(newJsonObj.toString());
		return transInfo;
	}

	
	/**
	 * 创建数据输出记录
	 * @param yongHuDM
	 * @param sourceSTLDH
	 * @param sourceId
	 * @param targetXTDH
	 * @return
	 * @throws Exception
	 */
	public static ChuanShuJL createRecordTrans(ShiTiLeiYS transSTLMap,Integer sourceId,JSONObject sourceData,YongHu yh,String ip) throws Exception{
		Session sess = HibernateSessionFactory.getSession();
		
		//此实体类
		//创建本系统需要向外传输的数据传输记录
		ChuanShuJL transInfo = new ChuanShuJL();
		transInfo.setSourceXTDH(transSTLMap.getSourceYYXT().getYingYongXTDH());
		transInfo.setSourceSTLDH(transSTLMap.getSourceSTLDH());
		transInfo.setChuangJianSJ(Calendar.getInstance().getTime());//创建时间
		transInfo.setChuangJianRen(yh.getYongHuMC());//创建人
		transInfo.setChuangJianIp(ip);//创建人ip
		
		transInfo.setSourceZJZ(sourceId);
		
		transInfo.setShiFouCSWC(false);//未传输完成
		transInfo.setShuJuLBMC("json");//传输内容是json 只有 json|xml
		transInfo.setXinXiLBMC("data");//传输的是data  data|file|task
		//查找本系统是否曾经向目标系统传输过此条数据(查找已完成的记录 获取目标代码) （可能有多次传输 多条记录 取任意一条已完成的即可）
		ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
				.add(Restrictions.eq("sourceXTDH", transSTLMap.getSourceYYXT().getYingYongXTDH()))
				.add(Restrictions.eq("sourceSTLDH", transSTLMap.getSourceSTLDH()))
				.add(Restrictions.eq("sourceZJZ", sourceId))
				.add(Restrictions.eq("targetXTDH", transSTLMap.getTargetYYXT().getYingYongXTDH()))
				.add(Restrictions.eq("targetSTLDH", transSTLMap.getTargetSTLDH()))
				.add(Restrictions.eq("shiFouCSWC", true))
				.setMaxResults(1)
				.uniqueResult();
		//已传输完成的数据 一定是有主键值的
		Integer targetId = null;
		if(hisTrans!=null){
			targetId = hisTrans.getTargetZJZ();
		}
		transInfo.setTargetXTDH(transSTLMap.getTargetYYXT().getYingYongXTDH());
		transInfo.setTargetSTLDH(transSTLMap.getTargetSTLDH());
		transInfo.setTargetZJZ(targetId);
		//根据字段映射 将数据中字段代号转换为目标系统的字段代号，将关联的对象 转换为目标系统的对象（主键值）
		
		JSONObject newJsonObj = new JSONObject();
		for(ZiDuanYS zdMap:transSTLMap.getZds()){
			Object value = null;
			if(sourceData.containsKey(zdMap.getSourceZDDH())){
				value = sourceData.get(zdMap.getSourceZDDH());
			}
			if(value==null || value instanceof JSONNull){
				//空值也要传递
				newJsonObj.put(zdMap.getTargetZDDH(), JSONNull.getInstance());
			}else{
				//根据源字段类型 确定如何传递此数据
				Integer zdlxdm = zdMap.getSourceZDLX().getZiDuanLXDM();
				if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_OBJECT)){
					//对象类型的数据 （不会自动级联传输关联的对象 此对象必须已存在）
					ShiTiLeiYS zdGLSTLYS = zdMap.getGuanLianSTLYS();
					//子对象的主键值
					JSONObject subObj = (JSONObject)value;
					Integer subPKValue = subObj.getInt(zdGLSTLYS.getSourceZJZDDH());
					//在数据传输表中 查找对应的子表传输记录(取最后一条)
					ChuanShuJL dependTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", transSTLMap.getSourceYYXT().getYingYongXTDH()))
							.add(Restrictions.eq("sourceSTLDH", zdGLSTLYS.getSourceSTLDH()))
							.add(Restrictions.eq("sourceZJZ", subPKValue))
							.add(Restrictions.eq("targetXTDH", transSTLMap.getTargetYYXT().getYingYongXTDH()))
							.add(Restrictions.eq("targetSTLDH", zdGLSTLYS.getTargetSTLDH()))
							.addOrder(Order.desc("chuanShuJLDM"))
							.setMaxResults(1)
							.uniqueResult();
					if(dependTrans!=null){
						//如果关联对象已通过接口传输 无论是否附件、是否传输完成，仅记录依赖关系即可
						ChuanShuJLYL subChuanShuJLYL = new ChuanShuJLYL();
						subChuanShuJLYL.setYiLaiCSJL(dependTrans);
						transInfo.addToyls(transInfo, subChuanShuJLYL);
						//记录对象类型字段的数据
						newJsonObj.put(
							zdMap.getTargetZDDH(), 
							JSONObject.fromObject("{"+
									zdGLSTLYS.getTargetZJZDDH()+":'"+encodeOutputPKValue(zdGLSTLYS.getSourceSTLDH(),subPKValue)+"'"
							+ "}")
						);
					}else {
						//对象类型字段的关联对象 必须已经先通过接口传输了
						throw new Exception("生成待传输JSON数据失败：源数据("+zdMap.getShiTiLeiYS().getSourceSTLDH()+")中字段("+zdMap.getSourceZDDH()+")依赖的子表数据(id:"+subPKValue+")尚未传输");
					}
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_File)){
					//附件类型的数据 （不会自动级联传输关联的对象 此对象必须已存在）
					ShiTiLeiYS zdGLSTLYS = zdMap.getGuanLianSTLYS();
					//子对象的主键值
					JSONObject subObj = (JSONObject)value;
					Integer subPKValue = subObj.getInt(zdGLSTLYS.getSourceZJZDDH());
					//在数据传输表中 查找对应的附件传输记录(取最后一条)
					ChuanShuJL dependTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", transSTLMap.getSourceYYXT().getYingYongXTDH()))
							.add(Restrictions.eq("xinXiLBMC", "file"))
							.add(Restrictions.eq("sourceZJZ", subPKValue))
							.add(Restrictions.eq("targetXTDH", transSTLMap.getTargetYYXT().getYingYongXTDH()))
							.addOrder(Order.desc("chuanShuJLDM"))
							.setMaxResults(1)
							.uniqueResult();
					if(dependTrans!=null){
						//如果关联附件已通过接口传输 无论是否附件、是否传输完成，仅记录依赖关系即可
						ChuanShuJLYL subChuanShuJLYL = new ChuanShuJLYL();
						subChuanShuJLYL.setYiLaiCSJL(dependTrans);
						transInfo.addToyls(transInfo, subChuanShuJLYL);
						//记录对象类型字段的数据
						newJsonObj.put(
							zdMap.getTargetZDDH(), 
							JSONObject.fromObject("{"+
									zdGLSTLYS.getTargetZJZDDH()+":'"+encodeOutputPKValue(zdGLSTLYS.getSourceSTLDH(),subPKValue)+"'"
							+ "}")
						);
					}else {
						//对象类型字段的关联对象 必须已经先通过接口传输了
						throw new Exception("生成待传输JSON数据失败：源数据("+zdMap.getShiTiLeiYS().getSourceSTLDH()+")中字段("+zdMap.getSourceZDDH()+")依赖的附件(id:"+subPKValue+")尚未传输");
					}
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_SET) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_Fileset) ){
					//集合类型的数据 （忽略）
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_STRING) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_TEXT)){
					newJsonObj.put(zdMap.getTargetZDDH(), value.toString());
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_DOUBLE) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_INT) ){
					newJsonObj.put(zdMap.getTargetZDDH(), value);
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_DATE) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_MONTH)){
					newJsonObj.put(zdMap.getTargetZDDH(),dtf.format((Date)value));
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_BOOLEAN)){
					newJsonObj.put(zdMap.getTargetZDDH(), value);
				}else{
					throw new Exception("未处理的字段类型！"+zdlxdm);
				}
			}
		}
		
		transInfo.setJsonData(newJsonObj.toString());
		return transInfo;
	}
	
	
	/**
	 * 创建附件输出记录
	 * @param yongHuDM
	 * @param sourceSTLDH
	 * @param sourceId
	 * @param targetXTDH
	 * @return
	 * @throws Exception
	 */
	public static ChuanShuJL createFuJianTrans(String sourceXTDH,Integer sourceZJZ,String targetXTDH,YongHu yh,String ip) throws Exception{
		if(sourceZJZ == null){
			throw new Exception("生成待传输附件数据：必须提供附件的主键值");
		}
		
		Session sess = HibernateSessionFactory.getSession();
		//查找本系统是否曾经向目标系统传输过此条附件
		ChuanShuJL transInfo = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("xinXiLBMC", "file"))
				.add(Restrictions.eq("sourceZJZ", sourceZJZ))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.setMaxResults(1)
				.uniqueResult();
		if(transInfo == null){
			//之前没有传输过 新增待传输记录
			transInfo = new ChuanShuJL();
			transInfo.setSourceXTDH(sourceXTDH);
			transInfo.setSourceZJZ(sourceZJZ);
			transInfo.setChuangJianSJ(Calendar.getInstance().getTime());//创建时间
			transInfo.setChuangJianRen(yh.getYongHuMC());//创建人
			transInfo.setChuangJianIp(ip);//创建人ip
			
			transInfo.setTargetXTDH(targetXTDH);
			transInfo.setTargetZJZ(null);
			
			transInfo.setXinXiLBMC("file");//传输的是数据  data|file
		}
		return transInfo;
	}
	
	private static String encodeOutputPKValue(String shiTiLeiDH,Integer id){
		return "${"+shiTiLeiDH+"_"+id+"?c}";
	}
	
	
}
