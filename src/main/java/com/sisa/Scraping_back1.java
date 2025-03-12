package com.sisa;
//https://jarroba.com/scraping-java-jsoup-ejemplos/
//http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraping_back1 {
	// We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
	private static final int MAX_PAGES_TO_SEARCH = 50;
	private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();
	
	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	Parameters params;
	String url;
	String domain;

	private String blog_and_title="";
	private String description="";
	private String text="";
	private String titlePost="";
	private String postLink;
	public String result;
	private int orden=0;
	private int post=0;
	private int cont;

	public Scraping_back1(Parameters parameters) {
		super();
		this.params=parameters;
		try {
			this.url=params.getUrl();
			this.domain = getDomainName(url);
			System.out.println("Domain:" + domain);
			search();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void search() {
        while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH){
            String currentUrl;
            
            if(this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            }else{
                currentUrl = this.nextUrl();
            }
            //if(currentUrl=="") { break;}
            if(pagesToVisit.size()==0 && pagesVisited.size()>1 ) {
            	break;
            }
            System.out.println("pagesToVisit.size()=" + pagesToVisit.size() + " --> pagesVisited.size()=" + pagesVisited.size() + " --> currentUrl=" + currentUrl);
            crawl(currentUrl); 
            this.pagesToVisit.addAll(this.getLinks());
        }
        
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }
	
	
	private String nextUrl(){
		String nextUrl="";
		int next = 0;
		
		do{
			next++;
			//System.out.println("nextUrl.this.pagesToVisit." + this.pagesToVisit.get(0));
		    if (this.pagesToVisit.size() == 0) {
		        break;
		    }
			nextUrl = this.pagesToVisit.remove(0);
			//if(pagesVisited.size()==20) {System.out.println("NEXT:" + next + "-" + nextUrl);}
		} while(this.pagesVisited.contains(nextUrl));

		 //if (this.pagesToVisit.size() != 0) {
			 this.pagesVisited.add(nextUrl);
		// }
		
		return nextUrl;
	}
	
/*
 	public void searchOK() {
        while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH){
            String currentUrl;
            
            if(this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            }else{
                currentUrl = this.nextUrl();
            }
            crawl(currentUrl); 
            this.pagesToVisit.addAll(this.getLinks());
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }
 * 
 * 
 * 
 * 
 * 
 * 
	public void searchOLD(){
		while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
			String currentUrl;
			if(this.pagesToVisit.isEmpty())  {
				currentUrl = url;
				this.pagesVisited.add(url);
			} else{
				currentUrl = this.nextUrl();
			}
			
			boolean exclude=params.getExclude();			
			if(exclude==false) {
				crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
			}else {
				exclude = false;
				for(String p: params.getTagsExclude()) {
					if(currentUrl.endsWith(p)|| currentUrl.contains(p)) {
						exclude=true;
					}

				}
				if(exclude==false) {
					crawl(currentUrl);
				}
			}

			//System.out.println("links.size():" + this.links.size());
			this.pagesToVisit.addAll(this.getLinks());
		}
		System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
	}

*/

	/* selectores
	 * 
	 * Elements articleLinks = document.select("h2 a[href^=\"http://www.mkyong.com/\"]");
	 * Elements otherLinks = document.select("a[href^=\"http://www.mkyong.com/page/\"]");
	 */


	/* filtros
	 * 
	 *  if (article.text().matches("^.*?(Java 8|java 8|JAVA 8).*$"))
	 */

	public boolean crawl(String url) {
		Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
		try {
			Document htmlDocument = connection.get();
			if(connection.response().statusCode() == 200) {
				System.out.println("\n**Visiting --> " + "-->"+ url);
			}
			if(!connection.response().contentType().contains("text/html")) { return false;}

			if (url.equals("https://" + domain)) {
				System.out.println("URL:"  + url + " ==== " + "https://" + domain);
			}
			Elements linksOnPage = htmlDocument.select("a[href]");

			this.links.add(url);
			blog_and_title 	= htmlDocument.title();
			postLink=url;
			post ++;
			
			for(Element link : linksOnPage)  {	

				if(link.absUrl("href").contains(domain)) {
					//this.links.add(link.absUrl("href"));

					/**/
					boolean exclude=params.getExclude();			
					if(exclude==false) {
						this.links.add(link.absUrl("href"));
					}else {
						exclude = false;
						for(String p: params.getTagsExclude()) {
							if(link.absUrl("href").endsWith(p)|| link.absUrl("href").contains(p)) {
								//System.out.println("Excluir: " + link.absUrl("href") + " ---> tag:" +  p );
								exclude=true;
							}
						}
						if(exclude==false) {
							this.links.add(link.absUrl("href"));
							//System.out.println("link add:" + this.pagesToVisit.size() + " --> " + link.absUrl("href"));
						}else {
							//System.out.println( " ---> excluido:" + link.absUrl("href"));
						}
					}
					 
					

				}
			}




		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return false;
	}




	/*
	 * Funcions
	 *
	 */


	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	
	/*
	private String nextUrl(){
		String nextUrl;
		int next = 0;
		do{
			next++;
			//System.out.println("nextUrl.this.pagesToVisit." + this.pagesToVisit.get(0));
			nextUrl = this.pagesToVisit.remove(0);
			System.out.println("NEXT:" + next + "-" + nextUrl);
		} while(this.pagesVisited.contains(nextUrl));

		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}

*/

	public List<String> getLinks(){
		return this.links;
	}

	public Set<String> getPagesVisited() {
		return pagesVisited;
	}

	public List<String> getPagesToVisit() {
		return pagesToVisit;
	}

}
