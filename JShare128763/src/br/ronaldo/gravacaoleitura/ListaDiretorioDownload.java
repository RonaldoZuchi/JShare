package br.ronaldo.gravacaoleitura;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.ronaldo.comundownload.ArquivoDownload;
import br.ronaldo.comundownload.DiretorioDownload;

public class ListaDiretorioDownload {
	
	public static void main(String args[]){
		
		File diretorio = new File(".\\");
		
		List<ArquivoDownload> listaArquivos = new ArrayList<>();
		List<DiretorioDownload> listaDiretorio = new ArrayList<>();
		
		for(File arquivo : diretorio.listFiles()){
			if(arquivo.isFile()){
				ArquivoDownload novo = new ArquivoDownload();
				novo.setNomeArquivo(arquivo.getName());
				novo.setTamanhoArquivo(arquivo.length());
				listaArquivos.add(novo);
			} else {
				DiretorioDownload novo = new DiretorioDownload();
				novo.setNome(arquivo.getName());
				listaDiretorio.add(novo);
			}
		}
		
		System.out.println("Lista de Diretórios");
		for(DiretorioDownload diretorioNovo : listaDiretorio){
			System.out.println("\t" + diretorioNovo.getNome());
		}
		
		System.out.println("Lista de Arquivos");
		for(ArquivoDownload arquivoNovo : listaArquivos){
			System.out.println("\t" + arquivoNovo.getNomeArquivo() + "\t" + arquivoNovo.getTamanhoArquivo());
		}
		
	}

}
