package metrics;

public class Metric {
	
	/**
	 * Nome da feature a qual se refere a metrica;
	 */
	private final String feature;
	
	/**
	 * Array contendo o tipo de métrica: Granularidade ou Localização.
	 */
	private SubMetric[] subMetrics;	
	
	/**
	 * Retorna o nome da feature
	 * @return Nome da feature.
	 */
	public String getFeature() {
		return feature;
	}

	public Metric(String feature) {
		this.subMetrics = new SubMetric[2];
		this.subMetrics[MetricTypeEnum.GRAN.ordinal()] = new SubMetric();
		this.subMetrics[MetricTypeEnum.LOCAL.ordinal()] = new SubMetric();
		
		this.feature = feature;
	}
	
	public SubMetric getSubMetric(MetricTypeEnum typeEnum) {
		return this.subMetrics[typeEnum.ordinal()];
	}	
	
	public void storeMetric(MetricTypeEnum metricType, String subMetric, Integer value) {
		this.subMetrics[metricType.ordinal()].addValue(subMetric, value);
		
	}
	
}
