package webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Svetoslav
 */
public class Link {
	private final URL url;
	private final List<String> history = new LinkedList();

	Link(URL context, String url, List<String> history) throws MalformedURLException{
		this.url = new URL(context, url);
		this.history.addAll(history);
		this.history.add(url);
	}
	
	Link(URL url) {
		this.url = url;
		this.history.add(url.toString());
	}

	InputStream openStream() throws IOException {
		return this.url.openStream();
	}

	Object getHost() {
		return this.url.getHost();
	}

	@Override
	public String toString() {
		return url.toString();
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return url.toString().equals(obj.toString());
	}
	
	URL getUrl() {
		return url;
	}

	List<String> getHistory() {
		return history;
	}

	void printHistory() {
		for(String s : history){
			System.out.println(s);
		}
	}
	
}
