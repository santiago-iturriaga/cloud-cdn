package jmetal.problems.cloudcdn;

public class RegionUsuario {
	Integer regUsrId;
	String regNombre;
	Integer regId;
	public Integer getRegUsrId() {
		return regUsrId;
	}
	public void setRegUsrId(Integer regUsrId) {
		this.regUsrId = regUsrId;
	}
	public String getRegNombre() {
		return regNombre;
	}
	public void setRegNombre(String regNombre) {
		this.regNombre = regNombre;
	}
	public Integer getRegId() {
		return regId;
	}
	public void setRegId(Integer regId) {
		this.regId = regId;
	}
	public RegionUsuario(Integer regUsrId, String regNombre, Integer regId) {
		super();
		this.regUsrId = regUsrId;
		this.regNombre = regNombre;
		this.regId = regId;
	}
}
