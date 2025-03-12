package com.sisa;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TopoCrawler {
	Parameters params;
	String namePage;
	String url;
	String domain;
	private int ordenPost=0;
	String[] Tipos = { "jpg", "JPG", "jpeg", "JPGE", "gif","GIF", "png", "PNG" };
	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private TreeMap<Integer,String> LinkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkContent =  (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();


	public TopoCrawler(Parameters parameters) {
		super();
		this.params=parameters;
		try {
			if(!params.getEntrada().isEmpty()) {
				this.url=params.getUrl();
			}else {
				/* incluimos la barra para todas las urls */
				this.url= params.getUrl().endsWith("/") ? params.getUrl() : params.getUrl() + "/";
			}


			this.domain = getDomainName(url);
			//SqlRemote sql = new SqlRemote();
			//this.pagesVisited = visitadas;

			//MAX_PAGES_TO_SEARCH = params.getIpaginas();

			crawl(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean crawl_proves(String url) {
		System.out.println("URL: " + url);
		//Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
		try {
			//Document htmlDocument = connection.get();

			Document htmlDocument = Jsoup.connect(url)
					.userAgent(USER_AGENT)
					.referrer("http://www.google.com") 
					.timeout(1000*5) //it's in milliseconds, so this means 5 seconds. 
					.ignoreHttpErrors(true)
					.get();

			String postLink=getMetaTag(htmlDocument, "og:url");
			System.out.println("postlink:" + postLink);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public boolean crawl(String url) {
		//Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);

		boolean explora=false;
		boolean imagenes = false;
		List<String> postImg = new LinkedList<String>();
		List<String> postImgThumb = new LinkedList<String>();
		Elements linksOnPage;
		Elements linksImages;
		try {
			//System.err.println(connection.get().baseUri());
			Document htmlDocument = Jsoup.connect(url)
					.userAgent(USER_AGENT)
					.referrer("http://www.google.com") 
					.timeout(1000*5) //it's in milliseconds, so this means 5 seconds. 
					.ignoreHttpErrors(true)
					.get();
			/*
			if(connection.response().statusCode() == 200) {
				System.out.println("**Visiting --> " + "-->"+ url );// + " pagesToVisit.size()=" + pagesToVisit.size());
			}
			if(!connection.response().contentType().contains("text/html")) { return false;}


			if(params.getEntrada().isEmpty()) {
				linksOnPage = htmlDocument.select("a[href]");
			}else {
				linksOnPage = htmlDocument.select(params.getEntrada());
			}
			 */
			String postLink=getMetaTag(htmlDocument, "og:url");
			String blog_and_title 	= htmlDocument.title();
			String postTitle= getMetaTag(htmlDocument, "og:title");
			String postDescription = getMetaTag(htmlDocument, "og:description");
			String text = htmlDocument.text();	

			if (!Objects.equals(url, new String("https://" + domain)) ) {
				explora=true;
				if (Objects.equals(namePage , postTitle)) {
					explora = false;
				}				
			}else {
				namePage = postTitle;
			}

			explora = true;

			//for(Element link : linksOnPage)  {
			//String href=link.absUrl("href");
			/* Recopilamos todos los links que se encuentran el las páginas
				if(href.indexOf("https://" + domain)==0){
					this.links.add(href);
				}
			 */
			
			Element title = htmlDocument.select("div.blog-posts.hfeed.div.div.div.div.post.hentry.uncustomized-post-template.h3").first();
			if(title !=null) {
				postTitle = title.text();
			}
			System.out.println("postLink=" + postLink);
			System.out.println("blog_and_title=" + blog_and_title);
			System.out.println("postTitle:" + postTitle);
			System.out.println("text=" + text);
			//Element post = htmlDocument.select("div.post-outer").first();
			String descrip ="";
			Elements body= htmlDocument.select("div.post-body.entry-content");
			// Elements body= htmlDocument.getElementsByClass("post-body");
			 for(Element bod : body)  {
				 System.err.println("body element:" + bod);
			 }
			 
			if(explora) {
				int orden=0;
				boolean thumb=false;
				boolean image=false;

				if (body!=null) {
					//linksImages = body.select("a[href]");
					linksImages = body.select("a.href");
					System.out.println("linksImages.count=" + linksImages.size());
					for(Element linkimage : linksImages)  {
						String href=linkimage.absUrl("href");
						//String href= linkimage.attr("href");
						String ext = href.substring(href.lastIndexOf('.')+1,href.length());
						int returnTipo = isTipo(ext, Tipos);
						if(returnTipo!=-1 ){
							image = true;
							String srcThumb="";
							Elements media = linkimage.select("[src]");
							for (Element src : media) {
								if (src.tagName().equals("img")){
									srcThumb = src.attr("abs:src");
									thumb=true;
								}
							}
							if(thumb && image) {
								postImg.add(href);
								postImgThumb.add(srcThumb);
								imagenes = true;
							}else if(!thumb && image){
								postImg.add(href);
								postImgThumb.add(href);
								imagenes = true;
							}
						}else {
							System.err.println("returnTipo=" + ext);
						}
						System.err.println("for linkimage=" + href + " ext=" + ext);
					}

					//postDescription = body.text();
					System.out.println("postDescription:" + postDescription);
				}else {
					System.err.println("body is null");
				}
			}




			//procesaLink(link);
			//}
			if(imagenes && explora) {
				for (int i = 0; i < postImg.size(); i++) {
					ordenPost ++;

					this.LinkPost.put(ordenPost, url);
					this.linkTitlePost.put(ordenPost,postTitle);
					this.linkContent.put(ordenPost,  postDescription);
					this.linksImg.put(ordenPost,postImg.get(i));
					this.linksImgThumb.put(ordenPost,postImgThumb.get(i));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error:" + e.getMessage());
			e.printStackTrace();
		}


		return false;
	}



	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) +"/" : domain +"/";
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

	public List<String> getLinks(){
		return this.links;
	}




	public TreeMap<Integer, String> getLinkPost() {
		return LinkPost;
	}
	public TreeMap<Integer, String> getLinkTitlePost() {
		return linkTitlePost;
	}
	public TreeMap<Integer, String> getLinkContent() {
		return linkContent;
	}

	public TreeMap<Integer, String> getLinksImg() {
		return linksImg;
	}
	public TreeMap<Integer, String> getLinksImgThumb() {
		return linksImgThumb;
	}

}
