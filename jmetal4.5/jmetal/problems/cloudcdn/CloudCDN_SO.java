package jmetal.problems.cloudcdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.problems.cloudcdn.greedy.routing.BestQoS;
import jmetal.problems.cloudcdn.greedy.routing.Cheapest;
import jmetal.problems.cloudcdn.greedy.routing.RandomRouting;
import jmetal.problems.cloudcdn.greedy.routing.RoutingAlgorithm;
import jmetal.problems.cloudcdn.greedy.routing.SimpleRR;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class CloudCDN_SO extends Problem {
	private static final long serialVersionUID = -6970983090454693518L;
	static final public Boolean DEBUG = true;
	static final public Double QOS_THRESHOLD = 1.0;
	static final public double ALPHA = 0.9;
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

	double[] contentsLowerLimits_;
	double[] contentsUpperLimits_;
	double[] vmTypesLowerLimits_;
	double[] vmTypesUpperLimits_;

	private ArrayList<Documento> documentos_ = new ArrayList<Documento>(
			CANTIDAD_MAXIMA_DE_DOCUMENTOS);
	private ArrayList<Region> regiones_ = new ArrayList<Region>(
			CANTIDAD_MAXIMA_DE_REGIONES);
	private ArrayList<RegionDatacenter> regionesDatacenters_ = new ArrayList<RegionDatacenter>(
			CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS);
	private ArrayList<RegionUsuario> regionesUsuarios_ = new ArrayList<RegionUsuario>(
			CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);
	private ArrayList<Trafico> trafico_ = new ArrayList<Trafico>(
			CANTIDAD_MAXIMA_DE_TRAFICO);
	private ArrayList<Maquina> maquinas_ = new ArrayList<Maquina>(
			CANTIDAD_MAXIMA_DE_MAQUINAS);

	private ArrayList<ArrayList<QoS>> qoS_ = new ArrayList<ArrayList<QoS>>(
			CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);

	private RoutingAlgorithm routingAlgorithm_;

	int totalSimTimeSecs;
	double totalSimTimeHours;
	double totalSimTimeMonths;
	double totalSimTimeDays;

	int startTrainingSecs, endTrainingSecs;
	double startTrainingHours, endTrainingHours;
	double startTrainingDays, endTrainingDays;
	double startTrainingMonths, endTrainingMonths;

	int startEvalSecs, endEvalSecs;
	double startEvalHours, endEvalHours;
	double startEvalDays, endEvalDays;
	double startEvalMonths, endEvalMonths;

	public CloudCDN_SO(String solutionType) {
		this(solutionType, "test/", 0, "RandomRouting", false);
	}

	public CloudCDN_SO(String solutionType, String pathName,
			int instanceNumber, String routingAlgorithm, boolean twophase) {
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

		numberOfVariables_ = regionesDatacenters_.size()
				+ (regionesDatacenters_.size() * 24); // 1 document assignment +
														// DC*24 hours VM
														// assignment
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 2;

		problemName_ = "CloudCDN_SO";

		length_ = new int[numberOfVariables_];
		for (int var = 0; var < regionesDatacenters_.size(); var++) {
			length_[var] = getDocumentos().size();
		}

		for (int var_dc = 0; var_dc < regionesDatacenters_.size(); var_dc++) {
			for (int var_hr = 0; var_hr < 24; var_hr++) {
				length_[var_dc * 24 + var_hr + regionesDatacenters_.size()] = getMaquinas()
						.size();
			}
		}

		contentsLowerLimits_ = new double[getDocumentos().size()];

		for (int i = 0; i < getDocumentos().size(); i++) {
			contentsLowerLimits_[i] = 0;
		}

		contentsUpperLimits_ = new double[getDocumentos().size()];

		for (int i = 0; i < getDocumentos().size(); i++) {
			contentsUpperLimits_[i] = 1;
		}

		vmTypesLowerLimits_ = new double[getMaquinas().size()];

		for (int i = 0; i < getMaquinas().size(); i++) {
			vmTypesLowerLimits_[i] = 0;
		}

		vmTypesUpperLimits_ = new double[getMaquinas().size()];

		for (int i = 0; i < getMaquinas().size(); i++) {
			vmTypesUpperLimits_[i] = 15; // getTrafico().size(); // Big enough
											// value.
		}

		if (routingAlgorithm.compareTo("SimpleRR") == 0) {
			routingAlgorithm_ = new SimpleRR(this);
		} else if (routingAlgorithm.compareTo("Cheapest") == 0) {
			routingAlgorithm_ = new Cheapest(this);
		} else if (routingAlgorithm.compareTo("BestQoS") == 0) {
			routingAlgorithm_ = new BestQoS(this);
		} else if (routingAlgorithm.compareTo("RandomRouting") == 0) {
			routingAlgorithm_ = new RandomRouting(this);
		}

		try {
			if (solutionType.compareTo("CloudCDNSolutionType") == 0)
				solutionType_ = new CloudCDNSolutionType(this);
			else {
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

	public double[] getNumberOfContentsLowerLimits() {
		return contentsLowerLimits_;
	}

	public double[] getNumberOfContentsUpperLimits() {
		return contentsUpperLimits_;
	}

	public double[] getNumberOfVMTypesLowerLimits() {
		return vmTypesLowerLimits_;
	}

	public double[] getNumberOfVMTypesUpperLimits() {
		return vmTypesUpperLimits_;
	}

	public double[] getContentsLowerLimits() {
		return contentsLowerLimits_;
	}

	public double[] getContentsUpperLimits() {
		return contentsUpperLimits_;
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

	public int getTotalNumVM(Solution solution) {
		int total = 0;

		for (int i = 0; i < getRegionesDatacenters().size(); i++) {
			for (int h = 0; h < 24; h++) {
				for (int j = 0; j < getMaquinas().size(); j++) {
					try {
						total += CloudCDNSolutionType.GetVMVariables(solution,
								i, h).getValue(j);
					} catch (JMException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return total;
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

	public void evaluateFinalSolution(Solution solution) {
		double fitness = 0.0;
		double storageCost = 0.0;
		double machineCost = 0.0;
		double trafficCost = 0.0;

		System.out.println(">> [INFO] Routing algorithm: "
				+ routingAlgorithm_.getClass().toString());

		System.out.println(">> [INFO] Expected fitness: "
				+ solution.getObjective(0) + " [Penalty: "
				+ solution.getOverallConstraintViolation() + "]");

		try {
			System.out.println(">> Storage >>");
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				double dataSize = 0.0;

				for (int j = 0; j < getDocumentos().size(); j++) {
					if (CloudCDNSolutionType.GetDocumentVariables(solution, i)
							.getValue(j) == 1) {
						dataSize += getDocumentos().get(j).docSize;
					}
				}

				System.out.println("DC " + i + " = " + dataSize + " GB");
				
				storageCost += getRegionesDatacenters().get(i)
						.computeStorageCost(dataSize);
			}

			System.out.println(">> VM >>");
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				int total_horas;
				total_horas = 0;
				
				for (int h = 0; h < 24; h++) {
					for (int j = 0; j < getMaquinas().size(); j++) {
						int numVM;
						numVM = CloudCDNSolutionType.GetVMVariables(solution,
								i, h).getValue(j);

						total_horas += numVM;
						
						double priceVM;
						priceVM = getRegionesDatacenters().get(i)
								.computeVMCost(j);

						if (numVM > 0) {
							machineCost += numVM * priceVM;
						}
					}
				}
				
				System.out.println("DC " + i + " = " + total_horas + " hours");
			}

			routingAlgorithm_.Compute(solution, startEvalSecs, endEvalSecs);

			System.out.println(">> Traffic >>");
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				trafficCost += getRegionesDatacenters().get(i)
						.computeTransferCost(
								routingAlgorithm_.getTrafficAmount()[i]);
				
				System.out.println("DC " + i + " = " + routingAlgorithm_.getTrafficAmount()[i] + " GB");
			}

			if ((QOS_THRESHOLD != 1.0)
					&& (routingAlgorithm_.getRatioQoS() >= QOS_THRESHOLD)) {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth());
			} else {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests()
						+ routingAlgorithm_.getNumberOfQoSViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth()
						+ routingAlgorithm_.getViolatedQoS());
			}

			fitness = (storageCost * (endEvalMonths - startEvalMonths))
					+ (machineCost)
					+ (trafficCost * (endEvalMonths - startEvalMonths));
		} catch (JMException e) {
			e.printStackTrace();
			fitness = Double.MAX_VALUE;
		}

		System.out.println(">> [INFO] Actual fitness: " + fitness
				+ " [Penalty: " + solution.getOverallConstraintViolation()
				+ "]");

		if (solution.getOverallConstraintViolation() > 0)
			fitness = -1;
		solution.setObjective(0, fitness);
	}

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 */
	public void evaluate(Solution solution) {
		FixSolution(solution);

		double fitness = 0.0;
		double storageCost = 0.0;
		double machineCost = 0.0;
		double trafficCost = 0.0;

		try {
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				double dataSize = 0.0;

				for (int j = 0; j < getDocumentos().size(); j++) {
					if (CloudCDNSolutionType.GetDocumentVariables(solution, i)
							.getValue(j) == 1) {
						dataSize += getDocumentos().get(j).docSize;
					}
				}

				storageCost += getRegionesDatacenters().get(i)
						.computeStorageCost(dataSize);
			}

			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				for (int h = 0; h < 24; h++) {
					for (int j = 0; j < getMaquinas().size(); j++) {
						int numVM;
						numVM = CloudCDNSolutionType.GetVMVariables(solution,
								i, h).getValue(j);

						double priceVM;
						priceVM = getRegionesDatacenters().get(i)
								.computeVMCost(j);

						if (numVM > 0) {
							machineCost += numVM * priceVM;
						}
					}
				}
			}

			routingAlgorithm_.Compute(solution, startTrainingSecs,
					endTrainingSecs);

			double traffic;
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				traffic = routingAlgorithm_.getTrafficAmount()[i];
				
				trafficCost += getRegionesDatacenters().get(i)
						.computeTransferCost(traffic);
			}

			if (routingAlgorithm_.getRatioQoS() >= QOS_THRESHOLD) {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth());
			} else {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests()
						+ routingAlgorithm_.getNumberOfQoSViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth()
						+ routingAlgorithm_.getViolatedQoS());
			}

			fitness = (storageCost * (endTrainingMonths - startTrainingMonths))
					+ (machineCost)
					+ (trafficCost * (endTrainingMonths - startTrainingMonths));
		} catch (JMException e) {
			e.printStackTrace();
			fitness = Double.MAX_VALUE;
		}

		solution.setObjective(0, fitness);
	}

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
				docSizeGB = DOC_SIZE_AMP * Double.valueOf((linea
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
				docSizeGB = DOC_SIZE_AMP * Double.valueOf((linea
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

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		routingAlgorithm_.Compute(solution, startTrainingSecs, endTrainingSecs);

		if ((QOS_THRESHOLD != 1.0) && (routingAlgorithm_.getRatioQoS() >= QOS_THRESHOLD)) {
			solution.setNumberOfViolatedConstraint(routingAlgorithm_
					.getNumberOfBandwidthViolatedRequests());
			solution.setOverallConstraintViolation(routingAlgorithm_
					.getTotalViolatedBandwidth());
		} else {
			solution.setNumberOfViolatedConstraint(routingAlgorithm_
					.getNumberOfBandwidthViolatedRequests()
					+ routingAlgorithm_.getNumberOfQoSViolatedRequests());
			solution.setOverallConstraintViolation(routingAlgorithm_
					.getTotalViolatedBandwidth()
					+ routingAlgorithm_.getViolatedQoS());
		}
	}
}
