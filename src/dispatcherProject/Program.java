package dispatcherProject;

import java.io.*;
import java.util.*;

//Değerler atandı

public class Program {

	private static final int Q1_QUANTUM = 1;
	private static final int Q2_QUANTUM = 1;
	private static final int Q3_QUANTUM = 1;
	private static final int TIMEOUT = 20;
	private static int q1 = Q1_QUANTUM;
	private static int q2 = Q2_QUANTUM;
	private static int q3 = Q3_QUANTUM;
	public static List<ProcessX> processler;
	public static List<ProcessX> hazirProcessler = new ArrayList<ProcessX>();
	public static ProcessX aktifProcess = null;
	public static Kuyruk gercekZamanliProcessler = new Kuyruk();
	public static Kuyruk kullaniciIsProcessleri1 = new Kuyruk();
	public static Kuyruk kullaniciIsProcessleri2 = new Kuyruk();
	public static Kuyruk kullaniciIsProcessleri3 = new Kuyruk();
	public static Timer timer = new Timer();
	public static int sayac = -1;
	public static boolean calismaDurumu = false;
	
	
	//Gerçek zamanlı kuyruk boş değilse, method prosesi kuyruğa alır,Başladı mı başlamadı mı kontrol eder, başlamadıysa başlatır. Proses başladıysa ve kalan süresi sıfır değilse prosesin yürütülmesine devam eder.Prosesin kalan süresi sıfır ise, yöntem işlemi sonlandırır ve onu sıradan kaldırır.

	public static void gercekZamanli(){
		aktifProcess = gercekZamanliProcessler.bas();
		if(!aktifProcess.basladiMi()) {
			calismaDurumu = true;
			hazirProcessler.add(aktifProcess);
			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--; return;
		}
		
		if(aktifProcess.kalanZaman != 0) {
			aktifProcess.devamEt(sayac); 
			aktifProcess.kalanZaman--;
			return;
		}
		else {
			aktifProcess.sonlandir(sayac);
			hazirProcessler.remove(aktifProcess);
			gercekZamanliProcessler.pop();
			aktifProcess = null;
			calismaDurumu = false;
			q1 = Q1_QUANTUM;
			q2 = Q2_QUANTUM;
			q3 = Q3_QUANTUM;
			calistir();
		}
	}

//Bu metod, üç seviyeli geri beslemeli görevlendirici işlemlerini yürütmekten sorumludur. Önce en yüksek öncelikli sıranın (sıra 1) boş olup olmadığını kontrol eder, başladı mı başlamadı mı kontrol eder, başlamadıysa başlatır. İşlem başladıysa ve kalan süresi sıfır değilse, metod kuyruğa ilişkin kuantum süresinin aşılıp aşılmadığını kontrol eder. Aşılmışsa, metod işlemin önceliğini artırır, daha düşük önceliğe sahip bir sonraki kuyruğa taşır ve bekleme süresini ayarlar. Kuantum süresi aşılmadıysa, metod işlemi yürütmeye devam eder. İşlemin kalan süresi sıfır ise, metod işlemi sonlandırır ve onu sıradan kaldırır. Ardından sıra için kuantum zamanını sıfırlar ve bir sonraki işlemi yürütmeye başlamak için calistir() metodunu çağırır.

En yüksek öncelikli sıra boşsa, metod ikinci öncelikli sırayı (kuyruk 2) benzer şekilde kontrol eder. İkinci öncelik sırası da boşsa, metod üçüncü öncelik sırasını (kuyruk 3) benzer şekilde kontrol eder. Üç kuyruk da boşsa, metod durur.
	
	public static void geriBeslemeli() {
		if (!kullaniciIsProcessleri1.isEmpty()) {
	            
	            aktifProcess = kullaniciIsProcessleri1.bas();
	            
	            if(!aktifProcess.basladiMi()) {
	    			calismaDurumu = true;
	    			hazirProcessler.add(aktifProcess);
	    			q1 = Q1_QUANTUM;
	    			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--;
	    			--q1;
	    			return;
	    		}
	    		
	    		if(aktifProcess.kalanZaman != 0) {
	    			if(q1-- == 0) {
	    				aktifProcess.oncelik++;
	    				processKuyrugaAta(kullaniciIsProcessleri1.pop());
	    				aktifProcess.bekle(sayac);
						aktifProcess.askiyaAlinmaZamani = sayac;
						aktifProcess=null;
	    				calismaDurumu = false;
	    				q1 = Q1_QUANTUM;
	    				calistir();
	    				return;
	    			}
	    			aktifProcess.devamEt(sayac); 
	    			aktifProcess.kalanZaman--;
	    			return;
	    		}
	    		else {
	    			aktifProcess.sonlandir(sayac);
	    			hazirProcessler.remove(aktifProcess);
	    			kullaniciIsProcessleri1.pop();
	    			aktifProcess = null;
	    			calismaDurumu = false;
    				q1 = Q1_QUANTUM;
    				calistir();
    				return;
	    		}
	        }
		  	else if (!kullaniciIsProcessleri2.isEmpty()) {
	            
	            aktifProcess = kullaniciIsProcessleri2.bas();
	            
	            if(!aktifProcess.basladiMi()) {
	    			calismaDurumu = true;
	    			hazirProcessler.add(aktifProcess);
    				q2 = Q2_QUANTUM;
	    			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--;
	    			q2--;
	    			return;
	    		}
	    		
	    		if(aktifProcess.kalanZaman != 0) {
	    			if(q2-- == 0) {
	    				aktifProcess.oncelik++;
	    				processKuyrugaAta(kullaniciIsProcessleri2.pop());
	    				aktifProcess.bekle(sayac);
						aktifProcess.askiyaAlinmaZamani = sayac;
						aktifProcess=null;
	    				calismaDurumu = false;
	    				q2 = Q2_QUANTUM;
	    				calistir();
	    				return;
	    			}
	    			aktifProcess.devamEt(sayac); 
	    			aktifProcess.kalanZaman--;
	    			return;
	    		}
	    		else {
	    			aktifProcess.sonlandir(sayac);
	    			hazirProcessler.remove(aktifProcess);
	    			kullaniciIsProcessleri2.pop();
	    			aktifProcess = null;
	    			calismaDurumu = false;
    				q2 = Q2_QUANTUM;
    				calistir();
    				return;
	    		}

	        }
	        else if (!kullaniciIsProcessleri3.isEmpty()) {
	            
	            
	            aktifProcess = kullaniciIsProcessleri3.bas();
	            
	            if(!aktifProcess.basladiMi()) {
	    			calismaDurumu = true;
	    			hazirProcessler.add(aktifProcess);
    				q3 = Q3_QUANTUM;
	    			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--;
	    			q3--;
	    			return;
	    		}
	    		
	    		if(aktifProcess.kalanZaman != 0) {
	    			if(q3-- == 0) {
	    				processKuyrugaAta(kullaniciIsProcessleri3.pop());
	    				aktifProcess.bekle(sayac);
						aktifProcess.askiyaAlinmaZamani = sayac;
						aktifProcess=null;
	    				calismaDurumu = false;
	    				q3 = Q3_QUANTUM;
	    				calistir();
	    				return;
	    			}
	    			aktifProcess.devamEt(sayac); 
	    			aktifProcess.kalanZaman--;
	    			return;
	    		}
	    		else {
	    			aktifProcess.sonlandir(sayac);
	    			hazirProcessler.remove(aktifProcess);
	    			aktifProcess = null;
	    			kullaniciIsProcessleri3.pop();
	    			calismaDurumu = false;
    				q3 = Q3_QUANTUM;
    				calistir();
    				return;
	    		}
			}
	       

	}
	
