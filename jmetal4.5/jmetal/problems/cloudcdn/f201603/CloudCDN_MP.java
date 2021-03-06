package jmetal.problems.cloudcdn.f201603;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.encodings.variable.Binary;
import jmetal.problems.cloudcdn.f201603.greedy.BestQoS;
import jmetal.problems.cloudcdn.f201603.greedy.BestQoSSecure;
import jmetal.problems.cloudcdn.f201603.greedy.CheapestComputing;
import jmetal.problems.cloudcdn.f201603.greedy.CheapestNetwork;
import jmetal.problems.cloudcdn.f201603.greedy.CheapestNetworkSecure;
import jmetal.problems.cloudcdn.f201603.greedy.IGreedyRouting;
import jmetal.problems.cloudcdn.f201603.greedy.RoundRobin;
import jmetal.util.JMException;

/*
 * Problem class for solving the multi provider (broker) formulation (Cadiz 201603).
 * */
public class CloudCDN_MP extends Problem {

    static final public long serialVersionUID = -6970983090454693518L;
    static final public Boolean DEBUG = true;

    static final public double DOC_SIZE_AMP = 1.0; // Amplifies the document size
    //static final public double DOC_SIZE_AMP = 3.0; // Amplifies the document size
    //static final public double DOC_SIZE_AMP = 10.0; // Amplifies the document size
    //static final public double DOC_SIZE_AMP = 15.0; // Amplifies the document size
    static final public int TRAFF_AMP = 1; // Aplifies the traffic xTRAFF_AMP times
    static final public int MAX_DOCUMENTS = Integer.MAX_VALUE; // Limita la cantidad de contenidos sin importar la instancia

    //static final public Double CONTENT_SIZE_MB = 0.25; // CONTENT_SIZE_MB = 2 MB
    static final public Double CONTENT_SIZE_MB = 0.46; // CONTENT_SIZE_MB = 2 MB
    //static final public Double CONTENT_SIZE_MB = 0.5; // CONTENT_SIZE_MB = 2 MB
    //static final public Double CONTENT_SIZE_MB = 2.0; // CONTENT_SIZE_MB = 2 MB
    //VMs may serve up to VM_PROCESSING requests simultaneously
    //static final public int VM_PROCESSING = 512; // Amount theoretically served by 1GB ethernet connection
    //static final public int VM_PROCESSING = 256;
    //static final public int VM_PROCESSING = 112; // 720p
    static final public int VM_PROCESSING = 81; // 1080p 300Mbps
    //static final public int VM_PROCESSING = 64;
    //static final public int VM_PROCESSING = 61; // 1080p 225Mbps
    //static final public int VM_PROCESSING = 32;

    static final public int SECONDS_PER_TIMESTEP = 1;
    public int TIME_HORIZON;

    public double STORAGE_COST_FACTOR;
    public double VM_RENTING_UPFRONT_FACTOR;

    static final public int VM_RENTING_STEPS = (60 * 60) / SECONDS_PER_TIMESTEP; // VMs are rented for 1 hour

    static final public int CANTIDAD_MAXIMA_DE_DOCUMENTOS = 30000;
    static final public int CANTIDAD_MAXIMA_DE_REGIONES = 10;
    static final public int CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS = 20;
    static final public int CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS = 100;
    static final public int CANTIDAD_MAXIMA_DE_TRAFICO = 1000000;
    static final public int CANTIDAD_MAXIMA_DE_QOS = 1000;

    static final public String SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS = " ";

    //static final public String NOMBRE_ARCHIVO_DE_DOCUMENTOS = "docs.video.f201603";
    //static final public String NOMBRE_ARCHIVO_DE_TRAFICO = "workload.video.f201603";
    static final public String NOMBRE_ARCHIVO_DE_DOCUMENTOS = "docs.video.f201604";
    static final public String NOMBRE_ARCHIVO_DE_TRAFICO = "workload.video.f201604";
    static final public String NOMBRE_ARCHIVO_DE_REGIONES = "reg.0";
    static final public String NOMBRE_ARCHIVO_DE_DATACENTERS = "dc.1";
    static final public String NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS = "reg_users.0";
    static final public String NOMBRE_ARCHIVO_DE_QOS = "qos.2";

