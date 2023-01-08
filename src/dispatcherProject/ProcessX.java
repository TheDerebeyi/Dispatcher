package dispatcherProject;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ProcessX {
	public static final String BLACK = "\u001B[30m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
//Proseslerin konsol uygulamas�nda kullan�caklar� renkler	
	List<String> colors;
	

//Proses �zellikleri
	public int gelisZamani;
	public int oncelik;
	public int zaman;
	public int kalanZaman;
	public int id;
	public static int idCounter;//idCounter static olarak tutulan ka� tane prosesin halihaz�rda olu�turuldu�unu bize g�steren de�i�ken
	public int renk; //0-6
	public int askiyaAlinmaZamani;
	ProcessBuilder pb;
//Process constructor	
	ProcessX(int _gelisZamani, int _oncelik, int _zaman){
		id = idCounter++;	//idCounter de�eri �u ana kadar olu�turulmu� proseslerin say�s�n� bize verecek
		renk = id % 7;		//id mod 7 i�lemi ile proses rengi belirlenir
		gelisZamani = _gelisZamani;
		oncelik = _oncelik;
		zaman = _zaman;	
		kalanZaman = zaman;
		askiyaAlinmaZamani = gelisZamani;

		colors = new ArrayList<String>();
		colors.add(BLUE);
		colors.add(GREEN);
		colors.add(PURPLE);
		colors.add(RED);
		colors.add(YELLOW);
		colors.add(BLACK);
		colors.add(CYAN);
		

	}
	
//Prosesin ba�lay�p ba�lamad���n� kontrol eder.	
	public boolean basladiMi(){
		if(zaman == kalanZaman) {
			return false;
		}
		return true;
	}
	//Process g�revlendirici taraf�ndan sonland�r�lmazsa, proses 20 saniye sonra kendili�inden sona erecektir.
	public void timeout(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses zamana��m�", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan s�re:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//prosesi ask�ya al�r
	public void bekle(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses ask�da", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan s�re:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//prosesi sonland�r�r
	public void sonlandir(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses sonland�", s).toString();
		System.out.format(colors.get(renk) +  "%-30s(id:%04d  oncelik:%d  kalan s�re:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//Gerekli de�i�kenler ile yeni bir proses olu�turur.
	public void baslat(int sayac) {
		//Process.jar y�r�tme i�lemini ger�ekle�tiren bizim olu�turdu�umuz bir harici program.
		//ald��� parametreleri kullanarak ...y�r�t�l�yor... k�sm�n� yazd�rmakla g�revli.
		
		//arg1: saniye, arg2 id, arg3 oncelik, arg4 kalanSure
		pb = new ProcessBuilder("java", "-jar", "./Process.jar", Integer.toString(sayac), Integer.toString(id), Integer.toString(oncelik), Integer.toString(zaman));
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses ba�lad�", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan s�re:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//ask�da olan prosesin devam etmesini sa�lar
	public void devamEt(int sayac){
		ExecutorService pool = Executors.newSingleThreadExecutor();

	        try {
	        	//saniye ve kalan s�re de�erleri g�ncellenir
	        	pb.command().set(3, Integer.toString(sayac));
	        	pb.command().set(6, Integer.toString(kalanZaman));
				
	        	Process proc = pb.start(); //Proses ...y�r�t�l�yor... k�sm�n� bize olu�turacak
	        	
	        	//Prosesin d�nd�rd��� de�erin al�n�p yazd�r�ld��� k�s�m. 
	            ProcessReadTask task = new ProcessReadTask(proc.getInputStream());
	            Future<List<String>> future = pool.submit(task);  //prosesten okuma i�lemi i�in ayr� bir thread kullan�ld�

	            List<String> result = future.get(100, TimeUnit.SECONDS);
	            
	            for (String s : result) {
	                System.out.println(colors.get(renk) + s.trim());
	            }
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
            pool.shutdown();
        }

	}
	
    private static class ProcessReadTask implements Callable<List<String>> {

        private InputStream inputStream;

        public ProcessReadTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public List<String> call() {
            return new BufferedReader(new InputStreamReader(inputStream))
				.lines()
				.collect(Collectors.toList());
        }
    }
    
    public void Yazdir() {	//Prosesin �zelliklerini yazd�r�r.
		Formatter formatter = new Formatter();
    	formatter.close();
    	formatter = new Formatter();
		System.out.format(colors.get(renk) + "(id:%04d  oncelik:%d  kalan s�re:%d sn  ask�ya al�nma zaman�:%d sn)\n", id,oncelik,kalanZaman, askiyaAlinmaZamani);
		formatter.close();
    }

}
