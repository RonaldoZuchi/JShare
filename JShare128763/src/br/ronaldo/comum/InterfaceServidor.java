package br.ronaldo.comum;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import br.ronaldo.comundownload.ArquivoDownload;

public interface InterfaceServidor extends Remote{
	
	public static final String NOME_SERVICO = "JShare";
	
	public void RegistrarNovoCliente(Cliente c) throws RemoteException;
	
	public void informarListaArquivo(Cliente c, List<ArquivoDownload> lista) throws RemoteException;
	
	public Map<Cliente, List<ArquivoDownload>> buscarArquivo (String nome) throws RemoteException;
	
	public byte[] downloadArquivo (ArquivoDownload arquivo) throws RemoteException;
	
	public void desconectar (Cliente c) throws RemoteException;

}
