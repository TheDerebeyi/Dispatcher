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
//Renk listesi olu�turuldu	
	List<String> colors;
	

//Proses �zellikleri tan�mland�	
	public int gelisZamani;
	public int oncelik;
	public int zaman;
	public int kalanZaman;
	public int id;
	public static int idCounter;
	public int renk; //0-6
	public int askiyaAlinmaZamani;
	ProcessBuilder pb;
//Proses constructur� olu�turuldu.
	ProcessX(int _gelisZamani, int _oncelik, int _zaman){
		id = idCounter++;
		renk = id % 7;
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
	
//Prosesin ba�lay�p ba�lamad���n� kontrol eder	
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
	//Prosesi ask�ya al�r.
	public void bekle(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses ask�da", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan s�re:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//Prosesi sonland�r�r
	public void sonlandir(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses sonland�", s).toString();
		System.out.format(colors.get(renk) +  "%-30s(id:%04d  oncelik:%d  kalan s�re:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//Gerekli de�i�kenler ile yeni bir proses builder olu�turulur.
	public void baslat(int sayac) {
		pb = new ProcessBuilder("java", "-jar", "./Program.jar", Integer.toString(sayac), Integer.toString(id), Integer.toString(oncelik), Integer.toString(zaman));
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses ba�lad�", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan s�re:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//Ask�da olan prosesin devam etmesini sa�lar
	public void devamEt(int sayac){
		ExecutorService pool = Executors.newSingleThreadExecutor();

	        try {
	    		//arg1: saniye, arg2 id, arg3 oncelik, arg4 kalanSure
	        	
	        	pb.command().set(3, Integer.toString(sayac));
	        	pb.command().set(6, Integer.toString(kalanZaman));
				//ProcessBuilder.start()
	        	Process proc = pb.start();
	        	
	            ProcessReadTask task = new ProcessReadTask(proc.getInputStream());
	            Future<List<String>> future = pool.submit(task);

	            List<String> result = future.get(100, TimeUnit.SECONDS);
	            
	            for (String s : result) {
	                System.out.println(colors.get(renk) + s.trim());
	            }
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
            pool.shutdown();
        }
		/*Program.timer.schedule( new TimerTask() {
					    public void run() {
					    	while(true) {System.out.print("�al��t�m.");}}}, 0);*/
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

}
