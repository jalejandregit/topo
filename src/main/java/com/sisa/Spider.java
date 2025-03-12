package com.sisa;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider{
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
	private static final int MAX_PAGES_TO_SEARCH = 5;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private String domain="";
    private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();
	
	/*
	private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
	 */
	
	
    private String title="";
    private String description="";
    private Document htmlDocument;
    private String text="";
    private String postLink;
    public String result;
    private List<String> links = new LinkedList<String>();
    //private HashSet<String> linksImg = (HashSet<String>) new HashSet<String>();
    
    Map<String, String> map = new HashMap<String, String>();
    private TreeMap<Integer,String> linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
    private TreeMap<Integer,String> linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
    String[] Tipos = { "jpg", "JPG", "jpeg", "JPGE", "gif","GIF", "png", "PNG" }; 
    private int orden=0;
    Document xspDocument=null;
    String url;
    
	public Spider(String urlIni) {
		// TODO Auto-generated constructor stub
		if(this.domain.equals("") ){
			try {
				domain = getDomainName(urlIni);
				url= urlIni;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.err.println("DOMAIN: " + domain);
		}
		
		
	}
	
	public String crawl(){
		map.put("img[src]", "src");		
		map.put("a[href]", "href");
		map.put("img[data-orig-file]", "data-orig-file");
		
		
		while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
			String currentUrl;
			//SpiderLeg2 leg = new SpiderLeg2();
			if(this.pagesToVisit.isEmpty()){
				currentUrl = url;
				this.pagesVisited.add(url);
			} else{
				currentUrl = this.nextUrl();
			}
			if(currentUrl.indexOf(domain) !=-1){
				this.pagesToVisit.add(currentUrl); 
				//this.title 	= leg.getTitle();
				///this.postLink = leg.getPostLink();
				//this.description		= leg.getDescription();
				//this.text		=leg.getText();
				//this.linksImg	= leg.getLinksImg();
				//this.linksImgThumb = leg.getLinksImgThumb();
				
				Connection connection = Jsoup.connect(currentUrl).userAgent(USER_AGENT);
				try {
					Document htmlDocument = connection.get();
					if(connection.response().statusCode() == 200 && connection.response().contentType().contains("text/html")) {
						xspDocument = htmlDocument;
			            result += "\nVisiting** Received web page at " + currentUrl;
			            Elements linksOnPage = htmlDocument.select("a[href]");
			            title 	= htmlDocument.title();
			            postLink=currentUrl;
			            text = htmlDocument.text();
			            description = getMetaTag(htmlDocument, "description");
			            if (description == null) {
			               description = getMetaTag(htmlDocument, "og:description");
			            }
			            
			            for(Element link : linksOnPage) {
			            	String href=link.absUrl("href");
			                this.links.add(href);
			                String ext = href.substring(href.lastIndexOf('.')+1,href.length());
			                int returnTipo = isTipo(ext, Tipos);
			                if(returnTipo!=-1 ){
			                	orden ++;
								 this.linksImg.put(orden, href);
								 System.err.println( "--> linkXsp: " + href );
								 Elements media = link.select("[src]");
								 for (Element src : media) {
						                if (src.tagName().equals("img")){
						                	System.err.println("IMG:"  +  src.tagName() + "-" + src.attr("abs:src"));
						                	//this.linksImg.add(src.attr("abs:src"));
						                	this.linksImgThumb.put(orden , src.attr("abs:src"));
						                } else{
						                	System.err.println("NO ES IMG: "  +  src.tagName() +"-" +  src.attr("abs:src"));
						                }
						           
						            }
								 
							 }
			                System.err.println("href: " + link.absUrl("href"));
			            }
			            
			            
			         }else {
			        	result = "Error al conectar la url:" + url;
			         }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		


		if(htmlDocument!=null){
        	//createSubscription(xspDocument);
        	
        	Iterator it = map.keySet().iterator();
			while(it.hasNext()){
				String KEY = (String) it.next();
				String ABS = map.get(KEY);
				Elements linksOnPageXsp = htmlDocument.select(KEY);
				for(Element linkXsp : linksOnPageXsp){
					String href=linkXsp.absUrl(ABS);
					String ext = href.substring(href.lastIndexOf('.')+1,href.length());
					int returnTipo = isTipo(ext, Tipos);

					 //if(returnTipo!=-1 && href.indexOf(domain) !=-1 ){
					if(returnTipo!=-1 ){
						 //this.linksImg.add(href);
						result += "\n--> linkXsp: " + href ;
					 }
				}
			}
        }else {
        	result +="\nhtmlDocument!=null";
        }
	
		
		
		return result;
	}
	
	
	   private String getMetaTag(Document document, String attr) {
	        Elements elements = document.select("meta[name=" + attr + "]");
	        for (Element element : elements) {
	            final String s = element.attr("content");
	            if (s != null) return s;
	        }
	        elements = document.select("meta[property=" + attr + "]");
	        for (Element element : elements) {
	            final String s = element.attr("content");
	            if (s != null) return s;
	        }
	        return null;
	    }
	   
	    public static int isTipo(String needle, String[] haystack){
			for (int i=0; i<haystack.length; i++){
				if (haystack[i] != null && haystack[i].equals(needle)
						|| needle == null && haystack[i] == null) return i;
			}

			return -1;
		}
	    
		public static String getDomainName(String url) throws URISyntaxException {
			URI uri = new URI(url);
			String domain = uri.getHost();
			return domain.startsWith("www.") ? domain.substring(4) : domain;
		}
		
		/**
		 * Returns the next URL to visit (in the order that they were found). We also do a check to make
		 * sure this method doesn't return a URL that has already been visited.
		 * 
		 * @return
		 */
		private String nextUrl() {
			String nextUrl;
			do{
				nextUrl = this.pagesToVisit.remove(0);
			} while(this.pagesVisited.contains(nextUrl));



			this.pagesVisited.add(nextUrl);
			return nextUrl;
		}
	    
}
