package br.ronaldo.gravacaoleitura;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LerGravarArquivo {
	
	public LerGravarArquivo(File arquivo){
		
		byte[] dados = ler(arquivo);
		
		gravar(new File("Cópia de " + arquivo.getName()), dados);
		
	}
	
	public byte[] ler(File arquivoNovo){
		Path path = Paths.get(arquivoNovo.getPath());
		try{
			byte[] dados = Files.readAllBytes(path);
			return dados;
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	public void gravar(File arquivoNovo, byte[] dados){
		try{
			Files.write(Paths.get(arquivoNovo.getPath()), dados, StandardOpenOption.CREATE);
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
}
