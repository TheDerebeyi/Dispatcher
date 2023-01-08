package dispatcherProject;

import java.io.*;
import java.util.*;


public class Program {

	private static final int Q1_QUANTUM = 1;
	private static final int Q2_QUANTUM = 1;	//�� seviyeli geri beselemli g�revlendirici zaman kuantumlar�
	private static final int Q3_QUANTUM = 1;	
	private static final int TIMEOUT = 20;		//Proses timeout i�in ge�mesi gereken s�re
	private static int q1 = Q1_QUANTUM;
	private static int q2 = Q2_QUANTUM;
	private static int q3 = Q3_QUANTUM;
	public static List<ProcessX> processler;	//Sim�lasyonda kullan�lacak t�m proseslerin listesi
	public static List<ProcessX> hazirProcessler = new ArrayList<ProcessX>(); //timeout kontrol� i�in kulland���m�z haz�r durumdaki t�m proseslerin listesi
	public static ProcessX aktifProcess = null;	//Anl�k olarak �al��an proses
	public static Kuyruk gercekZamanliProcessler = new Kuyruk(); //Ger�ek zamanl� prosesler kuyru�u, �ncelik 0
	public static Kuyruk kullaniciIsProcessleri1 = new Kuyruk(); //Kullan�c� i� proseslerinin kuyruklar�, �ncelik 1
	public static Kuyruk kullaniciIsProcessleri2 = new Kuyruk(); //�ncelik 2
	public static Kuyruk kullaniciIsProcessleri3 = new Kuyruk(); //�ncelik 3
	public static Timer timer = new Timer();
	public static int sayac = -1;	//Sistem zaman�n� sim�le eden saya�
	public static boolean calismaDurumu = false;
	
	//Ger�ek zamanl� kuyruk bo� de�ilse, method prosesi kuyru�a al�r. Ba�lad� m� ba�lamad� m� kontrol eder, 
	//ba�lamad�ysa ba�lat�r. Proses ba�lad�ysa ve kalan s�resi s�f�r de�ilse prosesin y�r�t�lmesine devam eder.
	//Prosesin kalan s�resi s�f�r ise, metod i�lemi sonland�r�r ve onu s�radan kald�r�r.
	public static void gercekZamanli(){
		aktifProcess = gercekZamanliProcessler.bas(); //listenin ba��ndaki proses �al���r
		if(!aktifProcess.basladiMi()) { //prosesin t an�nda y�r�tmesine hi� ba�lan�p ba�lanmad���
			calismaDurumu = true;

			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--; return;
		}
		
		if(aktifProcess.kalanZaman != 0) { //t an�nda prosesin hala yapacak i�i var
			aktifProcess.devamEt(sayac); 
			aktifProcess.kalanZaman--;
			return;
		}
		else {								//t an�nda proses i�levini tamamlam�� ve sonland�r�lacak
			aktifProcess.sonlandir(sayac);
			hazirProcessler.remove(aktifProcess);
			gercekZamanliProcessler.pop();
			aktifProcess = null;
			calismaDurumu = false;
			q1 = Q1_QUANTUM;		//ger�ek zamanl� bir proses bittikten sonra kullan�c� i� proseslerine ge�i� yap�lacak olabilir
			q2 = Q2_QUANTUM;		//geri beslemeli g�revlendirici i�in de�i�kenler set edilir
			q3 = Q3_QUANTUM;
			calistir();				//biti� noktas�nda bir sonraki prosese ge�i� yap�l�r
		}
	}
	//Bu metod, �� seviyeli geri beslemeli g�revlendirici i�lemlerini y�r�tmekten sorumludur. 
	//�nce en y�ksek �ncelikli s�ran�n (s�ra 1) bo� olup olmad���n� kontrol eder, ba�lad� m� ba�lamad� m�
	//kontrol eder, ba�lamad�ysa ba�lat�r. ��lem ba�lad�ysa ve kalan s�resi s�f�r de�ilse, metod kuyru�a ili�kin
	//kuantum s�resinin a��l�p a��lmad���n� kontrol eder. A��lm��sa, metod i�lemin �nceli�ini art�r�r, 
	//daha d���k �nceli�e sahip bir sonraki kuyru�a ta��r ve bekleme s�resini ayarlar. 
	//Kuantum s�resi a��lmad�ysa, metod i�lemi y�r�tmeye devam eder. ��lemin kalan s�resi s�f�r ise, 
	//metod i�lemi sonland�r�r ve onu s�radan kald�r�r. Ard�ndan s�ra i�in kuantum zaman�n� s�f�rlar 
	//ve bir sonraki i�lemi y�r�tmeye ba�lamak i�in calistir() metodunu �a��r�r.

