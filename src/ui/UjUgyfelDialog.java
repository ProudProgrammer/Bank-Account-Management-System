package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public final class UjUgyfelDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private MainWindow owner = null;
	
	private JPanel tartalomPanel = null;
	private JPanel nevPanel = null;
	private JPanel lakcimPanel = null;
	private JPanel telszamPanel = null;
	private JPanel szigszamPanel = null;
	private JPanel buttonPanel = null;
	
	private NevTextField nevTextField = new NevTextField(20, 30);
	private LakcimTextField lakcimTextField = new LakcimTextField(20, 100);
	private OnlyNumberTextField telszamTextField = new OnlyNumberTextField(20, 20);
	private IgazolvanyTextField szigszamTextField = new IgazolvanyTextField(20, 20);
	private JButton mentesButton = new JButton("�gyf�l l�trehoz�sa");
	private JButton megsemButton = new JButton("M�gsem");
	
	{
		mentesButton.setIcon(new ImageIcon("icons/Save.png"));
		megsemButton.setIcon(new ImageIcon("icons/Exit.png"));
	}
	
	public UjUgyfelDialog(MainWindow owner) {
		this.owner = owner;
		initialize();
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	
	private void initialize() {
		setSize(500, 300);
		setResizable(false);
		setModal(true);
		setTitle("�j �gyf�l l�trehoz�sa");
		setIconImage(this.getToolkit().createImage("icons/Profile.png"));
		setContentPane(getTartalomPanel());
		
		nevTextField.setToolTipText("Csak bet� �s space enged�lyezett ebben a mez�ben 30 karakter hossz�s�gban");
		lakcimTextField.setToolTipText("Csak bet�, sz�m �s . , / �r�sjelek enged�lyezettek ebben a mez�ben 100 karakter hossz�s�gban");
		telszamTextField.setToolTipText("Csak sz�m enged�lyezett ebben a mez�ben 20 karakter hossz�s�gban");
		szigszamTextField.setToolTipText("Csak bet� �s sz�m enged�lyezett ebben a mez�ben 20 karakter hossz�s�gban");
		
		nevTextField.addActionListener(this);
		lakcimTextField.addActionListener(this);
		telszamTextField.addActionListener(this);
		szigszamTextField.addActionListener(this);
		mentesButton.addActionListener(this);
		megsemButton.addActionListener(this);
	}
	
	private JPanel getTartalomPanel() {
		if (tartalomPanel == null) {
			tartalomPanel = new JPanel();
			tartalomPanel.setLayout(new GridLayout(5,1));
			tartalomPanel.add(getNevPanel());
			tartalomPanel.add(getLakcimPanel());
			tartalomPanel.add(getTelszamPanel());
			tartalomPanel.add(getSzigszamPanel());
			tartalomPanel.add(getButtonPanel());

		}
		return tartalomPanel;
	}
	
	private JPanel getNevPanel() {
		if(nevPanel == null) {
			nevPanel = new JPanel();
			nevPanel.setLayout(new GridLayout(1, 2));
			JPanel nevPanelBal = new JPanel();
			nevPanelBal.setLayout(new FlowLayout(FlowLayout.LEFT));
			JPanel nevPanelJobb = new JPanel();
			nevPanelJobb.setLayout(new FlowLayout(FlowLayout.RIGHT));
			nevPanel.add(nevPanelBal);
			nevPanel.add(nevPanelJobb);
			nevPanelBal.add(new JLabel("N�v:"));
			nevPanelJobb.add(nevTextField);
		}
		return nevPanel;
	}
	
	private JPanel getLakcimPanel() {
		if(lakcimPanel == null) {
			lakcimPanel = new JPanel();
			lakcimPanel.setLayout(new GridLayout(1, 2));
			JPanel lakcimPanelBal = new JPanel();
			lakcimPanelBal.setLayout(new FlowLayout(FlowLayout.LEFT));
			JPanel lakcimPanelJobb = new JPanel();
			lakcimPanelJobb.setLayout(new FlowLayout(FlowLayout.RIGHT));
			lakcimPanel.add(lakcimPanelBal);
			lakcimPanel.add(lakcimPanelJobb);
			lakcimPanelBal.add(new JLabel("Lakc�m:"));
			lakcimPanelJobb.add(lakcimTextField);
		}
		return lakcimPanel;
	}
	
	private JPanel getTelszamPanel() {
		if(telszamPanel == null) {
			telszamPanel = new JPanel();
			telszamPanel.setLayout(new GridLayout(1, 2));
			JPanel telszamPanelBal = new JPanel();
			telszamPanelBal.setLayout(new FlowLayout(FlowLayout.LEFT));
			JPanel telszamPanelJobb = new JPanel();
			telszamPanelJobb.setLayout(new FlowLayout(FlowLayout.RIGHT));
			telszamPanel.add(telszamPanelBal);
			telszamPanel.add(telszamPanelJobb);
			telszamPanelBal.add(new JLabel("Telefonsz�m:"));
			telszamPanelJobb.add(telszamTextField);
		}
		return telszamPanel;
	}
	
	private JPanel getSzigszamPanel() {
		if(szigszamPanel == null) {
			szigszamPanel = new JPanel();
			szigszamPanel.setLayout(new GridLayout(1, 2));
			JPanel szigszamPanelBal = new JPanel();
			szigszamPanelBal.setLayout(new FlowLayout(FlowLayout.LEFT));
			JPanel szigszamPanelJobb = new JPanel();
			szigszamPanelJobb.setLayout(new FlowLayout(FlowLayout.RIGHT));
			szigszamPanel.add(szigszamPanelBal);
			szigszamPanel.add(szigszamPanelJobb);
			szigszamPanelBal.add(new JLabel("Szem�lyigazolv�ny sz�m:"));
			szigszamPanelJobb.add(szigszamTextField);
		}
		return szigszamPanel;
	}
	
	private JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			buttonPanel.add(mentesButton);
			buttonPanel.add(megsemButton);
		}
		return buttonPanel;
	}

	/** 
	 *	Az �gyf�lsz�m 6 jegy� �s 100000-999999 tartom�nyban lehet, a v�letlensz�m gener�tor k�plete:
	 *	" min �s max k�z�tti v�letlen sz�m gener�l�sa = (int)(Math.random() * (max - min + 1) + min) "
	 *	A rendszer v�letlen�l gener�lja az �gyf�lsz�mot majd megpr�b�lja besz�rni az �gyf�l t�bl�ba, ha
	 *	nem siker�l akkor kiv�lt egy SQLException-t �s tov�bb pr�b�lkozik �gy 1000 k�s�rletig, majd le�ll
	 *	�s k�zli, hogy elfogytak a szabad �gyf�lsz�mok.
	 */
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == megsemButton) {
			setVisible(false);
			return;
		} else {
			if(nevTextField.getText().length() == 0 || lakcimTextField.getText().length() == 0 || 
					telszamTextField.getText().length() == 0 || szigszamTextField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Minden mez�t ki kell t�lteni!", "Kit�ltetlen mez�", JOptionPane.ERROR_MESSAGE);
				return;
			}
			boolean nemSikerult = true;
			int kiserlet = 0;
			while(nemSikerult & kiserlet <= 1000) {
				try {
					owner.getDBConnect().ujUgyfel((int)(Math.random() * 900000 + 100000), nevTextField.getText(), lakcimTextField.getText(), telszamTextField.getText(), szigszamTextField.getText(), "akt�v");
					nemSikerult = false;
				} catch (SQLException e) {
					kiserlet++;
				}
			}
			if(nemSikerult) {
				JOptionPane.showMessageDialog(this, "Nem siker�lt az �gyfelet l�trehozni mert elfogytak a szabad �gyf�lsz�mok", "Nincs szabad �gyf�lsz�m", JOptionPane.ERROR_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(this, "A " + nevTextField.getText() + " nev� �gyf�l l�trehozva", "Sikeres bevitel", JOptionPane.INFORMATION_MESSAGE);
			nevTextField.setText("");
			lakcimTextField.setText("");
			telszamTextField.setText("");
			szigszamTextField.setText("");
		}
	}
}
