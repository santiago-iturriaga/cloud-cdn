import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.LinkedList;

public class Carga {

	// configuraciones 1
	static final public String DIRECTORIO_DE_INSTANCIAS = ".";
	static final public String NOMBRE_ARCHIVO_DE_DOCUMENTOS = "documentos.txt";
	static final public String SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS = " ";

	// configuraciones 2
	static final public Integer CANTIDAD_MAXIMA_DE_DOCUMENTOS = 1000;
	static final public Integer CANTIDAD_MAXIMA_DE_LINEAS_DE_ARCHIVO = 1000;

	// configuraciones 3
	static final public Boolean DEBUG = true;

	// auxiliares
	static Collection<String> lineasArchivo = null;

	// estructura
	static Documento[] documentos = new Documento[CANTIDAD_MAXIMA_DE_DOCUMENTOS];
	static Integer cantidadDocumentos = 0;

	public static void main(String args[]) {
		File miDir = new File(DIRECTORIO_DE_INSTANCIAS);
		try {

			if (DEBUG) {
				System.out.println("DIRECTORIO_DE_INSTANCIAS: "
						+ miDir.getCanonicalPath());
			}

			lineasArchivo = leerArchivo(DIRECTORIO_DE_INSTANCIAS+"/"+NOMBRE_ARCHIVO_DE_DOCUMENTOS);
			
			if (DEBUG) {
				System.out.println("LINEAS LEIDAS=" + lineasArchivo.size());
				for(String linea:lineasArchivo)	{
					System.out.println(linea);	
					
			    }
				System.out.println("FIN");	
			}
			
			cantidadDocumentos = lineasArchivo.size();
			int i = 0;
			for(String linea:lineasArchivo)	{
				documentos[i++]= new Documento(Integer.valueOf((linea.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),Integer.valueOf((linea.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]));	
		    }
			
			if (DEBUG) {
				System.out.println("IMPRIMIENDO DOCUMENTOS: ");
				for(int j = 0;j<cantidadDocumentos;j++)	{
					System.out.println(documentos[j].getDocId() + " "+ documentos[j].getDocSize());	
			    }
				
			}
						
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static public Collection<String> leerArchivo(String nombreCompletoArchivo) {

		Collection<String> lineas = new LinkedList<String>();

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			archivo = new File(nombreCompletoArchivo);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			String linea;
			// leo hasta que no hay nada o hasta una linea vacia 
			while ((linea = br.readLine()) != null && linea.length()!=0)
				lineas.add(linea); 
			
			
			return lineas;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return lineas;
	}

}

class Documento {
	Integer docId;
	Integer docSize;
	public Documento(Integer docId, Integer docSize) {
		super();
		this.docId = docId;
		this.docSize = docSize;
	}
	public Integer getDocId() {
		return docId;
	}
	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	public Integer getDocSize() {
		return docSize;
	}
	public void setDocSize(Integer docSize) {
		this.docSize = docSize;
	}
	
	
	
	

}