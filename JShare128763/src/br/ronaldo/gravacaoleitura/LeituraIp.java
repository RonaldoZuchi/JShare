package br.ronaldo.gravacaoleitura;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LeituraIp {
	
	public LeituraIp(){
		
		InetAddress IP;
		
		try{
			 
			IP = InetAddress.getLocalHost();
			String ip = IP.getHostAddress();
			System.out.println("Meu Ip é : " + IP.getHostAddress());
			
		}catch(UnknownHostException e){
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]){
		new LeituraIp();
	}
	
}
