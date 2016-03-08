package jmetal.problems.cloudcdn;

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
import jmetal.problems.cloudcdn.greedy.routing.RoutingAlgorithm;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public abstract class CloudCDN_base extends Problem {
	static final public long serialVersionUID = -6970983090454693518L;
	static final public Boolean DEBUG = true;
	static final public Double QOS_THRESHOLD = 1.0;
	static final public double ALPHA = 1.0;
	static final public double DOC_SIZE_AMP = 140.0;

	static final public Integer CANTIDAD_MAXIMA_DE_DOCUMENTOS = 100000;
	static final public Integer CANTIDAD_MAXIMA_DE_REGIONES = 100;
	static final public Integer CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS = 50;
	static final public Integer CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS = 500;
	static final public Integer CANTIDAD_MAXIMA_DE_TRAFICO = 100000;
	static final public Integer CANTIDAD_MAXIMA_DE_QOS = 5000;
	static final public Integer CANTIDAD_MAXIMA_DE_MAQUINAS = 50;

	static final public String SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS = " ";

	static final public String NOMBRE_ARCHIVO_DE_DOCUMENTOS = "docs.0";
	static final public String NOMBRE_ARCHIVO_DE_REGIONES = "reg.0";
	static final public String NOMBRE_ARCHIVO_DE_DATACENTERS = "dc.0";
	static final public String NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS = "reg_users.0";
	static final public String NOMBRE_ARCHIVO_DE_TRAFICO = "workload.0";
	static final public String NOMBRE_ARCHIVO_DE_QOS = "qos.0";
	static final public String NOMBRE_ARCHIVO_DE_MAQUINAS = "vm.0";
	static final public String NOMBRE_ARCHIVO_DE_DATACENTERS_MAQUINAS = "vm_dc.0";

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
	protected ArrayList<Maquina> maquinas_ = new ArrayList<Maquina>(
			CANTIDAD_MAXIMA_DE_MAQUINAS);

	protected ArrayList<ArrayList<QoS>> qoS_ = new ArrayList<ArrayList<QoS>>(
			CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);

	protected int totalSimTimeSecs;
	protected double totalSimTimeHours;
	protected double totalSimTimeMonths;
	protected double totalSimTimeDays;

	protected int startTrainingSecs, endTrainingSecs;
	protected double startTrainingHours, endTrainingHours;
	protected double startTrainingDays, endTrainingDays;
	protected double startTrainingMonths, endTrainingMonths;

	protected int startEvalSecs, endEvalSecs;
	protected double startEvalHours, endEvalHours;
	protected double startEvalDays, endEvalDays;
	protected double startEvalMonths, endEvalMonths;

	public CloudCDN_base(String solutionType, String pathName,
			int instanceNumber, boolean twophase) {
		try {
			readProblem(pathName, instanceNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}

		documentos_.trimToSize();
		regiones_.trimToSize();
		regionesDatacenters_.trimToSize();
		regionesUsuarios_.trimToSize();
		trafico_.trimToSize();
		qoS_.trimToSize();
		maquinas_.trimToSize();

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

		if (twophase) {
			startEvalSecs = totalSimTimeSecs / 2;
			startEvalHours = totalSimTimeHours / 2.0;
			startEvalDays = totalSimTimeDays / 2.0;
			startEvalMonths = totalSimTimeMonths / 2.0;

			endEvalSecs = totalSimTimeSecs;
			endEvalHours = totalSimTimeHours;
			endEvalDays = totalSimTimeDays;
			endEvalMonths = totalSimTimeMonths;

			startTrainingSecs = 0;
			startTrainingHours = 0;
			startTrainingDays = 0;
			startTrainingMonths = 0;

			endTrainingSecs = totalSimTimeSecs / 2;
			endTrainingHours = totalSimTimeHours / 2.0;
			endTrainingDays = totalSimTimeDays / 2.0;
			endTrainingMonths = totalSimTimeMonths / 2.0;
		} else {
			startEvalSecs = 0;
			startEvalHours = 0;
			startEvalDays = 0;
			startEvalMonths = 0;

			endEvalSecs = totalSimTimeSecs;
			endEvalHours = totalSimTimeHours;
			endEvalDays = totalSimTimeDays;
			endEvalMonths = totalSimTimeMonths;

			startTrainingSecs = 0;
			startTrainingHours = 0;
			startTrainingDays = 0;
			startTrainingMonths = 0;

			endTrainingSecs = totalSimTimeSecs;
			endTrainingHours = totalSimTimeHours;
			endTrainingDays = totalSimTimeDays;
			endTrainingMonths = totalSimTimeMonths;
		}
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

	public ArrayList<Maquina> getMaquinas() {
		return maquinas_;
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

	public int StartTrainingSecs() {
		return startTrainingSecs;
	}

	public int EndTrainingSecs() {
		return endTrainingSecs;
	}

	public double StartTrainingMonths() {
		return startTrainingMonths;
	}

	public double EndTrainingMonths() {
		return endTrainingMonths;
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
			Collection<String> lineasArchivo = null;
			Path path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_DOCUMENTOS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				double docSizeGB;
				docSizeGB = DOC_SIZE_AMP
						* Double.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1])
						/ (1024 * 1024 * 1024);

				documentos_.add(new Documento(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						docSizeGB));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO DOCUMENTOS (TOP 10): ");
				for (int j = 0; j < documentos_.size() && j < 10; j++) {
					System.out.println(documentos_.get(j).getDocId() + " "
							+ documentos_.get(j).getDocSize());
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
								String.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3]),
								String.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[4])));
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

			ArrayList<Double> qosMedian = new ArrayList<Double>();
			for (int i = 0; i < qoS_.size(); i++) {
				ArrayList<Integer> aux;
				aux = new ArrayList<Integer>(qoS_.get(i).size());

				for (int j = 0; j < qoS_.get(i).size(); j++) {
					aux.add(qoS_.get(i).get(j).qosMetric);
				}

				aux.sort(null);
				qosMedian.add(((int) aux.get(aux.size() / 2)) * ALPHA);
			}

			// ** CARGANDO REGIONES USUARIOS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				int regId;
				regId = Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]);

				double qosThreshold;
				qosThreshold = (double) qosMedian.get(regId);

				regionesUsuarios_.add(new RegionUsuario(regId, String
						.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]),
						qosThreshold));
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

				int reqTime = Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]);

				// Mecanismo trucho para generar más carga.
				for (int truch = 0; truch < 1; truch++) {
					trafico_.add(new Trafico(
							reqTime,
							Integer.valueOf((linea
									.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
							docSizeGB,
							Integer.valueOf((linea
									.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3])));
				}
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

			// ** CARGANDO MAQUINAS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_MAQUINAS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				maquinas_
						.add(new Maquina(
								Integer.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
								String.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
								Integer.valueOf((linea
										.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2])));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO QOS: ");
				for (int j = 0; j < maquinas_.size(); j++) {
					System.out.println(maquinas_.get(j).getVmId() + " "
							+ maquinas_.get(j).getVmNombre() + " "
							+ maquinas_.get(j).getBandwidth()

					);
				}
			}

			// ** CARGANDO DATACENTERS MAQUINAS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_DATACENTERS_MAQUINAS);
			lineasArchivo = leerArchivo(path.toString());

			for (String linea : lineasArchivo) {
				int dcId;
				dcId = Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]);

				int vmId;
				vmId = Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]);

				double cost;
				cost = Double.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]);

				regionesDatacenters_.get(dcId).setVMCost(vmId, cost);
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO DATACENTERS MAQUINAS: ");
				for (int j = 0; j < regionesDatacenters_.size(); j++) {
					regionesDatacenters_.get(j).printVmCosts();
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
