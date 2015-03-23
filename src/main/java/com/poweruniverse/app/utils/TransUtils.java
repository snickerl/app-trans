package com.poweruniverse.app.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.poweruniverse.app.entity.trans.ChuanShuJL;
import com.poweruniverse.app.entity.trans.ChuanShuJLYL;
import com.poweruniverse.app.entity.trans.GongNengCZYS;
import com.poweruniverse.app.entity.trans.ShiTiLeiYS;
import com.poweruniverse.app.entity.trans.ZiDuanYS;
import com.poweruniverse.nim.data.entity.sys.GongNeng;
import com.poweruniverse.nim.data.entity.sys.ShiTiLei;
import com.poweruniverse.nim.data.entity.sys.ZiDuan;
import com.poweruniverse.nim.data.entity.sys.ZiDuanLX;
import com.poweruniverse.nim.data.entity.sys.base.EntityI;
import com.poweruniverse.nim.data.service.utils.HibernateSessionFactory;

/**
 * 解析客户端保存的json数据 插入到数据接口表中
 *
 */
public class TransUtils {
	public static SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 发送实体类数据 (先传输关联的附件)
	 * @param yongHuDM
	 * @param targetXTDH
	 * @param targetGNDH
	 * @param targetCZDH
	 * @param dataId
	 * @param data
	 */
	public static ChuanShuJL sendEntity(String sourceXTDH,String sourceSTLDH,Integer sourceId,String targetXTDH) throws Exception{
		Session sess = HibernateSessionFactory.getSession();
		ShiTiLei sourceSTL = ShiTiLei.getShiTiLeiByDH(sourceSTLDH);
		//
		ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceSTLDH", sourceSTLDH))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.uniqueResult();
		if(transSTLMap==null){
			throw new Exception("当前系统("+sourceXTDH+")的实体类("+sourceSTLDH+")未与目标系统("+targetXTDH+")建立映射!");
		}

		//当前实体类对象
		EntityI sourceObj = (EntityI)sess.load(sourceSTL.getShiTiLeiClassName(), sourceId);
		//查找当前实体类的映射信息 是否有对象类型的附件字段 
		List<ChuanShuJL> fjs = new ArrayList<ChuanShuJL>();
		for(ZiDuanYS zdMap:transSTLMap.getZds()){
			ZiDuan subZd = sourceSTL.getZiDuan(zdMap.getSourceZDDH());
			if(subZd.getZiDuanLX().getZiDuanLXDM().equals(ZiDuanLX.ZiDuanLX_File)){
				Object value = PropertyUtils.getProperty(sourceObj, subZd.getZiDuanDH());
				if(value!=null){
					EntityI subObj = (EntityI)value;
					//先启动本地接受附件的传输任务
					ChuanShuJL fjChuanShuJL1 = createFuJianTrans(sourceXTDH, subObj.pkValue(), "_system");
					sess.save(fjChuanShuJL1);
					//再启动向目标系统的附件传输(依赖上述传输任务)
					ChuanShuJL fjChuanShuJL = createFuJianTrans(sourceXTDH, subObj.pkValue(), targetXTDH);
					fjChuanShuJL.addToyls(fjChuanShuJL, fjChuanShuJL1);
					sess.save(fjChuanShuJL);
					
					fjs.add(fjChuanShuJL);
				}
			}
		}
		//先启动传输当前对象的传输
		ChuanShuJL transInfo = createRecordTrans(sourceXTDH,sourceSTLDH, sourceId,targetXTDH);
		sess.save(transInfo);
		//级联传输子类
		for(ZiDuanYS zdMap:transSTLMap.getZds()){
			ZiDuan subZd = sourceSTL.getZiDuan(zdMap.getSourceZDDH());
			if(subZd.getZiDuanLX().getZiDuanLXDM().equals(ZiDuanLX.ZiDuanLX_SET) ){
				Object value = PropertyUtils.getProperty(sourceObj, subZd.getZiDuanDH());
				if(value!=null){
					ShiTiLeiYS transSubSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
							.add(Restrictions.eq("sourceXTDH", sourceXTDH))
							.add(Restrictions.eq("sourceSTLDH", subZd.getGuanLianSTL().getShiTiLeiDH()))
							.add(Restrictions.eq("targetXTDH", targetXTDH))
							.uniqueResult();
					if(transSubSTLMap==null){
						throw new Exception("当前系统("+sourceXTDH+")的实体类("+subZd.getGuanLianSTL().getShiTiLeiDH()+")未与目标系统("+targetXTDH+")建立映射!");
					}
					
					@SuppressWarnings("unchecked")
					Set<EntityI> subObjs = (Set<EntityI>)value;
					for(EntityI subObj:subObjs){
						transInfo = sendEntity(sourceXTDH, subZd.getGuanLianSTL().getShiTiLeiDH(), subObj.pkValue(), targetXTDH);
						sess.save(transInfo);
					}
				}
			}
		}
		return transInfo;
	}
//	
	/**
	 * 发送数据及工作流
	 * @param yongHuDM
	 * @param sourceSTLDH
	 * @param sourceId
	 * @param targetXTDH
	 * @param targetGNDH
	 * @param targetCZDH
	 * @param targetId
	 * @param jsonData
	 * @return
	 */
	public static ChuanShuJL sendTask(String sourceXTDH,String sourceGNDH,String sourceCZDH,Integer sourceId,String targetXTDH) throws Exception{
		Session sess = HibernateSessionFactory.getSession();
		GongNeng gn = GongNeng.getGongNengByDH(sourceGNDH);
		
		//此实体类的映射关系
		ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceSTLDH", gn.getShiTiLei().getShiTiLeiDH()))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.uniqueResult();
		if(transSTLMap==null){
			throw new Exception("当前系统("+sourceXTDH+")的实体类("+gn.getShiTiLei().getShiTiLeiDH()+")未与目标系统("+targetXTDH+")建立映射!");
		}

		//此功能操作的映射关系
		GongNengCZYS transGNCZMap = (GongNengCZYS)sess.createCriteria(GongNengCZYS.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceGNDH", sourceGNDH))
				.add(Restrictions.eq("sourceCZDH", sourceCZDH))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.uniqueResult();
		if(transGNCZMap==null){
			throw new Exception("当前系统("+sourceXTDH+")的功能操作("+sourceGNDH+"."+sourceCZDH+")未与目标系统("+targetXTDH+")建立映射!");
		}
		//工作任务的传输
		ChuanShuJL taskChuanShuJL = createTaskTrans(sourceXTDH,sourceGNDH,sourceCZDH,sourceId,targetXTDH);
		sess.save(taskChuanShuJL);
		return taskChuanShuJL;
	}
	
	

	/**
	 * 发送一个功能操作/流程动作
	 * @param yongHuDM
	 * @param sourceSTLDH
	 * @param sourceId
	 * @param targetXTDH
	 * @param targetSTLDH
	 * @param targetId
	 * @param jsonData
	 * @return
	 */
	private static ChuanShuJL createTaskTrans(String sourceXTDH,String sourceGNDH,String sourceCZDH,Integer sourceId,String targetXTDH) throws Exception{
		Session sess = HibernateSessionFactory.getSession();
		GongNeng sourceGN = GongNeng.getGongNengByDH(sourceGNDH);
		//此实体类的映射关系
		ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceSTLDH", sourceGN.getShiTiLei().getShiTiLeiDH()))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.uniqueResult();
		if(transSTLMap==null){
			throw new Exception("源系统("+sourceXTDH+")的实体类("+sourceGN.getShiTiLei().getShiTiLeiDH()+")未与目标系统("+targetXTDH+")建立映射!");
		}

		//此功能操作的映射关系
		GongNengCZYS transGNCZMap = (GongNengCZYS)sess.createCriteria(GongNengCZYS.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceGNDH", sourceGNDH))
				.add(Restrictions.eq("sourceCZDH", sourceCZDH))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.uniqueResult();
		if(transGNCZMap==null){
			throw new Exception("源系统("+sourceXTDH+")的功能操作("+sourceGNDH+"."+sourceCZDH+")未与目标系统("+targetXTDH+")建立映射!");
		}
				
		//此实体类操作的映射关系
		ChuanShuJL transInfo = new ChuanShuJL();
		transInfo.setSourceXTDH(sourceXTDH);
		transInfo.setSourceSTLDH(transSTLMap.getSourceSTLDH());
		transInfo.setSourceZJZ(sourceId);
		transInfo.setChuangJianSJ(Calendar.getInstance().getTime());//创建时间
		
		transInfo.setShiFouCSWC(false);//未传输完成
		transInfo.setShuJuLBMC("json");//传输内容是json 只有 json|file
		transInfo.setXinXiLBMC("task");//传输的类别是task  data|file|task
		transInfo.setTargetLBMC("gn");//传输目标是gn  stl|gn|file

		transInfo.setTargetXTDH(transGNCZMap.getTargetXTDH());
		transInfo.setTargetGNDH(transGNCZMap.getTargetGNDH());
		transInfo.setTargetCZDH(transGNCZMap.getTargetCZDH());
		transInfo.setTargetZJZ(null);
		
		//只传输主键值
		JSONObject jsonData = new JSONObject();
		jsonData.put(transSTLMap.getTargetZJDH(), encodeOutputPKValue(transSTLMap.getSourceSTLDH(),sourceId));
		
		transInfo.setJsonData("["+jsonData.toString()+"]");
		
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
	private static ChuanShuJL createRecordTrans(String sourceXTDH,String sourceSTLDH,Integer sourceId,String targetXTDH) throws Exception{
		Session sess = HibernateSessionFactory.getSession();
		ShiTiLei sourceSTL = ShiTiLei.getShiTiLeiByDH(sourceSTLDH);
		
		//此实体类的映射关系
		ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceSTLDH", sourceSTLDH))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.uniqueResult();
		if(transSTLMap==null){
			throw new Exception("源系统("+sourceXTDH+")的实体类("+sourceSTLDH+")未与目标系统("+targetXTDH+")建立映射!");
		}
		
		//创建本系统需要向外传输的数据传输记录
		ChuanShuJL transInfo = new ChuanShuJL();
		transInfo.setSourceXTDH(sourceXTDH);
		transInfo.setSourceSTLDH(sourceSTLDH);
		transInfo.setChuangJianSJ(Calendar.getInstance().getTime());//创建时间
		
		transInfo.setSourceZJDH(sourceSTL.getZhuJianLie());//源数据的主键列代号
		transInfo.setSourceZJZ(sourceId);
		
		transInfo.setShiFouCSWC(false);//未传输完成
		transInfo.setShuJuLBMC("json");//传输内容是json 只有 json|file
		transInfo.setXinXiLBMC("data");//传输的是data  data|file|task
		transInfo.setTargetLBMC("stl");//传输目标是stl  stl|gn|file
		//查找本系统是否曾经向目标系统传输过此条数据(查找已完成的记录 获取目标代码) （可能有多次传输 多条记录 取任意一条已完成的即可）
		ChuanShuJL hisTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceSTLDH", sourceSTLDH))
				.add(Restrictions.eq("sourceZJZ", sourceId))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.add(Restrictions.eq("shiFouCSWC", true))
				.setMaxResults(1)
				.uniqueResult();
		//已传输完成的数据 一定是有主键值的
		Integer targetId = null;
		if(hisTrans!=null){
			targetId = hisTrans.getTargetZJZ();
		}
		transInfo.setTargetXTDH(transSTLMap.getTargetXTDH());
		transInfo.setTargetSTLDH(transSTLMap.getTargetSTLDH());
		transInfo.setTargetZJZ(targetId);
		//准备数据
		JSONObject jsonData = new JSONObject();
		
		Object sourceObj = sess.load(sourceSTL.getShiTiLeiClassName(), sourceId);
		//根据字段映射 将数据拼为json格式
		for(ZiDuanYS zdMap:transSTLMap.getZds()){
			Object value = PropertyUtils.getProperty(sourceObj, zdMap.getSourceZDDH());
			if(value == null){
				//空值直接传递
				jsonData.put(zdMap.getTargetZDDH(), JSONNull.getInstance());
			}else{
				//根据源字段类型 确定如何传递此数据
				Integer zdlxdm = zdMap.getSourceZDLX().getZiDuanLXDM();
				if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_OBJECT)){
					//对象类型的数据 （不会自动级联传输关联的对象 此对象必须已存在）
					ZiDuan subZd = sourceSTL.getZiDuan(zdMap.getSourceZDDH());
					EntityI subObj = (EntityI)value;
					//在数据传输表中 查找对应的子表传输记录(取最后一条)
					ChuanShuJL dependTrans = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
							.add(Restrictions.eq("sourceXTDH", sourceXTDH))
							.add(Restrictions.eq("sourceSTLDH", subZd.getGuanLianSTL().getShiTiLeiDH()))
							.add(Restrictions.eq("sourceZJZ", subObj.pkValue()))
							.add(Restrictions.eq("targetXTDH", transSTLMap.getTargetXTDH()))
							.addOrder(Order.desc("xinXiCSJLDM"))
							.setMaxResults(1)
							.uniqueResult();
					if(dependTrans!=null){
						//如果关联对象已通过接口传输 无论是否附件、是否传输完成，仅记录依赖关系即可
						ChuanShuJLYL subChuanShuJLYL = new ChuanShuJLYL();
						subChuanShuJLYL.setYiLaiCSJL(dependTrans);
						transInfo.addToyls(transInfo, subChuanShuJLYL);
						//子表的映射关系
						ShiTiLeiYS transSubMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
								.add(Restrictions.eq("sourceXTDH", transSTLMap.getSourceXTDH()))
								.add(Restrictions.eq("sourceSTLDH", subZd.getGuanLianSTL().getShiTiLeiDH()))
								.add(Restrictions.eq("targetXTDH", transSTLMap.getTargetXTDH()))
								.uniqueResult();
						jsonData.put(
							zdMap.getTargetZDDH(), 
							JSONObject.fromObject("{"+
									transSubMap.getTargetZJDH()+":'"+encodeOutputPKValue(subZd.getGuanLianSTL().getShiTiLeiDH(),subObj.pkValue())+"'"
							+ "}")
						);
					}else {
						//对象类型字段的关联对象 必须已经先通过接口传输了
						throw new Exception("生成待传输JSON数据：处理源数据("+zdMap.getShiTiLeiYS().getSourceSTLDH()+")中字段("+zdMap.getSourceZDDH()+")的值失败，未发现主键值为"+subObj.pkValue()+"的子表数据的传输记录");
					}
					
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_SET)){
					//集合类型的数据 （不级联传输）
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_STRING) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_TEXT)){
					jsonData.put(zdMap.getTargetZDDH(), value.toString());
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_DOUBLE) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_INT) ){
					jsonData.put(zdMap.getTargetZDDH(), value);
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_DATE) || zdlxdm.equals(ZiDuanLX.ZiDuanLX_MONTH)){
					jsonData.put(zdMap.getTargetZDDH(),dtf.format((Date)value));
				}else if(zdlxdm.equals(ZiDuanLX.ZiDuanLX_BOOLEAN)){
					jsonData.put(zdMap.getTargetZDDH(), value);
				}else{
					throw new Exception("未处理的字段类型！");
				}
			}
		}
		
		transInfo.setJsonData(jsonData.toString());
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
	private static ChuanShuJL createFuJianTrans(String sourceXTDH,Integer fuJianId,String targetXTDH) throws Exception{
		if(fuJianId == null){
			throw new Exception("生成待传输附件数据：必须提供附件的主键值");
		}
		
		Session sess = HibernateSessionFactory.getSession();
		//查找本系统是否曾经向目标系统传输过此条附件
		ChuanShuJL transInfo = (ChuanShuJL)sess.createCriteria(ChuanShuJL.class)
				.add(Restrictions.eq("sourceXTDH", sourceXTDH))
				.add(Restrictions.eq("sourceZJZ", fuJianId))
				.add(Restrictions.eq("targetXTDH", targetXTDH))
				.setMaxResults(1)
				.uniqueResult();
		if(transInfo == null){
			//之前没有传输过 新增待传输记录
			transInfo = new ChuanShuJL();
			transInfo.setSourceXTDH(sourceXTDH);
			transInfo.setChuangJianSJ(Calendar.getInstance().getTime());//创建时间
			transInfo.setSourceZJDH("fuJianDM");
			transInfo.setSourceZJZ(fuJianId);
			
			transInfo.setTargetLBMC("fj");//传输目标是stl  stl|gn|fj
			transInfo.setTargetXTDH(targetXTDH);
			transInfo.setTargetZJZ(null);
			
			transInfo.setShuJuLBMC("file");//传输内容是json json|table|file
			transInfo.setXinXiLBMC("file");//传输的是数据  data|file
			
		}
		return transInfo;
	}
	
	private static String encodeOutputPKValue(String shiTiLeiDH,Integer id){
		return "${"+shiTiLeiDH+"_"+id+"?c}";
	}
	
	
	public static void main(String[] args) {
//		Session sess = HibernateSessionFactory.getSession();
//
//		try {
//			sess = HibernateSessionFactory.getSession();
//			//插入 供应商对口事业部 信息
//			List<DuiKouSYB> dksybs = (List<DuiKouSYB>)sess.createCriteria(DuiKouSYB.class).list();
//			for(DuiKouSYB dksyb:dksybs ){
//				ChuanShuJL transInfo = outputTableData(1, "HDGC_GYS_DuiKouSYB", dksyb.getDuiKouSYBDM(), "eop", null);
//				
//				transInfo.setShiFouCSWC(true);
//				transInfo.setTargetZJZ(dksyb.getDuiKouSYBDM());
//				sess.update(transInfo);
//			}
//			
//			//插入 企业类型 信息
//			List<QiYeLX> qiYeLXs = (List<QiYeLX>)sess.createCriteria(QiYeLX.class).list();
//			for(QiYeLX qiYeLX:qiYeLXs ){
//				ChuanShuJL transInfo = outputTableData(1, "HDGC_GYS_QiYeLX", qiYeLX.getQiYeLXDM(), "eop", null);
//				
//				transInfo.setShiFouCSWC(true);
//				transInfo.setTargetZJZ(qiYeLX.getQiYeLXDM());
//				sess.update(transInfo);
//			}
//			
//			
//			//插入 申请资料采用标准 信息
//			List<ShenQingCYBZ> shenQingCYBZs = (List<ShenQingCYBZ>)sess.createCriteria(ShenQingCYBZ.class).list();
//			for(ShenQingCYBZ shenQingCYBZ:shenQingCYBZs ){
//				ChuanShuJL transInfo = outputTableData(1, "HDGC_GYS_ShenQingCYBZ", shenQingCYBZ.getShenQingCYBZDM(), "eop", null);
//				
//				transInfo.setShiFouCSWC(true);
//				transInfo.setTargetZJZ(shenQingCYBZ.getShenQingCYBZDM());
//				sess.update(transInfo);
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			if(sess !=null){
//				HibernateSessionFactory.closeSession(false);
//				sess = null;
//			}
//		}finally{
//			if(sess !=null){
//				HibernateSessionFactory.closeSession(true);
//			}
//		}

	}

}
