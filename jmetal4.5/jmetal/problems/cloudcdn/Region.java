package jmetal.problems.cloudcdn;

public class Region {
	int regId;
	String regNombre;
	
	
	public Region(int regId, String regNombre) {
		super();
		this.regId = regId;
		this.regNombre = regNombre;
	}
	public int getRegId() {
		return regId;
	}
	public void setRegId(int regId) {
		this.regId = regId;
	}
	public String getRegNombre() {
		return regNombre;
	}
	public void setRegNombre(String regNombre) {
		this.regNombre = regNombre;
	}
}
