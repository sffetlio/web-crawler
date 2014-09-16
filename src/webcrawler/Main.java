package webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Svetoslav
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws MalformedURLException {
		WebCrawler crawler = new WebCrawler();
		/*
        if (args.length != 2 || args[0] == null || args[1] == null){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Missing arguments");

            try {
                System.out.print("Enter url:");
                url = br.readLine();
                System.out.print("Enter needle:");
                needle = br.readLine();
            } catch (IOException e) {
            }
        }
		*/
		URL url = new URL("http://fmi.wikidot.com");
		List<String> path = crawler.crawl(url, "Докажете, че:");
		System.out.println("result :" + path.toString());
	}
	
}
