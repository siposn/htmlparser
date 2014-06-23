package htmlparser;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLParser {

    private final String CSV;
    private final List<File> folders;
    private CSVWriter writer;

    public HTMLParser() {
        this.CSV = "output.csv";
        this.folders = new ArrayList<>();
        try {
            this.writer = new CSVWriter(new FileWriter(this.CSV));
        } catch (IOException ex) {
            Logger.getLogger(HTMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
            HTMLParser p = new HTMLParser(); 
            p.start();
    }
    
    //Program kezdő pontja. Listában levő könyvtárak megnyitása
    public void start() {
        folders.add(new File("C:\\Users\\siposn\\Desktop\\MOB vizsgálat"));
        try {
            for(File folder : folders) {
                if(folder.exists()) {
                    openFolder(folder);
                }
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(HTMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Egy adott köynvtár rekurzív bejárása
    public void openFolder(File folder) {
       for(File entry : folder.listFiles()) {
            if(entry.isDirectory()) {
                openFolder(entry);
            } else {
                //Ha HTML file, parsolás
                createCSV(entry);
            }
        }
    }
    
    //HTML fájl parsolása
    public void createCSV(File file) {
        try {
            Document doc = Jsoup.parse(file, "UTF-8");
            Elements t = doc.getElementsByAttributeValue("size","6");
            Elements content = doc.getElementsByClass("table");
            for(Element row : content.select("tr")) {
                //Sorok celláinak kikeresése
                Elements tds = row.select("td");
                //CSV exporthoz sor lista összeállítása
                List<String> lista = new ArrayList<>();
                //Mappa név hozzáadása fájlhoz
                lista.add(file.getParentFile().getName());
                //Cím eltárolása a listába
                lista.add(t.html());
                for(int i = 0; i<tds.size(); i++) {
                    //Cellák tartalmának eltárolása
                    lista.add(tds.get(i).text());
                }
                //Lista kiírása
                writer.writeNext(lista.toArray(new String[lista.size()]));
            }
            //Üres sor a mappában levő HTML fájlok elkülönítésére
            writer.writeNext(new String[]{});
        } catch (IOException ex) {
            Logger.getLogger(HTMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
