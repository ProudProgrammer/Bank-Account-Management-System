package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;

import db.Ugyfel;

public class AtutalasPanel extends JPanel implements ActionListener, UgyfelValaszto {
	
	private static final long serialVersionUID = 1L;
	
	private MainWindow owner = null;
	
	private JPanel felsoPanel = null;
	private JButton atutalasButton = new JButton("�tutal�s ind�t�sa");
	
	private JPanel kozepsoPanel = null;
	
	private JPanel forrasPanel = null;
	private JPanel forrasButtonPanel = null;
	private JPanel forrasBelsoPanel = null;
	private JPanel forrasBelsoFelsoPanel = null;
	private JScrollPane forrasBelsoAlsoScrollPane = null;
	private JLabel forrasUgyfelszamLabel = new JLabel();
	private JLabel forrasNevLabel = new JLabel();
	private JLabel forrasLakcimLabel = new JLabel();
	private JLabel forrasTelszamLabel = new JLabel();
	private JLabel forrasSzigszamLabel = new JLabel();
	
	private JPanel celPanel = null;
	private JPanel celButtonPanel = null;
	private JPanel celBelsoPanel = null;
	private JPanel celBelsoFelsoPanel = null;
	private JScrollPane celBelsoAlsoScrollPane = null;
	private JLabel celUgyfelszamLabel = new JLabel();
	private JLabel celNevLabel = new JLabel();
	private JLabel celLakcimLabel = new JLabel();
	private JLabel celTelszamLabel = new JLabel();
	private JLabel celSzigszamLabel = new JLabel();
	
	/**
	 * 	A billen�kapcsol� vagy forr�s vagy c�l �ll�s� lehet
	 */
	private String billenoKapcsolo = "";
	private JButton forrasUgyfelValasztas = new JButton("Forr�s �gyf�l kiv�laszt�sa");
	private JButton celUgyfelValasztas = new JButton("C�l �gyf�l kiv�laszt�sa");
	
	/**
	 * 	A t�bl�zat sorai �s fejl�ce
	 */
	private Vector<Vector<String>> forrasSzamlaRowData = new Vector<Vector<String>>();
	private Vector<Vector<String>> celSzamlaRowData = new Vector<Vector<String>>();
	private static final Vector<String> COLUMN_NAMES = new Vector<String>();
	
	private JTable forrasTable = null;
	private JTable celTable = null;
	
	private ResultSet lekerdezesEredmeny = null;

	static {
		COLUMN_NAMES.add("Sz�mlasz�m");
		COLUMN_NAMES.add("�gyf�lsz�m");
		COLUMN_NAMES.add("Egyenleg");
		COLUMN_NAMES.add("St�tusz");
	}
	
	{
		forrasUgyfelValasztas.setIcon(new ImageIcon("icons/Load.png"));
		celUgyfelValasztas.setIcon(new ImageIcon("icons/Load.png"));
		atutalasButton.setIcon(new ImageIcon("icons/Line Chart.png"));
	}
	
	public AtutalasPanel(MainWindow owner) {
		this.owner = owner;
		setLayout(new BorderLayout());
		add(getFelsoPanel(), BorderLayout.NORTH);
		add(getKozepsoPanel(), BorderLayout.CENTER);
		
		atutalasButton.addActionListener(this);
		forrasUgyfelValasztas.addActionListener(this);
		celUgyfelValasztas.addActionListener(this);
	}

