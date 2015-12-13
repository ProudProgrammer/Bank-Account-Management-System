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
	 * 	A t�bl�zat sorai �s fejl�ce
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
	private JButton keresButton = new JButton("�gyf�l keres�se");
	private JButton kivalasztButton = new JButton("�gyf�l kiv�laszt�sa");
	private JButton megsemButton = new JButton("M�gsem");
	
	private ResultSet lekerdezesEredmeny = null;
	
	static {
		COLUMN_NAMES.add("�gyf�lsz�m");
		COLUMN_NAMES.add("N�v");
		COLUMN_NAMES.add("Lakc�m");
		COLUMN_NAMES.add("Telefonsz�m");
		COLUMN_NAMES.add("Szem�lyig. sz�m");
		COLUMN_NAMES.add("St�tusz");
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
		setTitle("�gyf�l keres�s �s kiv�laszt�s");
		setIconImage(this.getToolkit().createImage("icons/Profile.png"));
		
		setLayout(new BorderLayout());
		add(getFelsoPanel(), BorderLayout.NORTH);
		add(getKozepsoScrollPane(), BorderLayout.CENTER);
		add(getAlsoButtonPanel(), BorderLayout.SOUTH);
		
		ugyfelszamTextField.setToolTipText("Csak sz�m enged�lyezett ebben a mez�ben 6 karakter hossz�s�gban");
		szigszamTextField.setToolTipText("Csak bet� �s sz�m enged�lyezett ebben a mez�ben 20 karakter hossz�s�gban");
		nevTextField.setToolTipText("Csak bet� �s space enged�lyezett ebben a mez�ben 30 karakter hossz�s�gban");
		
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
			felsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "�gyf�l keres�se"));
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
			ugyfelszamPanelBal.add(new JLabel("�gyf�lsz�m: "));
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
			szigszamPanelBal.add(new JLabel("Szem�lyigazolv�ny sz�m: "));
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
			nevPanelBal.add(new JLabel("�gyf�l neve: "));
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
		JOptionPane.showMessageDialog(this, "Nincs kapcsolat az adatb�zissal!\n", "Kapcsolathiba", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == kivalasztButton) {
			if(jTable.getSelectedRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "Nincs kiv�lasztva �gyf�l!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(jTable.getSelectedRowCount() > 1) {
				JOptionPane.showMessageDialog(this, "Csak egy �gyf�l v�laszthat�!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
				return;
			}
			int kivalasztottSor = jTable.getSelectedRow();
			if(rowData.get(kivalasztottSor).get(5).equals("t�r�lt")) {
				JOptionPane.showMessageDialog(this, "Csak akt�v st�tusz� �gyf�l v�laszthat�!", "Hib�s kiv�laszt�s", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(this, "Az �gyf�l beviteli mez�ben nem megfelel� form�tum", "Hib�s form�tum", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		tablaFrissites();
	}
}
