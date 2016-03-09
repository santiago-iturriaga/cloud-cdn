package jmetal.problems.cloudcdn.f201603;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNRRSolutionType;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.problems.cloudcdn.f201603.Documento;
import jmetal.problems.cloudcdn.f201603.QoS;
import jmetal.problems.cloudcdn.f201603.Region;
import jmetal.problems.cloudcdn.f201603.RegionDatacenter;
import jmetal.problems.cloudcdn.f201603.RegionUsuario;
import jmetal.problems.cloudcdn.f201603.Trafico;
import jmetal.problems.cloudcdn.f201603.TraficoComparator;
import jmetal.problems.cloudcdn.greedy.routing.RoutingAlgorithm;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/*
 * Problem class for solving the multi provider (broker) formulation (Cádiz 201603).
 * */
public abstract class CloudCDN_MP extends Problem {
	static final public long serialVersionUID = -6970983090454693518L;
	static final public Boolean DEBUG = true;
	static final public double DOC_SIZE_AMP = 1.0; // Amplifies the document size
	
	static final public Integer SECONDS_PER_TIMESTEP = 1;
	static final public Double CONTENT_SIZE = 1.3; // KS = 1.3 MB
	static final public Integer MAX_PROVIDER_TRANSFER = 3; // PN = 3 contents uploaded per time step
	static final public Integer STORAGE_RENTING_TIME = 3600 * 24 * 30; // Storage costs are considered monthly
	static final public Integer VM_RENTING_TIME = 3600 / SECONDS_PER_TIMESTEP; // CT = VMs are rented for 1 hour
	static final public Integer VM_PROCESSING = 100; // CR = VMs may serve up to 100 requests simultaneously
	static final public Integer TIME_HORIZON = 3600 * 24; // 1 day
	
	static final public Integer CANTIDAD_MAXIMA_DE_DOCUMENTOS = 30000;
	static final public Integer CANTIDAD_MAXIMA_DE_REGIONES = 10;
	static final public Integer CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS = 20;
	static final public Integer CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS = 100;
	static final public Integer CANTIDAD_MAXIMA_DE_TRAFICO = 100000;
	static final public Integer CANTIDAD_MAXIMA_DE_QOS = 1000;

	static final public String SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS = " ";

	static final public String NOMBRE_ARCHIVO_DE_DOCUMENTOS = "docs.0";
	static final public String NOMBRE_ARCHIVO_DE_REGIONES = "reg.0";
	static final public String NOMBRE_ARCHIVO_DE_DATACENTERS = "dc.0";
	static final public String NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS = "reg_users.0";
	static final public String NOMBRE_ARCHIVO_DE_TRAFICO = "workload.0";
	static final public String NOMBRE_ARCHIVO_DE_QOS = "qos.0";
	
	protected Integer num_provedores_;
	
	protected ArrayList<Documento> documentos_ = new ArrayList<Documento>(
			CANTIDAD_MAXIMA_DE_DOCUMENTOS);
	protected ArrayList<Region> regiones_ = new ArrayList<Region>(
			CANTIDAD_MAXIMA_DE_REGIONES);
	protected ArrayList<RegionDatacenter> regionesDatacenters_ = new ArrayList<RegionDatacenter>(
			CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS);
	protected ArrayList<RegionUsuario> regionesUsuarios_ = new ArrayList<RegionUsuario>(
			CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);
	protected ArrayList<Trafico> trafico_ = new ArrayList<Trafico>(
			CANTIDAD_MAXIMA_DE_TRAFICO);
	protected ArrayList<ArrayList<QoS>> qoS_ = new ArrayList<ArrayList<QoS>>(
			CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);

	protected int totalSimTimeSecs;
	protected double totalSimTimeHours;
	protected double totalSimTimeMonths;
	protected double totalSimTimeDays;

