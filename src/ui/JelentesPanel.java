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
	 * 	A t�bl�zat sorai �s fejl�ce
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
	 * 	Csak pozit�v vagy negat�v sz�m, null�val nem kezd�dhet, BackSpace �s Enter enged�lyezett
	 */
	private OnlyNumberNegativeEnabledTextField osszegTolTextField = new OnlyNumberNegativeEnabledTextField(13);
	private OnlyNumberNegativeEnabledTextField osszegIgTextField = new OnlyNumberNegativeEnabledTextField(13);
	
	/**
	 * 	Csak pozit�v sz�m, null�val kezd�dhet, BackSpace �s Enter enged�lyezett
	 */
	private OnlyNumberTextField szamlaSzamTextField = new OnlyNumberTextField(10, 10);
	
	private JRadioButton rendezesDatumSzerint = new JRadioButton();
	private JRadioButton rendezesOsszegSzerint = new JRadioButton();
	private ButtonGroup rendezesSzempont = new ButtonGroup();
	
	private static final JButton lekerdezesButton = new JButton("Lek�rdez�s");
	
	private JPanel datumJPanel = null;
	private JPanel osszegJPanel = null;
	private JPanel szamlaszamJPanel = null;
	private JPanel lekerdezesJPanel = null;
	
	private ResultSet lekerdezesEredmeny = null;  //  @jve:decl-index=0:

	static {
		COLUMN_NAMES.add("D�tum");
		COLUMN_NAMES.add("Le�r�s");
		COLUMN_NAMES.add("�sszeg");
		COLUMN_NAMES.add("Sz�mlasz�m");
		COLUMN_NAMES.add("K�ld�");
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
		 * 	Ha az �sszeg beviteli mez�be csak egy '-' jel lett be�rva �s �gy
		 * 	veszti el a f�kuszt akkor azt a '-' jelet kit�rli.
		 * 	Figyel�k adapterrel megval�s�tva.
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
	 * 	Els� l�trehoz�sakor csak a fejl�cnevek vannak, a t�bl�zat �res
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
			felsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "Tranzakci� jelent�sek lek�rdez�se"));
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
			datumJPanel.add(new JLabel("Sz�r�s d�tum tartom�nyra:  "));
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
			osszegJPanel.add(new JLabel("Sz�r�s �sszeg tartom�nyra:  "));
			osszegJPanel.add(osszegTolTextField);
			osszegJPanel.add(new JLabel(" - "));
			osszegJPanel.add(osszegIgTextField);
		}
		return osszegJPanel;
	}
	
	private JPanel getSzamlaszamJPanel() {
		if(szamlaszamJPanel == null) {
			szamlaszamJPanel = new JPanel();
			szamlaszamJPanel.add(new JLabel("Sz�r�s sz�mlasz�mra:  "));
			szamlaszamJPanel.add(szamlaSzamTextField);
			szamlaszamJPanel.add(new JLabel("    Rendez�s d�tum szerint:  "));
			szamlaszamJPanel.add(rendezesDatumSzerint);
			szamlaszamJPanel.add(new JLabel("  Rendez�s �sszeg szerint:  "));
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
		return "�resen hagyott mez� eset�n a legsz�lesebb tartom�ny alapj�n t�rt�nik a keres�s";
	}

	/**
	 * 	Minden lek�rdez�sn�l egy teljesen �j t�bl�zat k�sz�l:
	 * 	- r�gi t�bl�zat referenci�ja fel�l�r�dik
	 * 	- r�gi t�bl�zat panelja eldob�sra ker�l
	 * 	- �j t�bl�zat �j panelon megjelen�t�sre ker�l
	 * 	Nagyon fontos a teljes main panel friss�t�se.
	 */
	private void tablaFrissites(String lekerdezesSzempont) {
		String rendezesSzempont = "";
		if(rendezesDatumSzerint.isSelected())
			rendezesSzempont = "Rendez�sD�tumSzerint";
		else
			rendezesSzempont = "Rendez�s�sszegSzerint";
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
					JOptionPane.showMessageDialog(this, "Az �sszeg beviteli mez�kben helytelen form�tum tal�lhat� vagy t�l nagy sz�m!", "Hib�s form�tum", JOptionPane.ERROR_MESSAGE);
					return;
				}
				lekerdezesEredmeny = main.getDBConnect().tranzakcioLekerdezesDatumEsOsszegSzerint(datumTol, datumIg, osszegTol, osszegIg, rendezesSzempont);
			}
			if(lekerdezesSzempont.equals("DatumEsSzamlaszamSzerint")) {
				int szamlaszam = 0;
				try {
					szamlaszam = Integer.parseInt(szamlaSzamTextField.getText());
				} catch(NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "A sz�mlasz�m beviteli mez�ben helytelen form�tum tal�lhat�!", "Hib�s form�tum", JOptionPane.ERROR_MESSAGE);
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
					JOptionPane.showMessageDialog(this, "A beviteli mez�kben helytelen form�tum tal�lhat� vagy t�l nagy sz�m!", "Hib�s form�tum", JOptionPane.ERROR_MESSAGE);
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
	 * 	Hi�ba a f�kusz figyel�k '-' jel kit�rl�s�re, mert Enter le�t�s�vel bent maradna
	 *  a '-' jel, viszont �gy, az els� k�t utas�t�s hat�s�ra kit�rl�dik.
	 */
	public void actionPerformed(ActionEvent e) {
		if(osszegTolTextField.getText().equals("-"))
			osszegTolTextField.setText("");
		if(osszegIgTextField.getText().equals("-"))
			osszegIgTextField.setText("");
		if(szamlaSzamTextField.getText().length() != 0 & szamlaSzamTextField.getText().length() != 10) {
			JOptionPane.showMessageDialog(this, "A sz�mlasz�m pontosan 10 sz�mjegy� kell, hogy legyen!\n" +
					"A megadott sz�mlasz�m csak " + szamlaSzamTextField.getText().length() + " sz�mjegy�.", "Hib�s form�tum", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if((osszegTolTextField.getText().length() != 0 & osszegIgTextField.getText().length() == 0)
				|| (osszegTolTextField.getText().length() == 0 & osszegIgTextField.getText().length() != 0)) {
			JOptionPane.showMessageDialog(this, "�sszeg tartom�ny sz�r�s eset�n az als� �s a fels� tartom�nyt is meg kell adni!", "Hib�s form�tum", JOptionPane.ERROR_MESSAGE);
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
