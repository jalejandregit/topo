package com.sisa;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SpiderLeg2 {
	// We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
	private static final int MAX_PAGES_TO_SEARCH = 26;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private Document htmlDocument;
	private String domain="";
	String[] Tipos = { "jpg", "JPG", "jpeg", "JPGE", "gif","GIF", "png", "PNG" };
	private TreeMap<Integer,String> LinkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();

	private String blog_and_title="";
	private String description="";
	private String text="";
	private String titlePost="";
	private String postLink;
	public String result;
	private int orden=0;
	private int post=0;
	private int cont;
	private String url;
	private Parameters params;
	
	public SpiderLeg2(Parameters parameters) {
		super();
		this.params=parameters;
		try {
			this.url=params.getUrl();
			this.domain = getDomainName(url);
			search();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void search() {
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
 	        	crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
 	        }
			 if(currentUrl.contains(domain)) {
 	        	cont ++;
 	        	this.pagesToVisit.addAll(this.getLinks());
	    	       // this.pagesToVisit.addAll(sleg.getLinks());
 	       }
		 }
	}

	/**
	 * This performs all the work. It makes an HTTP request, checks the response, and then gathers
	 * up all the links on the page. Perform a searchForWord after the successful crawl
	 *
	 * @param url
	 *            - The URL to visit
	 * @return whether or not the crawl was successful
	 */
	public boolean crawl(String url)
	{
		try
		{
			//System.out.println("CONEXION:" + url);
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;
			if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
				// indicating that everything is great.
			{
				System.out.println("\n**Visiting** Received web page at " + url);
			}
			if(!connection.response().contentType().contains("text/html"))
			{
				//System.out.println("**Fallo** Recuperé algo que no es HTML");
				return false;
			}
			
			if (url.equals("https://" + domain)) {
				//new String("test").equals("test") // --> true 
				System.out.println("URL:"  + url + " ==== " + "https://" + domain);
				//return false;
			}
			
			Elements linksOnPage = htmlDocument.select("a[href]");

			blog_and_title 	= htmlDocument.title();
			postLink=url;
			post ++;
			//this.LinkPost.put(post, url);
			this.links.add(url);
			text = htmlDocument.text();
			description = getMetaTag(htmlDocument, "description");
			if(params.getSelectEntrades()!=null) {
				System.out.println(params.getSelectEntrades() + "-->"+ getSelector(htmlDocument, params.getSelectEntrades()) );
			}
			if (description == null) {
				description = getMetaTag(htmlDocument, "og:description");
			}
			System.out.println("TITLE:" + blog_and_title );
			System.out.println("LINK:" + postLink );
			//System.out.println("TEXT:" + text );
			System.out.println("Pedir en parámetros ---> DESCRIPTION:" + description );
			titlePost = getSelector(htmlDocument,"#blog-posts > div > h3");
			System.out.println("Titulo: " + titlePost);
			boolean imagenes = false;
			for(Element link : linksOnPage)  {
				String href=link.absUrl("href");
				//this.links.add(href);
				String ext = href.substring(href.lastIndexOf('.')+1,href.length());
				int returnTipo = isTipo(ext, Tipos);
				if(returnTipo!=-1 ){
					//System.out.println("AHREF: " + href);
					orden ++;
					this.linkTitlePost.put(orden,titlePost);
					this.LinkPost.put(orden, url);
					imagenes = true;
					 this.linksImg.put(orden, href);
					 Elements media = link.select("[src]");
					 for (Element src : media) {
			                if (src.tagName().equals("img")){
			                	//System.err.println("SRC:"  +  src.tagName() + "-" + src.attr("abs:src"));
			                	//this.linksImg.add(src.attr("abs:src"));
			                	this.linksImgThumb.put(orden , src.attr("abs:src"));
			                } else{
			                	System.err.println("NO ES IMG: "  +  src.tagName() +"-" +  src.attr("abs:src"));
			                }
			           
			            }
				}else {
					//System.out.println("href=" + href);
				}

				
				if(url.contains(domain)) {
				//if (orden>0 && !url.equals("https://" + domain)) {
					this.links.add(link.absUrl("href"));
				}
				
			}
			return true;
		}
		catch(IOException ioe)
		{
			// We were not successful in our HTTP request
			return false;
		}
	}
	
	
	private String nextUrl()  {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while(this.pagesVisited.contains(nextUrl));
        if(nextUrl.contains(domain)) {
        	this.pagesVisited.add(nextUrl);
        }
        return nextUrl;
    }
	
	public TreeMap<Integer, String> getLinkTitlePost() {
		return linkTitlePost;
	}

	/**
	 * Performs a search on the body of on the HTML document that is retrieved. This method should
	 * only be called after a successful crawl.
	 *
	 * @param searchWord
	 *            - The word or string to look for
	 * @return whether or not the word was found
	 */
	public void searchForWord(String searchWord, List<String> emails)
	{

		if(this.htmlDocument == null)
		{
			System.out.println("ERROR! Call crawl() before performing analysis on the document");
			//return false;
		}
		Pattern pattern =
				Pattern.compile("\"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$\", Pattern.CASE_INSENSITIVE");

		Matcher matchs = pattern.matcher(searchWord);

		while (matchs.find()) {
			System.out.println(matchs.group());
		}
	}

	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}


	public static int isTipo(String needle, String[] haystack){
		for (int i=0; i<haystack.length; i++){
			if (haystack[i] != null && haystack[i].equals(needle)
					|| needle == null && haystack[i] == null) return i;
		}

		return -1;
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

	private String getSelector(Document document, String selector) {
		String text="";
		
        try {
        	text  = document.select(String.format(selector)).text();
        } catch (Exception e) {
            e.printStackTrace();
            //System.exit(2);
        }
		
		//Document doces = Jsoup.parse(html);
		//String query = "#ctl00_Body_gvResult > tbody:nth-child(1) > tr:nth-child(%d) > td:nth-child(%d)";
		//for (int i = 1; i <= 8; i++) {
		   // System.out.print(document.select(String.format(selector, i, 3)).text());
		    //System.out.print(" ");
		   // System.out.println(document.select(String.format(selector, i, 4)).text());
		//}
		//if (text != null) return text;
		return (text != "")?text:"";
		//return text;	
				
	}


	public List<String> getLinks()
	{
		return this.links;
	}
	public TreeMap<Integer, String> getLinkPost() {
		return LinkPost;
	}
	public TreeMap<Integer, String> getLinksImg() {
		return linksImg;
	}
	public TreeMap<Integer, String> getLinksImgThumb() {
		return linksImgThumb;
	}

}
