package db;

public class Ugyfel {
	
	private int ugyfelszam;
	private String nev;
	private String lakcim;
	private String telszam;
	private String szigszam;
	private String statusz;
	
	public Ugyfel(int ugyfelszam, String nev, String lakcim, String telszam,
			String szigszam, String statusz) {
		super();
		this.ugyfelszam = ugyfelszam;
		this.nev = nev;
		this.lakcim = lakcim;
		this.telszam = telszam;
		this.szigszam = szigszam;
		this.statusz = statusz;
	}

	public int getUgyfelszam() {
		return ugyfelszam;
	}

	public String getNev() {
		return nev;
	}

	public String getLakcim() {
		return lakcim;
	}

	public String getTelszam() {
		return telszam;
	}

	public String getSzigszam() {
		return szigszam;
	}

	public String getStatusz() {
		return statusz;
	}

	public void setUgyfelszam(int ugyfelszam) {
		this.ugyfelszam = ugyfelszam;
	}

	public void setNev(String nev) {
		this.nev = nev;
	}

	public void setLakcim(String lakcim) {
		this.lakcim = lakcim;
	}

	public void setTelszam(String telszam) {
		this.telszam = telszam;
	}

	public void setSzigszam(String szigszam) {
		this.szigszam = szigszam;
	}

	public void setStatusz(String statusz) {
		this.statusz = statusz;
	}
	
}
