package webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Svetoslav
 */
public class CrawlerThread implements Runnable {

	private final WebCrawler crawler;

	private final static Pattern hrefPattern = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);

	public CrawlerThread(WebCrawler crawler) {
		this.crawler = crawler;
	}

	@Override
	public void run() {
		while (crawler.isWorking() && !Thread.currentThread().isInterrupted()) {
			try {
				Link u = crawler.getUrlsToProcess().take();
				crawler.workingThreads++;

				Link l = processUrl(u);
				if (l != null) {
					// stop processing and exit
					crawler.getUrlsToProcess().clear();
					crawler.setResult(l);
				}
			} catch (InterruptedException ex) {
//				System.out.println(Thread.currentThread().getName()+" Interrupted ");
				break;
			}
			crawler.workingThreads--;
		}
	}

	public Link processUrl(Link url) {
//		System.out.println(Thread.currentThread().getName() + " processing: " + url);
		crawler.getUrlsProcessed().add(url);

		String inputLine;
		StringBuilder pageContent = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			while ((inputLine = in.readLine()) != null) {
				pageContent.append(inputLine);
			}

			String contentStr = pageContent.toString();
			if (contentStr.contains(crawler.getNeedle())) {
				return url;
			}

			findLinks(contentStr, url);

		} catch (MalformedURLException e) {
//            System.out.println("MalformedURLException: "+url);
			return null;
		} catch (IOException e) {
//            System.out.println("IOException: " + url);
			return null;
		}
		return null;
	}

	private void findLinks(String html, Link currentUrl) {
		Matcher m = hrefPattern.matcher(html);

		while (m.find()) {
			try {
				crawler.addForProcessing(new Link(crawler.getStartUrl().getUrl(), m.group(1), currentUrl.getHistory()));
			} catch (MalformedURLException e) {
//				System.out.println("MalformedURLException: "+m.group(1));
			}
		}
	}

}
