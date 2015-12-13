package ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import db.DBConnect;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class MainWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JMenuBar jJMenuBar = null;
	private JMenu szamlavezetesMenu = null;					
	private JMenu beallitasokMenu = null;				
	private JMenu sugoMenu = null;				
	
	private JMenu ugyfelekNyilvantartasaMenu = null;				//Számlavezetés - Ügyfelek nyilvántartása
	private JMenuItem ujUgyfelMenuItem = null;						//Számlavezetés - Ügyfelek nyilvántartása - Új ügyfél
	private JMenuItem ugyfelModositasMenuItem = null;				//Számlavezetés - Ügyfelek nyilvántartása - Ügyfél módosítás / lekérdezés
	
	private JMenu szamlakezelesMenu = null;							//Számlavezetés - Számlakezelés
	private JMenuItem ujSzamlaMenuItem = null;						//Számlavezetés - Számlakezelés - Új számla
	private JMenuItem szamlaTorlesMenuItem = null;					//Számlavezetés - Számlakezelés - Számla törlés / lekérdezés
	
	private JMenuItem atutalasokMenuItem = null;					//Számlavezetés - Átutalások
	private JMenuItem befizetesMenuItem = null;						//Számlavezetés - Pénztári befizetés
	private JMenuItem kifizetesMenuItem = null;						//Számlavezetés - Pénztári kifizetés
	private JMenuItem jelentesekMenuItem = null;					//Számlavezetés - Jelentések
	private JMenuItem kilepesMenuItem = null;						//Számlavezetés - Kilépés
	
	private JMenuItem adatbazisMenuItem = null;						//Beállítások - Adatbázis
	private JMenuItem felhasznaloiFeluletMenuItem = null;			//Beállítások - Felhasználói felület
	
	private JMenuItem nevjegyMenuItem = null;						//Súgó - Névjegy
	private JMenuItem felhFeltMenuItem = null;						//Súgó - Felhasználási feltételek
	
	private Rectangle rectangle = null;
	private HelpPanel helpPanel = null;								//Felhasználási feltételek lapja
	private MainPanel mainPanel = null;								//Nyitó (fõ)lap
	private JelentesPanel jelentesPanel = null;						//Jelentések lapja
	private AtutalasPanel atutalasPanel = null;						//Átutalás lapja
	private JPanel jPanel = null;									//Tartalompanel - ContentPane
	private JPanel activePanel = null;								//Mindig az éppen látható panelt jelenti
	
	private DBConnect dBConnect = null;								//Adatbáziskapcsolat objektuma
	private boolean connected = false;
	
	private File skinSettingsFile = new File("settings/skinsettings.dat"); 
	private Scanner skinBetoltese = null; 
	private String skin = "";
	
	private GregorianCalendar kalendar = new GregorianCalendar();
	
	public MainWindow() {
		initialize();
		setVisible(true);
		dataBaseInit();
	}

	private void initialize() {
		setSize(800, 600);
		setBounds(getRectangle());
        setTitle("Banki számlavezetõ rendszer");
        setIconImage(this.getToolkit().createImage("icons/Pie Chart.png"));
        setContentPane(getJPanel());
        setJMenuBar(getJJMenuBar());
        getJPanel().add(getMainPanel(), BorderLayout.CENTER);
        setActivePanel(mainPanel);
        
    	try {
			skinBetoltese = new Scanner(skinSettingsFile);
			if(skinBetoltese.hasNext()) {
				skinBetoltese.nextLine();
				skin = skinBetoltese.nextLine();
			}
		} catch (FileNotFoundException e) {
		} catch (NoSuchElementException e) {}
		
		try {
			UIManager.setLookAndFeel(skin);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception ex) {}
       
       
        getUjUgyfelMenuItem().addActionListener(this);
        getUgyfelModositasMenuItem().addActionListener(this);
        getUjSzamlaMenuItem().addActionListener(this);
        getSzamlaTorlesMenuItem().addActionListener(this);
        getAtutalasokMenuItem().addActionListener(this);
        getBefizetesMenuItem().addActionListener(this);
        getKifizetesMenuItem().addActionListener(this);
        getJelentesekMenuItem().addActionListener(this);
        getKilepesMenuItem().addActionListener(this);
        getAdatbazisMenuItem().addActionListener(this);
        getFelhasznaloiFeluletMenuItem().addActionListener(this);
        getNevjegyMenuItem().addActionListener(this);
        getFelhFeltMenuItem().addActionListener(this);
 
	/**
	 * 	Alkalmazás bezárása az ablakbezáróval
	 * 	Az adatbázis kapcsolat helyes lezárása
	 * 	Az adatbázis kapcsolat az alkalmazás futása alatt folyamatosan fent áll
	 */
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		if(connected)
        			dBConnect.close();
        		System.exit(0);
        	}
		});
	}
	
	/**	
	 * 	Interface az adatbázis kapcsolathoz
	 * 	A MainWindow felelõs az adatbázis kapcsolatért tehát tõle kell elkérni
	 */
	public DBConnect getDBConnect() {
		return dBConnect;
	}
	
	/**
	 * 	Induláskor az ablak mérete a képernyõ fele és középen elhelyezett
	 */
	private Rectangle getRectangle() {
		if (rectangle == null) {
			rectangle = new Rectangle((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - this.getWidth()/2,
					(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - this.getHeight()/2, this.getWidth(), this.getHeight());
		}
		return rectangle;
	}
	
	/**	
	 *	Panelcsere automatizálása
	 *	Az éppen aktív, tehát látható panelt kiveszi a tartalompanelbõl
	 *	és láthatatlanná teszi. Helyette a paraméterben megadott
	 *	panelt teszi be a tartalompanelbe és teszi láthatóvá.
	 */	
	private void changePanel(JPanel panel) {
		getActivePanel().setVisible(false);
		getJPanel().remove(getActivePanel());
		getJPanel().add(panel, BorderLayout.CENTER);
		panel.setVisible(true);
		setActivePanel(panel);
	}
	
	private JPanel getActivePanel() {
		return activePanel;
	}
	
	private void setActivePanel(JPanel panel) {
		activePanel = panel;
	}
	
	/**
	 *	Adatbázis inicializálása
	 * 	- az alkalmazás elindításakor mindig
	 * 	- új adatbázis kapcsolat beállítások megadásakor mindig
	 */
	private void dataBaseInit() {
		szamlavezetesMenu.setEnabled(false);
		beallitasokMenu.setEnabled(false);
		sugoMenu.setEnabled(false);
		mainPanel.getJTextArea().setText("Banki számlavezetõ rendszer\n\n" +
				"A rendszer még nem használatkész. Adatbázis beállítások véglegesítése folyamatban. Ez több percig is eltarthat.");
		new DbConnectThread();
	}
	
	/**	
	 *	Kapcsolódás az adatbázishoz külön szálon
	 * 	Ezátal a felhasználói interfész kirajzolása nincs blokkolva:
	 * 	A felhasználót ezáltal értesíteni lehet, hogy az datbázis beállítások érvényesítésére 
	 * 	kell várakozni. Persze a menu blokkolva van ilyenkor mert adatbázis nélkül nem lehet
	 * 	dolgozni. Tehát csak a GUI blokkolásának megakadályozása miatt van a külön szál
	 * 	nem azért, hogy kapcsolódás közben a felhasználó bármit is csinálhasson.
	 */ 
	private class DbConnectThread extends Thread {
		
		public DbConnectThread() {
			start();
		}
		
		public void run() {
			try {
				dBConnect = new DBConnect();
				connected = true;
			} catch (SQLException e) {
				connected = false;
				nincsKapcsolatMessage();
			}
			mainPanelFrissites();
			szamlavezetesMenu.setEnabled(true);
			beallitasokMenu.setEnabled(true);
			sugoMenu.setEnabled(true);
		}
	}
	
	/**
	 * 	Felügyeli(ha meghívódik), hogy mindig az aktuális értékek jelenjenek meg
	 *  a fõpanel ügyfelek darabszámát és számlák darabszámát illetõen.
	 *  Szinkronizált, mert két külön szál is meghívhatja (bár egyszerre nem).
	 */
	private synchronized void mainPanelFrissites() {
		if(connected) {
			BigDecimal ugyfelekSzama = null;
			BigDecimal szamlakSzama = null;
			try {
				ResultSet ugyfelekSzamaRS = dBConnect.getUgyfelekSzama();
				ResultSet szamlakSzamaRS = dBConnect.getSzamlakSzama();
				if(ugyfelekSzamaRS.next())
					ugyfelekSzama = ugyfelekSzamaRS.getBigDecimal(1);
				if(szamlakSzamaRS.next())
					szamlakSzama = szamlakSzamaRS.getBigDecimal(1);
				mainPanel.getJTextArea().setText("Banki számlavezetõ rendszer\n\n" + "A rendszer használatkész. Jó munkát.\n\n" +
						"Az adatbázisban levõ ügyfelek száma: " + ugyfelekSzama + 
						"\nAz adatbázisban levõ számlák száma: " + szamlakSzama);
			} catch (SQLException e) {
				connected = false;
				mainPanel.getJTextArea().setText("Banki számlavezetõ rendszer\n\n" + "Nincs kapcsolat az adatbázissal.");
				nincsKapcsolatMessage();
			}
		} else {
			mainPanel.getJTextArea().setText("Banki számlavezetõ rendszer\n\n" + "Nincs kapcsolat az adatbázissal.");
		}
		mainPanel.getJTextArea().append("\n\nA pontos dátum: " + kalendar.get(Calendar.YEAR) + "." + (kalendar.get(Calendar.MONTH) + 1) + 
				"." + kalendar.get(Calendar.DAY_OF_MONTH) + ".");
		}
	
	/**
	 * 	Tartalompanel - ContentPane
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
		}
		return jPanel;
	}
	
	/**
	 * 	A panelok
	 */
	public MainPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new MainPanel();
		}
		return mainPanel;
	}
	
	public HelpPanel getHelpPanel() {
		if (helpPanel == null) {
			try {
				helpPanel = new HelpPanel();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Nincs meg a következõ file: \"help.txt\" !", "Hiba", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		return helpPanel;
	}
	
	public JelentesPanel getJelentesPanel() {
		if (jelentesPanel == null) {
			jelentesPanel = new JelentesPanel(this);
		}
		return jelentesPanel;
	}
	
	public AtutalasPanel getAtutalasPanel() {
		if (atutalasPanel == null) {
			atutalasPanel = new AtutalasPanel(this);
		}
		return atutalasPanel;
	}

	/**
	* 	A menüsor és menüelemek
	*/
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getSzamlavezetesMenu());
			jJMenuBar.add(getBeallitasokMenu());
			jJMenuBar.add(getSugoMenu());
		}
		return jJMenuBar;
	}

	/**
	 * 	Számlavezetés
	 */
	private JMenu getSzamlavezetesMenu() {
		if (szamlavezetesMenu == null) {
			szamlavezetesMenu = new JMenu();
			szamlavezetesMenu.setText("Számlavezetés");
			szamlavezetesMenu.add(getUgyfelekNyilvantartasaMenu());
			szamlavezetesMenu.add(getSzamlakezelesMenu());
			szamlavezetesMenu.add(getAtutalasokMenuItem());
			szamlavezetesMenu.add(getBefizetesMenuItem());
			szamlavezetesMenu.add(getKifizetesMenuItem());
			szamlavezetesMenu.add(getJelentesekMenuItem());
			szamlavezetesMenu.addSeparator();
			szamlavezetesMenu.add(getKilepesMenuItem());
		}
		return szamlavezetesMenu;
	}

	private JMenu getUgyfelekNyilvantartasaMenu() {
		if (ugyfelekNyilvantartasaMenu == null) {
			ugyfelekNyilvantartasaMenu = new JMenu();
			ugyfelekNyilvantartasaMenu.setText("Ügyfelek nyilvántartása");
			ugyfelekNyilvantartasaMenu.add(getUjUgyfelMenuItem());
			ugyfelekNyilvantartasaMenu.add(getUgyfelModositasMenuItem());
			ugyfelekNyilvantartasaMenu.setIcon(new ImageIcon("icons/Profile.png"));
			
		}
		return ugyfelekNyilvantartasaMenu;
	}

	private JMenuItem getUjUgyfelMenuItem() {
		if (ujUgyfelMenuItem == null) {
			ujUgyfelMenuItem = new JMenuItem();
			ujUgyfelMenuItem.setText("Új ügyfél");
			ujUgyfelMenuItem.setIcon(new ImageIcon("icons/Add.png"));
		}
		return ujUgyfelMenuItem;
	}

	private JMenuItem getUgyfelModositasMenuItem() {
		if (ugyfelModositasMenuItem == null) {
			ugyfelModositasMenuItem = new JMenuItem();
			ugyfelModositasMenuItem.setText("Ügyfél módosítás / lekérdezés");
			ugyfelModositasMenuItem.setIcon(new ImageIcon("icons/Delete.png"));
		}
		return ugyfelModositasMenuItem;
	}
	
	private JMenu getSzamlakezelesMenu() {
		if (szamlakezelesMenu == null) {
			szamlakezelesMenu = new JMenu();
			szamlakezelesMenu.setText("Számlakezelés");
			szamlakezelesMenu.setIcon(new ImageIcon("icons/Print.png"));
			szamlakezelesMenu.add(getUjSzamlaMenuItem());
			szamlakezelesMenu.add(getSzamlaTorlesMenuItem());
		}
		return szamlakezelesMenu;
	}
	
	private JMenuItem getUjSzamlaMenuItem() {
		if (szamlaTorlesMenuItem == null) {
			szamlaTorlesMenuItem = new JMenuItem();
			szamlaTorlesMenuItem.setText("Új számla");
			szamlaTorlesMenuItem.setIcon(new ImageIcon("icons/Add.png"));
		}
		return szamlaTorlesMenuItem;
	}
	
	private JMenuItem getSzamlaTorlesMenuItem() {
		if (ujSzamlaMenuItem == null) {
			ujSzamlaMenuItem = new JMenuItem();
			ujSzamlaMenuItem.setText("Számla törlés / lekérdezés");
			ujSzamlaMenuItem.setIcon(new ImageIcon("icons/Delete.png"));
		}
		return ujSzamlaMenuItem;
	}

	private JMenuItem getAtutalasokMenuItem() {
		if (atutalasokMenuItem == null) {
			atutalasokMenuItem = new JMenuItem();
			atutalasokMenuItem.setText("Átutalások");
			atutalasokMenuItem.setIcon(new ImageIcon("icons/Line Chart.png"));
		}
		return atutalasokMenuItem;
	}

	private JMenuItem getBefizetesMenuItem() {
		if (befizetesMenuItem == null) {
			befizetesMenuItem = new JMenuItem();
			befizetesMenuItem.setText("Pénztári befizetés");
			befizetesMenuItem.setIcon(new ImageIcon("icons/Next.png"));
		}
		return befizetesMenuItem;
	}

	private JMenuItem getKifizetesMenuItem() {
		if (kifizetesMenuItem == null) {
			kifizetesMenuItem = new JMenuItem();
			kifizetesMenuItem.setText("Pénztári kifizetés");
			kifizetesMenuItem.setIcon(new ImageIcon("icons/Back.png"));
		}
		return kifizetesMenuItem;
	}

	private JMenuItem getJelentesekMenuItem() {
		if (jelentesekMenuItem == null) {
			jelentesekMenuItem = new JMenuItem();
			jelentesekMenuItem.setText("Jelentések");
			jelentesekMenuItem.setIcon(new ImageIcon("icons/Bar Chart.png"));
		}
		return jelentesekMenuItem;
	}
	
	private JMenuItem getKilepesMenuItem() {
		if (kilepesMenuItem == null) {
			kilepesMenuItem = new JMenuItem();
			kilepesMenuItem.setText("Kilépés");
			kilepesMenuItem.setIcon(new ImageIcon("icons/Exit.png"));
		}
		return kilepesMenuItem;
	}

	/**
	 * 	Beállítások
	 */
	private JMenu getBeallitasokMenu() {
		if (beallitasokMenu == null) {
			beallitasokMenu = new JMenu();
			beallitasokMenu.setText("Beállítások");
			beallitasokMenu.add(getAdatbazisMenuItem());
			beallitasokMenu.add(getFelhasznaloiFeluletMenuItem());
		}
		return beallitasokMenu;
	}
	
	private JMenuItem getAdatbazisMenuItem() {
		if (adatbazisMenuItem == null) {
			adatbazisMenuItem = new JMenuItem();
			adatbazisMenuItem.setText("Adatbázis");
			adatbazisMenuItem.setIcon(new ImageIcon("icons/Modify.png"));
		}
		return adatbazisMenuItem;
	}
	
	private JMenuItem getFelhasznaloiFeluletMenuItem() {
		if (felhasznaloiFeluletMenuItem == null) {
			felhasznaloiFeluletMenuItem = new JMenuItem();
			felhasznaloiFeluletMenuItem.setText("Felhasználói felület");
			felhasznaloiFeluletMenuItem.setIcon(new ImageIcon("icons/Loading.png"));
		}
		return felhasznaloiFeluletMenuItem;
	}
	
	/**
	 * 	Súgó
	 */
	private JMenu getSugoMenu() {
		if (sugoMenu == null) {
			sugoMenu = new JMenu();
			sugoMenu.setText("Súgó");
			sugoMenu.add(getNevjegyMenuItem());
			sugoMenu.add(getFelhFeltMenuItem());
		}
		return sugoMenu;
	}
	
	private JMenuItem getNevjegyMenuItem() {
		if (nevjegyMenuItem == null) {
			nevjegyMenuItem = new JMenuItem();
			nevjegyMenuItem.setText("Névjegy");
			nevjegyMenuItem.setIcon(new ImageIcon("icons/Info.png"));
		}
		return nevjegyMenuItem;
	}
	
	private JMenuItem getFelhFeltMenuItem() {
		if (felhFeltMenuItem == null) {
			felhFeltMenuItem = new JMenuItem();
			felhFeltMenuItem.setText("Felhasználási feltételek");
			felhFeltMenuItem.setIcon(new ImageIcon("icons/Comment.png"));
		}
		return felhFeltMenuItem;
	}

	/**
	 * 	Több helyen való felhasználása miatt ki lett emelve egy metódusba
	 * 	Ha nincs kapcsolat az adatbázissal és a Számlavezetés menüben történik
	 * 	akció akkor ez a hiba üzenet ugrik fel
	 */
	public void nincsKapcsolatMessage() {
		JOptionPane.showMessageDialog(this, "Nincs kapcsolat az adatbázissal!\n", "Kapcsolathiba", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Eseménykezelés(non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		/**
		 * 	Számlavezetés - Ügyfelek nyilvántartása - Új ügyfél
		 */
		if(event.getSource() == this.getUjUgyfelMenuItem()) {
			if(connected) {
				new UjUgyfelDialog(this);
				mainPanelFrissites();
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Ügyfelek nyilvántartása - Ügyfél módosítása/lekérdezése
		 */
		if(event.getSource() == this.getUgyfelModositasMenuItem()) {
			if(connected) {
				new UgyfelModositasDialog(this);
				mainPanelFrissites();
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Számlakezelés
		 */
		if(event.getSource() == this.getSzamlakezelesMenu()) {
			if(connected) {
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Számlakezelés - Új számla
		 */
		if(event.getSource() == this.getUjSzamlaMenuItem()) {
			if(connected) {
				new UjSzamlaDialog(this);
				mainPanelFrissites();
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Számlakezelés - Számla törlés / lekérdezés
		 */
		if(event.getSource() == this.getSzamlaTorlesMenuItem()) {
			if(connected) {
				new SzamlaModositasDialog(this, SzamlaModositasDialog.Tipus.TORLES);
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Átutalások
		 */
		if(event.getSource() == this.getAtutalasokMenuItem()) {
			if(connected) {
				if(getActivePanel() != getAtutalasPanel())
					changePanel(atutalasPanel);
			return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Pénztári befizetés
		 */
		if(event.getSource() == this.getBefizetesMenuItem()) {
			if(connected) {
				new SzamlaModositasDialog(this, SzamlaModositasDialog.Tipus.BEFIZETES);
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Pénztári kifizetés
		 */
		if(event.getSource() == this.getKifizetesMenuItem()) {
			if(connected) {
				new SzamlaModositasDialog(this, SzamlaModositasDialog.Tipus.KIFIZETES);
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Jelentések
		 */
		if(event.getSource() == this.getJelentesekMenuItem()) {
			if(connected) {
				if(getActivePanel() == getJelentesPanel())
					return;
				if(jelentesPanel != null)
					changePanel(jelentesPanel);
			return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Számlavezetés - Kilépés
		 */
		if(event.getSource() == this.getKilepesMenuItem()) {
			if(connected)
				dBConnect.close();
			System.exit(0);
		}
		/**
		 * 	Beállítások - Adatbázis
		 */
		if(event.getSource() == this.getAdatbazisMenuItem()) {
			new DBSettingsDialog(this);
			if(DBSettingsDialog.getReConnect()) {
				if(getActivePanel() != mainPanel) 
					changePanel(mainPanel);
				if(connected)
					dBConnect.close();
				dataBaseInit();
			}
			return;
		}
		/**
		 * 	Beállítások - Felhasznnálói felület
		 */
		if(event.getSource() == this.getFelhasznaloiFeluletMenuItem()) {
			new FelhasznaloiFeluletDialog(this);
			return;
		}
		/**
		 * 	Súgó - Névjegy
		 */
		if(event.getSource() == this.getNevjegyMenuItem()) {
			new NevjegyDialog(this);
			return;
		}
		/**
		 * 	Súgó - Felhasználási feltételek
		 */
		if(event.getSource() == getFelhFeltMenuItem()) {
			if(getActivePanel() == getHelpPanel())
				return;
			if(helpPanel != null)
				changePanel(helpPanel);
			return;
		}
	}
} 
