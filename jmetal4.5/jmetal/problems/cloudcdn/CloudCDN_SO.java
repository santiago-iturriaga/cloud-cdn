package jmetal.problems.cloudcdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.problems.cloudcdn.greedy.routing.SimpleRR;
import jmetal.util.JMException;

public class CloudCDN_SO extends Problem {
	private static final long serialVersionUID = -6970983090454693518L;
	static final public Boolean DEBUG = true;

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

	private Documento[] documentos_ = new Documento[CANTIDAD_MAXIMA_DE_DOCUMENTOS];
	private Integer cantidadDocumentos_ = 0;

	private Region[] regiones_ = new Region[CANTIDAD_MAXIMA_DE_REGIONES];
	private Integer cantidadRegiones_ = 0;

	private RegionDatacenter[] regionesDatacenters_ = new RegionDatacenter[CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS];
	private Integer cantidadRegionesDatacenters_ = 0;

	private RegionUsuario[] regionesUsuarios_ = new RegionUsuario[CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS];
	private Integer cantidadRegionesUsuarios_ = 0;

	private Trafico[] trafico_ = new Trafico[CANTIDAD_MAXIMA_DE_TRAFICO];
	private Integer cantidadTrafico_ = 0;

	private QoS[] qoS_ = new QoS[CANTIDAD_MAXIMA_DE_QOS];
	private Integer cantidadQoS_ = 0;

	private Maquina[] maquinas_ = new Maquina[CANTIDAD_MAXIMA_DE_MAQUINAS];
	private Integer cantidadMaquinas_ = 0;

	private SimpleRR routingSimpleRR_;

	public CloudCDN_SO(String solutionType) {
		this(solutionType, "test/", 0);
	}

