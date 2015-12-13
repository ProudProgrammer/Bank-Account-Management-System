package ui;

import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public final class JelentesPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 	A táblázat sorai és fejléce
	 */
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private static final Vector<String> COLUMN_NAMES = new Vector<String>();

	private MainWindow main = null;
	
	private JPanel felsoPanel = null;
	private JScrollPane jScrollPane = null;
	
	private JTable jTable = null;
	
	private static final JComboBox<Integer> EV_TOL = new JComboBox<Integer>();
	private static final JComboBox<Integer> HO_TOL = new JComboBox<Integer>();
	private static final JComboBox<Integer> NAP_TOL = new JComboBox<Integer>();
	private static final JComboBox<Integer> EV_IG = new JComboBox<Integer>();
	private static final JComboBox<Integer> HO_IG = new JComboBox<Integer>();
	private static final JComboBox<Integer> NAP_IG = new JComboBox<Integer>();
	
	/**
	 * 	Csak pozitív vagy negatív szám, nullával nem kezdõdhet, BackSpace és Enter engedélyezett
	 */
	private OnlyNumberNegativeEnabledTextField osszegTolTextField = new OnlyNumberNegativeEnabledTextField(13);
	private OnlyNumberNegativeEnabledTextField osszegIgTextField = new OnlyNumberNegativeEnabledTextField(13);
	
	/**
	 * 	Csak pozitív szám, nullával kezdõdhet, BackSpace és Enter engedélyezett
	 */
	private OnlyNumberTextField szamlaSzamTextField = new OnlyNumberTextField(10, 10);
	
	private JRadioButton rendezesDatumSzerint = new JRadioButton();
	private JRadioButton rendezesOsszegSzerint = new JRadioButton();
	private ButtonGroup rendezesSzempont = new ButtonGroup();
	
	private static final JButton lekerdezesButton = new JButton("Lekérdezés");
	
	private JPanel datumJPanel = null;
	private JPanel osszegJPanel = null;
	private JPanel szamlaszamJPanel = null;
	private JPanel lekerdezesJPanel = null;
	
	private ResultSet lekerdezesEredmeny = null;  //  @jve:decl-index=0:

	static {
		COLUMN_NAMES.add("Dátum");
		COLUMN_NAMES.add("Leírás");
		COLUMN_NAMES.add("Összeg");
		COLUMN_NAMES.add("Számlaszám");
		COLUMN_NAMES.add("Küldõ");
		for(int i = 2010; i <= 2020; i++) {
			EV_TOL.addItem(new Integer(i));
			EV_IG.addItem(new Integer(i));
		}
		for(int i = 1; i <= 12; i++) {
			HO_TOL.addItem(new Integer(i));
			HO_IG.addItem(new Integer(i));
		}
		for(int i = 1; i <= 31; i++) {
			NAP_TOL.addItem(new Integer(i));
			NAP_IG.addItem(new Integer(i));
		}
		EV_IG.setSelectedIndex(EV_IG.getItemCount()-1);
		HO_IG.setSelectedIndex(HO_IG.getItemCount()-1);
		NAP_IG.setSelectedIndex(NAP_IG.getItemCount()-1);
	}
	
	{
		rendezesSzempont.add(rendezesDatumSzerint);
		rendezesSzempont.add(rendezesOsszegSzerint);
		rendezesDatumSzerint.setSelected(true);
		
		lekerdezesButton.setIcon(new ImageIcon("icons/Search.png"));
	}
	
	public JelentesPanel(MainWindow main) {
		this.main = main;
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(2,1));
		add(getFelsoPanel());
		add(getJScrollPane());
		
		osszegTolTextField.setToolTipText(getToolTip());
		osszegIgTextField.setToolTipText(getToolTip());
		szamlaSzamTextField.setToolTipText(getToolTip());
		
		osszegTolTextField.addActionListener(this);
		osszegIgTextField.addActionListener(this);
		szamlaSzamTextField.addActionListener(this);
		
		lekerdezesButton.addActionListener(this);
		
		/**
		 * 	Ha az összeg beviteli mezõbe csak egy '-' jel lett beírva és így
		 * 	veszti el a fókuszt akkor azt a '-' jelet kitörli.
		 * 	Figyelõk adapterrel megvalósítva.
		 */
		osszegTolTextField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if(osszegTolTextField.getText().equals("-"))
					osszegTolTextField.setText("");
			}
		});
		osszegIgTextField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if(osszegIgTextField.getText().equals("-"))
					osszegIgTextField.setText("");
			}
		});
	}

	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}
 
	/**
	 * 	Elsõ létrehozásakor csak a fejlécnevek vannak, a táblázat üres
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(rowData, COLUMN_NAMES);
		}
		return jTable;
	}
	
	private JPanel getFelsoPanel() {
		if (felsoPanel == null) {
			felsoPanel = new JPanel();
			felsoPanel.setLayout(new GridLayout(4,1));
			felsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "Tranzakció jelentések lekérdezése"));
			felsoPanel.add(getDatumJPanel());
			felsoPanel.add(getOsszegJPanel());
			felsoPanel.add(getSzamlaszamJPanel());
			felsoPanel.add(getLekerdezesJPanel());
		}
		return felsoPanel;
	}
	
	private JPanel getDatumJPanel() {
		if(datumJPanel == null) {
			datumJPanel = new JPanel();
			datumJPanel.add(new JLabel("Szûrés dátum tartományra:  "));
			datumJPanel.add(EV_TOL);
			datumJPanel.add(HO_TOL);
			datumJPanel.add(NAP_TOL);
			datumJPanel.add(new JLabel(" - "));
			datumJPanel.add(EV_IG);
			datumJPanel.add(HO_IG);
			datumJPanel.add(NAP_IG);
		}
		return datumJPanel;
	}
	
	private JPanel getOsszegJPanel() {
		if(osszegJPanel == null) {
			osszegJPanel = new JPanel();
			osszegJPanel.add(new JLabel("Szûrés összeg tartományra:  "));
			osszegJPanel.add(osszegTolTextField);
			osszegJPanel.add(new JLabel(" - "));
			osszegJPanel.add(osszegIgTextField);
		}
		return osszegJPanel;
	}
	
	private JPanel getSzamlaszamJPanel() {
		if(szamlaszamJPanel == null) {
			szamlaszamJPanel = new JPanel();
			szamlaszamJPanel.add(new JLabel("Szûrés számlaszámra:  "));
			szamlaszamJPanel.add(szamlaSzamTextField);
			szamlaszamJPanel.add(new JLabel("    Rendezés dátum szerint:  "));
			szamlaszamJPanel.add(rendezesDatumSzerint);
			szamlaszamJPanel.add(new JLabel("  Rendezés összeg szerint:  "));
			szamlaszamJPanel.add(rendezesOsszegSzerint);
		}
		return szamlaszamJPanel;
	}
	
	private JPanel getLekerdezesJPanel() {
		if(lekerdezesJPanel == null) {
			lekerdezesJPanel = new JPanel();
			lekerdezesJPanel.add(lekerdezesButton);
		}
		return lekerdezesJPanel;
	}
	
	private String getToolTip() {
		return "Üresen hagyott mezõ esetén a legszélesebb tartomány alapján történik a keresés";
	}

	/**
	 * 	Minden lekérdezésnél egy teljesen új táblázat készül:
	 * 	- régi táblázat referenciája felülíródik
	 * 	- régi táblázat panelja eldobásra kerül
	 * 	- új táblázat új panelon megjelenítésre kerül
	 * 	Nagyon fontos a teljes main panel frissítése.
	 */
	private void tablaFrissites(String lekerdezesSzempont) {
		String rendezesSzempont = "";
		if(rendezesDatumSzerint.isSelected())
			rendezesSzempont = "RendezésDátumSzerint";
		else
			rendezesSzempont = "RendezésÖsszegSzerint";
		int datumTol = (Integer)EV_TOL.getSelectedItem() * 10000 + (Integer)HO_TOL.getSelectedItem() * 100 + (Integer)NAP_TOL.getSelectedItem();
		int datumIg = (Integer)EV_IG.getSelectedItem() * 10000 + (Integer)HO_IG.getSelectedItem() * 100 + (Integer)NAP_IG.getSelectedItem();
		try {
			if(lekerdezesSzempont.equals("CsakDatumSzerint"))
				lekerdezesEredmeny = main.getDBConnect().tranzakcioLekerdezesCsakDatumSzerint(datumTol, datumIg, rendezesSzempont);
			if(lekerdezesSzempont.equals("DatumEsOsszegSzerint")) {
				long osszegTol = 0;
				long osszegIg = 0;
				try {
					osszegTol = Long.parseLong(osszegTolTextField.getText());
					osszegIg = Long.parseLong(osszegIgTextField.getText());
				} catch(NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "Az összeg beviteli mezõkben helytelen formátum található vagy túl nagy szám!", "Hibás formátum", JOptionPane.ERROR_MESSAGE);
					return;
				}
				lekerdezesEredmeny = main.getDBConnect().tranzakcioLekerdezesDatumEsOsszegSzerint(datumTol, datumIg, osszegTol, osszegIg, rendezesSzempont);
			}
			if(lekerdezesSzempont.equals("DatumEsSzamlaszamSzerint")) {
				int szamlaszam = 0;
				try {
					szamlaszam = Integer.parseInt(szamlaSzamTextField.getText());
				} catch(NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "A számlaszám beviteli mezõben helytelen formátum található!", "Hibás formátum", JOptionPane.ERROR_MESSAGE);
					return;
				}
				lekerdezesEredmeny = main.getDBConnect().tranzakcioLekerdezesDatumEsSzamlaszamSzerint(datumTol, datumIg, szamlaszam, rendezesSzempont);
			}
			if(lekerdezesSzempont.equals("DatumEsOsszegEsSzamlaszamSzerint")) {
				long osszegTol = 0;
				long osszegIg = 0;
				int szamlaszam = 0;
				try {
					osszegTol = Long.parseLong(osszegTolTextField.getText());
					osszegIg = Long.parseLong(osszegIgTextField.getText());
					szamlaszam = Integer.parseInt(szamlaSzamTextField.getText());
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "A beviteli mezõkben helytelen formátum található vagy túl nagy szám!", "Hibás formátum", JOptionPane.ERROR_MESSAGE);
					return;
				}
				lekerdezesEredmeny = main.getDBConnect().tranzakcioLekerdezesDatumEsOsszegEsSzamlaszamSzerint(datumTol, datumIg, osszegTol, osszegIg, szamlaszam, rendezesSzempont);
			}
			rowData.removeAllElements();
			Vector<String> sor = null;
			while(lekerdezesEredmeny.next()) {
				sor = new Vector<String>();
				sor.add("" + lekerdezesEredmeny.getInt("datum"));
				sor.add(lekerdezesEredmeny.getString("leiras"));
				sor.add("" + lekerdezesEredmeny.getLong("osszeg"));
				sor.add("" + lekerdezesEredmeny.getLong("szamlaszam"));
				sor.add("" + lekerdezesEredmeny.getLong("kuldo"));
				rowData.add(sor);
			}
			jTable = new JTable(rowData, COLUMN_NAMES);
			getJScrollPane().setVisible(false);
			remove(getJScrollPane());
			jScrollPane = null;
			add(getJScrollPane());
			getJScrollPane().setVisible(true);
			main.setVisible(true);
		} catch (SQLException e1) {
			main.nincsKapcsolatMessage();
		} finally {
			try {
				if(lekerdezesEredmeny != null)
					lekerdezesEredmeny.close();
			} catch (SQLException e2) {
				main.nincsKapcsolatMessage();
			}
		}
	}
	
	/**
	 * 	Hiába a fókusz figyelõk '-' jel kitörlésére, mert Enter leütésével bent maradna
	 *  a '-' jel, viszont így, az elsõ két utasítás hatására kitörlõdik.
	 */
	public void actionPerformed(ActionEvent e) {
		if(osszegTolTextField.getText().equals("-"))
			osszegTolTextField.setText("");
		if(osszegIgTextField.getText().equals("-"))
			osszegIgTextField.setText("");
		if(szamlaSzamTextField.getText().length() != 0 & szamlaSzamTextField.getText().length() != 10) {
			JOptionPane.showMessageDialog(this, "A számlaszám pontosan 10 számjegyû kell, hogy legyen!\n" +
					"A megadott számlaszám csak " + szamlaSzamTextField.getText().length() + " számjegyû.", "Hibás formátum", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if((osszegTolTextField.getText().length() != 0 & osszegIgTextField.getText().length() == 0)
				|| (osszegTolTextField.getText().length() == 0 & osszegIgTextField.getText().length() != 0)) {
			JOptionPane.showMessageDialog(this, "Összeg tartomány szûrés esetén az alsó és a felsõ tartományt is meg kell adni!", "Hibás formátum", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(osszegTolTextField.getText().length() == 0 & osszegIgTextField.getText().length() == 0 & szamlaSzamTextField.getText().length() == 0) {
			tablaFrissites("CsakDatumSzerint");
			return;
		}
		if(szamlaSzamTextField.getText().length() == 0) {
			tablaFrissites("DatumEsOsszegSzerint");
			return;
		}
		if(osszegTolTextField.getText().length() == 0 & osszegIgTextField.getText().length() == 0) {
			tablaFrissites("DatumEsSzamlaszamSzerint");
			return;
		}
		tablaFrissites("DatumEsOsszegEsSzamlaszamSzerint");
	}
}
