package gov.usgs.wqp.shapefileconverter.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class TimeProfiler {
	private static Logger log;
	private static NumberFormat FORMATTER;
	private static Map<String, Long> timers;
	private static long averageInsertTime = 0;
	private static long averageRemoveTime = 0;
	
	static {
		log = ShapeFileUtils.getLogger(TimeProfiler.class);
		TimeProfiler.FORMATTER = new DecimalFormat("#0.000000000");
		TimeProfiler.timers = new HashMap<String, Long>();
		
		Map<String, Long> primer = new HashMap<String, Long>();		
		int count = 100;
		long insertTime = 0;
		for(int i = 0; i < count; i++) {
			long startTime = System.nanoTime();
			primer.put("Test", startTime);
			long endTime = System.nanoTime();
			
			primer.remove("Test");
			
			insertTime += (endTime - startTime);
		}
		TimeProfiler.averageInsertTime = insertTime/count;
		
		long removeTime = 0;
		for(int i = 0; i < count; i++) {
			primer.put("Test", removeTime);
			
			long startTime = System.nanoTime();
			primer.remove("Test");
			long endTime = System.nanoTime();
			
			removeTime += (endTime - startTime);
		}
		TimeProfiler.averageRemoveTime = removeTime/count;
		
		String msg = "TimeProfiler: Initializing with avgInsertTime [" + TimeProfiler.averageInsertTime + "ns] and avgRemoveTime [" + TimeProfiler.averageRemoveTime + "ns]";
		System.out.println(msg);
		log.info(msg);
	}
	
	public static void startTimer(String name) {
		long startTime = System.nanoTime();
		TimeProfiler.timers.put(name, startTime);
	}
	
	public static long endTimer(String name) {
		long endTime = System.nanoTime();
		
		long result = 0;
		
		Long startTime = TimeProfiler.timers.remove(name);
		if(startTime != null) {
			result = endTime - startTime;
			
			result -= TimeProfiler.averageInsertTime - TimeProfiler.averageRemoveTime;
		}
		
		return result;
	}
	
	public static long endTimer(String name, Logger log) {
		long endTime = System.nanoTime();
		
		long result = 0;
		
		Long startTime = TimeProfiler.timers.remove(name);
		if(startTime != null) {
			result = endTime - startTime;
			
			result -= TimeProfiler.averageInsertTime - TimeProfiler.averageRemoveTime;
		}
		
		String msg = "\n" + name + " [" + TimeProfiler.FORMATTER.format((result) / 1000000000d) + "] seconds";
		System.out.println(msg);
		log.info(msg);
		
		return result;
	}
	
	
}
