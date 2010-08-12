package foo;


public class Main {

	/**
	 * Inicializa o sistema.
	 * @param args parâmetros de execução. 
	 * 				1º parâmetro: diretório raiz / 2º parâmetro: arquivo de saída.
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