
public class WebCrawlingMain {

	/**
	 * The term from which we want to crawl data.
	 */
	private static final String TERM = "20183";
	
	/**
	 * Building database url
	 */
	private static final String BUILDING_URL = "https://web-app.usc.edu/maps/all_map_data2.js.pagespeed.jm.HVbGqVyZmP.js";
	
	/**
	 * Main method - start point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CourseCrawling webCrawl = new CourseCrawling();
		webCrawl.start("https://classes.usc.edu/term-" + TERM, BUILDING_URL);
	}



}