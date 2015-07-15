package jmetal.problems.cloudcdn;

public class Documento {
	Integer docId;
	Integer docSize;
	public Documento(Integer docId, Integer docSize) {
		super();
		this.docId = docId;
		this.docSize = docSize;
	}
	public Integer getDocId() {
		return docId;
	}
	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	public Integer getDocSize() {
		return docSize;
	}
	public void setDocSize(Integer docSize) {
		this.docSize = docSize;
	}
}
