package jmetal.problems.cloudcdn.f201603;

import java.util.Comparator;

public class Trafico {

    int reqTime;
    int docId;
    double docSize; // GB
    int numContenidos;
    int regUsrId;

    public Trafico(int reqTime, int docId, double docSize, int numContenidos, int regUsrId) {
        super();
        this.reqTime = reqTime;
        this.docId = docId;
        this.docSize = docSize;
        this.regUsrId = regUsrId;
        this.numContenidos = numContenidos;
    }

    public int getNumContenidos() {
        return numContenidos;
    }

    public int getReqTime() {
        return reqTime;
    }

    public void setReqTime(int reqTime) {
        this.reqTime = reqTime;
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

    public int getRegUsrId() {
        return regUsrId;
    }

    public void setRegUsrId(int regUsrId) {
        this.regUsrId = regUsrId;
    }
}
