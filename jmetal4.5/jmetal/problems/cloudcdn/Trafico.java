package jmetal.problems.cloudcdn;

import java.util.Comparator;

public class Trafico {
	int reqTime;
	int docId;
	double docSize; // GB
	int regUsrId;

	public Trafico(int reqTime, int docId, double docSize, int regUsrId) {
		super();
		this.reqTime = reqTime;
		this.docId = docId;
		this.docSize = docSize;
		this.regUsrId = regUsrId;
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
