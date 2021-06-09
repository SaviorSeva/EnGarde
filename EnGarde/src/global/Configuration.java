package global;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration {
	private static Configuration instance = null;
	Properties prop;
	
	public static InputStream charge(String nom) {
		// La méthode de chargement suivante ne dépend pas du système de fichier et sera
		// donc utilisable pour un .jar
		// Attention, par contre, le fichier doit se trouver dans le CLASSPATH
		InputStream is = null;
		try {
			is =  new FileInputStream(nom);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	private Configuration() {
		prop = new Properties();
		try {
			File file = new File("./res/defaut.cfg");
			InputStream propIn = new FileInputStream(file);
			prop.load(propIn);
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static Configuration instance() {
		if(instance == null) instance = new Configuration();
		return instance;
	}
	
	public String lis(String cle) throws NoSuchElementException{
		String res = prop.getProperty(cle);
		if(res == null) throw new NoSuchElementException("Prop " + cle + " not defined");
		return res;
	}
	
	public Logger logger() {
		Logger log = Logger.getLogger("Sokoban.Logger");
		return log;
	}
	
} 
