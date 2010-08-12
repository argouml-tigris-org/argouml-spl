package foo;


public class Main {

	/**
	 * Inicializa o sistema.
	 * @param args par�metros de execu��o. 
	 * 				1� par�metro: diret�rio raiz / 2� par�metro: arquivo de sa�da.
	 */
	public static void main(String[] args) {
		Log.info("Starting gattering metrics");
		if (args.length > 0) {
			GatherMetrics rd = new GatherMetrics(args[0]);			
			rd.gatherMetrics(args[1]);
			Log.info("End of gattering metrics. File " + args[1] + " generated.");
		} else {
			Log.info("No args found.");
		}		
	}
}