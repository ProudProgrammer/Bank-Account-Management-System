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
	 * 	ACID elvek �rdek�ben, hogy ne legyen:
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
	 * 	Visszaadja a d�tumot egy 8 jegy� eg�sz sz�m form�j�ban
	 */
	private int getDatum() {
		GregorianCalendar kalendar = new GregorianCalendar();
		return (kalendar.get(Calendar.YEAR) * 10000 + (kalendar.get(Calendar.MONTH)+1) * 100 + kalendar.get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * 	Az adatb�zisban tal�lhat� �sszes �gyf�l darabsz�m�t adja vissza
	 */
	public ResultSet getUgyfelekSzama() throws SQLException {
		if(ugyfelekSzama == null) 
			ugyfelekSzama = conn.prepareStatement("select count(*) from ugyfel");
		return ugyfelekSzama.executeQuery();
	}
	
	/**
	 * 	Az adatb�zisban tal�lhat� �sszes sz�mla darabsz�m�t adja vissza
	 */
	public ResultSet getSzamlakSzama() throws SQLException {
		if(szamlakSzama == null) 
			szamlakSzama = conn.prepareStatement("select count(*) from szamla");
		return szamlakSzama.executeQuery();
	}
	
	/**
	 * 	�j �gyf�l l�trehoz�sa
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
	 * 	�gyfelek m�dos�t�sa
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
	 * 	�gyfelek t�rl�se. Nem fizikai t�rl�s, csak st�tusz m�dos�t�s.
	 * 	T�rl�s ut�n m�dos�t�s m�r nem lehets�ges.
	 * 	Csak olyan �gyf�l t�r�lhet� akinek nincsenek akt�v sz�ml�i. 
	 */
	public boolean ugyfelTorlese(int ugyfelszam) throws SQLException {
		boolean sikerultE = false;
		tranzakcioNyitas();
		long aktivSzamlak = aktivSzamlakDarabSzamaUgyfelhez(ugyfelszam);
		if(aktivSzamlak == 0) {
			if(pstmtUgyfelTorlese == null)
				pstmtUgyfelTorlese = conn.prepareStatement("update ugyfel set statusz = 't�r�lt' where ugyfelszam = ?");
			pstmtUgyfelTorlese.setInt(1, ugyfelszam);
			pstmtUgyfelTorlese.executeUpdate();
			sikerultE = true;
		}
		tranzakcioZaras();
		return sikerultE;
	}
	
	/**
	 * 	�gyfelek lek�rdez�se
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
	 * 	�j sz�mla l�trehoz�sa (tranzakci�)
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
			tranzakcioRogzites(getDatum(), "kezdeti befizet�s", egyenleg, szamlaszam, 0);
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	/**
	 *  Befizet�s a sz�ml�ra (tranzakci�)
	 */
	public boolean szamlaBefizetes(long szamlaszam, long osszeg) throws SQLException {
		tranzakcioNyitas();
		if(szamlaStatuszLekerdezesSzamlaszamSzerint(szamlaszam).equals("t�r�lt")) {
			tranzakcioZaras();
			return false;
		}
		if(pstmtSzamlaBefizetes == null)
			pstmtSzamlaBefizetes = conn.prepareStatement("update szamla set egyenleg = egyenleg + ? where szamlaszam = ?");
		pstmtSzamlaBefizetes.setLong(1, osszeg);
		pstmtSzamlaBefizetes.setLong(2, szamlaszam);
		pstmtSzamlaBefizetes.executeUpdate();
		tranzakcioRogzites(getDatum(), "p�nzt�ri befizet�s", osszeg, szamlaszam, 0);
		tranzakcioZaras();
		return true;
	}
	
	/**
	 *  Kifizet�s a sz�ml�r�l (tranzakci�)
	 *  Ugyanakkor egy bejegyz�s a tranzakci� t�bl�ba. A k�t m�velet atomiv� t�tele biztos�tva.
	 *  A met�dus sz�mla t�rl�sekor is megh�v�dhat �gy egy m�r megkezdett tranzakci� egyik m�velete lesz,
	 *  teh�t a commit nem adhat� ki a kifizet�s v�grehajt�sa ut�n mert az �ppen fut� tranzakci�nak m�g
	 *  vannak tov�bbi m�veletei. Visszat�r�s, hogy siker�lt e.
	 */
	public boolean szamlaKifizetes(long szamlaszam, long osszeg) throws SQLException {
		boolean zarniKell = false;
		if(tranzakcioFolyamatban){
			//ha egy tranzakci� m�r folyamatban van akkor nem csin�l semmit
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
			tranzakcioRogzites(getDatum(), "p�nzt�ri kifizet�s", (-1 * osszeg), szamlaszam, 0);
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
	 * 	Sz�ml�k t�rl�se. Nem fizikai t�rl�s, csak st�tusz m�dos�t�s.
	 * 	T�rl�s ut�n m�dos�t�s m�r nem lehets�ges.
	 * 	Ha a sz�mla egyenlege nem 0, akkor az egyenlegen lev� �sszeg kinull�z�dik p�nzt�ri kifizet�s c�m�n �s
	 * 	term�szetesen ekkor keletkezik egy bejegyz�s a tranakci� jelent�sek t�bl�ban. K�t dolgot kell biztos�tani:
	 * 	1. Atomis�g, hogy f�lbeszakadt tranzakci� ne okozzon inkonzisztenci�t
	 * 	2. Konkurrencia, nehogy pont utaljanak a sz�ml�ra mik�zben �ppen azt t�r�lj�k
	 * 	Visszat�r�s a kifizetnival� �sszeggel
	 */
	public long szamlaTorles(long szamlaszam) throws SQLException {
		tranzakcioNyitas();
		long egyenleg = szamlaEgyenlegLekerdezesSzamlaszamSzerint(szamlaszam);
		if(egyenleg != 0)
			szamlaKifizetes(szamlaszam, egyenleg);
		if(pstmtSzamlaTorles == null)
			pstmtSzamlaTorles = conn.prepareStatement("update szamla set statusz = 't�r�lt' where szamlaszam = ?");
		pstmtSzamlaTorles.setLong(1, szamlaszam);
		pstmtSzamlaTorles.executeUpdate();
		tranzakcioZaras();
		return egyenleg;
	}
	
	/**
	 * 	Sz�ml�k lek�rdez�se �gyf�lsz�m alapj�n
	 */
	public ResultSet szamlaLekerdezesUgyfelszamSzerint(int ugyfelszam) throws SQLException {
		if(pstmtSzamlaLekerdezesUgyfelszamSzerint == null)
			pstmtSzamlaLekerdezesUgyfelszamSzerint = conn.prepareStatement("select * from szamla where ugyfelszam = ?");
		pstmtSzamlaLekerdezesUgyfelszamSzerint.setInt(1, ugyfelszam);
		return pstmtSzamlaLekerdezesUgyfelszamSzerint.executeQuery();
	}
	
	/**
	 * 	Sz�mla egyenleg�nek lek�rdez�se sz�mlasz�m alapj�n
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
	 * 	Sz�mla statusz lek�rdez�se sz�mlasz�m alapj�n
	 * 	Visszat�r�s: "akt�v" vagy "t�r�lt"
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
	 * 	Visszadaja mennyi akt�v sz�mlasz�ma van az �gyf�lnek, hiszen ez �gyf�l t�rl�sn�l nagyon fontos mert olyan
	 * 	�gyf�l akinek vannak akt�v sz�ml�i azok nem t�r�lhet�k
	 */
	public long aktivSzamlakDarabSzamaUgyfelhez(int ugyfelszam) throws SQLException {
		if(pstmtAktivSzamlakDarabSzamaUgyfelhez == null)
			pstmtAktivSzamlakDarabSzamaUgyfelhez = conn.prepareStatement("select count(*) from szamla where ugyfelszam = ? and statusz = 'akt�v'");
		pstmtAktivSzamlakDarabSzamaUgyfelhez.setInt(1, ugyfelszam);
		ResultSet darab = pstmtAktivSzamlakDarabSzamaUgyfelhez.executeQuery();
		darab.next();
		return darab.getLong("count(*)");
	}
	
	/**
	 * 	�tutal�s (tranzakci�)
	 */
	public boolean atutalas(long szamlarol, long szamlara, long osszeg, String leiras) throws SQLException {
		tranzakcioNyitas();
		if(szamlaStatuszLekerdezesSzamlaszamSzerint(szamlarol).equals("akt�v") & szamlaStatuszLekerdezesSzamlaszamSzerint(szamlara).equals("akt�v")
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
	 * 	Minden tranzakci� eset�n besz�r�dik egy rekord a tranzakcio t�bl�ba.
	 * 	Ehhez persze k�l�n meg kell h�vni ezt a met�dust.
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
	 * 	El�k�sz�tett lek�rdez�sek a tranzakci�kr�l:
	 * 	Lehet�s�g van d�tum �s �sszeg tartom�ny szetint sz�rni,
	 * 	valamint pontos sz�mlasz�m megad�s�val. Sz�mlasz�m alapj�n
	 * 	val� sz�r�sn�l figyelembe van v�ve mind a forr�s �s c�l sz�mla is.
	 */
	public ResultSet tranzakcioLekerdezesCsakDatumSzerint(int datumTol, int datumIg, String rendezesSzempont) throws SQLException {
		if(rendezesSzempont.equals("Rendez�sD�tumSzerint")) {
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
		if(rendezesSzempont.equals("Rendez�sD�tumSzerint")) {
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
		if(rendezesSzempont.equals("Rendez�sD�tumSzerint")) {
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
		if(rendezesSzempont.equals("Rendez�sD�tumSzerint")) {
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
	 * 	Az alkalmaz�sb�l val� kil�p�skor h�v�dik meg �s z�r�dik a Connection
	 */
	public void close() {
		if(conn != null)
			try {
				conn.close();
			} catch (SQLException e) {}
	}
}
