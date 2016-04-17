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
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
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
	private JLabel lblNomeArquivo;
	private JButton btnPesquisar;
	private JTextField txtNomeArquivo;
	private JLabel lblUsurio;
	private JTextField txtUsuario;
	private String nome;
	private Cliente cliente;
	private File[] listaArquivo;
	private ArquivoDownload listaArquivos;
	private List<ArquivoDownload> lista = new ArrayList<>();
	private Map<String, Cliente> mapaConectados = new HashMap<>();
	private Map<Cliente, List<ArquivoDownload>> mapaArquivos = new HashMap<>();
	private Map<Cliente, List<ArquivoDownload>> arquivoEncontrado = new HashMap<>();

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
		
		try {
			cliente = new Cliente();
			cliente.setIp(cbnIp.getSelectedItem().toString());
			cliente.setNome(txtUsuario.getText());
			cliente.setPorta(Integer.parseInt(txtPorta.getText()));
			RegistrarNovoCliente(cliente);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			String diretorioDownload = "C:/Users/Ronaldo Zuchi/Documents/Documentos Ronaldo/Meus Arquivos/Faculdade TADS/5º Ano/Download";
			File arquivoDownload = new File(diretorioDownload);
			listaArquivo = arquivoDownload.listFiles();
			for (int i=0; i<listaArquivo.length; i++){
				listaArquivos = new ArquivoDownload();
				File arquivo = listaArquivo[i];
				listaArquivos.setNomeArquivo(arquivo.getName());
				listaArquivos.setTamanhoArquivo(arquivo.length());
				lista.add(listaArquivos);
			}
			informarListaArquivo(cliente, lista);
		} catch (RemoteException e) {
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
		gbl_contentPane.columnWidths = new int[]{18, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblIp = new JLabel("IP");
		GridBagConstraints gbc_lblIp = new GridBagConstraints();
		gbc_lblIp.insets = new Insets(0, 0, 5, 5);
		gbc_lblIp.anchor = GridBagConstraints.WEST;
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
		gbc_lblPorta.anchor = GridBagConstraints.WEST;
		gbc_lblPorta.insets = new Insets(0, 0, 5, 5);
		gbc_lblPorta.gridx = 1;
		gbc_lblPorta.gridy = 1;
		contentPane.add(lblPorta, gbc_lblPorta);
		
		txtPorta = new JTextField();
		GridBagConstraints gbc_txtPorta = new GridBagConstraints();
		gbc_txtPorta.gridwidth = 4;
		gbc_txtPorta.insets = new Insets(0, 0, 5, 5);
		gbc_txtPorta.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPorta.gridx = 2;
		gbc_txtPorta.gridy = 1;
		contentPane.add(txtPorta, gbc_txtPorta);
		txtPorta.setColumns(10);
		
		lblUsurio = new JLabel("Usu\u00E1rio");
		GridBagConstraints gbc_lblUsurio = new GridBagConstraints();
		gbc_lblUsurio.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsurio.gridx = 6;
		gbc_lblUsurio.gridy = 1;
		contentPane.add(lblUsurio, gbc_lblUsurio);
		
		txtUsuario = new JTextField();
		GridBagConstraints gbc_txtUsuario = new GridBagConstraints();
		gbc_txtUsuario.gridwidth = 2;
		gbc_txtUsuario.insets = new Insets(0, 0, 5, 0);
		gbc_txtUsuario.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtUsuario.gridx = 7;
		gbc_txtUsuario.gridy = 1;
		contentPane.add(txtUsuario, gbc_txtUsuario);
		txtUsuario.setColumns(10);
		
		btnConectar = new JButton("Conectar");
		GridBagConstraints gbc_btnConectar = new GridBagConstraints();
		gbc_btnConectar.gridwidth = 3;
		gbc_btnConectar.insets = new Insets(0, 0, 5, 5);
		gbc_btnConectar.gridx = 4;
		gbc_btnConectar.gridy = 2;
		contentPane.add(btnConectar, gbc_btnConectar);
		
		btnDesconectar = new JButton("Desconectar");
		GridBagConstraints gbc_btnDesconectar = new GridBagConstraints();
		gbc_btnDesconectar.insets = new Insets(0, 0, 5, 0);
		gbc_btnDesconectar.gridx = 8;
		gbc_btnDesconectar.gridy = 2;
		contentPane.add(btnDesconectar, gbc_btnDesconectar);
		
		lblNomeArquivo = new JLabel("Nome Arquivo");
		GridBagConstraints gbc_lblNomeArquivo = new GridBagConstraints();
		gbc_lblNomeArquivo.anchor = GridBagConstraints.WEST;
		gbc_lblNomeArquivo.gridwidth = 4;
		gbc_lblNomeArquivo.insets = new Insets(0, 0, 5, 5);
		gbc_lblNomeArquivo.gridx = 1;
		gbc_lblNomeArquivo.gridy = 4;
		contentPane.add(lblNomeArquivo, gbc_lblNomeArquivo);
		
		txtNomeArquivo = new JTextField();
		GridBagConstraints gbc_txtNomeArquivo = new GridBagConstraints();
		gbc_txtNomeArquivo.gridwidth = 7;
		gbc_txtNomeArquivo.insets = new Insets(0, 0, 0, 5);
		gbc_txtNomeArquivo.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNomeArquivo.gridx = 1;
		gbc_txtNomeArquivo.gridy = 5;
		contentPane.add(txtNomeArquivo, gbc_txtNomeArquivo);
		txtNomeArquivo.setColumns(10);
		
		btnPesquisar = new JButton("Pesquisar");
		btnPesquisar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					buscarArquivo(txtNomeArquivo.getText());
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnPesquisar = new GridBagConstraints();
		gbc_btnPesquisar.gridx = 8;
		gbc_btnPesquisar.gridy = 5;
		contentPane.add(btnPesquisar, gbc_btnPesquisar);
	}

	@Override
	public void RegistrarNovoCliente(Cliente c) throws RemoteException {
			String ip = c.getIp();
			mapaConectados.put(ip, c);
	}

	@Override
	public void informarListaArquivo(Cliente c, List<ArquivoDownload> lista) throws RemoteException {

		mapaArquivos.put(c, lista);
		
	}

	@Override
	public Map<Cliente, List<ArquivoDownload>> buscarArquivo(String nome) throws RemoteException {
		
		System.out.println("Lista de Arquivos");
		System.out.println("IP " + "Arquivo");
		for(int i=0; i<mapaArquivos.size(); i++){
			if(nome.equals(mapaArquivos.containsValue(listaArquivos.getNomeArquivo()))){
				System.out.println(mapaArquivos.get(cliente.getIp()) + " " + mapaArquivos.containsValue(listaArquivos.getNomeArquivo()));
			}
		}		
		return null;
	}

	@Override
	public byte[] downloadArquivo(ArquivoDownload arquivo) throws RemoteException {

		return null;
	}

	@Override
	public void desconectar(Cliente c) throws RemoteException {
		mapaConectados.remove(c);
		for(int i=0; i<mapaArquivos.size(); i++){
			if(c == mapaArquivos.keySet()){
				mapaArquivos.remove(c);
			}
		}
		
	}

}
