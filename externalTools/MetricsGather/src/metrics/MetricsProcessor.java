package metrics;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import foo.Log;

public class MetricsProcessor {	
	/**
	 * Identificador de anotação de métrica.
	 */
	public static String IDENTIFIER = "//@#$LPS-";
	
	/**
	 * Identificador de tipo anotação de métrica de granularidade.
	 */
	public static String GRAN_IDENTIFIER = "GranularityType";
	
	/**
	 * Identificador de tipo anotação de métrica de localozação.
	 */
	public static String LOCAL_IDENTIFIER = "Localization";
	
	/**
	 * Map para armazenar as métricas de granularidade.
	 */
	private Map<String, Integer> granMetrics;
	/**
	 * Map para armazenar as métricas de localização.
	 */
	private Map<String, Integer> localMetrics;
	
	/**
	 * Vetor que armazena as métricas processadas
	 */
	private Vector<Metric> metrics;
	
	/**
	 * Construtor padrão.
	 */
	public MetricsProcessor(){
		granMetrics = new HashMap<String, Integer>();
		localMetrics = new HashMap<String, Integer>();
		metrics = new Vector<Metric>();
	}
	
	/**
	 * Contabilizar as métricas por tipo.
	 * @param line linha lida da classe Java.
	 * @param metricType Tipo de métrica. {@link #GRAN_IDENTIFIER} {@link #LOCAL_IDENTIFIER}
	 */
	private void insertMetric(String line, String metricType) {
		Map<String, Integer> metricMap;
		if (MetricsProcessor.GRAN_IDENTIFIER.equals(metricType)) {
			metricMap = granMetrics;
		} else {
			metricMap = localMetrics;
		}
		
		Integer value = 1;
		if (metricMap.containsKey(line)) {
			value = metricMap.get(line);
			value++;						
		}
		metricMap.put(line, value);		
	}	
	
	/**
	 * Contabiliza a métrica encontrada na linha do arquivo Java. 
	 * @param line linha lida da classe Java.
	 */
	public void insertMetric(String line) {
		if (line.contains(MetricsProcessor.GRAN_IDENTIFIER)) {			
			insertMetric(line, MetricsProcessor.GRAN_IDENTIFIER);
		} else if (line.contains(MetricsProcessor.LOCAL_IDENTIFIER)) {
			insertMetric(line, MetricsProcessor.LOCAL_IDENTIFIER);
		} else {
			Log.info("Identificador inválido. Dados: "  + line);
		}
	}
	
	/**
	 * Retorna o índice da feature no Vetor de metricas. Caso a feature não exista no vetor, ela será inserida. 
	 * @param feature Nome da feature
	 * @return índice da feature no vetor de metricas
	 */
	private Integer getFeatureMetricIndex(String feature) {
		Integer index = -1;
		for (int i = 0; i < metrics.size(); i++) {
			Metric m = (Metric) metrics.get(i);
			if (m.getFeature().equals(feature)) {
				index = i;
			}			
		}
		if (index == -1) {
			metrics.add(new Metric(feature));
			index = metrics.size()-1;
		}
		return index;
	}
	
	public boolean processGatheredMetrics() {
		boolean result = this.processGatheredMetrics(granMetrics);
		result = result && this.processGatheredMetrics(localMetrics);
		return result;
	}
	
	private boolean processGatheredMetrics(Map<String, Integer> metricMap) {
		try {
			Set<String> keySet = metricMap.keySet();				
			 for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				 String key = iterator.next();  
				 if(key != null) {
					 Log.debug(key + " - " + metricMap.get(key));		
					 
					 String feature = key.substring(key.indexOf("-")+1, key.indexOf(":"));
					 String[] metricsTypeAndSubType = key.substring(key.indexOf(":")+1).split(":");				 
					 Integer featureIndex = getFeatureMetricIndex(feature);				 
					 Metric metric = metrics.get(featureIndex);
					 metric.storeMetric(MetricTypeEnum.getByIdentifier(metricsTypeAndSubType[0]), 
							 metricsTypeAndSubType[1], metricMap.get(key));
				 }
			 }
			 return true;
		} catch (Exception e) {
			Log.error(e.getMessage());
			return false;
		}
	}
	
	public void saveGatheredMetrics(String filename) {
		String separator = ",";
		try {
			 PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			 pw.println("FEATURE" + separator + "TIPO_METRICA" + separator + "METRICA" + separator + "VALOR");

			 for (Iterator<Metric> it = metrics.iterator(); it.hasNext();) {
				 Metric metric = (Metric) it.next();
				 
				 for (int i=0; i< MetricTypeEnum.values().length; i++) {
					 // processar métricas de granularidade
					 Set<String> keySet = metric.getSubMetric(MetricTypeEnum.getByOrd(i)).getValues().keySet();
					 for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
						 String key = iterator.next();  
						 if(key != null) {
							 StringBuilder textOutput = new StringBuilder();			
							 textOutput.append(metric.getFeature());
							 textOutput.append(separator);
							 textOutput.append(MetricTypeEnum.getByOrd(i).getMetricIdentifier());
							 textOutput.append(separator);
							 textOutput.append(key);
							 textOutput.append(separator);
							 textOutput.append(metric.getSubMetric(MetricTypeEnum.getByOrd(i)).getValues().get(key));
							 pw.println(textOutput);
						 }
					 }
					 pw.flush();
				 }
			 }
			 pw.close();

		} catch (FileNotFoundException e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
