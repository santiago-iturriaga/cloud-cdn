package jmetal.problems.cloudcdn;

public class Trafico {
	Integer reqTime;
	Integer docId;
	Integer docSize;
	Integer regUsrId;
	public Trafico(Integer reqTime, Integer docId, Integer docSize,
			Integer regUsrId) {
		super();
		this.reqTime = reqTime;
		this.docId = docId;
		this.docSize = docSize;
		this.regUsrId = regUsrId;
	}
	public Integer getReqTime() {
		return reqTime;
	}
	public void setReqTime(Integer reqTime) {
		this.reqTime = reqTime;
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
	public Integer getRegUsrId() {
		return regUsrId;
	}
	public void setRegUsrId(Integer regUsrId) {
		this.regUsrId = regUsrId;
	}
}
