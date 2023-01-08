package dispatcherProject;

import java.io.*;
import java.util.*;


public class Program {

	private static final int Q1_QUANTUM = 1;
	private static final int Q2_QUANTUM = 1;	//Üç seviyeli geri beselemli görevlendirici zaman kuantumlarý
	private static final int Q3_QUANTUM = 1;	
	private static final int TIMEOUT = 20;		//Proses timeout için geçmesi gereken süre
	private static int q1 = Q1_QUANTUM;
	private static int q2 = Q2_QUANTUM;
	private static int q3 = Q3_QUANTUM;
	public static List<ProcessX> processler;	//Simülasyonda kullanýlacak tüm proseslerin listesi
	public static List<ProcessX> hazirProcessler = new ArrayList<ProcessX>(); //timeout kontrolü için kullandýðýmýz hazýr durumdaki tüm proseslerin listesi
	public static ProcessX aktifProcess = null;	//Anlýk olarak çalýþan proses
	public static Kuyruk gercekZamanliProcessler = new Kuyruk(); //Gerçek zamanlý prosesler kuyruðu, öncelik 0
	public static Kuyruk kullaniciIsProcessleri1 = new Kuyruk(); //Kullanýcý iþ proseslerinin kuyruklarý, öncelik 1
	public static Kuyruk kullaniciIsProcessleri2 = new Kuyruk(); //öncelik 2
	public static Kuyruk kullaniciIsProcessleri3 = new Kuyruk(); //öncelik 3
	public static Timer timer = new Timer();
	public static int sayac = -1;	//Sistem zamanýný simüle eden sayaç
	public static boolean calismaDurumu = false;
	
	//Gerçek zamanlý kuyruk boþ deðilse, method prosesi kuyruða alýr. Baþladý mý baþlamadý mý kontrol eder, 
	//baþlamadýysa baþlatýr. Proses baþladýysa ve kalan süresi sýfýr deðilse prosesin yürütülmesine devam eder.
	//Prosesin kalan süresi sýfýr ise, metod iþlemi sonlandýrýr ve onu sýradan kaldýrýr.
	public static void gercekZamanli(){
		aktifProcess = gercekZamanliProcessler.bas(); //listenin baþýndaki proses çalýþýr
		if(!aktifProcess.basladiMi()) { //prosesin t anýnda yürütmesine hiç baþlanýp baþlanmadýðý
			calismaDurumu = true;

			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--; return;
		}
		
		if(aktifProcess.kalanZaman != 0) { //t anýnda prosesin hala yapacak iþi var
			aktifProcess.devamEt(sayac); 
			aktifProcess.kalanZaman--;
			return;
		}
		else {								//t anýnda proses iþlevini tamamlamýþ ve sonlandýrýlacak
			aktifProcess.sonlandir(sayac);
			hazirProcessler.remove(aktifProcess);
			gercekZamanliProcessler.pop();
			aktifProcess = null;
			calismaDurumu = false;
			q1 = Q1_QUANTUM;		//gerçek zamanlý bir proses bittikten sonra kullanýcý iþ proseslerine geçiþ yapýlacak olabilir
			q2 = Q2_QUANTUM;		//geri beslemeli görevlendirici için deðiþkenler set edilir
			q3 = Q3_QUANTUM;
			calistir();				//bitiþ noktasýnda bir sonraki prosese geçiþ yapýlýr
		}
	}
	//Bu metod, üç seviyeli geri beslemeli görevlendirici iþlemlerini yürütmekten sorumludur. 
	//Önce en yüksek öncelikli sýranýn (sýra 1) boþ olup olmadýðýný kontrol eder, baþladý mý baþlamadý mý
	//kontrol eder, baþlamadýysa baþlatýr. Ýþlem baþladýysa ve kalan süresi sýfýr deðilse, metod kuyruða iliþkin
	//kuantum süresinin aþýlýp aþýlmadýðýný kontrol eder. Aþýlmýþsa, metod iþlemin önceliðini artýrýr, 
	//daha düþük önceliðe sahip bir sonraki kuyruða taþýr ve bekleme süresini ayarlar. 
	//Kuantum süresi aþýlmadýysa, metod iþlemi yürütmeye devam eder. Ýþlemin kalan süresi sýfýr ise, 
	//metod iþlemi sonlandýrýr ve onu sýradan kaldýrýr. Ardýndan sýra için kuantum zamanýný sýfýrlar 
	//ve bir sonraki iþlemi yürütmeye baþlamak için calistir() metodunu çaðýrýr.

