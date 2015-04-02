package com.poweruniverse.app.servlet;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.poweruniverse.app.job.TransJob;


@SuppressWarnings("deprecation")
public class ScheduleTaskServlet extends HttpServlet implements  SingleThreadModel {
	private static final long serialVersionUID = 1L;
	public static Scheduler scheduler = null;
//	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public void destroy() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			scheduler = sf.getScheduler();

			// 执行数据接口传输
			int transStartDelaySec = 10;// 启动后20秒开始执行
			int transScheduleDelaySec = 20;// 执行间隔300秒
			// 为TransJob的静态变量赋值
//			TransJob.setContextPath(getServletContext().getRealPath("/"));
			
			JobDetail transDetail = JobBuilder.newJob(TransJob.class)
					.withIdentity("trans", Scheduler.DEFAULT_GROUP)
					.build();
			
			Calendar transCalendar = Calendar.getInstance();
			transCalendar.add(Calendar.SECOND, transStartDelaySec);
			SimpleTrigger fileConverTrigger = TriggerBuilder
					.newTrigger()
					.withIdentity("trans", Scheduler.DEFAULT_GROUP)
					.startAt(transCalendar.getTime())
					.withSchedule(
							SimpleScheduleBuilder.repeatSecondlyForever(transScheduleDelaySec))
					.build();
			scheduler.scheduleJob(transDetail, fileConverTrigger);

//
			scheduler.start();
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
	}

}
