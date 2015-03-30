package com.poweruniverse.app.job;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.poweruniverse.app.entity.trans.ChuanShuJL;
import com.poweruniverse.app.entity.trans.ChuanShuJLYL;
import com.poweruniverse.app.entity.trans.ShiTiLeiYS;
import com.poweruniverse.app.entity.trans.YingYongXT;
import com.poweruniverse.app.trans.intface.TransInterface;
import com.poweruniverse.nim.base.description.Application;
import com.poweruniverse.nim.base.message.JSONMessageResult;
import com.poweruniverse.nim.base.utils.FreemarkerUtils;
import com.poweruniverse.nim.data.service.utils.HibernateSessionFactory;

/**
 * 定时执行 将消息发送到目标系统
 * @version
 * 
 */
public class TransJob implements Job {
	
	private static Logger logger = null;
	private Map<String,TransInterface> transImpMap = new HashMap<String,TransInterface>();
	// 当前任务是否正在运行
	private static boolean running = false;
	
	public void execute(JobExecutionContext arg1) throws JobExecutionException {
		
		if (running ) {
			logger.info("上一数据接口传输进程尚未完成，未再次启动数据接口传输任务...");
			return;
		} else {
			running = true;
			Session sess = null;
			try {
				sess = HibernateSessionFactory.getSession();
				//所有未完成的下级记录(子查询)
				DetachedCriteria subselect = DetachedCriteria.forClass(ChuanShuJLYL.class);
					subselect.createAlias("xinXiCSYL", "mx_yl");
					subselect.add(Restrictions.eq("mx_yl.shiFouCSWC", false));
					subselect.setProjection(Projections.property("xinXiCSJL.id"));
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//先传输附件
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				Criteria fileTransCriteria = sess.createCriteria(ChuanShuJL.class)
						.add(Restrictions.sqlRestriction("targetXTDH in (select xt.yingyongxtbh from trans_yingyongxt xt where xt.shifoujsfj = 1"))//只查询目标系统可以接收附件的
						.add(Restrictions.eq("xinXiLBMC", "file"))
						.add(Restrictions.eq("shiFouCSWC", false))//未完成 
						.add(Property.forName("chuanShuJLDM").notIn(subselect))//且不存在未完成的依赖
						.addOrder(Order.asc("chuanShuJLDM"));
					@SuppressWarnings("unchecked")
					List<ChuanShuJL> fileChuanShuJLs = (List<ChuanShuJL>)fileTransCriteria.setMaxResults(20).list();
					logger.debug("附件传输进程...启动 共"+fileChuanShuJLs.size()+"条");
					for(int i=0;i<fileChuanShuJLs.size();i++){
						ChuanShuJL fileChuanShuJL = fileChuanShuJLs.get(i);
						//传输此信息
						logger.debug("开始传输附件 id:" +fileChuanShuJL.getSourceZJZ()+"->"+fileChuanShuJL.getTargetXTDH()+" ... 第"+i+"条 共"+fileChuanShuJLs.size()+"条");
						//检查此数据的映射关系
						ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
								.add(Restrictions.eq("sourceXTDH", fileChuanShuJL.getSourceXTDH()))
								.add(Restrictions.eq("sourceSTLDH", fileChuanShuJL.getSourceSTLDH()))
								.add(Restrictions.eq("targetXTDH", fileChuanShuJL.getTargetXTDH()))
								.uniqueResult();
						if(transSTLMap!=null){
							logger.debug("		映射关系检查通过");
							//记录传输次数 传输时间
							if (!sess.getTransaction().isActive()) {
								sess.beginTransaction();
					        }
							fileChuanShuJL.setChuanShuCS(fileChuanShuJL.getChuanShuCS()==null?1:fileChuanShuJL.getChuanShuCS()+1);//发送次数+1
							fileChuanShuJL.setZuiHouFSSJ(Calendar.getInstance().getTime());//最后发送时间
							sess.update(fileChuanShuJL);
							sess.getTransaction().commit();
							logger.debug("		记录传输次数+1完成");
							sess.beginTransaction();
							
							
							JSONMessageResult uploadRet = null;
							if("_system".equals(fileChuanShuJL.getTargetXTDH())){
								//本地系统接受附件的任务
								logger.debug("		向源系统请求附件："+fileChuanShuJL.getSourceZJZ());
								uploadRet = takeFile(fileChuanShuJL);
								logger.debug("		目标服务器返回："+uploadRet);
							}else{
								//向目标系统发送附件的任务
								logger.debug("		向目标系统发送附件："+fileChuanShuJL.getSourceZJZ());
								uploadRet = transFile(fileChuanShuJL);
								logger.debug("		目标服务器返回："+uploadRet);
							}
							
							//记录传输结果
							if(uploadRet.isSuccess()){
								Integer newId = (Integer)uploadRet.get(transSTLMap.getTargetZJZDDH());//目标系统用目标系统实体类的主键列名返回新的主键值
								//记录传输成功信息（下一条相同记录传输时 会检查是否有已成功的记录 这里不做额外处理了）
								fileChuanShuJL = (ChuanShuJL)sess.load(ChuanShuJL.class, fileChuanShuJL.getChuanShuJLDM());
								
								fileChuanShuJL.setShiFouCSWC(true);
								fileChuanShuJL.setTargetZJZ(newId);
								//清除传输错误信息
								fileChuanShuJL.setCuoWuXXX(null);
							}else{
								//记录传输错误信息
								if(uploadRet.has("errorMsg")){
									fileChuanShuJL.setCuoWuXXX(uploadRet.getErrorMsg());
								}else{
									fileChuanShuJL.setCuoWuXXX("出现错误，但未返回错误信息");
								}
							}
							sess.update(fileChuanShuJL);
							sess.getTransaction().commit();
							logger.debug("		记录传输结果 完成");
							sess.beginTransaction();
							
						}else{
							logger.error("		映射关系不存在("+fileChuanShuJL.getSourceXTDH()+"."+fileChuanShuJL.getSourceSTLDH()+"->"+fileChuanShuJL.getTargetXTDH()+")，取消传输");
						}
					
						logger.debug("完成传输 第"+i+"条 共"+fileChuanShuJLs.size()+"条");
					}
					logger.debug("附件传输进程...完成 共"+fileChuanShuJLs.size()+"条");
			
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//再传输数据
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				Criteria dataTransCriteria = sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.sqlRestriction("targetXTDH in (select xt.yingyongxtbh from trans_yingyongxt xt where xt.shifoujssj = 1"))//只查询目标系统可以接收数据的
					.add(Restrictions.eq("xinXiLBMC", "data"))
					.add(Restrictions.eq("shiFouCSWC", false))//未完成 且不存在未完成的依赖关系
					.add(Property.forName("chuanShuJLDM").notIn(subselect))
					.addOrder(Order.asc("chuanShuJLDM"));
				@SuppressWarnings("unchecked")
				List<ChuanShuJL> dataChuanShuJLs = (List<ChuanShuJL>)dataTransCriteria.setMaxResults(10).list();
				logger.debug("数据传输进程...启动 共"+dataChuanShuJLs.size()+"条");
				for(int i=0;i<dataChuanShuJLs.size();i++){
					ChuanShuJL dataChuanShuJL = dataChuanShuJLs.get(i);
					logger.debug("开始传输 "+dataChuanShuJL.getSourceSTLDH()+"."+dataChuanShuJL.getSourceZJZ()+"->"+dataChuanShuJL.getTargetXTDH()+"."+dataChuanShuJL.getTargetSTLDH()+" ... 第"+i+"条 共"+dataChuanShuJLs.size()+"条");
					
					//检查此数据的映射关系
					ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
							.add(Restrictions.eq("sourceXTDH", dataChuanShuJL.getSourceXTDH()))
							.add(Restrictions.eq("sourceSTLDH", dataChuanShuJL.getSourceSTLDH()))
							.add(Restrictions.eq("targetXTDH", dataChuanShuJL.getTargetXTDH()))
							.uniqueResult();
					if(transSTLMap!=null){
						logger.debug("		映射关系检查通过");
						//记录传输次数 传输时间
						if (!sess.getTransaction().isActive()) {
							sess.beginTransaction();
				        }
						dataChuanShuJL.setChuanShuCS(dataChuanShuJL.getChuanShuCS()==null?1:dataChuanShuJL.getChuanShuCS()+1);//发送次数+1
						dataChuanShuJL.setZuiHouFSSJ(Calendar.getInstance().getTime());//最后发送时间
						sess.update(dataChuanShuJL);
						sess.getTransaction().commit();
						logger.debug("		记录传输次数+1完成");
						sess.beginTransaction();
						
						logger.debug("		向目标系统发送信息");
						JSONMessageResult uploadRet = transData(dataChuanShuJL);
						logger.debug("		目标服务器返回："+uploadRet);
						
						//记录传输结果
						if(uploadRet.isSuccess()){
							//记录传输成功信息（下一条相同记录传输时 会检查是否有已成功的记录 这里不做处理了）
							Integer newId = (Integer)uploadRet.get(transSTLMap.getTargetZJZDDH());//目标系统用目标系统实体类的主键列名返回新的主键值
							
							dataChuanShuJL = (ChuanShuJL)sess.load(ChuanShuJL.class, dataChuanShuJL.getChuanShuJLDM());
							dataChuanShuJL.setShiFouCSWC(true);
							dataChuanShuJL.setTargetZJZ(newId);
							//清除传输错误信息
							dataChuanShuJL.setCuoWuXXX(null);
						}else{
							//记录传输错误信息
							if(uploadRet.has("errorMsg")){
								dataChuanShuJL.setCuoWuXXX(uploadRet.getErrorMsg());
							}else{
								dataChuanShuJL.setCuoWuXXX("出现错误，但未返回错误信息");
							}
						}
						sess.update(dataChuanShuJL);
						sess.getTransaction().commit();
						logger.debug("		记录传输结果 完成");
						sess.beginTransaction();
						
					}else{
						logger.error("		映射关系不存在("+dataChuanShuJL.getSourceXTDH()+"."+dataChuanShuJL.getSourceSTLDH()+"->"+dataChuanShuJL.getTargetXTDH()+")，取消传输");
					}
					logger.debug("完成传输 第"+i+"条 共"+dataChuanShuJLs.size()+"条");
				}
				logger.debug("数据传输进程...完成 共"+dataChuanShuJLs.size()+"条");
				
				//最后传任务
				Criteria taskTransCriteria = sess.createCriteria(ChuanShuJL.class)
					.add(Restrictions.sqlRestriction("targetXTDH in (select xt.yingyongxtbh from trans_yingyongxt xt where xt.shifoujsrw = 1"))//只查询目标系统可以接收任务的
					.add(Restrictions.eq("xinXiLBMC", "task"))
					.add(Restrictions.eq("shiFouCSWC", false))//未完成 且不存在未完成的依赖关系
					.add(Property.forName("chuanShuJLDM").notIn(subselect))
					.addOrder(Order.asc("chuanShuJLDM"));
				@SuppressWarnings("unchecked")
				List<ChuanShuJL> taskChuanShuJLs = (List<ChuanShuJL>)taskTransCriteria.setMaxResults(10).list();
				logger.debug("任务传输进程...启动 共"+taskChuanShuJLs.size()+"条");
				for(int i=0;i<taskChuanShuJLs.size();i++){
					//传输此信息
					ChuanShuJL taskChuanShuJL = taskChuanShuJLs.get(i);
					logger.debug("开始传输 "+taskChuanShuJL.getSourceSTLDH()+"."+taskChuanShuJL.getSourceZJZ()+"->"+taskChuanShuJL.getTargetXTDH()+"."+taskChuanShuJL.getTargetGNDH()+"."+taskChuanShuJL.getTargetCZDH()+" ... 第"+i+"条 共"+taskChuanShuJLs.size()+"条");
					
					//检查此数据的映射关系
					ShiTiLeiYS transSTLMap = (ShiTiLeiYS)sess.createCriteria(ShiTiLeiYS.class)
							.add(Restrictions.eq("sourceXTDH", taskChuanShuJL.getSourceXTDH()))
							.add(Restrictions.eq("sourceSTLDH", taskChuanShuJL.getSourceSTLDH()))
							.add(Restrictions.eq("targetXTDH", taskChuanShuJL.getTargetXTDH()))
							.uniqueResult();
					if(transSTLMap!=null){
						//记录传输次数 传输时间
						if (!sess.getTransaction().isActive()) {
							sess.beginTransaction();
				        }
						taskChuanShuJL.setChuanShuCS(taskChuanShuJL.getChuanShuCS()==null?1:taskChuanShuJL.getChuanShuCS()+1);//发送次数+1
						taskChuanShuJL.setZuiHouFSSJ(Calendar.getInstance().getTime());//最后发送时间
						sess.update(taskChuanShuJL);
						sess.getTransaction().commit();
						logger.debug("		记录传输次数+1完成");
						sess.beginTransaction();
						
						logger.debug("		向目标系统发送信息");
						JSONMessageResult uploadRet = transTask(taskChuanShuJL);
						logger.debug("		目标服务器返回："+uploadRet);
						
						//记录传输结果
						if(uploadRet.isSuccess()){
							//记录传输成功信息（下一条相同记录传输时 会检查是否有已成功的记录 这里不做处理了）
							Integer newId = (Integer)uploadRet.get(transSTLMap.getTargetZJZDDH());//目标系统用目标系统实体类的主键列名返回新的主键值
							
							taskChuanShuJL = (ChuanShuJL)sess.load(ChuanShuJL.class, taskChuanShuJL.getChuanShuJLDM());
							taskChuanShuJL.setShiFouCSWC(true);
							taskChuanShuJL.setTargetZJZ(newId);
							//清除传输错误信息
							taskChuanShuJL.setCuoWuXXX(null);
						}else{
							//记录传输错误信息
							if(uploadRet.has("errorMsg")){
								taskChuanShuJL.setCuoWuXXX(uploadRet.getErrorMsg());
							}else{
								taskChuanShuJL.setCuoWuXXX("出现错误，但未返回错误信息");
							}
						}
						sess.update(taskChuanShuJL);
						sess.getTransaction().commit();
						logger.debug("		记录传输结果 完成");
						sess.beginTransaction();
							
					}else{
						logger.error("		映射关系不存在("+taskChuanShuJL.getSourceXTDH()+"."+taskChuanShuJL.getSourceSTLDH()+"->"+taskChuanShuJL.getTargetXTDH()+")，取消传输");
					}
				
					logger.debug("完成传输 第"+i+"条 共"+taskChuanShuJLs.size()+"条");
				}
				logger.debug("任务传输进程...完成 共"+taskChuanShuJLs.size()+"条");
			} catch (Exception e) {
				logger.error("	错误:"+e.getMessage());
				e.printStackTrace();
				if (sess != null) {
					HibernateSessionFactory.closeSession(false);
					sess = null;
				}
			} finally {
				running = false;
				logger.debug("接口数据传输进程...完成");
				if (sess != null) {
					HibernateSessionFactory.closeSession(true);
				}
			}
		}
	}
	
	
	//传输数据
	public JSONMessageResult transData(ChuanShuJL transInfo) throws Exception{
		JSONMessageResult result = null;
		// 发送数据
		try {
			String targetXTDH = transInfo.getTargetXTDH();
			String sourceXTDH = transInfo.getSourceXTDH();
			TransInterface transImpl = this.getTransImpl(targetXTDH);
			if(transImpl!=null){
				logger.debug("		从系统'"+sourceXTDH+"'传输数据'"+transInfo.getSourceSTLDH()+"."+transInfo.getSourceZJZ()+"'到系统'"+targetXTDH+"'开始" );
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
				
				//查找本次传输前 是否已成功传输并获取到目标系统的主键值
				Session sess = HibernateSessionFactory.getSession();
				Integer targetId = transInfo.getTargetZJZ();
				if(targetId == null){
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
					if(hisTrans!=null){
						targetId = hisTrans.getTargetZJZ();
					}
				}
				result = transImpl.postRecord(transInfo.getTargetSTLDH(), targetId,jsonString );
				logger.debug("		数据传输返回值:"+result);
			}else{
				logger.debug("		数据传输失败：数据接收接口不存在");
				result = new JSONMessageResult("数据接收接口不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("		数据传输失败:"+e.getMessage());
			result = new JSONMessageResult(e.getMessage());
		}
		return result;
	}
	
	//接收文件
	public JSONMessageResult takeFile(ChuanShuJL transInfo) throws Exception{
		JSONMessageResult result = null;
		FileOutputStream fos = null;
		String sourceXTDH = transInfo.getSourceXTDH();
		try {
			//取目标系统代号 获取对应的传输接口
			TransInterface transImpl = this.getTransImpl(sourceXTDH);
			if(transImpl!=null){
				logger.debug("		开始从系统'"+sourceXTDH+"'接收附件:"+transInfo.getSourceZJZ() );
				
				DataHandler fileHandler = transImpl.takeFile(transInfo.getSourceZJZ());
				BufferedInputStream bis = new BufferedInputStream(fileHandler.getInputStream());
				
				File outputFile = new File(Application.getInstance().getContextPath()+"WEB-INF/file/"+sourceXTDH+"/file_"+transInfo.getSourceZJZ());
				if(!outputFile.getParentFile().exists()){
					outputFile.getParentFile().mkdirs();
				}
				fos = new FileOutputStream(outputFile);
				byte[] data = new byte[10240];
				//
				int ret = bis.read(data);
				while(ret!=-1){
					fos.write(data);
				}
				//流
				fos.flush();
				fos.close();
				fos=null;
				logger.debug("		从系统'"+sourceXTDH+"'接收附件成功");
			}else{
				result = new JSONMessageResult("系统'"+sourceXTDH+"'的接口实现类不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("		从系统'"+sourceXTDH+"'接收附件失败:"+e.getMessage());
			result = new JSONMessageResult(e.getMessage());
		}finally{
			if(fos!=null){
				fos.close();
			}
		}
		return result;
	}

	//发送文件
	public JSONMessageResult transFile(ChuanShuJL transInfo) throws Exception{
		JSONMessageResult result = null;
		// 发送数据
		try {
			//取目标系统代号 获取对应的传输接口
			String targetXTDH = transInfo.getTargetXTDH();
			String sourceXTDH = transInfo.getSourceXTDH();
			TransInterface transImpl = this.getTransImpl(targetXTDH);
			if(transImpl!=null){
				logger.debug("		从系统'"+sourceXTDH+"'传输附件'"+transInfo.getSourceZJZ()+"'到系统'"+targetXTDH+"'开始" );
				File file = new File(Application.getInstance().getContextPath() +"WEB-INF/file/"+sourceXTDH+"/file_"+ transInfo.getSourceZJZ());
				if(file.exists()){
					result = transImpl.postFile(transInfo.getTargetZJZ(), file);
					logger.debug("		附件传输完成，目标系统返回值:"+result);
//				}else if(fj.getWenJianCD()==0){
//					logger.error("		文件不存在且大小为零，忽略此文件");
//					result = new JSONObject();
//					result.put("success", true);
//					result.put("errorMsg", "文件不存在或无文件名，且大小为零，忽略此文件");
				}else{
					logger.error("		附件传输失败:文件("+file.getName()+")不存在");
					result = new JSONMessageResult("文件("+file.getName()+")不存在");
				}
			}else{
				result = new JSONMessageResult("系统'"+targetXTDH+"'的接口实现类不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("		附件传输失败:"+e.getMessage());
			result = new JSONMessageResult(e.getMessage());
		}
		return result;
	}
	
	//发送任务 这里也会传递数据 主要用于流程检视变量的记录 最新数据在之前的数据传输中应该已经传递了
	public JSONMessageResult transTask(ChuanShuJL transInfo) throws Exception{
		JSONMessageResult result = null;
		// 发送数据
		try {
			String targetXTDH = transInfo.getTargetXTDH();
			String sourceXTDH = transInfo.getSourceXTDH();
			TransInterface transImpl = this.getTransImpl(targetXTDH);
			if(transImpl!=null){
				logger.debug("		从系统'"+sourceXTDH+"'传输任务'"+transInfo.getTargetGNDH()+"."+transInfo.getTargetCZDH()+"."+transInfo.getSourceZJZ()+"'到系统'"+targetXTDH+"'开始" );
				//替换数据中 关联对象的主键值为依赖的数据传输中 得到的目标系统主键值
				Map<String, Object> root = new HashMap<String, Object>();
				for(ChuanShuJLYL depend:transInfo.getYls()){
					ChuanShuJL yl = depend.getYiLaiCSJL();
					root.put(yl.getSourceSTLDH()+"_"+yl.getSourceZJZ(), yl.getTargetZJZ());
				}
				String jsonString =  transInfo.getJsonData();
				if(jsonString.indexOf("${")>=0){
					jsonString = FreemarkerUtils.processTemplate(jsonString, root);
				}
				
				//查找本次传输前 是否已成功传输并获取到目标系统的主键值
				Session sess = HibernateSessionFactory.getSession();
				Integer targetId = null;//不使用 transInfo中提供的TargetZJZ 以之前某次成功传输的数据记录中 记录的TargetZJZ为准
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
				if(hisTrans!=null){
					targetId = hisTrans.getTargetZJZ();
					
					result = transImpl.postTask(transInfo.getTargetGNDH(), transInfo.getTargetCZDH(), targetId, jsonString);
					logger.debug("		任务传输完成:目标系统返回值:"+result);
				}else{
					//对应的数据尚未传输成功 不能执行任务传输 (先数据 再任务)
					logger.error("		任务传输失败:依赖的数据尚未传输成功！");
					result = new JSONMessageResult("依赖的数据尚未传输成功！");
				}
			}else{
				result = new JSONMessageResult("系统'"+targetXTDH+"'的接口实现类不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("		任务传输失败:"+e.getMessage());
			result = new JSONMessageResult(e.getMessage());
		}
		return result;
	}

	private TransInterface getTransImpl(String xiTongDH){
		TransInterface impl = null;
		if(!transImpMap.containsKey(xiTongDH)){
			YingYongXT yyxt = (YingYongXT)HibernateSessionFactory.getSession().createCriteria(YingYongXT.class)
				.add(Restrictions.eq("yingYongXTBH", xiTongDH)).uniqueResult();
			if(yyxt.getJieKouSXL()!=null){
				try {
					impl = (TransInterface)(Class.forName(yyxt.getJieKouSXL()).newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
				transImpMap.put(xiTongDH, impl);
			}else{
				System.err.println("应用系统‘"+xiTongDH+"’定义中，未提供数据传输实现类名");
			}
		}else{
			impl = transImpMap.get(xiTongDH);
		}
		return impl;
	}
}
