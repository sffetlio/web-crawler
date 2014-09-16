package webcrawler;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Svetoslav
 */
class WebCrawler {

	private static final int THREADS = 12; //64
	private Link startUrl;
	private String needle;
	private Link result;
	private LinkedBlockingQueue<Link> urlsToProcess;
	private Set<Link> urlsProcessed;
	private Thread[] processingThreads;
	protected volatile int workingThreads = 0;

	public WebCrawler() {
		urlsProcessed = new HashSet();
		urlsToProcess = new LinkedBlockingQueue();

		processingThreads = new Thread[THREADS];
		for (int i = 0; i < processingThreads.length; i++) {
			processingThreads[i] = new Thread(new CrawlerThread(this));
			processingThreads[i].setName("Thread " + i);
		}
	}

	List<String> crawl(URL startUrl, String needle) {
		this.startUrl = new Link(startUrl);
		this.needle = needle;

		urlsProcessed.clear();
		urlsToProcess.clear();

		urlsToProcess.add(this.startUrl);

		// start all threads
		for (Thread processingThread : processingThreads) {
			processingThread.start();
		}

		// wait for all threads to join one by one
		for (Thread processingThread : processingThreads) {
			try {
				processingThread.join();
//				System.out.println("joined " + processingThread.getName());
			} catch (InterruptedException ex) {
			}
		}

		if (getResult() == null) {
			return new LinkedList<>();
		} else {
			return getResult().getHistory();
		}
	}

	protected synchronized void addForProcessing(Link url) {
		// check if link is to another site
		if (!url.getHost().equals(this.startUrl.getHost())) {
			return;
		}

//        System.out.println(url);
		if (!urlsProcessed.contains(url) && !urlsToProcess.contains(url)) {
			urlsToProcess.add(url);
		}
	}

	/**
	 * @return the start url
	 */
	public Link getStartUrl() {
		return startUrl;
	}

	/**
	 * @return the needle
	 */
	public String getNeedle() {
		return needle;
	}

	/**
	 * @return the urlsToProcess
	 */
	public LinkedBlockingQueue<Link> getUrlsToProcess() {
		return urlsToProcess;
	}

	/**
	 * @return the urlsProcessed
	 */
	public Set<Link> getUrlsProcessed() {
		return urlsProcessed;
	}

	synchronized boolean isWorking() {
		boolean isWorking = (workingThreads != 0 || !urlsToProcess.isEmpty());
		if (!isWorking) {
			for (int i = 0; i < processingThreads.length; i++) {
				processingThreads[i].interrupt();
			}
		}
		return isWorking;
	}

	/**
	 * @return the result
	 */
	public synchronized Link getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public synchronized void setResult(Link result) {
		this.result = result;
	}

}
