package jmetal.problems.cloudcdn.f201603;

public class Documento {

    private int docId;
    private double docSize;
    private int numContenidos;
    private int provId;

    public Documento(int docId, double docSize, int numContenidos, int provId) {
        super();
        this.docId = docId;
        this.docSize = docSize;
        this.setNumContenidos(numContenidos);
        this.setProvId(provId);
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public double getDocSize() {
        return docSize;
    }

    public void setDocSize(double docSize) {
        this.docSize = docSize;
    }

    int getProvId() {
        return provId;
    }

    void setProvId(int provId) {
        this.provId = provId;
    }

    int getNumContenidos() {
        return numContenidos;
    }

    void setNumContenidos(int numContenidos) {
        this.numContenidos = numContenidos;
    }
}
