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
//Proseslerin konsol uygulamasýnda kullanýcaklarý renkler	
	List<String> colors;
	

//Proses özellikleri
	public int gelisZamani;
	public int oncelik;
	public int zaman;
	public int kalanZaman;
	public int id;
	public static int idCounter;//idCounter static olarak tutulan kaç tane prosesin halihazýrda oluþturulduðunu bize gösteren deðiþken
	public int renk; //0-6
	public int askiyaAlinmaZamani;
	ProcessBuilder pb;
//Process constructor	
	ProcessX(int _gelisZamani, int _oncelik, int _zaman){
		id = idCounter++;	//idCounter deðeri þu ana kadar oluþturulmuþ proseslerin sayýsýný bize verecek
		renk = id % 7;		//id mod 7 iþlemi ile proses rengi belirlenir
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
	
//Prosesin baþlayýp baþlamadýðýný kontrol eder.	
	public boolean basladiMi(){
		if(zaman == kalanZaman) {
			return false;
		}
		return true;
	}
	//Process görevlendirici tarafýndan sonlandýrýlmazsa, proses 20 saniye sonra kendiliðinden sona erecektir.
	public void timeout(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses zamanaþýmý", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan süre:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//prosesi askýya alýr
	public void bekle(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses askýda", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan süre:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//prosesi sonlandýrýr
	public void sonlandir(int sayac) {
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses sonlandý", s).toString();
		System.out.format(colors.get(renk) +  "%-30s(id:%04d  oncelik:%d  kalan süre:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//Gerekli deðiþkenler ile yeni bir proses oluþturur.
	public void baslat(int sayac) {
		//Process.jar yürütme iþlemini gerçekleþtiren bizim oluþturduðumuz bir harici program.
		//aldýðý parametreleri kullanarak ...yürütülüyor... kýsmýný yazdýrmakla görevli.
		
		//arg1: saniye, arg2 id, arg3 oncelik, arg4 kalanSure
		pb = new ProcessBuilder("java", "-jar", "./Process.jar", Integer.toString(sayac), Integer.toString(id), Integer.toString(oncelik), Integer.toString(zaman));
		Formatter formatter = new Formatter();
    	String s = formatter.format("%d.0000 sn", sayac).toString();
    	formatter.close();
    	formatter = new Formatter();
    	String s2 = formatter.format("%-10s proses baþladý", s).toString();
		System.out.format(colors.get(renk) + "%-30s(id:%04d  oncelik:%d  kalan süre:%d sn)\n", s2, id,oncelik,kalanZaman);
		formatter.close();
	}
	//askýda olan prosesin devam etmesini saðlar
	public void devamEt(int sayac){
		ExecutorService pool = Executors.newSingleThreadExecutor();

	        try {
	        	//saniye ve kalan süre deðerleri güncellenir
	        	pb.command().set(3, Integer.toString(sayac));
	        	pb.command().set(6, Integer.toString(kalanZaman));
				
	        	Process proc = pb.start(); //Proses ...yürütülüyor... kýsmýný bize oluþturacak
	        	
	        	//Prosesin döndürdüðü deðerin alýnýp yazdýrýldýðý kýsým. 
	            ProcessReadTask task = new ProcessReadTask(proc.getInputStream());
	            Future<List<String>> future = pool.submit(task);  //prosesten okuma iþlemi için ayrý bir thread kullanýldý

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
    
    public void Yazdir() {	//Prosesin özelliklerini yazdýrýr.
		Formatter formatter = new Formatter();
    	formatter.close();
    	formatter = new Formatter();
		System.out.format(colors.get(renk) + "(id:%04d  oncelik:%d  kalan süre:%d sn  askýya alýnma zamaný:%d sn)\n", id,oncelik,kalanZaman, askiyaAlinmaZamani);
		formatter.close();
    }

}
