package br.ronaldo.comundownload;

import java.io.Serializable;

public class ArquivoDownload implements Serializable{

	private static final long serialVersionUID = -455063189151041498L;
	
	private String nomeArquivo;
	private long tamanhoArquivo;
	
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	public long getTamanhoArquivo() {
		return tamanhoArquivo;
	}
	public void setTamanhoArquivo(long tamanhoArquivo) {
		this.tamanhoArquivo = tamanhoArquivo;
	}
	
	@Override
	public String toString() {
		return nomeArquivo + " - " + tamanhoArquivo;
	}

}
