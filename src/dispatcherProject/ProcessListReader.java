package dispatcherProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
//Listeden okuma iþlemi yapmak için oluþturuldu.
public class ProcessListReader {
//Girdi dosyasýndan iþlem listesini okur ve ProcessX nesnelerinin bir listesini döndürür
    public static List<ProcessX> readProcessList(String giris) {
        List<List<String>> stringList = new ArrayList<List<String>>();
//Dosyadan satýr satýr okunup boþluk kontrolü yapýlýp virgülle ayýrýrýz ve ortaya çýkan listeyi saklarýz. 
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
 //Karþýlaþtýrýlan prosesleri idlerine göre sýralar.       
        stringList.sort(new Comparator<List<String>>() {

			@Override
			public int compare(List<String> o1, List<String> o2) {
				
				return Integer.parseInt(o1.get(0).strip()) - Integer.parseInt(o2.get(0).strip());
			}
        	
        });
//Proses listesi oluþturuldu.        
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