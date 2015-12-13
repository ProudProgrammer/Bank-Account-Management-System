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
	
	private JMenu ugyfelekNyilvantartasaMenu = null;				//Sz�mlavezet�s - �gyfelek nyilv�ntart�sa
	private JMenuItem ujUgyfelMenuItem = null;						//Sz�mlavezet�s - �gyfelek nyilv�ntart�sa - �j �gyf�l
	private JMenuItem ugyfelModositasMenuItem = null;				//Sz�mlavezet�s - �gyfelek nyilv�ntart�sa - �gyf�l m�dos�t�s / lek�rdez�s
	
	private JMenu szamlakezelesMenu = null;							//Sz�mlavezet�s - Sz�mlakezel�s
	private JMenuItem ujSzamlaMenuItem = null;						//Sz�mlavezet�s - Sz�mlakezel�s - �j sz�mla
	private JMenuItem szamlaTorlesMenuItem = null;					//Sz�mlavezet�s - Sz�mlakezel�s - Sz�mla t�rl�s / lek�rdez�s
	
	private JMenuItem atutalasokMenuItem = null;					//Sz�mlavezet�s - �tutal�sok
	private JMenuItem befizetesMenuItem = null;						//Sz�mlavezet�s - P�nzt�ri befizet�s
	private JMenuItem kifizetesMenuItem = null;						//Sz�mlavezet�s - P�nzt�ri kifizet�s
	private JMenuItem jelentesekMenuItem = null;					//Sz�mlavezet�s - Jelent�sek
	private JMenuItem kilepesMenuItem = null;						//Sz�mlavezet�s - Kil�p�s
	
	private JMenuItem adatbazisMenuItem = null;						//Be�ll�t�sok - Adatb�zis
	private JMenuItem felhasznaloiFeluletMenuItem = null;			//Be�ll�t�sok - Felhaszn�l�i fel�let
	
	private JMenuItem nevjegyMenuItem = null;						//S�g� - N�vjegy
	private JMenuItem felhFeltMenuItem = null;						//S�g� - Felhaszn�l�si felt�telek
	
	private Rectangle rectangle = null;
	private HelpPanel helpPanel = null;								//Felhaszn�l�si felt�telek lapja
	private MainPanel mainPanel = null;								//Nyit� (f�)lap
	private JelentesPanel jelentesPanel = null;						//Jelent�sek lapja
	private AtutalasPanel atutalasPanel = null;						//�tutal�s lapja
	private JPanel jPanel = null;									//Tartalompanel - ContentPane
	private JPanel activePanel = null;								//Mindig az �ppen l�that� panelt jelenti
	
	private DBConnect dBConnect = null;								//Adatb�ziskapcsolat objektuma
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
        setTitle("Banki sz�mlavezet� rendszer");
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
	 * 	Alkalmaz�s bez�r�sa az ablakbez�r�val
	 * 	Az adatb�zis kapcsolat helyes lez�r�sa
	 * 	Az adatb�zis kapcsolat az alkalmaz�s fut�sa alatt folyamatosan fent �ll
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
	 * 	Interface az adatb�zis kapcsolathoz
	 * 	A MainWindow felel�s az adatb�zis kapcsolat�rt teh�t t�le kell elk�rni
	 */
	public DBConnect getDBConnect() {
		return dBConnect;
	}
	
	/**
	 * 	Indul�skor az ablak m�rete a k�perny� fele �s k�z�pen elhelyezett
	 */
	private Rectangle getRectangle() {
		if (rectangle == null) {
			rectangle = new Rectangle((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - this.getWidth()/2,
					(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - this.getHeight()/2, this.getWidth(), this.getHeight());
		}
		return rectangle;
	}
	
	/**	
	 *	Panelcsere automatiz�l�sa
	 *	Az �ppen akt�v, teh�t l�that� panelt kiveszi a tartalompanelb�l
	 *	�s l�thatatlann� teszi. Helyette a param�terben megadott
	 *	panelt teszi be a tartalompanelbe �s teszi l�that�v�.
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
	 *	Adatb�zis inicializ�l�sa
	 * 	- az alkalmaz�s elind�t�sakor mindig
	 * 	- �j adatb�zis kapcsolat be�ll�t�sok megad�sakor mindig
	 */
	private void dataBaseInit() {
		szamlavezetesMenu.setEnabled(false);
		beallitasokMenu.setEnabled(false);
		sugoMenu.setEnabled(false);
		mainPanel.getJTextArea().setText("Banki sz�mlavezet� rendszer\n\n" +
				"A rendszer m�g nem haszn�latk�sz. Adatb�zis be�ll�t�sok v�gleges�t�se folyamatban. Ez t�bb percig is eltarthat.");
		new DbConnectThread();
	}
	
	/**	
	 *	Kapcsol�d�s az adatb�zishoz k�l�n sz�lon
	 * 	Ez�tal a felhaszn�l�i interf�sz kirajzol�sa nincs blokkolva:
	 * 	A felhaszn�l�t ez�ltal �rtes�teni lehet, hogy az datb�zis be�ll�t�sok �rv�nyes�t�s�re 
	 * 	kell v�rakozni. Persze a menu blokkolva van ilyenkor mert adatb�zis n�lk�l nem lehet
	 * 	dolgozni. Teh�t csak a GUI blokkol�s�nak megakad�lyoz�sa miatt van a k�l�n sz�l
	 * 	nem az�rt, hogy kapcsol�d�s k�zben a felhaszn�l� b�rmit is csin�lhasson.
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
	 * 	Fel�gyeli(ha megh�v�dik), hogy mindig az aktu�lis �rt�kek jelenjenek meg
	 *  a f�panel �gyfelek darabsz�m�t �s sz�ml�k darabsz�m�t illet�en.
	 *  Szinkroniz�lt, mert k�t k�l�n sz�l is megh�vhatja (b�r egyszerre nem).
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
				mainPanel.getJTextArea().setText("Banki sz�mlavezet� rendszer\n\n" + "A rendszer haszn�latk�sz. J� munk�t.\n\n" +
						"Az adatb�zisban lev� �gyfelek sz�ma: " + ugyfelekSzama + 
						"\nAz adatb�zisban lev� sz�ml�k sz�ma: " + szamlakSzama);
			} catch (SQLException e) {
				connected = false;
				mainPanel.getJTextArea().setText("Banki sz�mlavezet� rendszer\n\n" + "Nincs kapcsolat az adatb�zissal.");
				nincsKapcsolatMessage();
			}
		} else {
			mainPanel.getJTextArea().setText("Banki sz�mlavezet� rendszer\n\n" + "Nincs kapcsolat az adatb�zissal.");
		}
		mainPanel.getJTextArea().append("\n\nA pontos d�tum: " + kalendar.get(Calendar.YEAR) + "." + (kalendar.get(Calendar.MONTH) + 1) + 
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
				JOptionPane.showMessageDialog(this, "Nincs meg a k�vetkez� file: \"help.txt\" !", "Hiba", JOptionPane.ERROR_MESSAGE);
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
	* 	A men�sor �s men�elemek
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
	 * 	Sz�mlavezet�s
	 */
	private JMenu getSzamlavezetesMenu() {
		if (szamlavezetesMenu == null) {
			szamlavezetesMenu = new JMenu();
			szamlavezetesMenu.setText("Sz�mlavezet�s");
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
			ugyfelekNyilvantartasaMenu.setText("�gyfelek nyilv�ntart�sa");
			ugyfelekNyilvantartasaMenu.add(getUjUgyfelMenuItem());
			ugyfelekNyilvantartasaMenu.add(getUgyfelModositasMenuItem());
			ugyfelekNyilvantartasaMenu.setIcon(new ImageIcon("icons/Profile.png"));
			
		}
		return ugyfelekNyilvantartasaMenu;
	}

	private JMenuItem getUjUgyfelMenuItem() {
		if (ujUgyfelMenuItem == null) {
			ujUgyfelMenuItem = new JMenuItem();
			ujUgyfelMenuItem.setText("�j �gyf�l");
			ujUgyfelMenuItem.setIcon(new ImageIcon("icons/Add.png"));
		}
		return ujUgyfelMenuItem;
	}

	private JMenuItem getUgyfelModositasMenuItem() {
		if (ugyfelModositasMenuItem == null) {
			ugyfelModositasMenuItem = new JMenuItem();
			ugyfelModositasMenuItem.setText("�gyf�l m�dos�t�s / lek�rdez�s");
			ugyfelModositasMenuItem.setIcon(new ImageIcon("icons/Delete.png"));
		}
		return ugyfelModositasMenuItem;
	}
	
	private JMenu getSzamlakezelesMenu() {
		if (szamlakezelesMenu == null) {
			szamlakezelesMenu = new JMenu();
			szamlakezelesMenu.setText("Sz�mlakezel�s");
			szamlakezelesMenu.setIcon(new ImageIcon("icons/Print.png"));
			szamlakezelesMenu.add(getUjSzamlaMenuItem());
			szamlakezelesMenu.add(getSzamlaTorlesMenuItem());
		}
		return szamlakezelesMenu;
	}
	
	private JMenuItem getUjSzamlaMenuItem() {
		if (szamlaTorlesMenuItem == null) {
			szamlaTorlesMenuItem = new JMenuItem();
			szamlaTorlesMenuItem.setText("�j sz�mla");
			szamlaTorlesMenuItem.setIcon(new ImageIcon("icons/Add.png"));
		}
		return szamlaTorlesMenuItem;
	}
	
	private JMenuItem getSzamlaTorlesMenuItem() {
		if (ujSzamlaMenuItem == null) {
			ujSzamlaMenuItem = new JMenuItem();
			ujSzamlaMenuItem.setText("Sz�mla t�rl�s / lek�rdez�s");
			ujSzamlaMenuItem.setIcon(new ImageIcon("icons/Delete.png"));
		}
		return ujSzamlaMenuItem;
	}

	private JMenuItem getAtutalasokMenuItem() {
		if (atutalasokMenuItem == null) {
			atutalasokMenuItem = new JMenuItem();
			atutalasokMenuItem.setText("�tutal�sok");
			atutalasokMenuItem.setIcon(new ImageIcon("icons/Line Chart.png"));
		}
		return atutalasokMenuItem;
	}

	private JMenuItem getBefizetesMenuItem() {
		if (befizetesMenuItem == null) {
			befizetesMenuItem = new JMenuItem();
			befizetesMenuItem.setText("P�nzt�ri befizet�s");
			befizetesMenuItem.setIcon(new ImageIcon("icons/Next.png"));
		}
		return befizetesMenuItem;
	}

	private JMenuItem getKifizetesMenuItem() {
		if (kifizetesMenuItem == null) {
			kifizetesMenuItem = new JMenuItem();
			kifizetesMenuItem.setText("P�nzt�ri kifizet�s");
			kifizetesMenuItem.setIcon(new ImageIcon("icons/Back.png"));
		}
		return kifizetesMenuItem;
	}

	private JMenuItem getJelentesekMenuItem() {
		if (jelentesekMenuItem == null) {
			jelentesekMenuItem = new JMenuItem();
			jelentesekMenuItem.setText("Jelent�sek");
			jelentesekMenuItem.setIcon(new ImageIcon("icons/Bar Chart.png"));
		}
		return jelentesekMenuItem;
	}
	
	private JMenuItem getKilepesMenuItem() {
		if (kilepesMenuItem == null) {
			kilepesMenuItem = new JMenuItem();
			kilepesMenuItem.setText("Kil�p�s");
			kilepesMenuItem.setIcon(new ImageIcon("icons/Exit.png"));
		}
		return kilepesMenuItem;
	}

	/**
	 * 	Be�ll�t�sok
	 */
	private JMenu getBeallitasokMenu() {
		if (beallitasokMenu == null) {
			beallitasokMenu = new JMenu();
			beallitasokMenu.setText("Be�ll�t�sok");
			beallitasokMenu.add(getAdatbazisMenuItem());
			beallitasokMenu.add(getFelhasznaloiFeluletMenuItem());
		}
		return beallitasokMenu;
	}
	
	private JMenuItem getAdatbazisMenuItem() {
		if (adatbazisMenuItem == null) {
			adatbazisMenuItem = new JMenuItem();
			adatbazisMenuItem.setText("Adatb�zis");
			adatbazisMenuItem.setIcon(new ImageIcon("icons/Modify.png"));
		}
		return adatbazisMenuItem;
	}
	
	private JMenuItem getFelhasznaloiFeluletMenuItem() {
		if (felhasznaloiFeluletMenuItem == null) {
			felhasznaloiFeluletMenuItem = new JMenuItem();
			felhasznaloiFeluletMenuItem.setText("Felhaszn�l�i fel�let");
			felhasznaloiFeluletMenuItem.setIcon(new ImageIcon("icons/Loading.png"));
		}
		return felhasznaloiFeluletMenuItem;
	}
	
	/**
	 * 	S�g�
	 */
	private JMenu getSugoMenu() {
		if (sugoMenu == null) {
			sugoMenu = new JMenu();
			sugoMenu.setText("S�g�");
			sugoMenu.add(getNevjegyMenuItem());
			sugoMenu.add(getFelhFeltMenuItem());
		}
		return sugoMenu;
	}
	
	private JMenuItem getNevjegyMenuItem() {
		if (nevjegyMenuItem == null) {
			nevjegyMenuItem = new JMenuItem();
			nevjegyMenuItem.setText("N�vjegy");
			nevjegyMenuItem.setIcon(new ImageIcon("icons/Info.png"));
		}
		return nevjegyMenuItem;
	}
	
	private JMenuItem getFelhFeltMenuItem() {
		if (felhFeltMenuItem == null) {
			felhFeltMenuItem = new JMenuItem();
			felhFeltMenuItem.setText("Felhaszn�l�si felt�telek");
			felhFeltMenuItem.setIcon(new ImageIcon("icons/Comment.png"));
		}
		return felhFeltMenuItem;
	}

	/**
	 * 	T�bb helyen val� felhaszn�l�sa miatt ki lett emelve egy met�dusba
	 * 	Ha nincs kapcsolat az adatb�zissal �s a Sz�mlavezet�s men�ben t�rt�nik
	 * 	akci� akkor ez a hiba �zenet ugrik fel
	 */
	public void nincsKapcsolatMessage() {
		JOptionPane.showMessageDialog(this, "Nincs kapcsolat az adatb�zissal!\n", "Kapcsolathiba", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Esem�nykezel�s(non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		/**
		 * 	Sz�mlavezet�s - �gyfelek nyilv�ntart�sa - �j �gyf�l
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
		 * 	Sz�mlavezet�s - �gyfelek nyilv�ntart�sa - �gyf�l m�dos�t�sa/lek�rdez�se
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
		 * 	Sz�mlavezet�s - Sz�mlakezel�s
		 */
		if(event.getSource() == this.getSzamlakezelesMenu()) {
			if(connected) {
				return;
			}
			nincsKapcsolatMessage();
			return;
		}
		/**
		 * 	Sz�mlavezet�s - Sz�mlakezel�s - �j sz�mla
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
		 * 	Sz�mlavezet�s - Sz�mlakezel�s - Sz�mla t�rl�s / lek�rdez�s
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
		 * 	Sz�mlavezet�s - �tutal�sok
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
		 * 	Sz�mlavezet�s - P�nzt�ri befizet�s
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
		 * 	Sz�mlavezet�s - P�nzt�ri kifizet�s
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
		 * 	Sz�mlavezet�s - Jelent�sek
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
		 * 	Sz�mlavezet�s - Kil�p�s
		 */
		if(event.getSource() == this.getKilepesMenuItem()) {
			if(connected)
				dBConnect.close();
			System.exit(0);
		}
		/**
		 * 	Be�ll�t�sok - Adatb�zis
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
		 * 	Be�ll�t�sok - Felhasznn�l�i fel�let
		 */
		if(event.getSource() == this.getFelhasznaloiFeluletMenuItem()) {
			new FelhasznaloiFeluletDialog(this);
			return;
		}
		/**
		 * 	S�g� - N�vjegy
		 */
		if(event.getSource() == this.getNevjegyMenuItem()) {
			new NevjegyDialog(this);
			return;
		}
		/**
		 * 	S�g� - Felhaszn�l�si felt�telek
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
