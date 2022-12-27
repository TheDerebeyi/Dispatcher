package dispatcherProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
//Listeden okuma i�lemi yapmak i�in olu�turuldu.
public class ProcessListReader {
//Girdi dosyas�ndan i�lem listesini okur ve ProcessX nesnelerinin bir listesini d�nd�r�r
    public static List<ProcessX> readProcessList(String giris) {
        List<List<String>> stringList = new ArrayList<List<String>>();
//Dosyadan sat�r sat�r okunup bo�luk kontrol� yap�l�p virg�lle ay�r�r�z ve ortaya ��kan listeyi saklar�z. 
        try (BufferedReader br = new BufferedReader(new FileReader(giris))) {
            String line;
            while ((line = br.readLine()) != null) {
            	if(line.isBlank()) {
            		continue;
            	}
                String[] values = line.split(",");
                stringList.add(Arrays.asList(values));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
 //Kar��la�t�r�lan prosesleri idlerine g�re s�ralar.       
        stringList.sort(new Comparator<List<String>>() {

			@Override
			public int compare(List<String> o1, List<String> o2) {
				
				return Integer.parseInt(o1.get(0).strip()) - Integer.parseInt(o2.get(0).strip());
			}
        	
        });
//Proses listesi olu�turuldu.        
        List<ProcessX> processList = new ArrayList<>();
        for(var string : stringList) {
            ProcessX processX = new ProcessX(
                    Integer.parseInt(string.get(0).strip()),
                    Integer.parseInt(string.get(1).strip()),
                    Integer.parseInt(string.get(2).strip())
                ); 
            processList.add(processX);
        }
        
        return processList;
    }
}