	public CloudCDN_MP(String solutionType, String pathName, int instanceNumber) {
		try {
			readProblem(pathName, instanceNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}

		regiones_.trimToSize();
		regionesDatacenters_.trimToSize();
		regionesUsuarios_.trimToSize();
		trafico_.trimToSize();
		qoS_.trimToSize();

		try {
			if (solutionType.compareTo("CloudCDNSolutionType") == 0) {
				solutionType_ = new CloudCDNSolutionType(this);
			} else if (solutionType.compareTo("CloudCDNRRSolutionType") == 0) {
				solutionType_ = new CloudCDNRRSolutionType(this);
			} else {
				throw new JMException("Solution type invalid");
			}
		} catch (JMException e) {
			e.printStackTrace();
		}

		totalSimTimeSecs = getTrafico().get(getTrafico().size() - 1).reqTime;
		totalSimTimeHours = totalSimTimeSecs / (60.0 * 60.0);
		totalSimTimeDays = totalSimTimeHours / 24.0;
		totalSimTimeMonths = totalSimTimeDays / 30.0;
	}

	public ArrayList<Documento> getDocumentos() {
		return documentos_;
	}

	public ArrayList<Region> getRegiones() {
		return regiones_;
	}

	public ArrayList<RegionDatacenter> getRegionesDatacenters() {
		return regionesDatacenters_;
	}

	public ArrayList<RegionUsuario> getRegionesUsuarios() {
		return regionesUsuarios_;
	}

	public ArrayList<Trafico> getTrafico() {
		return trafico_;
	}

	public ArrayList<ArrayList<QoS>> getQoS() {
		return qoS_;
	}

	public QoS getQoS(int regUsr, int regDC) {
		return qoS_.get(regUsr).get(regDC);
	}

	public int TotalSimTimeSecs() {
		return totalSimTimeSecs;
	}

	public double TotalSimTimeMonths() {
		return totalSimTimeMonths;
	}

	public void FixSolution(Solution solution) {
		for (int j = 0; j < getDocumentos().size(); j++) {
			int docCount;
			docCount = 0;

			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				try {
					docCount += CloudCDNSolutionType.GetDocumentVariables(
							solution, i).getValue(j);
				} catch (JMException e) {
					e.printStackTrace();
				}
			}

			if (docCount == 0) {
				int randDC;
				randDC = PseudoRandom.randInt(0, getRegionesDatacenters()
						.size() - 1);

				try {
					CloudCDNSolutionType.GetDocumentVariables(solution, randDC)
							.setValue(j, 1);
				} catch (JMException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public abstract void evaluateFinalSolution(Solution solution);

	public void readProblem(String pathName, int instanceNumber)
			throws IOException {
		try {
			File miDir = new File(pathName);

			if (DEBUG) {
				System.out.println("DIRECTORIO_DE_INSTANCIAS: "
						+ miDir.getCanonicalPath());
			}

			// ** CARGANDO DOCUMENTOS **//
			num_provedores_ = 1; // Temporalmente fijado en 1
			
			Collection<String> lineasArchivo = null;
			Path path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_DOCUMENTOS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				double docSizeMB;
				docSizeMB = DOC_SIZE_AMP
						* Double.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1])
						/ (1024 * 1024);
				
				int numContenidos = (int) Math.ceil(docSizeMB / CONTENT_SIZE);
				
				documentos_.add(new Documento(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						docSizeMB, numContenidos, 1)); // Prov. ID es siempre 1
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO DOCUMENTOS (TOP 10): ");
				for (int j = 0; j < documentos_.size() && j < 10; j++) {
					System.out.println(documentos_.get(j).getDocId() + " "
							+ documentos_.get(j).getDocSize() + " " + documentos_.get(j).getNumContenidos());
				}
			}

			// ** CARGANDO REGIONES **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_REGIONES);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				regiones_
						.add(new Region(
								Integer.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
								String.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1])));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO REGIONES: ");
				for (int j = 0; j < regiones_.size(); j++) {
					System.out.println(regiones_.get(j).getRegId() + " "
							+ regiones_.get(j).getRegNombre());
				}
			}

			// ** CARGANDO DATACENTERS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_DATACENTERS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				regionesDatacenters_
						.add(new RegionDatacenter(
								Integer.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
								String.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
								Integer.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]),
								Double.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3]) / STORAGE_RENTING_TIME,
								Double.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[4]),
								Double.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[5]) / VM_RENTING_TIME));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO REGIONES DATACENTERS: ");
				for (int j = 0; j < regionesDatacenters_.size(); j++) {
					System.out.println(regionesDatacenters_.get(j)
							.getRegDctId()
							+ " "
							+ regionesDatacenters_.get(j).getRegNombre()
							+ " "
							+ regionesDatacenters_.get(j).getRegId());
				}

			}

			// ** CARGANDO QOS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_QOS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				QoS q;
				q = new QoS(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]));

				if (q.regUsrId >= qoS_.size()) {
					qoS_.add(new ArrayList());
				}
				qoS_.get(q.regUsrId).add(q);
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO QOS: ");
				for (int j = 0; j < qoS_.size(); j++) {
					for (int i = 0; i < qoS_.get(j).size(); i++) {
						System.out.println(qoS_.get(j).get(i).getRegUsrId()
								+ " " + qoS_.get(j).get(i).getRegDocId() + " "
								+ qoS_.get(j).get(i).getQosMetric());
					}
				}
			}

			// ** CARGANDO REGIONES USUARIOS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				int regId;
				regId = Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]);

				regionesUsuarios_.add(new RegionUsuario(regId, String
						.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2])));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO REGIONES USUARIOS: ");
				for (int j = 0; j < regionesUsuarios_.size(); j++) {
					System.out.println(regionesUsuarios_.get(j).getRegUsrId()
							+ " " + regionesUsuarios_.get(j).getRegNombre()
							+ " " + regionesUsuarios_.get(j).getRegId());
				}
			}

			// ** CARGANDO TRAFICO **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_TRAFICO);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				double docSizeGB;
				docSizeGB = DOC_SIZE_AMP
						* Double.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2])
						/ (1024 * 1024 * 1024);

				int reqTime = (Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0])) % TIME_HORIZON;

				trafico_.add(new Trafico(
						reqTime,
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						docSizeGB,
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3])));
			}

			trafico_.sort(new TraficoComparator());

			if (DEBUG) {
				System.out.println("IMPRIMIENDO TRAFICO (top 10): ");
				for (int j = 0; j < trafico_.size() && j < 10; j++) {
					System.out.println(trafico_.get(j).getReqTime() + " "
							+ trafico_.get(j).getDocId() + " "
							+ trafico_.get(j).getDocSize() + " "
							+ trafico_.get(j).getRegUsrId());
				}
			}
		} catch (Exception e) {
			System.err.println("readProblem(): error when reading data file "
					+ e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	private Collection<String> leerArchivo(String nombreCompletoArchivo) {
		Collection<String> lineas = new LinkedList<String>();

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			archivo = new File(nombreCompletoArchivo);
			if (!archivo.exists()) {
				System.out.println("No existe el archivo "
						+ nombreCompletoArchivo);
				System.exit(0);
			}

			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			String linea;
			// leo hasta que no hay nada o hasta una linea vacia
			while ((linea = br.readLine()) != null && linea.length() != 0) {
				lineas.add(linea);
			}

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