    protected Integer numProvedores_ = 0;

    protected ArrayList<Documento> documentos_ = new ArrayList<>(
            CANTIDAD_MAXIMA_DE_DOCUMENTOS);
    protected ArrayList<Region> regiones_ = new ArrayList<>(
            CANTIDAD_MAXIMA_DE_REGIONES);
    protected ArrayList<RegionDatacenter> regionesDatacenters_ = new ArrayList<>(
            CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS);
    protected ArrayList<RegionUsuario> regionesUsuarios_ = new ArrayList<>(
            CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);
    protected ArrayList<Trafico> trafico_ = new ArrayList<>(
            CANTIDAD_MAXIMA_DE_TRAFICO);

    protected ArrayList<ArrayList<QoS>> sortedQoS_ = new ArrayList<>(CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);
    protected ArrayList<RegionDatacenter> sortedNetworkCost_ = new ArrayList<>();
    protected ArrayList<HashMap<Integer, QoS>> qoS_ = new ArrayList<>(CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);

    protected int MAX_TRAFFIC_CONGESTION = 0;

    protected double[] RILowerLimits_;
    protected double[] RIUpperLimits_;

    protected IGreedyRouting router = null;

    public CloudCDNSolutionf201603Type solutionTypeCustom_;

    // === Routing aux. variables
    public int[][][] vmNeeded;
    public int[][][] vmOverflow;
    public int[][] vmMaxNeeded;
    public int[][] vmMaxOverflow;

    public int[][] vmNeededUnsecure;
    public int[][] vmOverflowUnsecure;

