package com.wcs.ncp.concurrent;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.wcs.base.util.BtcLogger;

import edu.emory.mathcs.backport.java.util.Collections;

@Startup
@Singleton
public class ThreadPool {
	private final ThreadPoolExecutor executor;

	private final ScheduledExecutorService timer;

	private final BtcLogger logger = new BtcLogger("tp");

	public ThreadPool() {
		logger.info(" Initializing ThreadPool in " + Thread.currentThread());
		// 初始2个线程，最大5个，超过2个后，空闲的线程会在30秒后结束, 阻塞队列的长度为5，超过就会阻塞
		executor = new ThreadPoolExecutor(2, 5, 30l, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
		// 一开始将 启动2个线程（corePoolSize）
		executor.prestartAllCoreThreads();
		timer = Executors.newScheduledThreadPool(1);
		// 启动后10分钟执行，以后每3个小时执行一次
		timer.scheduleAtFixedRate(new JVMMonitorTask(), 10 * 60, 3 * 60 * 60, TimeUnit.SECONDS);
	}

	/**
	 * 应用关闭的时候关闭线程池
	 */
	@PreDestroy
	public void shutdown() {
		executor.shutdown();
	}

	/**
	 * 执行线程
	 * 
	 * @param r
	 * @return
	 */
	public void execute(Runnable r) {
		logger.info("pool size :" + executor.getPoolSize() + ", queue size : " + executor.getQueue().size()
				+ ", completed task : " + executor.getCompletedTaskCount());
		executor.execute(r);
	}

	private class JVMMonitorTask implements Runnable {

		/**
		 * 监控线程的相关信息
		 */
		private String monitorThread() {
			ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
			long[] tids = threadBean.getAllThreadIds();
			String str = "Thread count :" + threadBean.getThreadCount() + " ,DaemonThreadCount :"
					+ threadBean.getDaemonThreadCount() + " ,PeakThreadCount :" + threadBean.getPeakThreadCount()
					+ " ,TotalStartedThreadCount :" + threadBean.getTotalStartedThreadCount() + "\n";

			List<String> list = new ArrayList<String>(tids.length);

			for (long tid : tids) {
				ThreadInfo info = threadBean.getThreadInfo(tid);
				list.add(info.getThreadName());
			}

			Collections.sort(list);
			for (String s : list) {
				str = str + "thread name : " + s + "\n";
			}

			return str;
		}

		/**
		 * 监控编译的信息
		 */
		private String monitorCompilation() {
			CompilationMXBean cbean = ManagementFactory.getCompilationMXBean();
			return "TotalCompilationTime is " + cbean.getTotalCompilationTime();
		}

		/**
		 * 监控垃圾收集的信息
		 */
		private String monitorGarbageCollection() {
			List<GarbageCollectorMXBean> gbeans = ManagementFactory.getGarbageCollectorMXBeans();
			String countInfo = "";
			for (GarbageCollectorMXBean gbean : gbeans) {
				countInfo += "Garbage collection count of " + gbean.getName() + " is :" + gbean.getCollectionCount()
						+ " , memory pool name :" + Arrays.toString(gbean.getMemoryPoolNames()) + "\n";

			}
			return countInfo;
		}

		/**
		 * 监控内存信息
		 */
		private String monitorMemory() {
			// MemoryMXBean 相关
			MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			MemoryUsage mu = mbean.getHeapMemoryUsage();
			MemoryUsage mu1 = mbean.getNonHeapMemoryUsage();
			String info = "";
			info = "heap memory usage init : " + mu.getInit() + ", max :" + mu.getMax() + ", used :" + mu.getUsed();
			info = info + "\n" + "nonheap memory usage init : " + mu1.getInit() + ", max :" + mu1.getMax() + ", used :"
					+ mu1.getUsed();
			// MemoryManagerMXBean 相关
			List<MemoryManagerMXBean> mmbeans = ManagementFactory.getMemoryManagerMXBeans();
			String mmbeanName = "";
			String mpName = "";
			for (MemoryManagerMXBean mmbean : mmbeans) {
				mmbeanName += mmbean.getName() + ";";
				mpName += Arrays.toString(mmbean.getMemoryPoolNames()) + ";";
			}
			info += "\nMemoryManagerMXBean names is :" + mmbeanName;
			info += "\nMemoryPoolNames is :" + mpName;
			// MemoryPoolMXBean 相关
			List<MemoryPoolMXBean> mpbeans = ManagementFactory.getMemoryPoolMXBeans();
			String poolName = "";
			for (MemoryPoolMXBean mpbean : mpbeans) {
				poolName = poolName + mpbean.getName() + ";";
			}
			info += "\nMemoryPool name :" + poolName;
			return info;
		}

		/**
		 * 监控操作系统的信息
		 */
		private String monitorOperatingSystem() {
			OperatingSystemMXBean obean = ManagementFactory.getOperatingSystemMXBean();
			return "OS arch :" + obean.getArch() + " , OS version : " + obean.getVersion() + " ,OS Name :"
					+ obean.getName() + " , AvailableProcessors :" + obean.getAvailableProcessors()
					+ " ,SystemLoadAverage :" + obean.getSystemLoadAverage();
		}

		/**
		 * 监控运行期属性
		 */
		private String monitorRuntime() {
			RuntimeMXBean rbean = ManagementFactory.getRuntimeMXBean();
			return "InputArguments  >> " + rbean.getInputArguments() + " \nclasspath >> " + rbean.getClassPath()
					+ " \nUp time >> " + rbean.getUptime() + " \nLibraryPath >>  " + rbean.getLibraryPath()
					+ " \nManagementSpecVersion >> " + rbean.getManagementSpecVersion() + " \nRuntime bean name >> "
					+ rbean.getName() + " \nSpecName >> " + rbean.getSpecName() + " \nSpec Vendor >> "
					+ rbean.getSpecVendor() + " \nSpecVension >> " + rbean.getSpecVersion() + " \nBootClassPath >> "
					+ rbean.getBootClassPath() + " \nStart time >> " + rbean.getStartTime() + " \nVmName >> "
					+ rbean.getVmName() + " \nVmVendor >> " + rbean.getVmVendor() + " \nVmVersion>> "
					+ rbean.getVmVersion() + " \nSystemProperties>> " + rbean.getSystemProperties();
		}

		/**
		 * 统计classload 的信息
		 */
		private String monitorClassLoading() {
			ClassLoadingMXBean cbean = ManagementFactory.getClassLoadingMXBean();
			return "LoadedClassCount :" + cbean.getLoadedClassCount() + " ,TotalLoadedClassCount :"
					+ cbean.getTotalLoadedClassCount() + " ,UnloadedClassCount :" + cbean.getUnloadedClassCount();

		}

		@Override
		public void run() {
			String info = "";
			info += "\n" + monitorThread();
			info += "\n" + monitorClassLoading();
			info += "\n" + monitorCompilation();
			info += "\n" + monitorGarbageCollection();
			info += "\n" + monitorMemory();
			info += "\n" + monitorOperatingSystem();
			info += "\n" + monitorRuntime();
			logger.info("\n\nJMX Bean Info :" + info);
		}

	}

	/**
	 * 执行线程，并得到返回的结果
	 * 
	 * @param c
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, String> submit(Callable<Map<String, String>> c) throws InterruptedException, ExecutionException {
		Future<Map<String, String>> f = executor.submit(c);
		return f.get();
	}
}
