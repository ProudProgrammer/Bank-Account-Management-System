package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;

import db.Ugyfel;

public class SzamlaModositasDialog extends JDialog implements ActionListener, UgyfelValaszto {
	
	public enum Tipus {
		TORLES, BEFIZETES, KIFIZETES
	}
	
	private static final long serialVersionUID = 1L;
	
	private MainWindow owner = null;
	private Tipus tipus = null;
	
	private JPanel felsoPanel = null;
	private JPanel kozepsoPanel = null;
	private JPanel felsoKozepPanel = null;
	private JScrollPane alsoKozepScrollPane = null;
	private JPanel alsoPanel = null;

	private JButton kivalasztButton = new JButton("�gyf�l kiv�laszt�sa");
	
	private JLabel ugyfelszamLabel = new JLabel();
	private JLabel nevLabel = new JLabel();
	private JLabel lakcimLabel = new JLabel();
	private JLabel telszamLabel = new JLabel();
	private JLabel szigszamLabel = new JLabel();
	
	private JTable jTable = null;
	/**
	 * 	A sz�mla t�bl�zat sorai �s fejl�ce
	 */
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private static final Vector<String> COLUMN_NAMES = new Vector<String>();
	
	private JButton valtozoButton = new JButton();
	private JButton megsemButton = new JButton("M�gsem");
	
	private ResultSet lekerdezesEredmeny = null;
	
	static {
		COLUMN_NAMES.add("Sz�mlasz�m");
		COLUMN_NAMES.add("�gyf�lsz�m");
		COLUMN_NAMES.add("Egyenleg");
		COLUMN_NAMES.add("St�tusz");
	}
	
	{
		kivalasztButton.setIcon(new ImageIcon("icons/Load.png"));
		megsemButton.setIcon(new ImageIcon("icons/Exit.png"));
		
		valtozoButton.setEnabled(false);
	}
	
	public SzamlaModositasDialog(MainWindow owner, SzamlaModositasDialog.Tipus tipus) {
		this.owner = owner;
		this.tipus = tipus;
		setSize(600, 400);
		setLocationRelativeTo(owner);
		setModal(true);
		
		if(tipus == Tipus.TORLES) {
			setTitle("Sz�mla t�rl�se");
			valtozoButton.setText("Sz�mla t�rl�se");
			valtozoButton.setIcon(new ImageIcon("icons/Delete.png"));
		}
		if(tipus == Tipus.BEFIZETES) {
			setTitle("P�nzt�ri befizet�s");
			valtozoButton.setText("P�nzt�ri befizet�s");
			valtozoButton.setIcon(new ImageIcon("icons/Next.png"));
		}
		if(tipus == Tipus.KIFIZETES) {
			setTitle("P�nzt�ri kifizet�s");
			valtozoButton.setText("P�nzt�ri kifizet�s");
			valtozoButton.setIcon(new ImageIcon("icons/Back.png"));
		}
		
		setIconImage(this.getToolkit().createImage("icons/Print.png"));
		setLayout(new BorderLayout());
		add(getFelsoPanel(), BorderLayout.NORTH);
		add(getKozepsoPanel(), BorderLayout.CENTER);
		add(getAlsoButtonPanel(), BorderLayout.SOUTH);

		kivalasztButton.addActionListener(this);
		valtozoButton.addActionListener(this);
		megsemButton.addActionListener(this);
		
		setVisible(true);
	}

	private JPanel getFelsoPanel() {
		if(felsoPanel == null) {
			felsoPanel = new JPanel();
			felsoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			felsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			felsoPanel.add(kivalasztButton);
		}
		return felsoPanel;
	}
	
	private JPanel getKozepsoPanel() {
		if(kozepsoPanel == null) {
			kozepsoPanel = new JPanel();
			kozepsoPanel.setLayout(new GridLayout(2, 1));
			kozepsoPanel.add(getFelsoKozepPanel());
			kozepsoPanel.add(getAlsoKozepScrollPane());
		}
		return kozepsoPanel;
	}
	
