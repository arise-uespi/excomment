package IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import suporte.Constantes;
import suporte.Variaveis;

public class Props {
	public boolean loadProperties() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		p.load(new FileInputStream("user.props"));

		Variaveis.host = p.getProperty(Constantes.HOST);
		Variaveis.port = p.getProperty(Constantes.PORT);
		Variaveis.database = p.getProperty(Constantes.DATABASE);
		Variaveis.user = p.getProperty(Constantes.USER);
		Variaveis.password = p.getProperty(Constantes.PASSWORD);
		return true;
	}

	public void write(Map<String, String> map) throws IOException {
		Properties p = new Properties();

		File file = new File("user.props");
		if(!file.exists()) {
			file.createNewFile();
		} 

		p.load(new FileInputStream(file));
		p.putAll(map);

		FileOutputStream out = new FileOutputStream(file, false);
		p.store(out, "/* properties updated */");
	}
}