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
import java.util.TreeMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraping_back2 {
	// We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
	private static final int MAX_PAGES_TO_SEARCH = 50;
	String[] Tipos = { "jpg", "JPG", "jpeg", "JPGE", "gif","GIF", "png", "PNG" };
	private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();

	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	Parameters params;
	String url;
	String domain;

	//private String blog_and_title="";
	//private String description="";
	//private String text="";
	
	//private String postLink;
	public String result;
	private int orden=0;
	private int post=0;
	//private int cont;
	private TreeMap<Integer,String> LinkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();


	public Scraping_back2(Parameters parameters) {
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
			if(pagesToVisit.size()==0 && pagesVisited.size()>1 ) {
				break;
			}
			//System.out.println("pagesToVisit.size()=" + pagesToVisit.size() + " --> pagesVisited.size()=" + pagesVisited.size() + " --> currentUrl=" + currentUrl);
			crawl(currentUrl); 
			this.pagesToVisit.addAll(this.getLinks());
		}

		System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
	}


	private String nextUrl(){
		String nextUrl="";
		do{
			if (this.pagesToVisit.size() == 0) {
				break;
			}
			nextUrl = this.pagesToVisit.remove(0);
		} while(this.pagesVisited.contains(nextUrl));

		this.pagesVisited.add(nextUrl);

		return nextUrl;
	}



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
			this.links.add(url);
			
			if (url.equals("https://" + domain)) {
				System.out.println("URL:"  + url + " ==== " + "https://" + domain);
			}
			
			post ++;
			String postLink=url;
			String blog_and_title 	= htmlDocument.title();
			String titlePost= getSelector(htmlDocument,"#blog-posts > div.post.uncustomized-post-template > h3");
			String text = htmlDocument.text();		
			String description = getMetaTag(htmlDocument, "description");
			if(!params.getSelectEntrades().isEmpty() ) {
				System.out.println(params.getSelectEntrades() + "-->"+ getSelector(htmlDocument, params.getSelectEntrades()) );
			}
			if (description == null) {
				description = getMetaTag(htmlDocument, "og:description");
			}
			
			System.out.println("TITLE:" + blog_and_title );
			System.out.println("LINK:" + postLink );
			System.out.println("Pedir en parámetros ---> DESCRIPTION:" + description );			
			System.out.println("Titulo: " + titlePost);
			boolean imagenes = false;
			Elements linksOnPage = htmlDocument.select("a[href]");
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
				}
				
				addLink(link);
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

	public void addLink(Element link) {
		if(link.absUrl("href").contains(domain)) {
			//Exclusiones mediante tags
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
			//End Exclusiones mediante tags
		}
	}

	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
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

	public static int isTipo(String needle, String[] haystack){
		for (int i=0; i<haystack.length; i++){
			if (haystack[i] != null && haystack[i].equals(needle)
					|| needle == null && haystack[i] == null) return i;
		}

		return -1;
	}
	
	public List<String> getLinks(){
		return this.links;
	}

	public Set<String> getPagesVisited() {
		return pagesVisited;
	}

	public List<String> getPagesToVisit() {
		return pagesToVisit;
	}
	public TreeMap<Integer, String> getLinkTitlePost() {
		return linkTitlePost;
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
