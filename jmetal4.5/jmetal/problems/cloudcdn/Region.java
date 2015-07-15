package jmetal.problems.cloudcdn;

public class Region {
	Integer regId;
	String regNombre;
	
	
	public Region(Integer regId, String regNombre) {
		super();
		this.regId = regId;
		this.regNombre = regNombre;
	}
	public Integer getRegId() {
		return regId;
	}
	public void setRegId(Integer regId) {
		this.regId = regId;
	}
	public String getRegNombre() {
		return regNombre;
	}
	public void setRegNombre(String regNombre) {
		this.regNombre = regNombre;
	}
}