	//En yüksek öncelikli sýra boþsa, metod ikinci öncelikli sýrayý (kuyruk 2) benzer þekilde kontrol eder. 
	//Ýkinci öncelik sýrasý da boþsa, metod üçüncü öncelik sýrasýný (kuyruk 3) benzer þekilde kontrol eder. 
	//Üç kuyruk da boþsa, metod durur.	
	public static void geriBeslemeli() {
		if (!kullaniciIsProcessleri1.isEmpty()) {//geri beslemeli sistemde ilk kontrol ilk kuyruðun boþ olup olmadýðý
	            									//eðer daha öncelikli bir kuyruk dolu ise önce o kuyruk çalýþýr
	            aktifProcess = kullaniciIsProcessleri1.bas();
	            
	            if(!aktifProcess.basladiMi()) {
	    			calismaDurumu = true;
	    			
	    			q1 = Q1_QUANTUM;
	    			aktifProcess.baslat(sayac); aktifProcess.kalanZaman--;
	    			--q1;	//her adým sonunda kuyruk zaman kuantumu azaltýlýr
	    			return;
	    		}
	    		
	    		if(aktifProcess.kalanZaman != 0) {
	    			if(q1-- == 0) {	//arttýrým iþlemi kontrolden sonra gerçekleþecek, kuyruk zaman kuantumu 0'a eþitse proses bir alt öncelikli kuyruða gönderilir
	    				aktifProcess.oncelik++;
	    				processKuyrugaAta(kullaniciIsProcessleri1.pop());
	    				aktifProcess.bekle(sayac);
						aktifProcess.askiyaAlinmaZamani = sayac;	//timeout için proses askýya alýnma zamaný güncellenir.
						aktifProcess=null;
	    				calismaDurumu = false;
	    				q1 = Q1_QUANTUM;		//kuyruk zaman kuantum deðeri varsayýlana set edilir
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
		  	else if (!kullaniciIsProcessleri2.isEmpty()) { //öncelik seviyesi 2 olan kuyruk
	            
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
	        else if (!kullaniciIsProcessleri3.isEmpty()) {//öncelik seviyesi 3 olan kuyruk
	            
	            
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
		switch(processX.oncelik){//parametre olarak alýnan prosesin öncelik deðerine göre kuyruklara gönderilme kýsmý
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

	
	public static void test() {	//sistem zamaný simüle iþleminde her t anýnda yapýlacak kontrol iþlemleri için bir fonksiyon
			sayac++;

		for(int i = 0; i< processler.size();i++) { //simülasyonda kullanýlacak tüm prosesler taranýr
			ProcessX p = processler.get(i);
			if(p.gelisZamani == sayac) {			//prosesin geliþ zamaný gelmiþ mi
				hazirProcessler.add(p);				//timeout kontrolü için tüm hazýr prosesler bir listeye eklenir
				processKuyrugaAta(p);				//prosesler önceliklerine göre kuyruklara atanýr
				if(aktifProcess != null && p.oncelik < aktifProcess.oncelik) {//eðer aktif prosesin öncelik seviyesinden yüksek bir öncelik seviyeli proses gelmiþse
																		      //çalýþmasý durdurulur ve bu yeni proses yürütlür
					if(aktifProcess.kalanZaman==0 ) {	//aktif proses zaten sonlandýrýlacak mýydý kontrol edilir
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
						aktifProcess.oncelik++;			//aktif proses ancak bir kullanýcý iþ prosesi olabilir, geri beslemeli sistemin bir alt kuyruða gönderme süreci yürütülür
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


			calistir();	//yürütmeyi saðlayan fonksiyon

			for(int i = 0; i < hazirProcessler.size(); i++) {	//timeout kontrolü
				ProcessX p = hazirProcessler.get(i);

				if(sayac - p.askiyaAlinmaZamani == TIMEOUT) {	//TIMEOUT süresi boyunca sonlandýrýlmamýþ prosesler sonlandýrýlýr
					i=-1;//hazir processler listesini biz tarýyoruz ancak timeouta uðramýþ proses listeden çýkarýlacak bu yüzden baþtan tekrar tarama yapmamýz gerekiyor. 
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
	
	public static void calistir(){	//yürütmeyi saðlar
		if(!gercekZamanliProcessler.isEmpty()) {	//gerçek zamanlý bir proses hazýrsa önce o çalýþýr
			gercekZamanli();
			return;
		}
		if(!kullaniciIsProcessleri1.isEmpty() || !kullaniciIsProcessleri2.isEmpty() || !kullaniciIsProcessleri3.isEmpty() ) {
			geriBeslemeli();						//eðer kullanýcý iþ proses kuyruklarý boþ deðilse geri beslemeli sistem çalýþtýrýlýr
			return;
		}
		if(processler.get(processler.size()-1).gelisZamani > sayac) {		//simülasyonda baþka proses kaldý mý kontrolü
			Formatter formatter = new Formatter();							//eðer hala gelebilecek bir proses varsa sistem boþta bekler
	    	String s = formatter.format("%d.0000 sn", sayac).toString();
	    	formatter.close();
	    	formatter = new Formatter();
	    	String s2 = formatter.format("%-10s sistem boþta", s).toString();
			System.out.format("\u001B[31m" + "%-30s\n", s2);
			formatter.close();
			return;
		}
		else							//eðer simülasyondaki tüm prosesler bitmiþse program sonlandýrýlýr.
			System.exit(1);
	}
	
	public static void main(String[] args) {
		
		processler = ProcessListReader.readProcessList("./giris.txt");		//Dosyadan prosesleri alma iþlemi
		
		timer.scheduleAtFixedRate( new TimerTask() {				//Sistem zamaný bir timer ile simüle edilir
		    public void run() {										//1000 ms aralýklarla arttýrýlan bir sayaç deðiþkeni üzerinden sistem zamaný tutulur
		        test();}}, 0, 1000);								
		
		
		
	}

}
