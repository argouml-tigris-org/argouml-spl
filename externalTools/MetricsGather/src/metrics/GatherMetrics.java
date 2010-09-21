package metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import util.Log;




public class GatherMetrics {

	/**
	 * Diret�rio raiz (onde se inicia a varredura).
	 * */
	private String rootDir;
	
	private MetricsProcessor metricsProcessor;
	
	/**
	 * Filtro de diret�rios.
	 * */
	FilenameFilter dirFilter = new FilenameFilter() { 
		public boolean accept(File dir, String name) {
			name = name.toLowerCase();
			return (!name.startsWith(".") && !name.startsWith("build")
					&& !name.endsWith(".properties") && !name.endsWith(".xml")
					&& !name.endsWith(".launch") && !name.endsWith(".log") && !name.endsWith(".txt")
					&& !name.endsWith(".ico") && !name.endsWith(".bat") && !name.endsWith(".sh")
					&& !name.endsWith(".mf") && !name.endsWith(".ini") && !name.endsWith(".class")
					&& !name.endsWith(".java") && !name.endsWith(".html") && !name.endsWith(".gif") 
					&& !name.equals("meta-inf")
					&& !name.equals("lib") && !name.equals("bin") && !name.equals("templates")
					&& !name.equals("staging") && !name.equals("tests") && !name.equals("argouml-build")
					&& !name.equals("argouml-core-tools") && !name.equals("argouml-core-infra") 
					);
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
	 * Construtor padr�o
	 * @param rootDir Diret�rio raiz (onde se inicia a varredura).
	 */
	public GatherMetrics(String rootDir) {
		this.rootDir = rootDir.replace("\\", File.separator);
		metricsProcessor = new MetricsProcessor();
	}
	
	/**
	 * Colhe as m�tricas e as salva em arquivo.
	 * @param filename Nome do arquivo que conter� as m�tricas
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
				metricsProcessor.insertMetric(br.readLine().trim());
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
 