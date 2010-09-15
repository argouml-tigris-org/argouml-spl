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

import util.Log;


public class MetricsProcessor {	
	/**
	 * Identificador de anotação de métrica.
	 */
	public static final String IDENTIFIER = "//@#$LPS-";
	
	/**
	 * Informa se iniciou-se um comentário.
	 */
	private boolean startComment;
	
	/**
	 * Map para armazenar as métricas de granularidade.
	 */
	private Map<String, Integer> granMetrics;
	/**
	 * Map para armazenar as métricas de localização.
	 */
	private Map<String, Integer> localMetrics;	
	/**
	 * Map para armazenar outras métricas.
	 */
	private Map<String, Integer> otherMetrics;
	
	/**
	 * Vetor que armazena as métricas processadas
	 */
	private Vector<Metric> metrics;
	
	/** 
	 * Armazena métrica LOC.
	 */
	private Integer locMetric;
	
	/**
	 * Construtor padrão.
	 */
	public MetricsProcessor(){
		granMetrics = new HashMap<String, Integer>();
		localMetrics = new HashMap<String, Integer>();
		otherMetrics = new HashMap<String, Integer>();
		metrics = new Vector<Metric>();
		locMetric = new Integer(0);
		startComment = false;
	}
	
	/**
	 * Contabilizar as métricas por tipo.
	 * @param line linha lida da classe Java.
	 * @param metricType Tipo de métrica. {@link #GRAN_IDENTIFIER} {@link #LOCAL_IDENTIFIER}
	 */
	private void insertMetric(String line, MetricType metricType) {
		if (MetricType.LOC.equals(metricType)) {
			locMetric++;
		} else {		
			Map<String, Integer> metricMap;
			if (MetricType.GRANULARITY.equals(metricType)) {
				metricMap = granMetrics;
			} else if(MetricType.LOCALIZATION.equals(metricType)) {
				metricMap = localMetrics;
			} else {
				metricMap = otherMetrics;
			}

			Integer value = 1;
			if (metricMap.containsKey(line)) {
				value = metricMap.get(line);
				value++;						
			}
			metricMap.put(line, value);
		}
	}	
	
	/**
	 * Verifica se a linha é um comentário ou linha em branco
	 * @param line linha a ser verificada
	 * @return <code>true</code> se a linha for comentário ou branco,
	 * <code>false</code> caso contrário.
	 */
	private boolean isCommentOrBlankLine(String line) {
		if (line.startsWith("/*")) {
			if (line.endsWith("*/")) {
				return true;
			}
			startComment = true;
			return true;
		} else if (startComment && line.endsWith("*/")) {
			startComment = false;
			return true;
		}
		if (startComment) {
			return true;
		} else {
			return (line.startsWith("//") || line.startsWith("*") || line.isEmpty());
		}
	}
	
	/**
	 * Contabiliza a métrica encontrada na linha do arquivo Java. 
	 * @param line linha lida da classe Java.
	 */
	public void insertMetric(String line) {
		
		line = line.trim();
		// Common Metrics
		if (line.contains(MetricsProcessor.IDENTIFIER)) {
			if (line.contains(MetricType.GRANULARITY.getIdentifier())) {			
				insertMetric(line, MetricType.GRANULARITY);
			} else if (line.contains(MetricType.LOCALIZATION.getIdentifier())) {
				insertMetric(line, MetricType.LOCALIZATION);
			} else{
				Log.info("Identificador inválido. Dados: "  + line);
			}
		// LOC Metric
		} else if (!isCommentOrBlankLine(line)) {
			insertMetric(line, MetricType.LOC);
		// AND e OR Metrics
		} else if (line.matches("//#if defined\\(.*\\) (and|or) defined\\(.*\\)")) {
			String feature1 = line.substring(line.indexOf("(")+1, line.indexOf(")"));
			String feature2 = line.substring(line.lastIndexOf("(")+1, line.lastIndexOf(")"));
			String auxLine1 = "//@#$LPS-"+feature1+":%s:N/A";
			String auxLine2 = "//@#$LPS-"+feature2+":%s:N/A";
				
			if (line.toLowerCase().contains(MetricType.OR.getIdentifier().toLowerCase())) {				
				insertMetric(String.format(auxLine1, MetricType.OR.getIdentifier()), MetricType.OR);
				insertMetric(String.format(auxLine2, MetricType.OR.getIdentifier()), MetricType.OR);
			} else {
				insertMetric(String.format(auxLine1, MetricType.AND.getIdentifier()), MetricType.AND);
				insertMetric(String.format(auxLine2, MetricType.AND.getIdentifier()), MetricType.AND);				
			}
		} 
	}
	
	/**
	 * Retorna o índice da feature no Vetor de metricas. 
	 * Caso a feature não exista no vetor, ela será inserida. 
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
	
	/**
	 * Processa as métricas colhidas.
	 * @return <code>true</code> se processamento correto, <false> caso contrário.
	 */
	public boolean processGatheredMetrics() {
		boolean result = this.processGatheredMetrics(otherMetrics);
		result = result && this.processGatheredMetrics(granMetrics);
		result = result && this.processGatheredMetrics(localMetrics);
		return result;
	}
	
	/**
	 * Processa as métricas colhidas.
	 * @param metricMap Map contendo as métricas
	 * @return <code>true</code> se processamento correto, <false> caso contrário.
	 */
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
					 metric.storeMetric(MetricType.getByIdentifier(metricsTypeAndSubType[0]), 
							 metricsTypeAndSubType[1], metricMap.get(key));
				 }
			 }
			 return true;
		} catch (Exception e) {
			Log.error(e.getMessage());
			return false;
		}
	}
	
	/**
	 * Salva as métricas em arquivo, no formado CSV.
	 * @param filename nome do arquivo.
	 */
	public void saveGatheredMetrics(String filename) {
		String separator = ",";
		try {
			 PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			 pw.println("FEATURE" + separator + "TIPO_METRICA" + separator + "METRICA" + separator + "VALOR");
			 // Gravar LOC
			 StringBuilder textOutput = new StringBuilder();			
			 textOutput.append("TODAS");
			 textOutput.append(separator);
			 textOutput.append(MetricType.LOC.getIdentifier());
			 textOutput.append(separator);
			 textOutput.append("N/A");
			 textOutput.append(separator);
			 textOutput.append(this.locMetric);
			 pw.println(textOutput);

			 for (Iterator<Metric> it = metrics.iterator(); it.hasNext();) {
				 Metric metric = (Metric) it.next();
				 for (int i=0; i<MetricType.values().length-1; i++) {
					 Set<String> keySet = metric.getSubMetric(MetricType.values()[i]).getValues().keySet();
					 for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
						 String key = iterator.next();  
						 if(key != null) {
							 textOutput = new StringBuilder();			
							 textOutput.append(metric.getFeature());
							 textOutput.append(separator);
							 textOutput.append(MetricType.values()[i].getIdentifier());
							 textOutput.append(separator);
							 textOutput.append(key);
							 textOutput.append(separator);
							 textOutput.append(metric.getSubMetric(MetricType.values()[i]).getValues().get(key));
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
