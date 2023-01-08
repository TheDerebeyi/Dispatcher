package dispatcherProject;

public class Kuyruk {
	private class Dugum {		//Kuyruk yap�s� d���mlerden olu�ur
		public ProcessX veri;	//D���m�n tuttu�u proses
		public Dugum sonraki;	//Sonraki d���m
		Dugum(ProcessX processX){
			veri = processX;
			sonraki = null;
		}
	}
	private int genislik;	//kuyruk geni�li�i
	private Dugum bas;		//kuyruk ba��
	private Dugum son;		//kuyruk sonu
	
	Kuyruk(){
		bas = null;
		genislik = 0;
	}
	
	public ProcessX bas() {	//ba� d���m� d�nd�r�r
		
		return (bas.veri != null)?bas.veri:null ;
	}
	
	public ProcessX pop() {		//ba� d���m� kuyruktan ��kar�r
		
		if(genislik == 0) {
			return null;
		}
		
		Dugum cache = bas;
		
		bas = bas.sonraki;
		if(genislik > 0) {genislik--;}
		return cache.veri;
	}
	
	public void push(ProcessX processX) {	//kuyru�a yeni d���m ekler.
		if(genislik == 0) {
			bas = new Dugum(processX);
			son = bas;
			genislik++;
			return;
		}
		
		son.sonraki = new Dugum(processX);
		Dugum cache = son.sonraki;
		son = cache;
		genislik++;
	}
	
	public void Yazdir() {	//kuyru�u yazd�r�r
		if(genislik==0) return;
		Dugum cache = bas;
		for(int i = 0; i<genislik;i++) {
			cache.veri.Yazdir();
			cache = cache.sonraki;
			}
	}
	
	public boolean isEmpty() {	//kuyruk bo� mu kontrol edilir
		if(genislik == 0) {return true;}
		return false;
	}

	public boolean remove(ProcessX p) {	//kuyruktan belirli bir p prosesini ��kar�r
		if(bas == null) return false;
		if(bas.veri == p) {
			bas = bas.sonraki;
			genislik--;
			return true;
		}
		Dugum cache = bas;
		Dugum cache2 = bas.sonraki;
		for(int i = 0;i < genislik-1;i++) {
			if(cache2.veri == p) {
				cache.sonraki = cache2.sonraki;
				cache2.sonraki = null;
				genislik--;
				return true;
			}
			Dugum cache3 = cache2;
			
			cache2 = cache2.sonraki;
			cache = cache3;
		}
		return false;
	}
	
}