	public CloudCDN_SO(String solutionType, String pathName, int instanceNumber) {
		try {
			readProblem(pathName, instanceNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}

		numberOfVariables_ = cantidadRegionesDatacenters_ * 2;
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 2;

		problemName_ = "CloudCDN_SO";

		length_ = new int[numberOfVariables_];
		for (int var = 0; var < getNumberOfVariables() / 2; var++) {
			length_[var] = getCantidadDocumentos();
		}
		for (int var = getNumberOfVariables() / 2; var < getNumberOfVariables(); var++) {
			length_[var] = getCantidadMaquinas();
		}

		contentsLowerLimits_ = new double[getCantidadDocumentos()];

		for (int i = 0; i < getCantidadDocumentos(); i++) {
			contentsLowerLimits_[i] = 0;
		}

		contentsUpperLimits_ = new double[getCantidadDocumentos()];

		for (int i = 0; i < getCantidadDocumentos(); i++) {
			contentsUpperLimits_[i] = 1;
		}

		vmTypesLowerLimits_ = new double[getCantidadMaquinas()];

		for (int i = 0; i < getCantidadMaquinas(); i++) {
			vmTypesLowerLimits_[i] = 0;
		}

		vmTypesUpperLimits_ = new double[getCantidadMaquinas()];

		for (int i = 0; i < getCantidadMaquinas(); i++) {
			vmTypesUpperLimits_[i] = getCantidadTrafico(); // Big enough value.
		}

		routingSimpleRR_ = new SimpleRR(this);

		try {
			if (solutionType.compareTo("CloudCDNSolutionType") == 0)
				solutionType_ = new CloudCDNSolutionType(this);
			else {
				throw new JMException("Solution type invalid");
			}
		} catch (JMException e) {
			e.printStackTrace();
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

	public Documento[] getDocumentos() {
		return documentos_;
	}

	public int getCantidadDocumentos() {
		return cantidadDocumentos_;
	}

	public Region[] getRegiones() {
		return regiones_;
	}

	public int getCantidadRegiones() {
		return cantidadRegiones_;
	}

	public RegionDatacenter[] getRegionesDatacenters() {
		return regionesDatacenters_;
	}

	public int getCantidadRegionesDatacenters() {
		return cantidadRegionesDatacenters_;
	}

	public RegionUsuario[] getRegionesUsuarios() {
		return regionesUsuarios_;
	}

	public int getCantidadRegionesUsuarios() {
		return cantidadRegionesUsuarios_;
	}

	public Trafico[] getTrafico() {
		return trafico_;
	}

	public Integer getCantidadTrafico() {
		return cantidadTrafico_;
	}

	public QoS[] getQoS() {
		return qoS_;
	}

	public int getCantidadQoS() {
		return cantidadQoS_;
	}

	public Maquina[] getMaquinas() {
		return maquinas_;
	}

	public int getCantidadMaquinas() {
		return cantidadMaquinas_;
	}

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 */
	public void evaluate(Solution solution) {
		double fitness;

		double storageCost = 0.0;
		double machineCost = 0.0;
		double trafficCost = 0.0;

		try {
			for (int i = 0; i < getNumberOfVariables() / 2; i++) {
				double dataSize = 0.0;

				for (int j = 0; j < getCantidadDocumentos(); j++) {
					ArrayInt var = (ArrayInt) solution.getDecisionVariables()[i];

					if (var.getValue(j) == 1) {
						dataSize += getDocumentos()[j].docSize;
					}
				}

				storageCost += getRegionesDatacenters()[i]
						.computeStorageCost(dataSize);
			}

			for (int i = getNumberOfVariables() / 2; i < getNumberOfVariables(); i++) {
				for (int j = 0; j < getCantidadMaquinas(); j++) {
					ArrayInt var = (ArrayInt) solution.getDecisionVariables()[i];

					if (var.getValue(j) > 0) {
						machineCost += getRegionesDatacenters()[i
								- getNumberOfVariables() / 2].computeVMCost(j)
								* var.getValue(j);
					}
				}
			}

			routingSimpleRR_.Compute(solution);

			if (routingSimpleRR_.isFeasible()) {
				for (int i = 0; i < getCantidadRegionesDatacenters(); i++) {
					trafficCost += getRegionesDatacenters()[i]
							.computeTransferCost(routingSimpleRR_
									.getTrafficAmount()[i]);
				}
			} else {
				fitness = -1;
			}

			fitness = storageCost + machineCost + trafficCost;
		} catch (JMException e) {
			e.printStackTrace();
			fitness = -1;
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
			// TODO: arreglar numeración de documentos!
			lineasArchivo = leerArchivo(path.toString());

			cantidadDocumentos_ = lineasArchivo.size();
			int i = 0;
			for (String linea : lineasArchivo) {
				double docSizeGB;
				docSizeGB = Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1])
						/ (1024 * 1024 * 1024);

				documentos_[i++] = new Documento(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						docSizeGB);
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO DOCUMENTOS (TOP 10): ");
				for (int j = 0; j < cantidadDocumentos_ && j < 10; j++) {
					System.out.println(documentos_[j].getDocId() + " "
							+ documentos_[j].getDocSize());
				}
			}

			// ** CARGANDO REGIONES **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_REGIONES);
			lineasArchivo = leerArchivo(path.toString());

			cantidadRegiones_ = lineasArchivo.size();
			i = 0;
			for (String linea : lineasArchivo) {
				regiones_[i++] = new Region(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						String.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO REGIONES: ");
				for (int j = 0; j < cantidadRegiones_; j++) {
					System.out.println(regiones_[j].getRegId() + " "
							+ regiones_[j].getRegNombre());
				}
			}

			// ** CARGANDO DATACENTERS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_DATACENTERS);
			lineasArchivo = leerArchivo(path.toString());

			cantidadRegionesDatacenters_ = lineasArchivo.size();
			i = 0;
			for (String linea : lineasArchivo) {
				regionesDatacenters_[i++] = new RegionDatacenter(
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						String.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]),
						String.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3]),
						String.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[4]));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO REGIONES DATACENTERS: ");
				for (int j = 0; j < cantidadRegionesDatacenters_; j++) {
					System.out.println(regionesDatacenters_[j].getRegDctId()
							+ " " + regionesDatacenters_[j].getRegNombre()
							+ " " + regionesDatacenters_[j].getRegId());
				}

			}

			// ** CARGANDO REGIONES USUARIOS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS);
			lineasArchivo = leerArchivo(path.toString());

			cantidadRegionesUsuarios_ = lineasArchivo.size();
			i = 0;
			for (String linea : lineasArchivo) {
				regionesUsuarios_[i++] = new RegionUsuario(
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						String.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO REGIONES USUARIOS: ");
				for (int j = 0; j < cantidadRegionesUsuarios_; j++) {
					System.out.println(regionesUsuarios_[j].getRegUsrId() + " "
							+ regionesUsuarios_[j].getRegNombre() + " "
							+ regionesUsuarios_[j].getRegId());
				}
			}

			// ** CARGANDO TRAFICO **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_TRAFICO);
			lineasArchivo = leerArchivo(path.toString());

			cantidadTrafico_ = lineasArchivo.size();
			i = 0;
			for (String linea : lineasArchivo) {
				double docSizeGB;
				docSizeGB = Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2])
						/ (1024 * 1024 * 1024);

				trafico_[i++] = new Trafico(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						docSizeGB, Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3]));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO TRAFICO (top 10): ");
				for (int j = 0; j < cantidadTrafico_ && j < 10; j++) {
					System.out.println(trafico_[j].getReqTime() + " "
							+ trafico_[j].getDocId() + " "
							+ trafico_[j].getDocSize() + " "
							+ trafico_[j].getRegUsrId());
				}
			}

			// ** CARGANDO QOS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_QOS);
			lineasArchivo = leerArchivo(path.toString());

			cantidadQoS_ = lineasArchivo.size();
			i = 0;
			for (String linea : lineasArchivo) {
				qoS_[i++] = new QoS(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]));
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO QOS: ");
				for (int j = 0; j < cantidadQoS_; j++) {
					System.out.println(qoS_[j].getRegUsrId() + " "
							+ qoS_[j].getRegDocId() + " "
							+ qoS_[j].getQosMetric());
				}
			}

			// ** CARGANDO MAQUINAS **//
			path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_MAQUINAS);
			lineasArchivo = leerArchivo(path.toString());

			cantidadMaquinas_ = lineasArchivo.size();
			i = 0;
			for (String linea : lineasArchivo) {
				maquinas_[i++] = new Maquina(Integer.valueOf((linea
						.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
						String.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
						Integer.valueOf((linea
								.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2])

				);
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO QOS: ");
				for (int j = 0; j < cantidadMaquinas_; j++) {
					System.out.println(maquinas_[j].getVmId() + " "
							+ maquinas_[j].getVmNombre() + " "
							+ maquinas_[j].getBandwidth()

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

				regionesDatacenters_[dcId].setVMCost(vmId, cost);
			}

			if (DEBUG) {
				System.out.println("IMPRIMIENDO DATACENTERS MAQUINAS: ");
				for (int j = 0; j < cantidadRegionesDatacenters_; j++) {
					regionesDatacenters_[j].printVmCosts();
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
		double[] constraint = new double[this.getNumberOfConstraints()];

		double total = 0.0;
		int number = 0;

		// TODO: implementar constraint C1 y C3.

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);
	}
}