	private JPanel getFelsoPanel() {
		if(felsoPanel == null) {
			felsoPanel = new JPanel();
			felsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "�tutal�s tranzakci� kezdem�nyez�se"));
			felsoPanel.add(atutalasButton);
		}
		return felsoPanel;
	}
	
	private JPanel getKozepsoPanel() {
		if(kozepsoPanel == null) {
			kozepsoPanel = new JPanel();
			kozepsoPanel.setLayout(new GridLayout(1, 2));
			kozepsoPanel.add(getForrasPanel());
			kozepsoPanel.add(getCelPanel());
		}
		return kozepsoPanel;
	}
	
	private JPanel getForrasPanel() {
		if(forrasPanel == null) {
			forrasPanel = new JPanel();
			forrasPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "Forr�s (honnan) sz�mla kiv�laszt�sa"));
			forrasPanel.setLayout(new BorderLayout());
			forrasPanel.add(getForrasButtonPanel(), BorderLayout.NORTH);
			forrasPanel.add(getForrasBelsoPanel(), BorderLayout.CENTER);
		}
		return forrasPanel;
	}
	
	private JPanel getForrasButtonPanel() {
		if(forrasButtonPanel == null) {
			forrasButtonPanel = new JPanel();
			forrasButtonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			forrasButtonPanel.add(forrasUgyfelValasztas);
		}
		return forrasButtonPanel;
	}
	
	private JPanel getForrasBelsoPanel() {
		if(forrasBelsoPanel == null) {
			forrasBelsoPanel = new JPanel();
			forrasBelsoPanel.setLayout(new BorderLayout());
			forrasBelsoPanel.add(getForrasBelsoFelsoPanel(), BorderLayout.NORTH);
			forrasBelsoPanel.add(getForrasBelsoAlsoScrollPane(), BorderLayout.CENTER);
		}
		return forrasBelsoPanel;
	}
	
	private JPanel getForrasBelsoFelsoPanel() {
		if(forrasBelsoFelsoPanel == null) {
			forrasBelsoFelsoPanel = new JPanel();
			forrasBelsoFelsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			forrasBelsoFelsoPanel.setLayout(new GridLayout(5, 2));
			forrasBelsoFelsoPanel.add(new JLabel("�gyf�lsz�m:"));
			forrasBelsoFelsoPanel.add(forrasUgyfelszamLabel);
			
			forrasBelsoFelsoPanel.add(new JLabel("N�v:"));
			forrasBelsoFelsoPanel.add(forrasNevLabel);
			
			forrasBelsoFelsoPanel.add(new JLabel("Lakc�m:"));
			forrasBelsoFelsoPanel.add(forrasLakcimLabel);
			
			forrasBelsoFelsoPanel.add(new JLabel("Telefonsz�m:"));
			forrasBelsoFelsoPanel.add(forrasTelszamLabel);
			
			forrasBelsoFelsoPanel.add(new JLabel("Szem�lyigazolv�ny sz�m:"));
			forrasBelsoFelsoPanel.add(forrasSzigszamLabel);
		}
		return forrasBelsoFelsoPanel;
	}
	
	private JScrollPane getForrasBelsoAlsoScrollPane() {
		if(forrasBelsoAlsoScrollPane == null) {
			forrasBelsoAlsoScrollPane = new JScrollPane();
			forrasBelsoAlsoScrollPane.setViewportView(getForrasTable());
		}
		return forrasBelsoAlsoScrollPane;
	}
	
	private JTable getForrasTable() {
		if (forrasTable == null) {
			forrasTable = new JTable(forrasSzamlaRowData, COLUMN_NAMES);
		}
		return forrasTable;
	}
	
	private JPanel getCelPanel() {
		if(celPanel == null) {
			celPanel = new JPanel();
			celPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "C�l (hova) sz�mla kiv�laszt�sa"));
			celPanel.setLayout(new BorderLayout());
			celPanel.add(getCelButtonPanel(), BorderLayout.NORTH);
			celPanel.add(getCelBelsoPanel(), BorderLayout.CENTER);
		}
		return celPanel;
	}
	
	private JPanel getCelButtonPanel() {
		if(celButtonPanel == null) {
			celButtonPanel = new JPanel();
			celButtonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			celButtonPanel.add(celUgyfelValasztas);
		}
		return celButtonPanel;
	}
	
	private JPanel getCelBelsoPanel() {
		if(celBelsoPanel == null) {
			celBelsoPanel = new JPanel();
			celBelsoPanel.setLayout(new BorderLayout());
			celBelsoPanel.add(getCelBelsoFelsoPanel(), BorderLayout.NORTH);
			celBelsoPanel.add(getCelBelsoAlsoScrollPane(), BorderLayout.CENTER);
		}
		return celBelsoPanel;
	}
	
	private JPanel getCelBelsoFelsoPanel() {
		if(celBelsoFelsoPanel == null) {
			celBelsoFelsoPanel = new JPanel();
			celBelsoFelsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			celBelsoFelsoPanel.setLayout(new GridLayout(5, 2));
			celBelsoFelsoPanel.add(new JLabel("�gyf�lsz�m:"));
			celBelsoFelsoPanel.add(celUgyfelszamLabel);
			
			celBelsoFelsoPanel.add(new JLabel("N�v:"));
			celBelsoFelsoPanel.add(celNevLabel);
			
			celBelsoFelsoPanel.add(new JLabel("Lakc�m:"));
			celBelsoFelsoPanel.add(celLakcimLabel);
			
			celBelsoFelsoPanel.add(new JLabel("Telefonsz�m:"));
			celBelsoFelsoPanel.add(celTelszamLabel);
			
			celBelsoFelsoPanel.add(new JLabel("Szem�lyigazolv�ny sz�m:"));
			celBelsoFelsoPanel.add(celSzigszamLabel);
		}
		return celBelsoFelsoPanel;
	}
	
	private JScrollPane getCelBelsoAlsoScrollPane() {
		if(celBelsoAlsoScrollPane == null) {
			celBelsoAlsoScrollPane = new JScrollPane();
			celBelsoAlsoScrollPane.setViewportView(getCelTable());
		}
		return celBelsoAlsoScrollPane;
	}
	
	private JTable getCelTable() {
		if (celTable == null) {
			celTable = new JTable(celSzamlaRowData, COLUMN_NAMES);
		}
		return celTable;
	}
	
	@Override
	public void kiValasztottUgyfel(Ugyfel ugyfel) {
		if(billenoKapcsolo.equals("forr�s")) {
			forrasUgyfelszamLabel.setText("" + ugyfel.getUgyfelszam());
			forrasNevLabel.setText(ugyfel.getNev());
			forrasLakcimLabel.setText(ugyfel.getLakcim());
			forrasTelszamLabel.setText(ugyfel.getTelszam());
			forrasSzigszamLabel.setText(ugyfel.getSzigszam());
			forrasTablaFrissites();
		}
		if(billenoKapcsolo.equals("c�l")) {
			celUgyfelszamLabel.setText("" + ugyfel.getUgyfelszam());
			celNevLabel.setText(ugyfel.getNev());
			celLakcimLabel.setText(ugyfel.getLakcim());
			celTelszamLabel.setText(ugyfel.getTelszam());
			celSzigszamLabel.setText(ugyfel.getSzigszam());
			celTablaFrissites();
		}
	}
	
	private void forrasTablaFrissites() {
		try {
			lekerdezesEredmeny = owner.getDBConnect().szamlaLekerdezesUgyfelszamSzerint(Integer.parseInt(forrasUgyfelszamLabel.getText()));
			forrasSzamlaRowData.removeAllElements();
			Vector<String> sor = null;
			while(lekerdezesEredmeny.next()) {
				sor = new Vector<String>();
				sor.add("" + lekerdezesEredmeny.getLong("szamlaszam"));
				sor.add("" + lekerdezesEredmeny.getInt("ugyfelszam"));
				sor.add("" + lekerdezesEredmeny.getLong("egyenleg"));
				sor.add(lekerdezesEredmeny.getString("statusz"));
				forrasSzamlaRowData.add(sor);
			}
			forrasTable = new JTable(forrasSzamlaRowData, COLUMN_NAMES);
			getForrasBelsoAlsoScrollPane().setVisible(false);
			forrasBelsoPanel.remove(getForrasBelsoAlsoScrollPane());
			forrasBelsoAlsoScrollPane = null;
			forrasBelsoPanel.add(getForrasBelsoAlsoScrollPane(), BorderLayout.CENTER);
			getForrasBelsoAlsoScrollPane().setVisible(true);
			owner.setVisible(true);
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
	
	private void celTablaFrissites() {
		try {
			lekerdezesEredmeny = owner.getDBConnect().szamlaLekerdezesUgyfelszamSzerint(Integer.parseInt(celUgyfelszamLabel.getText()));
			celSzamlaRowData.removeAllElements();
			Vector<String> sor = null;
			while(lekerdezesEredmeny.next()) {
				sor = new Vector<String>();
				sor.add("" + lekerdezesEredmeny.getLong("szamlaszam"));
				sor.add("" + lekerdezesEredmeny.getInt("ugyfelszam"));
				sor.add("" + lekerdezesEredmeny.getLong("egyenleg"));
				sor.add(lekerdezesEredmeny.getString("statusz"));
				celSzamlaRowData.add(sor);
			}
			celTable = new JTable(celSzamlaRowData, COLUMN_NAMES);
			getCelBelsoAlsoScrollPane().setVisible(false);
			celBelsoPanel.remove(getCelBelsoAlsoScrollPane());
			celBelsoAlsoScrollPane = null;
			celBelsoPanel.add(getCelBelsoAlsoScrollPane(), BorderLayout.CENTER);
			getCelBelsoAlsoScrollPane().setVisible(true);
			owner.setVisible(true);
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
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == atutalasButton) {
			if((celTable.getSelectedRowCount() == 0) || (forrasTable.getSelectedRowCount() == 0)) {
				JOptionPane.showMessageDialog(this, "Nincs kiv�lasztva sz�mla!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if((celTable.getSelectedRowCount() > 1) || (forrasTable.getSelectedRowCount() > 1)) {
				JOptionPane.showMessageDialog(this, "Csak egy sz�mla v�laszthat�!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
				return;
			}
			int forrasKivalasztottSor = forrasTable.getSelectedRow();
			int celKivalasztottSor = celTable.getSelectedRow();
			if(forrasSzamlaRowData.get(forrasKivalasztottSor).get(3).equals("t�r�lt") || celSzamlaRowData.get(celKivalasztottSor).get(3).equals("t�r�lt")) {
				JOptionPane.showMessageDialog(this, "Csak akt�v st�tusz� sz�mla v�laszthat�!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(forrasSzamlaRowData.get(forrasKivalasztottSor).get(0).equals(celSzamlaRowData.get(celKivalasztottSor).get(0))) {
				JOptionPane.showMessageDialog(this, "A forr�s �s a c�l sz�mla nem egyezhet meg!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
				return;
			}
			long forrasSzamlaszam = 0;
			long celSzamlaszam = 0;
			long egyenleg = 0;
			try {
				forrasSzamlaszam = Long.parseLong(forrasSzamlaRowData.get(forrasKivalasztottSor).get(0));
				celSzamlaszam = Long.parseLong(celSzamlaRowData.get(celKivalasztottSor).get(0));
				egyenleg = Long.parseLong(forrasSzamlaRowData.get(forrasKivalasztottSor).get(2));
			} catch(NumberFormatException numberEx) {
				JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
				return;
			}
			long osszeg = OsszegBevitelDialog.showOsszegBevitelDialog(this);
			if(osszeg > egyenleg) {
				JOptionPane.showMessageDialog(this, "A sz�ml�n jelenleg nem �ll rendelkez�sre ekkora �sszeg!", "Bevitel hiba", JOptionPane.ERROR_MESSAGE);
			}else if(osszeg == 0) {
				//nem csin�l semmit
			}else {
				String leiras = LeirasBevitelDialog.showOsszegBevitelDialog(this);
				try {
					if(owner.getDBConnect().atutalas(forrasSzamlaszam, celSzamlaszam, osszeg, leiras)) {
						JOptionPane.showMessageDialog(this, "A " + forrasSzamlaszam + " sz�m� sz�ml�r�l a " + celSzamlaszam + 
								" sz�m� sz�ml�ra az �tutal�s sikeresen megt�rt�nt " + osszeg + " Ft �sszegben.", "Sikeres tranzakci�", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(this, "Az �tutal�s sikertelen! Pr�b�lja �jra!", "Tranzakci� hiba", JOptionPane.ERROR_MESSAGE);
				}
			}
			celTablaFrissites();
			forrasTablaFrissites();
		}
		if(e.getSource() == forrasUgyfelValasztas) {
			billenoKapcsolo = "forr�s";
			new UgyfelKivalasztasDialog(this, owner, this);
			return;
		}
		if(e.getSource() == celUgyfelValasztas) {
			billenoKapcsolo = "c�l";
			new UgyfelKivalasztasDialog(this, owner, this);
			return;
		}
	}
}
