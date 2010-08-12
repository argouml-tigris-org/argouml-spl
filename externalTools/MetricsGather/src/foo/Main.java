package foo;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String outputFilename = "..\\metrics.csv"; 
		Log.info("Starting gattering metrics");
		
		GatherMetrics rd = new GatherMetrics("..\\..\\workspace-argouml-spl"); 
		rd.gatherMetrics(outputFilename);	
		
		Log.info("End of gattering metrics. File " + outputFilename + " generated.");
	}

}