    // =========================
    public CloudCDN_MP(String solutionType, String scenPath, String instPath, String routingAlgorithm, int time_horizon) throws JMException {
        TIME_HORIZON = time_horizon;
        // Monthly storage costs are considered according the TIME_HORIZON
        STORAGE_COST_FACTOR = (double) TIME_HORIZON / (double) (30 * 24 * (60 * 60));
        // VMs are rented for 1 hour
        VM_RENTING_UPFRONT_FACTOR = (double) TIME_HORIZON / (double) ((60 * 60) * 24 * 365);

        try {
            readProblem(scenPath, instPath);
        } catch (IOException e) {
            Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
            throw new JMException(e.getMessage());
        }

        regiones_.trimToSize();
        regionesDatacenters_.trimToSize();
        regionesUsuarios_.trimToSize();
        trafico_.trimToSize();
        qoS_.trimToSize();

        if ((solutionType.compareTo("CloudCDNSolutionf201603b100Type") == 0)
                || (solutionType.compareTo("CloudCDNSolutionf201603Type") == 0)) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 100);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else if (solutionType.compareTo("CloudCDNSolutionf201603b5Type") == 0) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 5);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else if (solutionType.compareTo("CloudCDNSolutionf201603b10Type") == 0) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 10);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else if (solutionType.compareTo("CloudCDNSolutionf201603b25Type") == 0) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 25);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else if (solutionType.compareTo("CloudCDNSolutionf201603b50Type") == 0) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 50);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else if (solutionType.compareTo("CloudCDNSolutionf201603b200Type") == 0) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 200);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else if (solutionType.compareTo("CloudCDNSolutionf201603b500Type") == 0) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 500);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else if (solutionType.compareTo("CloudCDNSolutionf201603b1000Type") == 0) {
            try {
                solutionType_ = solutionTypeCustom_ = new CloudCDNSolutionf201603Type(this, 1000);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }
        } else {
            throw new JMException("Solution type invalid");
        }

        numberOfVariables_ = 2;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "CloudCDN_MP";

        length_ = new int[numberOfVariables_];
        length_[0] = getRegionesDatacenters().size();
        length_[1] = getRegionesDatacenters().size() * solutionTypeCustom_.NUM_BUCKETS;

        RILowerLimits_ = new double[getRegionesDatacenters().size()];
        for (int i = 0; i < getRegionesDatacenters().size(); i++) {
            RILowerLimits_[i] = 0.0;
        }

        int upperVMLimit;
        upperVMLimit = (MAX_TRAFFIC_CONGESTION / VM_PROCESSING) + 1;

        RIUpperLimits_ = new double[getRegionesDatacenters().size()];
        for (int i = 0; i < getRegionesDatacenters().size(); i++) {
            RIUpperLimits_[i] = upperVMLimit;
        }

        vmNeeded = new int[getRegionesDatacenters().size()][getNumProvedores()][VM_RENTING_STEPS];
        vmOverflow = new int[getRegionesDatacenters().size()][getNumProvedores()][VM_RENTING_STEPS];
        vmMaxNeeded = new int[getRegionesDatacenters().size()][getNumProvedores()];
        vmMaxOverflow = new int[getRegionesDatacenters().size()][getNumProvedores()];

        vmNeededUnsecure = new int[getRegionesDatacenters().size()][VM_RENTING_STEPS];
        vmOverflowUnsecure = new int[getRegionesDatacenters().size()][VM_RENTING_STEPS];

        if (routingAlgorithm.compareTo("CheapestNetwork") == 0) {
            System.out.println("Greedy routing: CheapestNetwork");
            router = new CheapestNetwork(this);
        } else if (routingAlgorithm.compareTo("CheapestComputing") == 0) {
            System.out.println("Greedy routing: CheapestComputing");
            router = new CheapestComputing(this);
        } else if (routingAlgorithm.compareTo("RoundRobin") == 0) {
            System.out.println("Greedy routing: RoundRobin");
            router = new RoundRobin(this);
        } else if (routingAlgorithm.compareTo("BestQoS") == 0) {
            System.out.println("Greedy routing: BestQoS");
            router = new BestQoS(this);
        } else if (routingAlgorithm.compareTo("BestQoSSecure") == 0) {
            System.out.println("Greedy routing: BestQoSSecure");
            router = new BestQoSSecure(this);
        } else if (routingAlgorithm.compareTo("CheapestNetworkSecure") == 0) {
            System.out.println("Greedy routing: CheapestNetworkSecure");
            router = new CheapestNetworkSecure(this);
        } else {
            throw new JMException("Unknown routing algorithm.");
        }
    }

    public Integer getNumProvedores() {
        return numProvedores_;
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

    public ArrayList<HashMap<Integer, QoS>> getQoS() {
        return qoS_;
    }

    public QoS getQoS(int regUsr, int regDC) {
        return qoS_.get(regUsr).get(regDC);
    }

    public ArrayList<QoS> getSortedQoS(int regUsr) {
        return sortedQoS_.get(regUsr);
    }

    public ArrayList<RegionDatacenter> getSortedNetworkCost() {
        return sortedNetworkCost_;
    }
    
    public double[] GetRILowerLimits() {
        return RILowerLimits_;
    }

    public double[] GetRIUpperLimits() {
        return RIUpperLimits_;
    }

    public void readProblem(String scenPath, String instPath)
            throws IOException {
        try {
            File scenDir = new File(scenPath);
            File instDir = new File(instPath);

            if (DEBUG) {
                System.out.println("DIRECTORIO DE ESCENARIO: "
                        + scenDir.getCanonicalPath());
                System.out.println("DIRECTORIO DE INSTANCIA: "
                        + instDir.getCanonicalPath());
            }

            // ** CARGANDO DOCUMENTOS **//
            double totalStorageControl = 0.0;

            Collection<String> lineasArchivo;
            Path path = Paths.get(instPath, NOMBRE_ARCHIVO_DE_DOCUMENTOS);
            lineasArchivo = leerArchivo(path.toString());

            for (String linea : lineasArchivo) {
                double docSizeMB;
                docSizeMB = DOC_SIZE_AMP
                        * Double.valueOf((linea
                                .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1])
                        / (1024.0 * 1024.0);

                int numContenidos;
                numContenidos = (int) Math.ceil((double) docSizeMB / (double) CONTENT_SIZE_MB);

                totalStorageControl += (numContenidos * CONTENT_SIZE_MB);

                int docId;
                docId = Integer.valueOf((linea.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]);

                int provId;
                provId = Integer.valueOf((linea.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]);

                if (provId > numProvedores_) {
                    numProvedores_ = provId;
                }

                if (docId < MAX_DOCUMENTS) {
                    documentos_.add(new Documento(docId, docSizeMB, numContenidos, provId));
                }
            }

            numProvedores_++;

            /*
            if (DEBUG) {
                System.out.println("IMPRIMIENDO DOCUMENTOS (TOP 10): ");
                for (int j = 0; j < documentos_.size() && j < 10; j++) {
                    System.out.println(documentos_.get(j).getDocId() + " "
                            + documentos_.get(j).getDocSize() + " " + documentos_.get(j).getNumContenidos());
                }
            }
             */
            // ** CARGANDO REGIONES **//
            path = Paths.get(scenPath, NOMBRE_ARCHIVO_DE_REGIONES);
            lineasArchivo = leerArchivo(path.toString());

            for (String linea : lineasArchivo) {
                regiones_
                        .add(new Region(
                                Integer.valueOf((linea
                                        .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
                                String.valueOf((linea
                                        .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1])));
            }

            /*
            if (DEBUG) {
                System.out.println("IMPRIMIENDO REGIONES: ");
                for (int j = 0; j < regiones_.size(); j++) {
                    System.out.println(regiones_.get(j).getRegId() + " "
                            + regiones_.get(j).getRegNombre());
                }
            }
             */
            // ** CARGANDO DATACENTERS **//
            path = Paths.get(scenPath, NOMBRE_ARCHIVO_DE_DATACENTERS);
            lineasArchivo = leerArchivo(path.toString());

            for (String linea : lineasArchivo) {
            	RegionDatacenter r;
                r = new RegionDatacenter(
                    Integer.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
                    String.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
                    Integer.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]),
                    Double.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3]) * STORAGE_COST_FACTOR,
                    Double.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[4]),
                    Double.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[5]),
                    Double.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[6]),
                    Double.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[7]) * VM_RENTING_UPFRONT_FACTOR);

                regionesDatacenters_.add(r);
                sortedNetworkCost_.add(r);
            }
            
            sortedNetworkCost_.sort(new RegionDatacenterNetworkCheapestComparator());

            /*
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
             */
            // ** CARGANDO QOS **//
            path = Paths.get(scenPath, NOMBRE_ARCHIVO_DE_QOS);
            lineasArchivo = leerArchivo(path.toString());

            for (String linea : lineasArchivo) {
                QoS q;
                q = new QoS(Integer.valueOf((linea
                        .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]),
                        Integer.valueOf((linea
                                .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]),
                        Double.valueOf((linea
                                .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2]));

                if (q.regUsrId >= qoS_.size()) {
                    qoS_.add(new HashMap<Integer, QoS>());
                }

                qoS_.get(q.regUsrId).put(q.regDcId, q);

                if (q.regUsrId >= sortedQoS_.size()) {
                    sortedQoS_.add(new ArrayList<QoS>());
                }

                sortedQoS_.get(q.regUsrId).add(q);
            }

            for (int i = 0; i < sortedQoS_.size(); i++) {
                sortedQoS_.get(i).sort(new BestQoSComparator());
            }

            /*
            if (DEBUG) {
                System.out.println("IMPRIMIENDO QOS: ");
                for (int j = 0; j < qoS_.size(); j++) {
                    for (int i = 0; i < qoS_.get(j).size(); i++) {
                        System.out.println(qoS_.get(j).get(i).getRegUsrId()
                                + " " + qoS_.get(j).get(i).getRegDcId() + " "
                                + qoS_.get(j).get(i).getQosMetric());
                    }
                }
            }
             */
            // ** CARGANDO REGIONES USUARIOS **//
            path = Paths.get(scenPath, NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS);
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

            /*
            if (DEBUG) {
                System.out.println("IMPRIMIENDO REGIONES USUARIOS: ");
                for (int j = 0; j < regionesUsuarios_.size(); j++) {
                    System.out.println(regionesUsuarios_.get(j).getRegUsrId()
                            + " " + regionesUsuarios_.get(j).getRegNombre()
                            + " " + regionesUsuarios_.get(j).getRegId());
                }
            }
             */
            int[] trafficHistogram = new int[TIME_HORIZON];
            for (int i = 0; i < TIME_HORIZON; i++) {
                trafficHistogram[i] = 0;
            }

            // ** CARGANDO TRAFICO **//
            path = Paths.get(instPath, NOMBRE_ARCHIVO_DE_TRAFICO);
            lineasArchivo = leerArchivo(path.toString());

            double totalTrafficControl = 0.0;

            for (String linea : lineasArchivo) {
                for (int j = 0; j < TRAFF_AMP; j++) {
                    Trafico aux;
                    int docId;

                    docId = Integer.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1]) % MAX_DOCUMENTS;

                    /*
                    // TODO Ignoro el requested doc size y asumo que siempre se pide el doc completo.
                
                    double docSizeGB;
                    docSizeGB = DOC_SIZE_AMP
                        * Double.valueOf((linea
                                .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[2])
                        / (1024 * 1024 * 1024);
                     */
                    int reqTimeStep = ((Integer.valueOf((linea
                            .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0])) / SECONDS_PER_TIMESTEP) % TIME_HORIZON;

                    for (int i = 0; (i < getDocumentos().get(docId).getNumContenidos()) && (reqTimeStep + i < TIME_HORIZON); i++) {
                        trafficHistogram[reqTimeStep + i]++;
                    }

                    totalTrafficControl += getDocumentos().get(docId).getDocSize();

                    aux = new Trafico(
                            reqTimeStep,
                            docId,
                            getDocumentos().get(docId).getDocSize(),
                            getDocumentos().get(docId).getNumContenidos(),
                            Integer.valueOf((linea
                                    .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3]));

                    trafico_.add(aux);
                }
            }

            trafico_.sort(new TraficoComparator());

            for (int i = 0; i < TIME_HORIZON; i++) {
                if (MAX_TRAFFIC_CONGESTION < trafficHistogram[i]) {
                    MAX_TRAFFIC_CONGESTION = trafficHistogram[i];
                }
            }

            /*
            if (DEBUG) {
                System.out.println("IMPRIMIENDO TRAFICO (top 10): ");
                for (int j = 0; j < trafico_.size() && j < 10; j++) {
                    System.out.println(trafico_.get(j).getReqTime() + " "
                            + trafico_.get(j).getDocId() + " "
                            + trafico_.get(j).getDocSize() + " "
                            + trafico_.get(j).getRegUsrId());
                }
            }
             */
            System.out.println("Total documents: " + getDocumentos().size());
            System.out.println("Total requests: " + getTrafico().size());

            RegionDatacenter.TOTAL_STORAGE = totalStorageControl / 1024;
            RegionDatacenter.TOTAL_TRANSFER = totalTrafficControl / 1024;

            System.out.println("Total storage: " + RegionDatacenter.TOTAL_STORAGE + " GB");
            System.out.println("Total traffic: " + RegionDatacenter.TOTAL_TRANSFER + " GB");
        } catch (Exception e) {
            Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
            System.exit(1);
        }
    }

    private Collection<String> leerArchivo(String nombreCompletoArchivo) {
        Collection<String> lineas = new LinkedList<>();

        File archivo;
        FileReader fr = null;
        BufferedReader br;

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
            // Leo hasta que no haya nada o hasta una linea vacía
            while ((linea = br.readLine()) != null && linea.length() != 0) {
                lineas.add(linea);
            }

            return lineas;

        } catch (Exception e) {
            Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return lineas;
    }

    public double computeNetworkCost(int[] trafficSummary) {
        double totalCost = 0.0;

        for (int i = 0; i < getRegionesDatacenters().size(); i++) {
            totalCost += getRegionesDatacenters().get(i).computeTransferCost(trafficSummary[i] * CONTENT_SIZE_MB);
        }

        return totalCost;
    }

    public double computeStorageCost(Solution solution) {
        int dcCount = getRegionesDatacenters().size();
        int docCount = getDocumentos().size();

        int[] storageContents = new int[dcCount];
        Binary storageVariables = solutionTypeCustom_.GetDocStorageVariables(solution);

        for (int docId = 0; docId < getDocumentos().size(); docId++) {
            Documento aux;
            aux = getDocumentos().get(docId);

            int varIdx;

            for (int dcId = 0; dcId < getRegionesDatacenters().size(); dcId++) {
                varIdx = solutionTypeCustom_.GetDCDocIndex(dcCount, docCount, dcId, docId);

                if (storageVariables.getIth(varIdx)) {
                    storageContents[dcId] += aux.getNumContenidos();
                }
            }
        }

        double storageCost = 0.0;
        for (int i = 0; i < getRegionesDatacenters().size(); i++) {
            storageCost += getRegionesDatacenters().get(i).computeStorageCost(storageContents[i] * CONTENT_SIZE_MB);
        }

        return storageCost;
    }

    public double computeComputingCost(Solution solution, int[] reservedAllocation, int[] onDemandAllocation) throws JMException {
        double totalCost = 0.0;
        double auxUpfront;
        //double auxRes;
        double auxOnDem;

        for (int i = 0; i < getRegionesDatacenters().size(); i++) {
            auxUpfront = (getRegionesDatacenters().get(i).vmResUpfrontPrice * solutionTypeCustom_.GetRIDCCount(solution, i));
            //auxRes = (getRegionesDatacenters().get(i).vmResPrice * reservedAllocation[i]);
            auxOnDem = (getRegionesDatacenters().get(i).vmPrice * onDemandAllocation[i]);

            //totalCost += auxUpfront + auxRes + auxOnDem;
            totalCost += auxUpfront + auxOnDem;
        }

        return totalCost;
    }

    public class EvaluateOutput {

        public double NetworkCost;
        public double StorageCost;
        public double ComputingCost;
    }

    public EvaluateOutput evaluate(Solution solution, Optional<Integer> justProvider, boolean genOutput) throws JMException {
        try {
            double totalQoS;
            int[] reservedAllocation = new int[getRegionesDatacenters().size()];
            int[] onDemandAllocation = new int[getRegionesDatacenters().size()];
            int[] trafficSummary = new int[getRegionesDatacenters().size()];

            totalQoS = router.Route(solution, trafficSummary, reservedAllocation, onDemandAllocation, justProvider);

            double networkCost = computeNetworkCost(trafficSummary);
            double storageCost = computeStorageCost(solution);
            double computingCost = computeComputingCost(solution, reservedAllocation, onDemandAllocation);

            // Single objective: only cost aware
            solution.setFitness(networkCost + storageCost + computingCost);

            // Multi objective: cost and qos aware
            solution.setObjective(0, networkCost + storageCost + computingCost);

            if (solution.getNumberOfObjectives() > 1) {
                solution.setObjective(1, totalQoS);
            }

            if (genOutput) {
                EvaluateOutput output = new EvaluateOutput();
                output.NetworkCost = networkCost;
                output.StorageCost = storageCost;
                output.ComputingCost = computingCost;
                return output;
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
            throw new JMException(e.getMessage());
        }
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        Optional<Integer> justProvId = Optional.empty();
        evaluate(solution, justProvId, false);
    }
}
