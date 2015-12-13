package ui;

import java.awt.BorderLayout;
import java.awt.Component;
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

public class UgyfelKivalasztasDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private MainWindow main = null;
	private UgyfelValaszto ugyfelvalaszto = null;
	
	private int ugyfelszam = 0;
	
	private JPanel felsoPanel = null;
	private JScrollPane kozepsoScrollPane = null;
	private JPanel alsoButtonPanel = null;
	
	private JTable jTable = null;
	
	/**
	 * 	A táblázat sorai és fejléce
	 */
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private static final Vector<String> COLUMN_NAMES = new Vector<String>();
	
	private JPanel ugyfelszamPanel = null;
	private JPanel szigszamPanel = null;
	private JPanel nevPanel = null;
	private JPanel keresButtonPanel = null;
	
	private OnlyNumberTextField ugyfelszamTextField = new OnlyNumberTextField(20, 6);
	private IgazolvanyTextField szigszamTextField = new IgazolvanyTextField(20, 20);
	private NevTextField nevTextField = new NevTextField(20, 30);
	private JButton keresButton = new JButton("Ügyfél keresése");
	private JButton kivalasztButton = new JButton("Ügyfél kiválasztása");
	private JButton megsemButton = new JButton("Mégsem");
	
	private ResultSet lekerdezesEredmeny = null;
	
	static {
		COLUMN_NAMES.add("Ügyfélszám");
		COLUMN_NAMES.add("Név");
		COLUMN_NAMES.add("Lakcím");
		COLUMN_NAMES.add("Telefonszám");
		COLUMN_NAMES.add("Személyig. szám");
		COLUMN_NAMES.add("Státusz");
	}
	
	{
		keresButton.setIcon(new ImageIcon("icons/Search.png"));
		kivalasztButton.setIcon(new ImageIcon("icons/Load.png"));
		megsemButton.setIcon(new ImageIcon("icons/Exit.png"));
	}
	
	public UgyfelKivalasztasDialog(Component owner, MainWindow main, UgyfelValaszto ugyfelvalaszto) {
		this.main = main;
		this.ugyfelvalaszto = ugyfelvalaszto;
		
		setSize(650, 500);
		setResizable(true);
		setModal(true);
		setLocationRelativeTo(owner);
		setTitle("Ügyfél keresés és kiválasztás");
		setIconImage(this.getToolkit().createImage("icons/Profile.png"));
		
		setLayout(new BorderLayout());
		add(getFelsoPanel(), BorderLayout.NORTH);
		add(getKozepsoScrollPane(), BorderLayout.CENTER);
		add(getAlsoButtonPanel(), BorderLayout.SOUTH);
		
		ugyfelszamTextField.setToolTipText("Csak szám engedélyezett ebben a mezõben 6 karakter hosszúságban");
		szigszamTextField.setToolTipText("Csak betû és szám engedélyezett ebben a mezõben 20 karakter hosszúságban");
		nevTextField.setToolTipText("Csak betû és space engedélyezett ebben a mezõben 30 karakter hosszúságban");
		
		ugyfelszamTextField.addActionListener(this);
		szigszamTextField.addActionListener(this);
		nevTextField.addActionListener(this);
		keresButton.addActionListener(this);
		kivalasztButton.addActionListener(this);
		megsemButton.addActionListener(this);
		
		setVisible(true);
	}
	
	private JPanel getFelsoPanel() {
		if (felsoPanel == null) {
			felsoPanel = new JPanel();
			felsoPanel.setLayout(new GridLayout(4,1));
			felsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "Ügyfél keresése"));
			felsoPanel.add(getUgyfelszamPanel());
			felsoPanel.add(getSzigszamPanel());
			felsoPanel.add(getNevPanel());
			felsoPanel.add(getButtonPanel());
		}
		return felsoPanel;
	}
	
	private JPanel getUgyfelszamPanel() {
		if(ugyfelszamPanel == null) {
			ugyfelszamPanel = new JPanel(new GridLayout(1, 2));
			JPanel ugyfelszamPanelBal = new JPanel(new FlowLayout());
			JPanel ugyfelszamPanelJobb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			ugyfelszamPanelBal.add(new JLabel("Ügyfélszám: "));
			ugyfelszamPanelJobb.add(ugyfelszamTextField);
			ugyfelszamPanel.add(ugyfelszamPanelBal);
			ugyfelszamPanel.add(ugyfelszamPanelJobb);
		}
		return ugyfelszamPanel;
	}
	
	private JPanel getSzigszamPanel() {
		if(szigszamPanel == null) {
			szigszamPanel = new JPanel(new GridLayout(1, 2));
			JPanel szigszamPanelBal = new JPanel(new FlowLayout());
			JPanel szigszamPanelJobb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			szigszamPanelBal.add(new JLabel("Személyigazolvány szám: "));
			szigszamPanelJobb.add(szigszamTextField);
			szigszamPanel.add(szigszamPanelBal);
			szigszamPanel.add(szigszamPanelJobb);
		}
		return szigszamPanel;
	}
	
	private JPanel getNevPanel() {
		if(nevPanel == null) {
			nevPanel = new JPanel(new GridLayout(1, 2));
			JPanel nevPanelBal = new JPanel(new FlowLayout());
			JPanel nevPanelJobb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			nevPanelBal.add(new JLabel("Ügyfél neve: "));
			nevPanelJobb.add(nevTextField);
			nevPanel.add(nevPanelBal);
			nevPanel.add(nevPanelJobb);
		}
		return nevPanel;
	}
	
	private JPanel getButtonPanel() {
		if(keresButtonPanel == null) {
			keresButtonPanel = new JPanel();
			keresButtonPanel.add(keresButton);
		}
		return keresButtonPanel;
	}
	
	private JScrollPane getKozepsoScrollPane() {
		if (kozepsoScrollPane == null) {
			kozepsoScrollPane = new JScrollPane();
			kozepsoScrollPane.setViewportView(getJTable());
		}
		return kozepsoScrollPane;
	}
	
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(rowData, COLUMN_NAMES);
		}
		return jTable;
	}
	
	private JPanel getAlsoButtonPanel() {
		if(alsoButtonPanel == null) {
			alsoButtonPanel = new JPanel();
			alsoButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			alsoButtonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			alsoButtonPanel.add(kivalasztButton);
			alsoButtonPanel.add(megsemButton);
		}
		return alsoButtonPanel;
	}

	private void tablaFrissites() {
		try {
			lekerdezesEredmeny = main.getDBConnect().ugyfelLekerdezes(ugyfelszam, szigszamTextField.getText(), nevTextField.getText());
			rowData.removeAllElements();
			Vector<String> sor = null;
			while(lekerdezesEredmeny.next()) {
				sor = new Vector<String>();
				sor.add("" + lekerdezesEredmeny.getInt("ugyfelszam"));
				sor.add(lekerdezesEredmeny.getString("nev"));
				sor.add(lekerdezesEredmeny.getString("lakcim"));
				sor.add(lekerdezesEredmeny.getString("telszam"));
				sor.add(lekerdezesEredmeny.getString("szigszam"));
				sor.add(lekerdezesEredmeny.getString("statusz"));
				rowData.add(sor);
			}
			jTable = new JTable(rowData, COLUMN_NAMES);
			getKozepsoScrollPane().setVisible(false);
			remove(getKozepsoScrollPane());
			kozepsoScrollPane = null;
			add(getKozepsoScrollPane());
			getKozepsoScrollPane().setVisible(true);
			setVisible(true);
		} catch (SQLException e1) {
			nincsKapcsolatMessage();
		} finally {
			try {
				if(lekerdezesEredmeny != null)
					lekerdezesEredmeny.close();
			} catch (SQLException e2) {
				nincsKapcsolatMessage();
			}
		}
	}
	
	private void nincsKapcsolatMessage() {
		JOptionPane.showMessageDialog(this, "Nincs kapcsolat az adatbázissal!\n", "Kapcsolathiba", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == kivalasztButton) {
			if(jTable.getSelectedRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "Nincs kiválasztva ügyfél!", "Hibás kiválasztás", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(jTable.getSelectedRowCount() > 1) {
				JOptionPane.showMessageDialog(this, "Csak egy ügyfél választható!", "Hibás kiválasztás", JOptionPane.ERROR_MESSAGE);
				return;
			}
			int kivalasztottSor = jTable.getSelectedRow();
			if(rowData.get(kivalasztottSor).get(5).equals("törölt")) {
				JOptionPane.showMessageDialog(this, "Csak aktív státuszú ügyfél választható!", "Hibás kiválasztás", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				ugyfelszam = Integer.parseInt(rowData.get(kivalasztottSor).get(0));
			} catch(NumberFormatException numberEx) {
				JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Ugyfel kivalasztottUgyfel = new Ugyfel(ugyfelszam, rowData.get(kivalasztottSor).get(1), rowData.get(kivalasztottSor).get(2), 
					rowData.get(kivalasztottSor).get(3), rowData.get(kivalasztottSor).get(4), rowData.get(kivalasztottSor).get(5));
			ugyfelvalaszto.kiValasztottUgyfel(kivalasztottUgyfel);
			setVisible(false);
			return;
		}
		if(e.getSource() == megsemButton) {
			setVisible(false);
			return;
		}
		if(ugyfelszamTextField.getText().equals(""))
			ugyfelszam = 0;
		else {
			try {
				ugyfelszam = Integer.parseInt(ugyfelszamTextField.getText());
			} catch(NumberFormatException numberEx) {
				JOptionPane.showMessageDialog(this, "Az ügyfél beviteli mezõben nem megfelelõ formátum", "Hibás formátum", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		tablaFrissites();
	}
}
