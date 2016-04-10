package br.ronaldo.interfaceusuario;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import br.ronaldo.comum.Cliente;
import br.ronaldo.comum.InterfaceServidor;
import br.ronaldo.comundownload.ArquivoDownload;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class TelaAcesso extends JFrame implements InterfaceServidor{

	private JPanel contentPane;
	private JTextField txtPorta;
	private JButton btnConectar;
	private JButton btnDesconectar;
	private JComboBox cbnIp;
	private InterfaceServidor servidor;
	private Registry registro;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaAcesso frame = new TelaAcesso();
					frame.setVisible(true);
					frame.configurar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void configurar() {
		
		btnDesconectar.setEnabled(false);
		List<String> lista = getIpDisponivel();
		cbnIp.setModel(new DefaultComboBoxModel<String>(new Vector<String>(lista)));
		cbnIp.setSelectedIndex(0);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				//fecharTodosClientes();
			}
		});
		
		btnConectar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				conectar();
				
			}
			
		});
		
		btnDesconectar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				desconectar();
				
			}
			
		});
		
	}

	protected void desconectar() {
		
		try {
			UnicastRemoteObject.unexportObject(this, true);
			UnicastRemoteObject.unexportObject(registro, true);

			cbnIp.setEnabled(true);
			txtPorta.setEnabled(true);
			btnConectar.setEnabled(true);

			btnDesconectar.setEnabled(false);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

	protected void conectar() {
		
		String porta = txtPorta.getText().trim();
		
		if(!porta.matches("[0-9]+") || porta.length() >5){
			JOptionPane.showMessageDialog(this, "Porta Inválida");
			return;
		}
		
		int portaInteira = Integer.parseInt(porta);
		
		if(portaInteira < 1024 || portaInteira > 65535){
			JOptionPane.showMessageDialog(this, "Porta Inválida");
			return;
		}
		
		try{
			servidor = (InterfaceServidor) UnicastRemoteObject.exportObject(this, 0);
			registro = LocateRegistry.createRegistry(portaInteira);
			registro.rebind(InterfaceServidor.NOME_SERVICO, servidor);
			
			cbnIp.setEnabled(false);
			txtPorta.setEnabled(false);
			btnConectar.setEnabled(false);
			btnDesconectar.setEnabled(true);
		}catch(RemoteException e){
			JOptionPane.showMessageDialog(this, "Erro ao Iniciar o Serviço");
			e.printStackTrace();
		}
		
	}

	private List<String> getIpDisponivel() {
		
		List<String> lista = new ArrayList<String>();
		
		try{
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			while(ifaces.hasMoreElements()){
				NetworkInterface ifc = ifaces.nextElement();
				if(ifc.isUp()){
					Enumeration<InetAddress> endereco = ifc.getInetAddresses();
					while(endereco.hasMoreElements()){
						InetAddress iddr = endereco.nextElement();
						String ip = iddr.getHostAddress();
						if (ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
							lista.add(ip);
						}
					}
				}
			}
		}catch(SocketException e){
			e.printStackTrace();
		}
		return lista;
	}

	public TelaAcesso() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblIp = new JLabel("IP");
		GridBagConstraints gbc_lblIp = new GridBagConstraints();
		gbc_lblIp.insets = new Insets(0, 0, 5, 5);
		gbc_lblIp.anchor = GridBagConstraints.EAST;
		gbc_lblIp.gridx = 1;
		gbc_lblIp.gridy = 0;
		contentPane.add(lblIp, gbc_lblIp);
		
		cbnIp = new JComboBox();
		GridBagConstraints gbc_cbnIp = new GridBagConstraints();
		gbc_cbnIp.gridwidth = 6;
		gbc_cbnIp.insets = new Insets(0, 0, 5, 5);
		gbc_cbnIp.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbnIp.gridx = 2;
		gbc_cbnIp.gridy = 0;
		contentPane.add(cbnIp, gbc_cbnIp);
		
		JLabel lblPorta = new JLabel("Porta");
		GridBagConstraints gbc_lblPorta = new GridBagConstraints();
		gbc_lblPorta.anchor = GridBagConstraints.EAST;
		gbc_lblPorta.insets = new Insets(0, 0, 0, 5);
		gbc_lblPorta.gridx = 1;
		gbc_lblPorta.gridy = 1;
		contentPane.add(lblPorta, gbc_lblPorta);
		
		txtPorta = new JTextField();
		GridBagConstraints gbc_txtPorta = new GridBagConstraints();
		gbc_txtPorta.gridwidth = 4;
		gbc_txtPorta.insets = new Insets(0, 0, 0, 5);
		gbc_txtPorta.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPorta.gridx = 2;
		gbc_txtPorta.gridy = 1;
		contentPane.add(txtPorta, gbc_txtPorta);
		txtPorta.setColumns(10);
		
		btnConectar = new JButton("Conectar");
		GridBagConstraints gbc_btnConectar = new GridBagConstraints();
		gbc_btnConectar.insets = new Insets(0, 0, 0, 5);
		gbc_btnConectar.gridx = 7;
		gbc_btnConectar.gridy = 1;
		contentPane.add(btnConectar, gbc_btnConectar);
		
		btnDesconectar = new JButton("Desconectar");
		GridBagConstraints gbc_btnDesconectar = new GridBagConstraints();
		gbc_btnDesconectar.insets = new Insets(0, 0, 0, 5);
		gbc_btnDesconectar.gridx = 8;
		gbc_btnDesconectar.gridy = 1;
		contentPane.add(btnDesconectar, gbc_btnDesconectar);
	}

	@Override
	public void RegistrarNovoCliente(Cliente c) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informarListaArquivo(Cliente c, List<ArquivoDownload> lista) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Cliente, List<ArquivoDownload>> buscarArquivo(String nome) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] downloadArquivo(ArquivoDownload arquivo) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void desconectar(Cliente c) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