	//En y�ksek �ncelikli s�ra bo�sa, metod ikinci �ncelikli s�ray� (kuyruk 2) benzer �ekilde kontrol eder. 
	//�kinci �ncelik s�ras� da bo�sa, metod ���nc� �ncelik s�ras�n� (kuyruk 3) benzer �ekilde kontrol eder. 
	//�� kuyruk da bo�sa, metod durur.	
	public static void geriBeslemeli() {
		if (!kullaniciIsProcessleri1.isEmpty()) {//geri beslemeli sistemde ilk kontrol ilk kuyru�un bo� olup olmad���
	            									//e�er daha �ncelikli bir kuyruk dolu ise �nce o kuyruk �al���r
	            aktifProcess = kullaniciIsProcessleri1.bas();
	            
	            if(!aktifProcess.basladiMi()) {
	    			calismaDurumu = true;
	    			
	    			q1 = Q1_QUANTUM;
	    			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--;
	    			--q1;	//her ad�m sonunda kuyruk zaman kuantumu azalt�l�r
	    			return;
	    		}
	    		
	    		if(aktifProcess.kalanZaman != 0) {
	    			if(q1-- == 0) {	//artt�r�m i�lemi kontrolden sonra ger�ekle�ecek, kuyruk zaman kuantumu 0'a e�itse proses bir alt �ncelikli kuyru�a g�nderilir
	    				aktifProcess.oncelik++;
	    				processKuyrugaAta(kullaniciIsProcessleri1.pop());
	    				aktifProcess.bekle(sayac);
						aktifProcess.askiyaAlinmaZamani = sayac;	//timeout i�in proses ask�ya al�nma zaman� g�ncellenir.
						aktifProcess=null;
	    				calismaDurumu = false;
	    				q1 = Q1_QUANTUM;		//kuyruk zaman kuantum de�eri varsay�lana set edilir
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
		  	else if (!kullaniciIsProcessleri2.isEmpty()) { //�ncelik seviyesi 2 olan kuyruk
	            
	            aktifProcess = kullaniciIsProcessleri2.bas();
	            
	            if(!aktifProcess.basladiMi()) {
	    			calismaDurumu = true;
	    			
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
	        else if (!kullaniciIsProcessleri3.isEmpty()) {//�ncelik seviyesi 3 olan kuyruk
	            
	            
	            aktifProcess = kullaniciIsProcessleri3.bas();
	            
	            if(!aktifProcess.basladiMi()) {
	    			calismaDurumu = true;
	    			
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
		switch(processX.oncelik){//parametre olarak al�nan prosesin �ncelik de�erine g�re kuyruklara g�nderilme k�sm�
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

	
	public static void test() {	//sistem zaman� sim�le i�leminde her t an�nda yap�lacak kontrol i�lemleri i�in bir fonksiyon
			sayac++;

		for(int i = 0; i< processler.size();i++) { //sim�lasyonda kullan�lacak t�m prosesler taran�r
			ProcessX p = processler.get(i);
			if(p.gelisZamani == sayac) {			//prosesin geli� zaman� gelmi� mi
				hazirProcessler.add(p);				//timeout kontrol� i�in t�m haz�r prosesler bir listeye eklenir
				processKuyrugaAta(p);				//prosesler �nceliklerine g�re kuyruklara atan�r
				if(aktifProcess != null && p.oncelik < aktifProcess.oncelik) {//e�er aktif prosesin �ncelik seviyesinden y�ksek bir �ncelik seviyeli proses gelmi�se
																		      //�al��mas� durdurulur ve bu yeni proses y�r�tl�r
					if(aktifProcess.kalanZaman==0 ) {	//aktif proses zaten sonland�r�lacak m�yd� kontrol edilir
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
						aktifProcess.oncelik++;			//aktif proses ancak bir kullan�c� i� prosesi olabilir, geri beslemeli sistemin bir alt kuyru�a g�nderme s�reci y�r�t�l�r
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


			calistir();	//y�r�tmeyi sa�layan fonksiyon

			for(int i = 0; i < hazirProcessler.size(); i++) {	//timeout kontrol�
				ProcessX p = hazirProcessler.get(i);

				if(sayac - p.askiyaAlinmaZamani == TIMEOUT) {	//TIMEOUT s�resi boyunca sonland�r�lmam�� prosesler sonland�r�l�r
					i=-1;//hazir processler listesini biz tar�yoruz ancak timeouta u�ram�� proses listeden ��kar�lacak bu y�zden ba�tan tekrar tarama yapmam�z gerekiyor. 
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
	
	public static void calistir(){	//y�r�tmeyi sa�lar
		if(!gercekZamanliProcessler.isEmpty()) {	//ger�ek zamanl� bir proses haz�rsa �nce o �al���r
			gercekZamanli();
			return;
		}
		if(!kullaniciIsProcessleri1.isEmpty() || !kullaniciIsProcessleri2.isEmpty() || !kullaniciIsProcessleri3.isEmpty() ) {
			geriBeslemeli();						//e�er kullan�c� i� proses kuyruklar� bo� de�ilse geri beslemeli sistem �al��t�r�l�r
			return;
		}
		if(processler.get(processler.size()-1).gelisZamani > sayac) {		//sim�lasyonda ba�ka proses kald� m� kontrol�
			Formatter formatter = new Formatter();							//e�er hala gelebilecek bir proses varsa sistem bo�ta bekler
	    	String s = formatter.format("%d.0000 sn", sayac).toString();
	    	formatter.close();
	    	formatter = new Formatter();
	    	String s2 = formatter.format("%-10s sistem bo�ta", s).toString();
			System.out.format("\u001B[31m" + "%-30s\n", s2);
			formatter.close();
			return;
		}
		else							//e�er sim�lasyondaki t�m prosesler bitmi�se program sonland�r�l�r.
			System.exit(1);
	}
	
	public static void main(String[] args) {
		
		processler = ProcessListReader.readProcessList("./giris.txt");		//Dosyadan prosesleri alma i�lemi
		
		timer.scheduleAtFixedRate( new TimerTask() {				//Sistem zaman� bir timer ile sim�le edilir
		    public void run() {										//1000 ms aral�klarla artt�r�lan bir saya� de�i�keni �zerinden sistem zaman� tutulur
		        test();}}, 0, 1000);								
		
		
		
	}

}