	public static void processKuyrugaAta(ProcessX processX) {
		switch(processX.oncelik){
		case 0:
			gercekZamanliProcessler.push(processX);
			break;
		case 1:
			kullaniciIsProcessleri1.push(processX);
			break;
		case 2:
			kullaniciIsProcessleri2.push(processX);
			break;
		case 3:
			kullaniciIsProcessleri3.push(processX);
			break;
		}
	}

	
	public static void test() {
			sayac++;
		//System.out.println(sayac);
		for(int i = 0; i< processler.size();i++) {
			ProcessX p = processler.get(i);
			if(p.gelisZamani == sayac) {
				
				processKuyrugaAta(p);
				if(aktifProcess != null && p.oncelik < aktifProcess.oncelik) {
					//aktifProcess.kalanZaman--;
					if(aktifProcess.kalanZaman==0 ) {
						aktifProcess.sonlandir(sayac);
		    			hazirProcessler.remove(aktifProcess);
		    			switch(aktifProcess.oncelik) {
						case 1:
							kullaniciIsProcessleri1.pop();
							break;
						case 2:
							kullaniciIsProcessleri2.pop();
							break;
						case 3:
							kullaniciIsProcessleri3.pop();
							break;
						}
		    			aktifProcess = null;
		    			calismaDurumu = false;
					}
					else {
					switch(aktifProcess.oncelik) {
					case 1:
						aktifProcess.oncelik++;
						kullaniciIsProcessleri1.pop();
						break;
					case 2:
						aktifProcess.oncelik++;
						kullaniciIsProcessleri2.pop();
						break;
					case 3:
						kullaniciIsProcessleri3.pop();
						break;
					}
					aktifProcess.bekle(sayac);
					processKuyrugaAta(aktifProcess);
					aktifProcess.askiyaAlinmaZamani = sayac;
					aktifProcess=null;
					calismaDurumu = false;
					}
				}
			}
		}

		//gercekZamanliProcessler.Yazdir();
		
		//if(!calismaDurumu)
			calistir();

			for(int i = 0; i < hazirProcessler.size(); i++) {
				ProcessX p = hazirProcessler.get(i);
				if(sayac - p.askiyaAlinmaZamani == TIMEOUT) {
					hazirProcessler.remove(p);
					switch(p.oncelik) {
					case 0:
						gercekZamanliProcessler.remove(p);
						break;
					case 1:
						kullaniciIsProcessleri1.remove(p);
						break;
					case 2:
						kullaniciIsProcessleri2.remove(p);
						break;
					case 3:
						kullaniciIsProcessleri3.remove(p);
						break;
					}
					p.timeout(sayac);
				}
			}
		
	}
	
	public static void calistir(){
		if(!gercekZamanliProcessler.isEmpty()) {
			gercekZamanli();
			return;
		}
		if(!kullaniciIsProcessleri1.isEmpty() || !kullaniciIsProcessleri2.isEmpty() || !kullaniciIsProcessleri3.isEmpty() ) {
			geriBeslemeli();
			return;
		}
		if(processler.get(processler.size()-1).gelisZamani > sayac) {
			Formatter formatter = new Formatter();
	    	String s = formatter.format("%d.0000 sn", sayac).toString();
	    	formatter.close();
	    	formatter = new Formatter();
	    	String s2 = formatter.format("%-10s sistem bo�ta", s).toString();
			System.out.format("\u001B[31m" + "%-30s\n", s2);
			formatter.close();
			return;
		}
		else
			System.exit(1);
	}
	
	public static void main(String[] args) {
		
		processler = ProcessListReader.readProcessList("./src/dispatcherProject/giri�.txt");
		
		timer.scheduleAtFixedRate( new TimerTask() {
		    public void run() {
		        test();}}, 0, 100);
		
		
		
	}

}
