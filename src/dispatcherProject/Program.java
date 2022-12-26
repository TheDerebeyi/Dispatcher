package dispatcherProject;

import java.io.*;
import java.util.*;


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
	
	
	public static void gercekZamanli(){

	}
	
	public static void geriBeslemeli() {

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
