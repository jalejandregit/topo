package com.sisa;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


/**
 * @author jesus
 *
 */



public class WebCrawler {
	
	/*
	public WebCrawler(String domain) {
		super();
		this.domain = domain;
	}
	*/
	SpiderLeg sleg;
	private static final int MAX_PAGES_TO_SEARCH = 26;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
    
    private String domain="";
    //String[] Tipos = { "jpg", "JPG", "jpeg", "JPGE", "gif","GIF", "png", "PNG" }; 
    int cont=0;
    private List<String> links = new LinkedList<String>();

	private TreeMap<Integer,String> LinkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
    public List<String> getLinks() {
		return links;
	}


	private String nextUrl()
    {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while(this.pagesVisited.contains(nextUrl));
        if(nextUrl.contains(domain)) {
        	this.pagesVisited.add(nextUrl);
        }
        return nextUrl;
    }
    
    
//    public void search(String url, String searchWord) {
    public void search(String url) {
    	
    	
    	try {
    		domain = getDomainName(url);		
    		System.out.println("DOMINIO:" + domain);
        	sleg = new SpiderLeg(domain);
	    	 while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
	    	        String currentUrl;
	    	        
	    	        
	    	        if(this.pagesToVisit.isEmpty()) {
	    	            currentUrl = url;
	    	           // if(currentUrl.indexOf(domain) !=-1){
	    	            if(currentUrl.contains(domain)) {
	    	            	this.pagesVisited.add(url);
	    	           }
	    	            
	    	        }
	    	        else	    	        {
	    	            currentUrl = this.nextUrl();
	    	        }
	    	        
	    	        if(currentUrl.contains(domain)) {
	    	        	sleg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
	    	        }
	    	        // SpiderLeg
	    	        //leg.searchForWord(currentUrl, emails);
	    	        //if(currentUrl.indexOf(domain) !=-1){
	    	        if(currentUrl.contains(domain)) {
	    	        	cont ++;
	    	        	this.pagesToVisit.addAll(sleg.getLinks());
		    	       // this.pagesToVisit.addAll(sleg.getLinks());
	    	       }
	    	        
	    	 }
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	this.links = sleg.getLinks();
    	this.LinkPost= sleg.getLinkPost();
    	this.linksImg =sleg.getLinksImg();
    	this.linksImgThumb = sleg.getLinksImgThumb();
    	this.linkTitlePost =sleg.getLinkTitlePost();
    	System.out.println("CONTADOR =" + cont);	 
    }
    
    



	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}
	
    public Set<String> getPagesVisited() {
		return pagesVisited;
	}


	public List<String> getPagesToVisit() {
		return pagesToVisit;
	}


	public TreeMap<Integer, String> getLinkPost() {
		return LinkPost;
	}

	public TreeMap<Integer, String> getLinkTitlePost() {
		return linkTitlePost;
	}
	
	public TreeMap<Integer, String> getLinksImg() {
		return linksImg;
	}


	public TreeMap<Integer, String> getLinksImgThumb() {
		return linksImgThumb;
	}


}
