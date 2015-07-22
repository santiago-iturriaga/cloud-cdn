package jmetal.problems.cloudcdn;

public class Documento {
	int docId;
	double docSize;
	public Documento(int docId, double docSize) {
		super();
		this.docId = docId;
		this.docSize = docSize;
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
}
