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
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNRRSolutionType;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.experiments.studies.CloudCDNSimpleStudy_f201603;
import jmetal.problems.cloudcdn.f201603.Documento;
import jmetal.problems.cloudcdn.f201603.QoS;
import jmetal.problems.cloudcdn.f201603.Region;
import jmetal.problems.cloudcdn.f201603.RegionDatacenter;
import jmetal.problems.cloudcdn.f201603.RegionUsuario;
import jmetal.problems.cloudcdn.f201603.Trafico;
import jmetal.problems.cloudcdn.f201603.TraficoComparator;
import jmetal.problems.cloudcdn.greedy.routing.RRCheapest;
import jmetal.problems.cloudcdn.greedy.routing.RoutingAlgorithm;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/*
 * Problem class for solving the multi provider (broker) formulation (C�diz 201603).
 * */
public class CloudCDN_MP extends Problem {

    static final public long serialVersionUID = -6970983090454693518L;
    static final public Boolean DEBUG = true;
    static final public double DOC_SIZE_AMP = 10.0; // Amplifies the document size
    static final public Integer MAX_DOCUMENTS = 100; // Limita la cantidad de contenidos sin importar la instancia

    static final public Integer SECONDS_PER_TIMESTEP = 1;
    static final public Double CONTENT_SIZE = 1.3; // KS = 1.3 MB
    static final public Integer MAX_PROVIDER_TRANSFER = 3; // PN = 3 contents uploaded per time step
    static final public Integer STORAGE_RENTING_TIME = 3600 * 24 * 30; // Storage costs are considered monthly
    static final public Integer VM_RENTING_TIME = 3600 / SECONDS_PER_TIMESTEP; // CT = VMs are rented for 1 hour
    static final public Integer VM_PROCESSING = 50; // CR = VMs may serve up to VM_PROCESSING requests simultaneously
    static final public Integer TIME_HORIZON = 3600 * 1; // 1 hous
    //static final public Integer TIME_HORIZON = 3600 * 24; // 1 day

    static final public Integer CANTIDAD_MAXIMA_DE_DOCUMENTOS = 30000;
    static final public Integer CANTIDAD_MAXIMA_DE_REGIONES = 10;
    static final public Integer CANTIDAD_MAXIMA_DE_REGIONES_DATACENTERS = 20;
    static final public Integer CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS = 100;
    static final public Integer CANTIDAD_MAXIMA_DE_TRAFICO = 100000;
    static final public Integer CANTIDAD_MAXIMA_DE_QOS = 1000;

    static final public String SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS = " ";

    static final public String NOMBRE_ARCHIVO_DE_DOCUMENTOS = "docs.0";
    static final public String NOMBRE_ARCHIVO_DE_REGIONES = "reg.0";
    static final public String NOMBRE_ARCHIVO_DE_DATACENTERS = "dc.1";
    static final public String NOMBRE_ARCHIVO_DE_REGIONES_USUARIOS = "reg_users.0";
    static final public String NOMBRE_ARCHIVO_DE_TRAFICO = "workload.0";
    static final public String NOMBRE_ARCHIVO_DE_QOS = "qos.0";

    protected Integer num_provedores_;

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
    protected ArrayList<ArrayList<QoS>> qoS_ = new ArrayList<>(
            CANTIDAD_MAXIMA_DE_REGIONES_USUARIOS);

    protected int totalSimTimeSecs;
    protected double totalSimTimeHours;
    protected double totalSimTimeMonths;
    protected double totalSimTimeDays;

    protected int MAX_TRAFFIC_CONGESTION = 0;
    
    protected double[] RILowerLimits_;
    protected double[] RIUpperLimits_;

