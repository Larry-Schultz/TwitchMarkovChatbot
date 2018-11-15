package com.catch42.Markov_Chatbot.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catch42.Markov_Chatbot.model.ApplicationStatistics;
import com.catch42.Markov_Chatbot.repository.ChannelTextRepository;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

@Service
public class StatisticsService {
	
	private static final Logger log_ = LoggerFactory.getLogger(StatisticsService.class);
	
	@Autowired
	private ChannelTextRepository channelTextRepository;
	
	private SystemInfo si;

	public StatisticsService() {
		this.si = new SystemInfo();
	}
	
	public ApplicationStatistics getApplicationStatistics() {
		HardwareAbstractionLayer hardware = this.si.getHardware();
		Double cpuLoad = this.getProcessorLoad(hardware.getProcessor());
		Double memoryUsage = this.getMemoryUsage(hardware.getMemory());
		Long totalMarkovChains = this.getTotalMarkovChains();
		
		DecimalFormat decimalFormat = new DecimalFormat("#.00"); 
		
		String cpuLoadString = null;
		String memoryUsageString = null;
		String totalMarkovChainsString = null;
		
		if(cpuLoad != null && cpuLoad > 0) {
			cpuLoadString = decimalFormat.format(cpuLoad) + "%";
		}
		if(memoryUsage != null && memoryUsage > 0) {
			memoryUsageString = decimalFormat.format(memoryUsage) + "%";
		}
		
		DecimalFormat decimalAndCommaFormat = new DecimalFormat("#,###");
		if(totalMarkovChains != null && totalMarkovChains > 0L) {
			totalMarkovChainsString = decimalAndCommaFormat.format(totalMarkovChains);
		}
		
		ApplicationStatistics applicationStatistics = new ApplicationStatistics(cpuLoadString, memoryUsageString, totalMarkovChainsString);
		
		return applicationStatistics;
	}
	
	public Double getProcessorLoad(CentralProcessor processor) {
		double processorLoad = processor.getSystemCpuLoadBetweenTicks() * 100;
		if(processorLoad > 0) {
			return processorLoad;
		} else {
			return null;
		}
	}
	
	public Double getMemoryUsage(GlobalMemory memory) {
		long availableMemory = memory.getAvailable();
		long totalMemory = memory.getTotal();
		
		if(availableMemory > 0 && totalMemory > 0) {
			BigDecimal availableMemoryDouble = new BigDecimal(availableMemory);
			BigDecimal totalMemoryDouble = new BigDecimal(totalMemory);
			BigDecimal percentAvailable = availableMemoryDouble.divide(totalMemoryDouble, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
			BigDecimal percentUsed = (new BigDecimal(100)).subtract(percentAvailable);
			return percentUsed.doubleValue();
		} else {
			return null;
		}
	}
	
	public Long getTotalMarkovChains() {
		List<Object> queryResults = this.channelTextRepository.getMarkovChainCount();
		Long count = (Long) queryResults.get(0);
		return count;
	}

}
