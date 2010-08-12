package foo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import metrics.MetricsProcessor;


public class GatherMetrics {

	/**
	 * Diretório raiz (onde se inicia a varredura).
	 * */
	private String rootDir;
	
	private MetricsProcessor metricsProcessor;
	
	/**
	 * Filtro para não listar diretórios iniciados com '.'
	 * */
	FilenameFilter dirFilter = new FilenameFilter() { 
		public boolean accept(File dir, String name) {
			return (!name.startsWith(".") && !name.endsWith(".properties") && !name.endsWith(".xml")
					&& !name.endsWith(".launch") && !name.endsWith(".log") && !name.endsWith(".txt")
					&& !name.endsWith(".ico") && !name.endsWith(".bat") && !name.endsWith(".sh")
					&& !name.endsWith(".MF") && !name.endsWith(".ini") && !name.endsWith(".class")
					&& !name.endsWith(".java") && !name.startsWith("build") && !name.equals("META-INF") 
					&& !name.equals("lib") && !name.equals("bin") && !name.equals("templates")
					&& !name.equals("staging")); 
			} 
		};	 

	/**
	 * Filtro para listar apenas os arquivos .java
	 * */
	FilenameFilter javaFileFilter = new FilenameFilter() { 
		public boolean accept(File dir, String name) {
			return name.endsWith(".java");
			} 
		};	 
		
	/**
	 * Construtor padrão
	 * @param rootDir Diretório raiz (onde se inicia a varredura).
	 */
	public GatherMetrics(String rootDir) {
		this.rootDir = rootDir.replace("\\", File.separator);
		metricsProcessor = new MetricsProcessor();
	}
	
	/**
	 * Colhe as métricas e as salva em arquivo.
	 * @param filename Nome do arquivo que conterá as métricas
	 */
	public void gatherMetrics(String filename) {
		this.listDir(new File(rootDir));	
		metricsProcessor.processGatheredMetrics();
		metricsProcessor.saveGatheredMetrics(filename);
	}
	
	private void listDir(File dir) {
		if (dir.isDirectory()) {			
			listDirFiles(dir);
			String[] children = dir.list(dirFilter);  
		    for (int i=0; i<children.length; i++) {  
		    	listDir(new File(dir, children[i]));  
		    }
		}		
	}
	
	private void listDirFiles(File dir) {
		Log.debug(dir.toString());
		String[] children = dir.list(javaFileFilter);  
	    for (int i=0; i<children.length; i++) {  
	    	Log.debug(children[i]);
	    	processFile(new File(dir, children[i]));
	    }
	}
	
	private void processFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			while (br.ready()) {
				String line = br.readLine().trim();				
				if (line.contains(MetricsProcessor.IDENTIFIER)) {
					metricsProcessor.insertMetric(line);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		
	}	
}
 