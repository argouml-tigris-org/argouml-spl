package metrics;

public enum MetricTypeEnum {
	GRAN(MetricsProcessor.GRAN_IDENTIFIER), LOCAL(MetricsProcessor.LOCAL_IDENTIFIER), LOC(MetricsProcessor.LOC_METRIC);
	
	 /** Origem da venda.*/
    private final String metricIdentifier;

    /**
     * Construtor protegido.
     * @param saleOrigin origem da venda.
     */
    private MetricTypeEnum(String metricIdentifier) {
        this.metricIdentifier = metricIdentifier;
    }

    /**
     * Retorna a origem da venda.
     * @return saleOrigin.
     */
    public String getMetricIdentifier() {
        return this.metricIdentifier;
    }
    
    public static MetricTypeEnum getByIdentifier(String metricIdentifier) {
    	if (MetricTypeEnum.GRAN.getMetricIdentifier().equals(metricIdentifier)) {
    		return MetricTypeEnum.GRAN;
    	} else if (MetricTypeEnum.LOCAL.getMetricIdentifier().equals(metricIdentifier)) {
    		return MetricTypeEnum.LOCAL;
    	} else {
    		return MetricTypeEnum.LOC;
    	}
    }

    public static MetricTypeEnum getByOrd(Integer ord) {    
    	if (MetricTypeEnum.GRAN.ordinal() == ord) {
    		return MetricTypeEnum.GRAN;
    	} else if (MetricTypeEnum.LOCAL.ordinal() == ord) {
    		return MetricTypeEnum.LOCAL;
    	} else {
    		return MetricTypeEnum.LOC;
    	}    		
    }
    
}