    public CloudCDN_MP(String solutionType, String pathName, int instanceNumber, String routingAlgorithm) throws JMException {
        try {
            readProblem(pathName, instanceNumber);
        } catch (IOException e) {
            Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
            throw new JMException(e.getMessage());
        }

        regiones_.trimToSize();
        regionesDatacenters_.trimToSize();
        regionesUsuarios_.trimToSize();
        trafico_.trimToSize();
        qoS_.trimToSize();

        if (solutionType.compareTo("CloudCDNSolutionf201603Type") == 0) {
            try {
                solutionType_ = new CloudCDNSolutionf201603Type(this);
            } catch (Exception e) {
                Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
                throw new JMException(e.getMessage());
            }

        } else {
            throw new JMException("Solution type invalid");
        }

        totalSimTimeSecs = getTrafico().get(getTrafico().size() - 1).reqTime;
        totalSimTimeHours = totalSimTimeSecs / (60.0 * 60.0);
        totalSimTimeDays = totalSimTimeHours / 24.0;
        totalSimTimeMonths = totalSimTimeDays / 30.0;

        numberOfVariables_ = 2;
        //numberOfVariables_ = documentos_.size() * regionesDatacenters_.size(); // flat encoding
        //numberOfVariables_ = documentos_.size(); // document oriented encoding
        //numberOfVariables_ = regionesDatacenters_.size(); // datacenter oriented encoding
        numberOfObjectives_ = 1;
        numberOfConstraints_ = 1;
        problemName_ = "CloudCDN_MP";

        length_ = new int[numberOfVariables_];
        length_[0] = getRegionesDatacenters().size();
        length_[1] = getRegionesDatacenters().size() * getDocumentos().size();

        RILowerLimits_ = new double[getRegionesDatacenters().size()];
        for (int i = 0; i < getRegionesDatacenters().size(); i++) {
            RILowerLimits_[i] = 0.0;
        }
        
        int upperVMLimit = (int) Math.ceil(MAX_TRAFFIC_CONGESTION / VM_PROCESSING);

        RIUpperLimits_ = new double[getRegionesDatacenters().size()];
        for (int i = 0; i < getRegionesDatacenters().size(); i++) {
            RIUpperLimits_[i] = upperVMLimit; // TODO arreglar el tope máximo de VM
        }

        //if (routingAlgorithm.compareTo("RRCheapest") == 0) {
        //	routingAlgorithm_ = new RRCheapest(this);
        //}
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

    public double[] GetRILowerLimits() {
        return RILowerLimits_;
    }

    public double[] GetRIUpperLimits() {
        return RIUpperLimits_;
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
            num_provedores_ = 1; // TODO Incluir ID de provider en la instancia. Temporalmente fijado en 1

            Collection<String> lineasArchivo = null;
            Path path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_DOCUMENTOS);
            lineasArchivo = leerArchivo(path.toString());

            for (String linea : lineasArchivo) {
                double docSizeMB;
                docSizeMB = DOC_SIZE_AMP
                        * Double.valueOf((linea
                                .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[1])
                        / (1024 * 1024);

                int numContenidos;
                numContenidos = (int) Math.ceil(docSizeMB / CONTENT_SIZE);

                int docId;
                docId = Integer.valueOf((linea.split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0]);

                if (docId < MAX_DOCUMENTS) {
                    documentos_.add(new Documento(docId, docSizeMB, numContenidos, 1)); // Prov. ID es siempre 1
                }
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

            int[] trafficHistogram = new int[TIME_HORIZON];
            for (int i = 0; i < TIME_HORIZON; i++) {
                trafficHistogram[i] = 0;
            }

            // ** CARGANDO TRAFICO **//
            path = Paths.get(pathName, NOMBRE_ARCHIVO_DE_TRAFICO);
            lineasArchivo = leerArchivo(path.toString());

            for (String linea : lineasArchivo) {
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
                int reqTime = (Integer.valueOf((linea
                        .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[0])) % TIME_HORIZON;

                for (int i = 0; (i < getDocumentos().get(docId).getNumContenidos()) && (reqTime + i < TIME_HORIZON); i++) {
                    trafficHistogram[reqTime + i]++;
                }

                aux = new Trafico(
                        reqTime,
                        docId,
                        getDocumentos().get(docId).getDocSize(),
                        Integer.valueOf((linea
                                .split(SEPARADOR_DE_COLUMNAS_EN_ARCHIVOS))[3]));

                trafico_.add(aux);
            }

            trafico_.sort(new TraficoComparator());

            for (int i = 0; i < TIME_HORIZON; i++) {
                if (MAX_TRAFFIC_CONGESTION < trafficHistogram[i]) {
                    MAX_TRAFFIC_CONGESTION = trafficHistogram[i];
                }
            }

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
            Logger.getLogger(CloudCDN_MP.class.getName()).log(Level.SEVERE, null, e);
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

    @Override
    public void evaluate(Solution solution) throws JMException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
