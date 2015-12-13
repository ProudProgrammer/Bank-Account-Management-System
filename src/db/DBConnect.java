package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class DBConnect {
	
	private Connection conn = null;
	
	private boolean tranzakcioFolyamatban = false;
	
	private PreparedStatement ugyfelekSzama = null;
	private PreparedStatement szamlakSzama = null;
	
	private PreparedStatement pstmtUjUgyfel = null;
	private Statement stmtUgyfelModositasa = null;
	private PreparedStatement pstmtUgyfelTorlese = null;
	private Statement stmtUgyfelLekerdezes = null;
	
	private PreparedStatement pstmtUjSzamla = null;
	private PreparedStatement pstmtSzamlaBefizetes = null;
	private PreparedStatement pstmtSzamlaKifizetes = null;
	private PreparedStatement pstmtSzamlaTorles = null;
	private PreparedStatement pstmtSzamlaLekerdezesUgyfelszamSzerint = null;
	private PreparedStatement pstmtSzamlaEgyenlegLekerdezesSzamlaszamSzerint = null;
	private PreparedStatement pstmtSzamlaStauszLekerdezesSzamlaszamSzerint = null;
	private PreparedStatement pstmtAktivSzamlakDarabSzamaUgyfelhez = null;
	
	private PreparedStatement pstmtAtutalasLevonas = null;
	private PreparedStatement pstmtAtutalasHozzaadas = null;
	
	private PreparedStatement pstmtTranzakcioRogzites = null;
	private PreparedStatement pstmtForTLCSDSZRDSZ = null;
	private PreparedStatement pstmtForTLCSDSZROSZ = null;
	private PreparedStatement pstmtForTLDEOSZRDSZ = null;
	private PreparedStatement pstmtForTLDEOSZROSZ = null;
	private PreparedStatement pstmtForTLDESZSZRDSZ = null;
	private PreparedStatement pstmtForTLDESZSZROSZ = null;
	private PreparedStatement pstmtForTLDEOESZSZRDSZ = null;
	private PreparedStatement pstmtForTLDEOESZSZROSZ = null;
	
	private String[] settings = DBSettings.getSettings();
	
	public DBConnect() throws SQLException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		conn = DriverManager.getConnection("jdbc:mysql:" + settings[0], settings[1], settings[2]);
		conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		conn.setAutoCommit(true);
	}
	
	/**
	 * 	ACID elvek érdekében, hogy ne legyen:
	 * 	- dirty read
	 * 	- lost update
	 *  - inkonzisztencia
	 */
	private void tranzakcioNyitas() throws SQLException {
		tranzakcioFolyamatban = true;
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		conn.setAutoCommit(false);
	}
	
	private void tranzakcioZaras() throws SQLException {
		conn.commit();
		conn.setAutoCommit(true);
		conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		tranzakcioFolyamatban = false;
	}
	
	/**
	 * 	Visszaadja a dátumot egy 8 jegyû egész szám formájában
	 */
	private int getDatum() {
		GregorianCalendar kalendar = new GregorianCalendar();
		return (kalendar.get(Calendar.YEAR) * 10000 + (kalendar.get(Calendar.MONTH)+1) * 100 + kalendar.get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * 	Az adatbázisban található összes ügyfél darabszámát adja vissza
	 */
	public ResultSet getUgyfelekSzama() throws SQLException {
		if(ugyfelekSzama == null) 
			ugyfelekSzama = conn.prepareStatement("select count(*) from ugyfel");
		return ugyfelekSzama.executeQuery();
	}
	
	/**
	 * 	Az adatbázisban található összes számla darabszámát adja vissza
	 */
	public ResultSet getSzamlakSzama() throws SQLException {
		if(szamlakSzama == null) 
			szamlakSzama = conn.prepareStatement("select count(*) from szamla");
		return szamlakSzama.executeQuery();
	}
	
	/**
	 * 	Új ügyfél létrehozása
	 */
	public void ujUgyfel(int ugyfelszam, String nev, String lakcim, String telszam, String szigszam, String statusz) throws SQLException {
		if(pstmtUjUgyfel == null)
			pstmtUjUgyfel = conn.prepareStatement("insert into ugyfel values(?,?,?,?,?,?)");
		pstmtUjUgyfel.setInt(1, ugyfelszam);
		pstmtUjUgyfel.setString(2, nev);
		pstmtUjUgyfel.setString(3, lakcim);
		pstmtUjUgyfel.setString(4, telszam);
		pstmtUjUgyfel.setString(5, szigszam);
		pstmtUjUgyfel.setString(6, statusz);
		pstmtUjUgyfel.executeUpdate();
	}
	
	/**
	 * 	Ügyfelek módosítása
	 */
	public void ugyfelModositasa(int ugyfelszam, String nev, String lakcim, String telszam, String szigszam) throws SQLException {
		boolean voltelotte = false;
		if(!(nev.equals(""))) {
			nev = "nev = '" + nev + "'";
			voltelotte = true;
		}
		if(!(lakcim.equals(""))) {
			if(voltelotte)
				lakcim = ", lakcim = '" + lakcim + "'";
			else
				lakcim = "lakcim = '" + lakcim + "'";
			voltelotte = true;
		}
		if(!(telszam.equals(""))) {
			if(voltelotte)
				telszam = ", telszam = '" + telszam + "'";
			else
				telszam = "telszam = '" + telszam + "'";
			voltelotte = true;
		}
		if(!(szigszam.equals(""))) {
			if(voltelotte)
				szigszam = ", szigszam = '" + szigszam + "'";
			else
				szigszam = "szigszam = '" + szigszam + "'";
			voltelotte = true;
		}
		stmtUgyfelModositasa = conn.createStatement();
		stmtUgyfelModositasa.executeUpdate("update ugyfel set " + nev + lakcim + telszam + szigszam +  " where ugyfelszam = '" + ugyfelszam + "'");
	}
	
	/**
	 * 	Ügyfelek törlése. Nem fizikai törlés, csak státusz módosítás.
	 * 	Törlés után módosítás már nem lehetséges.
	 * 	Csak olyan ügyfél törölhetõ akinek nincsenek aktív számlái. 
	 */
	public boolean ugyfelTorlese(int ugyfelszam) throws SQLException {
		boolean sikerultE = false;
		tranzakcioNyitas();
		long aktivSzamlak = aktivSzamlakDarabSzamaUgyfelhez(ugyfelszam);
		if(aktivSzamlak == 0) {
			if(pstmtUgyfelTorlese == null)
				pstmtUgyfelTorlese = conn.prepareStatement("update ugyfel set statusz = 'törölt' where ugyfelszam = ?");
			pstmtUgyfelTorlese.setInt(1, ugyfelszam);
			pstmtUgyfelTorlese.executeUpdate();
			sikerultE = true;
		}
		tranzakcioZaras();
		return sikerultE;
	}
	
	/**
	 * 	Ügyfelek lekérdezése
	 */
	public ResultSet ugyfelLekerdezes(int ugyfelszam, String szigszam, String nev) throws SQLException {
		stmtUgyfelLekerdezes = conn.createStatement();
		if(ugyfelszam == 0 & szigszam.equals("") & nev.equals(""))
			return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel order by nev");
		if(ugyfelszam == 0 & szigszam.equals(""))
			return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel where nev like '" + nev + "%' order by nev");
		if(ugyfelszam == 0 & nev.equals(""))
			return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel where szigszam = '" + szigszam + "' order by nev");
		if(szigszam.equals("") & nev.equals(""))
			return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel where ugyfelszam = '" + ugyfelszam + "' order by nev");
		if(ugyfelszam == 0)
			return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel where szigszam = '" + szigszam + "' and nev like '" + nev + "%' order by nev");
		if(szigszam.equals(""))
			return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel where ugyfelszam = '" + ugyfelszam + "' and nev like '" + nev + "%' order by nev");
		if(nev.equals(""))
			return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel where ugyfelszam = '" + ugyfelszam + "' and szigszam = '" + szigszam + "' order by nev");
		return stmtUgyfelLekerdezes.executeQuery("select * from ugyfel where ugyfelszam = '" + ugyfelszam + "' and szigszam = '" + szigszam + "' and nev like '" + nev + "%' order by nev");
	}
	
	/**
	 * 	Új számla létrehozása (tranzakció)
	 */
	public void ujSzamla(long szamlaszam, int ugyfelszam, long egyenleg, String statusz) throws SQLException {
		conn.setAutoCommit(false);
		if(pstmtUjSzamla == null)
			pstmtUjSzamla = conn.prepareStatement("insert into szamla values(?,?,?,?)");
		pstmtUjSzamla.setLong(1, szamlaszam);
		pstmtUjSzamla.setInt(2, ugyfelszam);
		pstmtUjSzamla.setLong(3, egyenleg);
		pstmtUjSzamla.setString(4, statusz);
		pstmtUjSzamla.executeUpdate();
		if(egyenleg > 0)
			tranzakcioRogzites(getDatum(), "kezdeti befizetés", egyenleg, szamlaszam, 0);
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	/**
	 *  Befizetés a számlára (tranzakció)
	 */
	public boolean szamlaBefizetes(long szamlaszam, long osszeg) throws SQLException {
		tranzakcioNyitas();
		if(szamlaStatuszLekerdezesSzamlaszamSzerint(szamlaszam).equals("törölt")) {
			tranzakcioZaras();
			return false;
		}
		if(pstmtSzamlaBefizetes == null)
			pstmtSzamlaBefizetes = conn.prepareStatement("update szamla set egyenleg = egyenleg + ? where szamlaszam = ?");
		pstmtSzamlaBefizetes.setLong(1, osszeg);
		pstmtSzamlaBefizetes.setLong(2, szamlaszam);
		pstmtSzamlaBefizetes.executeUpdate();
		tranzakcioRogzites(getDatum(), "pénztári befizetés", osszeg, szamlaszam, 0);
		tranzakcioZaras();
		return true;
	}
	
	/**
	 *  Kifizetés a számláról (tranzakció)
	 *  Ugyanakkor egy bejegyzés a tranzakció táblába. A két mûvelet atomivá tétele biztosítva.
	 *  A metódus számla törlésekor is meghívódhat így egy már megkezdett tranzakció egyik mûvelete lesz,
	 *  tehát a commit nem adható ki a kifizetés végrehajtása után mert az éppen futó tranzakciónak még
	 *  vannak további mûveletei. Visszatérés, hogy sikerült e.
	 */
	public boolean szamlaKifizetes(long szamlaszam, long osszeg) throws SQLException {
		boolean zarniKell = false;
		if(tranzakcioFolyamatban){
			//ha egy tranzakció már folyamatban van akkor nem csinál semmit
		}else {
			tranzakcioNyitas();
			zarniKell = true;
		}
		if(szamlaEgyenlegLekerdezesSzamlaszamSzerint(szamlaszam) >= osszeg) {
			if(pstmtSzamlaKifizetes == null)
				pstmtSzamlaKifizetes = conn.prepareStatement("update szamla set egyenleg = egyenleg - ? where szamlaszam = ?");
			pstmtSzamlaKifizetes.setLong(1, osszeg);
			pstmtSzamlaKifizetes.setLong(2, szamlaszam);
			pstmtSzamlaKifizetes.executeUpdate();
			tranzakcioRogzites(getDatum(), "pénztári kifizetés", (-1 * osszeg), szamlaszam, 0);
		}else {
			if(zarniKell)
				tranzakcioZaras();
			return false;
		}
		if(zarniKell)
			tranzakcioZaras();
		return true;
	}
	
	/**
	 * 	Számlák törlése. Nem fizikai törlés, csak státusz módosítás.
	 * 	Törlés után módosítás már nem lehetséges.
	 * 	Ha a számla egyenlege nem 0, akkor az egyenlegen levõ összeg kinullázódik pénztári kifizetés címén és
	 * 	természetesen ekkor keletkezik egy bejegyzés a tranakció jelentések táblában. Két dolgot kell biztosítani:
	 * 	1. Atomiság, hogy félbeszakadt tranzakció ne okozzon inkonzisztenciát
	 * 	2. Konkurrencia, nehogy pont utaljanak a számlára miközben éppen azt töröljük
	 * 	Visszatérés a kifizetnivaló összeggel
	 */
	public long szamlaTorles(long szamlaszam) throws SQLException {
		tranzakcioNyitas();
		long egyenleg = szamlaEgyenlegLekerdezesSzamlaszamSzerint(szamlaszam);
		if(egyenleg != 0)
			szamlaKifizetes(szamlaszam, egyenleg);
		if(pstmtSzamlaTorles == null)
			pstmtSzamlaTorles = conn.prepareStatement("update szamla set statusz = 'törölt' where szamlaszam = ?");
		pstmtSzamlaTorles.setLong(1, szamlaszam);
		pstmtSzamlaTorles.executeUpdate();
		tranzakcioZaras();
		return egyenleg;
	}
	
	/**
	 * 	Számlák lekérdezése ügyfélszám alapján
	 */
	public ResultSet szamlaLekerdezesUgyfelszamSzerint(int ugyfelszam) throws SQLException {
		if(pstmtSzamlaLekerdezesUgyfelszamSzerint == null)
			pstmtSzamlaLekerdezesUgyfelszamSzerint = conn.prepareStatement("select * from szamla where ugyfelszam = ?");
		pstmtSzamlaLekerdezesUgyfelszamSzerint.setInt(1, ugyfelszam);
		return pstmtSzamlaLekerdezesUgyfelszamSzerint.executeQuery();
	}
	
	/**
	 * 	Számla egyenlegének lekérdezése számlaszám alapján
	 */
	public long szamlaEgyenlegLekerdezesSzamlaszamSzerint(long szamlaszam) throws SQLException {
		if(pstmtSzamlaEgyenlegLekerdezesSzamlaszamSzerint == null)
			pstmtSzamlaEgyenlegLekerdezesSzamlaszamSzerint = conn.prepareStatement("select egyenleg from szamla where szamlaszam = ?");
		pstmtSzamlaEgyenlegLekerdezesSzamlaszamSzerint.setLong(1, szamlaszam);
		ResultSet egyenleg = pstmtSzamlaEgyenlegLekerdezesSzamlaszamSzerint.executeQuery();
		egyenleg.next();
		return egyenleg.getLong("egyenleg");
	}
	
	/**
	 * 	Számla statusz lekérdezése számlaszám alapján
	 * 	Visszatérés: "aktív" vagy "törölt"
	 */
	public String szamlaStatuszLekerdezesSzamlaszamSzerint(long szamlaszam) throws SQLException {
		if(pstmtSzamlaStauszLekerdezesSzamlaszamSzerint == null)
			pstmtSzamlaStauszLekerdezesSzamlaszamSzerint = conn.prepareStatement("select statusz from szamla where szamlaszam = ?");
		pstmtSzamlaStauszLekerdezesSzamlaszamSzerint.setLong(1, szamlaszam);
		ResultSet statusz = pstmtSzamlaStauszLekerdezesSzamlaszamSzerint.executeQuery();
		statusz.next();
		return statusz.getString("statusz");
	}
	
	/**
	 * 	Visszadaja mennyi aktív számlaszáma van az ügyfélnek, hiszen ez ügyfél törlésnél nagyon fontos mert olyan
	 * 	ügyfél akinek vannak aktív számlái azok nem törölhetõk
	 */
	public long aktivSzamlakDarabSzamaUgyfelhez(int ugyfelszam) throws SQLException {
		if(pstmtAktivSzamlakDarabSzamaUgyfelhez == null)
			pstmtAktivSzamlakDarabSzamaUgyfelhez = conn.prepareStatement("select count(*) from szamla where ugyfelszam = ? and statusz = 'aktív'");
		pstmtAktivSzamlakDarabSzamaUgyfelhez.setInt(1, ugyfelszam);
		ResultSet darab = pstmtAktivSzamlakDarabSzamaUgyfelhez.executeQuery();
		darab.next();
		return darab.getLong("count(*)");
	}
	
	/**
	 * 	Átutalás (tranzakció)
	 */
	public boolean atutalas(long szamlarol, long szamlara, long osszeg, String leiras) throws SQLException {
		tranzakcioNyitas();
		if(szamlaStatuszLekerdezesSzamlaszamSzerint(szamlarol).equals("aktív") & szamlaStatuszLekerdezesSzamlaszamSzerint(szamlara).equals("aktív")
				& szamlaEgyenlegLekerdezesSzamlaszamSzerint(szamlarol) >= osszeg) {
			if(pstmtAtutalasLevonas == null)
				pstmtAtutalasLevonas = conn.prepareStatement("update szamla set egyenleg = egyenleg - ? where szamlaszam = ?");
			pstmtAtutalasLevonas.setLong(1, osszeg);
			pstmtAtutalasLevonas.setLong(2, szamlarol);
			pstmtAtutalasLevonas.executeUpdate();
			if(pstmtAtutalasHozzaadas == null)
				pstmtAtutalasHozzaadas = conn.prepareStatement("update szamla set egyenleg = egyenleg + ? where szamlaszam = ?");
			pstmtAtutalasHozzaadas.setLong(1, osszeg);
			pstmtAtutalasHozzaadas.setLong(2, szamlara);
			pstmtAtutalasHozzaadas.executeUpdate();
			tranzakcioRogzites(getDatum(), leiras, osszeg, szamlara, szamlarol);
		}else {
			tranzakcioZaras();
			return false;
		}
		tranzakcioZaras();
		return true;
	}
	
	/**
	 * 	Minden tranzakció esetén beszúródik egy rekord a tranzakcio táblába.
	 * 	Ehhez persze külön meg kell hívni ezt a metódust.
	 */
	public void tranzakcioRogzites(int datum, String leiras, long osszeg, long szamlaszam, long kuldo) throws SQLException {
		if(pstmtTranzakcioRogzites == null)
			pstmtTranzakcioRogzites = conn.prepareStatement("insert into tranzakcio values(?,?,?,?,?)");
		pstmtTranzakcioRogzites.setInt(1, datum);
		pstmtTranzakcioRogzites.setString(2, leiras);
		pstmtTranzakcioRogzites.setLong(3, osszeg);
		pstmtTranzakcioRogzites.setLong(4, szamlaszam);
		pstmtTranzakcioRogzites.setLong(5, kuldo);
		pstmtTranzakcioRogzites.executeUpdate();
	}
	
	/**
	 * 	Elõkészített lekérdezések a tranzakciókról:
	 * 	Lehetõség van dátum és összeg tartomány szetint szûrni,
	 * 	valamint pontos számlaszám megadásával. Számlaszám alapján
	 * 	való szûrésnél figyelembe van véve mind a forrás és cél számla is.
	 */
	public ResultSet tranzakcioLekerdezesCsakDatumSzerint(int datumTol, int datumIg, String rendezesSzempont) throws SQLException {
		if(rendezesSzempont.equals("RendezésDátumSzerint")) {
			if (pstmtForTLCSDSZRDSZ == null)
				pstmtForTLCSDSZRDSZ = conn.prepareStatement("select * from tranzakcio where datum >= ? and datum <= ? order by datum");
			pstmtForTLCSDSZRDSZ.setInt(1, datumTol);
			pstmtForTLCSDSZRDSZ.setInt(2, datumIg);
			return pstmtForTLCSDSZRDSZ.executeQuery();
		}
		if (pstmtForTLCSDSZROSZ == null)
			pstmtForTLCSDSZROSZ = conn.prepareStatement("select * from tranzakcio where datum >= ? and datum <= ? order by osszeg");
		pstmtForTLCSDSZROSZ.setInt(1, datumTol);
		pstmtForTLCSDSZROSZ.setInt(2, datumIg);
		return pstmtForTLCSDSZROSZ.executeQuery();
	}
	
	public ResultSet tranzakcioLekerdezesDatumEsOsszegSzerint(int datumTol, int datumIg, long osszegTol, long osszegIg, String rendezesSzempont) throws SQLException {
		if(rendezesSzempont.equals("RendezésDátumSzerint")) {
			if (pstmtForTLDEOSZRDSZ == null)
				pstmtForTLDEOSZRDSZ = conn.prepareStatement("select * from tranzakcio where ((datum >= ? and datum <= ?) and (osszeg >= ? and osszeg <= ?)) order by datum");
			pstmtForTLDEOSZRDSZ.setInt(1, datumTol);
			pstmtForTLDEOSZRDSZ.setInt(2, datumIg);
			pstmtForTLDEOSZRDSZ.setLong(3, osszegTol);
			pstmtForTLDEOSZRDSZ.setLong(4, osszegIg);
			return pstmtForTLDEOSZRDSZ.executeQuery();
		}
		if (pstmtForTLDEOSZROSZ == null)
			pstmtForTLDEOSZROSZ = conn.prepareStatement("select * from tranzakcio where ((datum >= ? and datum <= ?) and (osszeg >= ? and osszeg <= ?)) order by osszeg");
		pstmtForTLDEOSZROSZ.setInt(1, datumTol);
		pstmtForTLDEOSZROSZ.setInt(2, datumIg);
		pstmtForTLDEOSZROSZ.setLong(3, osszegTol);
		pstmtForTLDEOSZROSZ.setLong(4, osszegIg);
		return pstmtForTLDEOSZROSZ.executeQuery();
			
	}
	
	public ResultSet tranzakcioLekerdezesDatumEsSzamlaszamSzerint(int datumTol, int datumIg, long szamlaszam, String rendezesSzempont) throws SQLException {
		if(rendezesSzempont.equals("RendezésDátumSzerint")) {
			if (pstmtForTLDESZSZRDSZ == null)
				pstmtForTLDESZSZRDSZ = conn.prepareStatement("select * from tranzakcio where ((datum >= ? and datum <= ?) and (szamlaszam = ? or kuldo = ?)) order by datum");
			pstmtForTLDESZSZRDSZ.setInt(1, datumTol);
			pstmtForTLDESZSZRDSZ.setInt(2, datumIg);
			pstmtForTLDESZSZRDSZ.setLong(3, szamlaszam);
			pstmtForTLDESZSZRDSZ.setLong(4, szamlaszam);
			return pstmtForTLDESZSZRDSZ.executeQuery();
		}
		if (pstmtForTLDESZSZROSZ == null)
			pstmtForTLDESZSZROSZ = conn.prepareStatement("select * from tranzakcio where ((datum >= ? and datum <= ?) and (szamlaszam = ? or kuldo = ?)) order by osszeg");
		pstmtForTLDESZSZROSZ.setInt(1, datumTol);
		pstmtForTLDESZSZROSZ.setInt(2, datumIg);
		pstmtForTLDESZSZROSZ.setLong(3, szamlaszam);
		pstmtForTLDESZSZROSZ.setLong(4, szamlaszam);
		return pstmtForTLDESZSZROSZ.executeQuery();
	}
	
	public ResultSet tranzakcioLekerdezesDatumEsOsszegEsSzamlaszamSzerint(int datumTol, int datumIg, long osszegTol, long osszegIg, long szamlaszam, String rendezesSzempont) throws SQLException {
		if(rendezesSzempont.equals("RendezésDátumSzerint")) {
			if(pstmtForTLDEOESZSZRDSZ == null)
				pstmtForTLDEOESZSZRDSZ = conn.prepareStatement("select * from tranzakcio where ((datum >= ? and datum <= ?) and (osszeg >= ? and osszeg <= ?) and (szamlaszam = ? or kuldo = ?)) order by datum");
			pstmtForTLDEOESZSZRDSZ.setInt(1, datumTol);
			pstmtForTLDEOESZSZRDSZ.setInt(2, datumIg);
			pstmtForTLDEOESZSZRDSZ.setLong(3, osszegTol);
			pstmtForTLDEOESZSZRDSZ.setLong(4, osszegIg);
			pstmtForTLDEOESZSZRDSZ.setLong(5, szamlaszam);
			pstmtForTLDEOESZSZRDSZ.setLong(6, szamlaszam);
			return pstmtForTLDEOESZSZRDSZ.executeQuery();
		}
		if(pstmtForTLDEOESZSZROSZ == null)
			pstmtForTLDEOESZSZROSZ = conn.prepareStatement("select * from tranzakcio where ((datum >= ? and datum <= ?) and (osszeg >= ? and osszeg <= ?) and (szamlaszam = ? or kuldo = ?)) order by osszeg");
		pstmtForTLDEOESZSZROSZ.setInt(1, datumTol);
		pstmtForTLDEOESZSZROSZ.setInt(2, datumIg);
		pstmtForTLDEOESZSZROSZ.setLong(3, osszegTol);
		pstmtForTLDEOESZSZROSZ.setLong(4, osszegIg);
		pstmtForTLDEOESZSZROSZ.setLong(5, szamlaszam);
		pstmtForTLDEOESZSZROSZ.setLong(6, szamlaszam);
		return pstmtForTLDEOESZSZROSZ.executeQuery();
	}
	
	/**
	 * 	Az alkalmazásból való kilépéskor hívódik meg és záródik a Connection
	 */
	public void close() {
		if(conn != null)
			try {
				conn.close();
			} catch (SQLException e) {}
	}
}