	private JPanel getFelsoKozepPanel() {
		if(felsoKozepPanel == null) {
			felsoKozepPanel = new JPanel();
			felsoKozepPanel.setLayout(new GridLayout(5, 2));
			felsoKozepPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Kiv�lasztott �gyf�l param�terei �s sz�ml�i"));
			
			felsoKozepPanel.add(new JLabel("�gyf�lsz�m:"));
			felsoKozepPanel.add(ugyfelszamLabel);
			
			felsoKozepPanel.add(new JLabel("N�v:"));
			felsoKozepPanel.add(nevLabel);
			
			felsoKozepPanel.add(new JLabel("Lakc�m:"));
			felsoKozepPanel.add(lakcimLabel);
			
			felsoKozepPanel.add(new JLabel("Telefonsz�m:"));
			felsoKozepPanel.add(telszamLabel);
			
			felsoKozepPanel.add(new JLabel("Szem�lyigazolv�ny sz�m:"));
			felsoKozepPanel.add(szigszamLabel);
		}
		return felsoKozepPanel;
	}
	
	private JScrollPane getAlsoKozepScrollPane() {
		if (alsoKozepScrollPane == null) {
			alsoKozepScrollPane = new JScrollPane();
			alsoKozepScrollPane.setViewportView(getJTable());
		}
		return alsoKozepScrollPane;
	}
	
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(rowData, COLUMN_NAMES);
		}
		return jTable;
	}
	
	private JPanel getAlsoButtonPanel() {
		if(alsoPanel == null) {
			alsoPanel = new JPanel();
			alsoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			alsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			alsoPanel.add(valtozoButton);
			alsoPanel.add(megsemButton);
		}
		return alsoPanel;
	}
	
	@Override
	public void kiValasztottUgyfel(Ugyfel ugyfel) {
		ugyfelszamLabel.setText("" + ugyfel.getUgyfelszam());
		nevLabel.setText(ugyfel.getNev());
		lakcimLabel.setText(ugyfel.getLakcim());
		telszamLabel.setText(ugyfel.getTelszam());
		szigszamLabel.setText(ugyfel.getSzigszam());
		tablaFrissites();
		if(getJTable().getRowCount() != 0)
			valtozoButton.setEnabled(true);
		else
			valtozoButton.setEnabled(false);
	}
	
	private void tablaFrissites() {
		try {
			lekerdezesEredmeny = owner.getDBConnect().szamlaLekerdezesUgyfelszamSzerint(Integer.parseInt(ugyfelszamLabel.getText()));
			rowData.removeAllElements();
			Vector<String> sor = null;
			while(lekerdezesEredmeny.next()) {
				sor = new Vector<String>();
				sor.add("" + lekerdezesEredmeny.getLong("szamlaszam"));
				sor.add("" + lekerdezesEredmeny.getInt("ugyfelszam"));
				sor.add("" + lekerdezesEredmeny.getLong("egyenleg"));
				sor.add(lekerdezesEredmeny.getString("statusz"));
				rowData.add(sor);
			}
			jTable = new JTable(rowData, COLUMN_NAMES);
			getKozepsoPanel().setVisible(false);
			remove(getKozepsoPanel());
			kozepsoPanel = null;
			felsoKozepPanel = null;
			alsoKozepScrollPane = null;
			add(getKozepsoPanel());
			getKozepsoPanel().setVisible(true);
			setVisible(true);
		} catch(SQLException e1) {
			owner.nincsKapcsolatMessage();
		} catch(NumberFormatException e1) {
			JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
			return;
		} finally {
			try {
				if(lekerdezesEredmeny != null)
					lekerdezesEredmeny.close();
			} catch (SQLException e2) {
				owner.nincsKapcsolatMessage();
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == kivalasztButton) {
			new UgyfelKivalasztasDialog(this, owner, this);
			return;
		}
		if(event.getSource() == megsemButton) {
			setVisible(false);
			return;
		}
		if(jTable.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Nincs kiv�lasztva sz�mla!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(jTable.getSelectedRowCount() > 1) {
			JOptionPane.showMessageDialog(this, "Csak egy sz�mla v�laszthat�!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int kivalasztottSor = jTable.getSelectedRow();
		if(rowData.get(kivalasztottSor).get(3).equals("t�r�lt")) {
			JOptionPane.showMessageDialog(this, "Csak akt�v st�tusz� sz�mla v�laszthat�!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(event.getSource() == valtozoButton) {
			long szamlaszam = 0;
			try {
				szamlaszam = Long.parseLong(rowData.get(kivalasztottSor).get(0));
			} catch(NumberFormatException numberEx) {
				JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
				return;
			}
			/**
			 *  Nem az �ppen lek�rdezett egyenleg ker�l kifizet�sre hanem majd az inform�ci�s �zenetben szerepl�
			 *  hiszen a lek�rdezett egyenleg nem friss�l �gyint�z�s k�zben �s lehets�ges, hogy k�zben utal�s t�rt�nt.
			 */
			if(tipus == Tipus.TORLES) {
				if(JOptionPane.showConfirmDialog(this, "Biztosan t�rl�sre ker�lj�n a " + szamlaszam + " sz�m� sz�mla?",
						"T�rl�s meger�s�t�se", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try {
						long kifizetes = owner.getDBConnect().szamlaTorles(szamlaszam);
						tablaFrissites();
						JOptionPane.showMessageDialog(this, "" + kifizetes + " Ft p�nzt�ri kifizet�s a " + szamlaszam + " sz�m� sz�ml�r�l.", "P�nzt�ri kifizet�s", JOptionPane.INFORMATION_MESSAGE);
					} catch (SQLException e) {
						owner.nincsKapcsolatMessage();
					}
				}
				return;
			}
			if(tipus == Tipus.BEFIZETES) {
				long osszeg = OsszegBevitelDialog.showOsszegBevitelDialog(this);
				if(osszeg != 0) {
					if((JOptionPane.showConfirmDialog(this, "Befizet�s meger�s�t�se a " + szamlaszam + " sz�m� sz�ml�ra "
							+ osszeg + " Ft �sszegben.", "Meger�s�t�s", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)) {
						try {
							if(owner.getDBConnect().szamlaBefizetes(szamlaszam, osszeg)) {
							}else {
								JOptionPane.showMessageDialog(this, "Nem siker�lt a sz�ml�ra a befizet�s mert id�k�zben t�r�lve lett a sz�mla.",
										"Sikertelen befizet�s", JOptionPane.ERROR_MESSAGE);
							}
							tablaFrissites();
						} catch (SQLException e) {
							owner.nincsKapcsolatMessage();
						}
					}
				}
				return;
			}
			if(tipus == Tipus.KIFIZETES) {
				long egyenleg = 0;
				try {
					egyenleg = Long.parseLong(rowData.get(kivalasztottSor).get(2));
				} catch(NumberFormatException numberEx) {
					JOptionPane.showMessageDialog(this, "Rendszerhiba!", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
					return;
				}
				long osszeg = OsszegBevitelDialog.showOsszegBevitelDialog(this);
				if(osszeg != 0 & osszeg <= egyenleg) {
					if((JOptionPane.showConfirmDialog(this, "Kifizet�s meger�s�t�se a " + szamlaszam + " sz�m� sz�ml�r�l "
							+ osszeg + " Ft �sszegben.", "Meger�s�t�s", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)) {
						try {
							if(owner.getDBConnect().szamlaKifizetes(szamlaszam, osszeg)) {
							}else {
								JOptionPane.showMessageDialog(this, "A sz�ml�n jelenleg nem �ll rendelkez�sre ekkora �sszeg!\n" +
										"A sz�mla adatainak friss�t�s�hez �jra le kell k�rdezni a sz�ml�t!", "Kifizet�s hiba", JOptionPane.ERROR_MESSAGE);
							}
							tablaFrissites();
						} catch (SQLException e) {
							owner.nincsKapcsolatMessage();
						}
					}
				}
				/**
				 * 	Ha a megjelen�tett egyenleg kevesebb m�ris hiba�zenet. Mindegy, hogy k�zben n�velve lett e.
				 */
				if(osszeg > egyenleg) {
					JOptionPane.showMessageDialog(this, "A sz�ml�n jelenleg nem �ll rendelkez�sre ekkora �sszeg!\n" +
							"A sz�mla adatainak friss�t�s�hez �jra le kell k�rdezni a sz�ml�t!", "Kifizet�s hiba", JOptionPane.ERROR_MESSAGE);
					return;
				}
				return;
			}
		}
	}
}
