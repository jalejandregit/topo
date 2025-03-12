package com.sisa;
//https://jarroba.com/scraping-java-jsoup-ejemplos/
//http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/
//https://regex101.com/

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.logging.Level;

public class Scraping_back3 {
	// We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
	private static int MAX_PAGES_TO_SEARCH = 10;
	private int nVisits;
	private String lastVisit;
	String[] Tipos = { "jpg", "JPG", "jpeg", "JPGE", "gif","GIF", "png", "PNG" };
	private Set<String> pagesVisited = new HashSet<String>();
	private Set<String> readPagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();

	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	Parameters params;
	String namePage;
	String url;
	String domain;
	String tagSelect;
	Level info = Level.INFO;

	//private String blog_and_title="";
	//private String description="";
	//private String text="";

	//private String postLink;
	public String result;
	private int orden=0;
	private int ordenPost=0;
	private int post=0;
	//private int cont;
	private TreeMap<Integer,String> LinkPost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linkTitlePost = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImg = (TreeMap<Integer,String>) new TreeMap<Integer,String>();
	private TreeMap<Integer,String> linksImgThumb = (TreeMap<Integer,String>) new TreeMap<Integer,String>();

	LOG log;

	public Scraping_back3(Parameters parameters,Set<String> readVisited,List<String> readPagesToVisit) {
		super();
		this.params=parameters;
		this.pagesVisited = readVisited;
		/*
		for( Iterator it = readVisited.iterator(); it.hasNext(); ) { 
			String x = it.next().toString();
			//System.out.println("add pages visited:" + x);
			this.pagesVisited.add(x);
		} 
		 */

		//this.pagesToVisit = readPagesToVisit;
		/*
		for( Iterator it2 = pagesVisited.iterator(); it2.hasNext(); ) { 
			String x = it2.next().toString();
			System.out.println("visited:" + x);
		} 
		 */

		try {
			if(!params.getEntrada().isEmpty()) {
				this.url=params.getUrl();
			}else {
				/* incluimos la barra para todas las urls */
				this.url= params.getUrl().endsWith("/") ? params.getUrl() : params.getUrl() + "/";
			}


			this.domain = getDomainName(url);
			//log = new LOG(this.domain );

			MAX_PAGES_TO_SEARCH = params.getIpaginas();
			System.out.println("Domain:" + domain + " MAX_PAGES_TO_SEARCH=" + MAX_PAGES_TO_SEARCH + " iniUrl=" + url + " paginas cargadas:" + this.pagesVisited.size());
			search();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void search() {
		System.out.println("110 search this.pagesVisited.size()=" + this.pagesVisited.size());
		while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH){
			String currentUrl;

			if(this.pagesToVisit.isEmpty()) {
				currentUrl = url;
				this.pagesVisited.add(url);
			}else{
				//do { 
				currentUrl = this.nextUrl();
				//} while(this.readPagesVisited.contains(currentUrl)); //mientras contenga la url seguirá el bucle
			}

			///System.out.println("ToVisit.size()=" + pagesToVisit.size() + " --> Visited.size()=" + pagesVisited.size() + " this.getLinks()->tovisit " + this.getLinks().size() +  " --> currentUrl=" + currentUrl);
			//if(pagesToVisit.size()==0 && pagesVisited.size()>1 ) {
			//tovisit 11 --> currentUrl=https://urtohijodelia.blogspot.com/2009/12/lia.html
			//	break;
			//}
			if(nVisits == this.getLinks().size() && lastVisit == currentUrl) {
				break;
			}
			nVisits = this.getLinks().size();
			lastVisit = currentUrl;


			//if(!existUrlInSet(currentUrl)) {
			crawl(currentUrl); 
			//}
			//if(!isRead(currentUrl)) {
			this.pagesToVisit.addAll(this.getLinks());
			//}

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
		 //System.out.println(this.pagesToVisit.size() + " --> " + nextUrl);
		} while(this.pagesVisited.contains(nextUrl));

		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}



	boolean existUrlInSet(String url) {
		boolean existe=false;
		//for( Iterator it2 = pagesVisited.iterator(); it2.hasNext(); ) { 
		for( Iterator it2 = pagesVisited.iterator(); it2.hasNext(); ) { 
			String x = it2.next().toString();
			if(Objects.equals(x, url)) {
				existe= true;
				break;
			}
		}
		System.out.println(existe + " ->" + url);
		return existe; 
	}

	public boolean crawl(String url) {
		/*
		if (!Objects.equals(url, new String("https://" + domain)) ) {
			if(!procesaLink(url)  ) {
				System.out.println("**Descartada --> " + "-->"+ url );
				return false;
			}
		}
		*/
		Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);

		try {
			boolean explora=false;
			boolean imagenes = false;
			List<String> postImg = new LinkedList<String>();
			List<String> postImgThumb = new LinkedList<String>();
			Elements linksOnPage;
			Document htmlDocument = connection.get();
			if(connection.response().statusCode() == 200) {
				System.out.println("**Visiting --> " + "-->"+ url );// + " pagesToVisit.size()=" + pagesToVisit.size());
			}
			if(!connection.response().contentType().contains("text/html")) { return false;}

			if(params.getEntrada().isEmpty()) {
				linksOnPage = htmlDocument.select("a[href]");
			}else {
				linksOnPage = htmlDocument.select(params.getEntrada());
			}

			//
			//String postLink=url;
			String postLink=getMetaTag(htmlDocument, "og:url");
			String blog_and_title 	= htmlDocument.title();
			String postTitle= getMetaTag(htmlDocument, "og:title");
			String postDescription = getMetaTag(htmlDocument, "og:description");
			String text = htmlDocument.text();		
			//String description = getMetaTag(htmlDocument, "description");
			if(!params.getSelectEntrades().isEmpty() ) {
				//System.out.println(params.getSelectEntrades() + "-->"+ getSelector(htmlDocument, params.getSelectEntrades()) );
			}
			//if (description == null) {
			//	description = getMetaTag(htmlDocument, "og:description");
			///}
			//if (!url.equals("https://" + domain)) {
			/**/
			if (!Objects.equals(url, new String("https://" + domain)) ) {
				explora = true;
				if (Objects.equals(namePage , postTitle)) {
					explora = false;
				}
				if(!procesaLink(url)  ) {
					System.out.println("**Descartada --> " + "-->"+ url );
					explora = false;
				}
				//if (isRead(url) ){
				//	explora = false;
				//}
				//System.out.println("url_" + url + "!Object.equals -->https://" + domain  + " explora? " + explora);
			}else {
				namePage = postTitle;
			}
			//System.out.println("namePage=" + namePage + "--> postTitle=" + postTitle);

			//explora = true;
			for(Element link : linksOnPage)  {

				String href=link.absUrl("href");

				if(href.indexOf("https://" + domain)==0){
					//System.out.println("188-href= " + href + "=" + "https://" + domain + " match:" + href.indexOf("https://" + domain)) ; 
					//if (!isRead(href) ){
					//	explora = true;
					//if(existUrlInSet(href)) {
					//	explora=false; 
					//}else {
					//addLink(link);
					//}
					//if(addLink(link)) {
					//System.out.println("190-explora: " + href);
					//}
					//}
					//addLink(link);
					this.links.add(href);
				}



				if(explora) {
					//System.out.println("** Explorando: " + link);
					orden=0;
					boolean thumb=false;
					boolean image=false;
					//String href=link.absUrl("href");
					String ext = href.substring(href.lastIndexOf('.')+1,href.length());
					int returnTipo = isTipo(ext, Tipos);

					if(returnTipo!=-1 ){
						image = true;
						String srcThumb="";
						Elements media = link.select("[src]");
						for (Element src : media) {
							if (src.tagName().equals("img")){
								//System.err.println("SRC:"  +  src.tagName() + "-" + src.attr("abs:src"));
								//this.linksImg.add(src.attr("abs:src"));
								srcThumb = src.attr("abs:src");

								thumb=true;
							} else{
								//System.err.println("NO ES IMG: "  +  src.tagName() +"-" +  src.attr("abs:src"));
							}

						}
						if(thumb && image) {
							postImg.add(href);
							postImgThumb.add(srcThumb);
							imagenes = true;
						}
					}
				}

			}
			//System.out.println("226-imagenes: " + imagenes + " explora: "  + explora + " href=" + url);
			if(imagenes && explora) {
				if ( Objects.equals(blog_and_title, postTitle)){
					System.out.println("Iguales --> " + postTitle + " url=" + url);
				}
				for (int i = 0; i < postImg.size(); i++) {
					ordenPost ++;
					this.linkTitlePost.put(ordenPost,postTitle);
					this.LinkPost.put(ordenPost, url);
					this.linksImg.put(ordenPost,postImg.get(i));
					this.linksImgThumb.put(ordenPost,postImgThumb.get(i));
					//System.out.println("Url: " + url);
					//System.out.println("Blog:" + blog_and_title );
					//System.out.println("Titulo: " + postTitle);
					//System.out.println("LINK:" + postLink );
					//System.out.println("DESCRIPTION:" + postDescription );			
					//
				}
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		return false;

	}

	public boolean isReadxxxxxxxxxxxxxxxxxxxxxx(String urlCompare) {
		boolean leida=false;
		for( Iterator it = readPagesVisited.iterator(); it.hasNext(); ) { 
			String x = it.next().toString();
			if(Objects.equals(x, urlCompare) ){
				leida=true;
				break;
			}
		}
		//System.out.println(leida  + " -> " + urlCompare);
		return leida;

	}

	/**
	 * AddLink permite anexar url en función de los parametros suministrados
	 * 1/0-Verificar si la url pertenece al dominio y la anexa, si no lo es la descarta
	 * 2/0-Si no hay tags de exlusiones, comprobar si hay filtro de Incluiones
	 * 2/1-Hay exclusiones
	 * 2/1/1 -> determinar si hay que excluir con getTagsExclude() true |false 
	 * 2/1/2 -> determinar si es candidata a incluir getTagsInclude() true |false
	 * 2/1/3 -> Si -> exclude==false && include==true -> Añadir
	 * @param link
	 */
	
	public boolean procesaLink(String href) {
		boolean ok=false;
		String msg="";
		//String href = link.absUrl("href");


		if(href.contains(domain)) {
			//Exclusiones mediante tags
			boolean exclude=params.getExclude();
			boolean include=params.getInclude();

			if(exclude==false) {
				/* no hay tags de exclusiones, comprobamos si hay filtro de tags para inclusiones*/
				msg="no hay tags de exclusiones, paso-1=" + href;
				if(include) {
					include=false;
					for(String i: params.getTagsInclude()) {
						if(href.endsWith(i)|| href.contains(i)) {
							include=true;

						}
					}
					if(include) {
						//---> this.links.add(link.absUrl("href"));
						ok=true;
						msg="no hay tags de exclusiones, include=" + include + " -> " + href ;
					}
				}else {
					/* no hay tags exclusiones ni de inclusiones */
					//---> this.links.add(href);
					ok=true;
				}

			}else {
				/* 2/1  ->  hay exclusiones 
				 * 2/1/1 -> determinar si hay que excluir con getTagsExclude() true |false 
				 * 2/1/2 -> determinar si es candidata a incluir getTagsInclude() true |false
				 * 2/1/3 -> Si -> exclude==false && include==true -> Añadir
				 * */

				exclude = false;
				for(String p: params.getTagsExclude()) {
					if(href.endsWith(p)|| href.contains(p)) {
						//System.out.println("Excluir: " + link.absUrl("href") + " ---> tag:" +  p );
						exclude=true;
					}
				}

				if(include==false && exclude==false) {
					//---> this.links.add(link.absUrl("href"));
					ok=true;
				}else {
					include=false;
					for(String i: params.getTagsInclude()) {
						if(href.endsWith(i)|| href.contains(i)) {
							include=true;
						}
					}
					if(include && exclude==false) {
						//---> this.links.add(link.absUrl("href"));
						ok=true;
					}
				}

				/*
				if(include) {
					include=false;
					for(String i: params.getTagsInclude()) {
						if(link.absUrl("href").endsWith(i)|| link.absUrl("href").contains(i)) {
							include=true;
						}
					}
				}
				//si no tenemos nada en includes --> include=false
				if(exclude==false && include) {
					this.links.add(link.absUrl("href"));
					//System.out.println("link add:" + this.pagesToVisit.size() + " --> " + link.absUrl("href"));
				}else if(include==false) {
					this.links.add(link.absUrl("href"));
				}
				 */
			}
			//End Exclusiones mediante tags
		}
		/*eliminar duplicados de la lista */
		this.links = links.stream().distinct().collect(Collectors.toList());
		if(ok) {
			//System.out.println(msg);
		}
		return ok;
	}
	
	
	public boolean procesaLink(Element link) {
		boolean ok=false;
		String msg="";
		String href = link.absUrl("href");


		if(href.contains(domain)) {
			//Exclusiones mediante tags
			boolean exclude=params.getExclude();
			boolean include=params.getInclude();

			if(exclude==false) {
				/* no hay tags de exclusiones, comprobamos si hay filtro de tags para inclusiones*/
				msg="no hay tags de exclusiones, paso-1=" +link.absUrl("href");
				if(include) {
					include=false;
					for(String i: params.getTagsInclude()) {
						if(href.endsWith(i)|| href.contains(i)) {
							include=true;

						}
					}
					if(include) {
						//---> this.links.add(link.absUrl("href"));
						ok=true;
						msg="no hay tags de exclusiones, include=" + include + " -> " +link.absUrl("href");
					}
				}else {
					/* no hay tags exclusiones ni de inclusiones */
					//---> this.links.add(href);
					ok=true;
				}

			}else {
				/* 2/1  ->  hay exclusiones 
				 * 2/1/1 -> determinar si hay que excluir con getTagsExclude() true |false 
				 * 2/1/2 -> determinar si es candidata a incluir getTagsInclude() true |false
				 * 2/1/3 -> Si -> exclude==false && include==true -> Añadir
				 * */

				exclude = false;
				for(String p: params.getTagsExclude()) {
					if(link.absUrl("href").endsWith(p)|| link.absUrl("href").contains(p)) {
						//System.out.println("Excluir: " + link.absUrl("href") + " ---> tag:" +  p );
						exclude=true;
					}
				}

				if(include==false && exclude==false) {
					//---> this.links.add(link.absUrl("href"));
					ok=true;
				}else {
					include=false;
					for(String i: params.getTagsInclude()) {
						if(link.absUrl("href").endsWith(i)|| link.absUrl("href").contains(i)) {
							include=true;
						}
					}
					if(include && exclude==false) {
						//---> this.links.add(link.absUrl("href"));
						ok=true;
					}
				}

				/*
				if(include) {
					include=false;
					for(String i: params.getTagsInclude()) {
						if(link.absUrl("href").endsWith(i)|| link.absUrl("href").contains(i)) {
							include=true;
						}
					}
				}
				//si no tenemos nada en includes --> include=false
				if(exclude==false && include) {
					this.links.add(link.absUrl("href"));
					//System.out.println("link add:" + this.pagesToVisit.size() + " --> " + link.absUrl("href"));
				}else if(include==false) {
					this.links.add(link.absUrl("href"));
				}
				 */
			}
			//End Exclusiones mediante tags
		}
		/*eliminar duplicados de la lista */
		this.links = links.stream().distinct().collect(Collectors.toList());
		if(ok) {
			//System.out.println(msg);
		}
		return ok;
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

	public boolean crawl_param_Entrades(String url) {
		Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
		try {
			Document htmlDocument = connection.get();
			if(connection.response().statusCode() == 200) {
				System.out.println("\n**Visiting --> " + "-->"+ url);
			}
			if(!connection.response().contentType().contains("text/html")) { return false;}
			Elements linksOnPage = htmlDocument.select("a[href]");
			for(Element link : linksOnPage)  {
				boolean explora = false;
				String href=link.absUrl("href");
				//String ext = href.substring(href.lastIndexOf('.')+1,href.length());
				//int returnTipo = isTipo(ext, Tipos);
				/* verificar si la url ha de ser incluida
				boolean include=false;
				for(String p: params.getTagsInclude()) {
					if(href.endsWith(p)|| href.contains(p)) {
						//System.out.println("INCLUDE:" + p);
						include = true;
					}

				}
				 */

				//if (href.contains("https://" + domain) && (returnTipo==-1 ) && include) {
				//if (href.contains("https://" + domain) && (returnTipo==-1 )) {
				//
				if(href.indexOf("https://" + domain)==0){
					explora = procesaLink(link);
				}

				//}
				if(explora) {
					post ++;
					System.out.println("\n" + post + "--> explora=" + link.absUrl("href") +  " con:" + params.getEntrada());
					Elements el = htmlDocument.select(params.getEntrada());
					for(Element e : el)  {	
						//System.out.println("Element:" + e.html());
						//log.w(Level.INFO, e.html());
						post ++;
						String postLink = null;
						String postTitle = null;
						/*  ok */
						//Element titlePost = e.select("h2.title").first(); alpinista samurai
						// Element titlePost = e.select("h3.post-title").first(); //urto
						/*  ok */

						Elements links = e.select("a[href]");
						for (Element link2 : links) {
							String urlTmp = link2.attr("href");
							String ext = urlTmp.substring(urlTmp.lastIndexOf('.')+1,urlTmp.length());
							int returnTipo = isTipo(ext, Tipos);
							if (urlTmp.contains("https://" + domain) &&  (returnTipo==-1 )) {
								//addLink(link2);
								//System.out.println("add: " + link2.absUrl("href"));
							}

							//System.out.println("text : " + link.text());
							//if(link2.text().equals(titlePost.text())) {
							postLink = link2.attr("href");
							postTitle = link2.text();
							// }
						} 

						String blog_and_title 	= htmlDocument.title();

						//Element postLink=e.select("h2.title > a[href]").first();

						//String titlePost= e.absUrl("h2");
						String text = htmlDocument.text();		
						String description = getMetaTag(htmlDocument, "description");

						/**/
						System.out.println(post + " --> BlogTitle: --> " + blog_and_title );
						System.out.println(post + " --> PostLink: --> " + postLink );
						//System.out.println("Pedir en parámetros ---> DESCRIPTION:" + description );			
						System.out.println(post + " --> PostTitle --> : " + postTitle);

					}
				}


			}


			//Elements el = htmlDocument.select("div[class~=post-[0-9]+]");


			//Elements linksOnPage = htmlDocument.getElementsByClass("post-[0-9]+");
			//Element post= htmlDocument.getElementById("post-[0-9]+");
			//System.out.println("POST="+ post.html());





		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.links.add(url);
	}

	public boolean crawloriginal(String url) {
		Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
		try {
			Document htmlDocument = connection.get();
			if(connection.response().statusCode() == 200) {
				System.out.println("\n**Visiting --> " + "-->"+ url +" doamin:" + domain);
			}
			if(!connection.response().contentType().contains("text/html")) { return false;}
			this.links.add(url);


			Elements linksOnPage = htmlDocument.select("a[href]");
			//Pattern pat = Pattern.compile("#post-\\d{3}");
			//Elements linksOnPage = htmlDocument.select("div[id~=^post-[0-9]+$]");
			//Elements linksOnPage = htmlDocument.select("div[id~=post-[0-9]+]");




			if (url.equals("https://" + domain)) {
				System.out.println("URL:"  + url + " ==== " + "https://" + domain);
				for(Element link : linksOnPage)  {	
					procesaLink(link);
				}
			}else {
				//Elements page = linksOnPage.select("div[id~=^post-[0-9]+$]");
				//Elements page = linksOnPage.select("div#^post-[0-9]+$");
				Element page = htmlDocument.getElementById("^post-[0-9]+$");

				//for(Element link : page) {
				if(page!=null) {
					System.out.println("page1:" + page.html());
				}

				//}


				//Elements page2 = linksOnPage.select("div[id~=post-[0-9]+]");
				Element page2 = htmlDocument.getElementById("post-[0-9]+");
				if(page2!=null) {
					System.out.println("page2:" + page2.html());
				}

				//}


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
				/**/
				System.out.println("TITLE:" + blog_and_title );
				System.out.println("LINK:" + postLink );
				System.out.println("Pedir en parámetros ---> DESCRIPTION:" + description );			
				System.out.println("Titulo: " + titlePost);

				boolean imagenes = false;
				boolean isValid=false;
				boolean thumb=false;
				String thumbnail = null;
				for(Element link : linksOnPage)  {	
					String href=link.absUrl("href");
					//this.links.add(href);
					String ext = href.substring(href.lastIndexOf('.')+1,href.length());
					int returnTipo = isTipo(ext, Tipos);
					if(returnTipo!=-1 ){
						//System.out.println("AHREF: " + href);
						isValid = isValidLink(href);
						imagenes = true;
						//this.linksImg.put(orden, href);
						Elements media = link.select("[src]");
						for (Element src : media) {
							if (src.tagName().equals("img")){
								//System.err.println("SRC:"  +  src.tagName() + "-" + src.attr("abs:src"));
								//this.linksImg.add(src.attr("abs:src"));
								thumb=true;
								thumbnail = src.attr("abs:src");
								//this.linksImgThumb.put(orden , src.attr("abs:src"));
							} else{
								System.err.println("NO ES IMG: "  +  src.tagName() +"-" +  src.attr("abs:src"));
							}

						}
						if (imagenes && thumb && isValid) {
							orden ++;
							this.linkTitlePost.put(orden,titlePost);
							this.LinkPost.put(orden, url);
							this.linksImg.put(orden, href);
							this.linksImgThumb.put(orden , thumbnail);
						}
					}

					procesaLink(link);
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

	/**
	 * @param link
	 * @return
	 */
	public boolean isValidLink(String link) {
		boolean validLink = false;
		try {
			Response response = Jsoup.connect(link)
					.userAgent("Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2")
					.timeout(0).ignoreHttpErrors(true)
					.ignoreContentType(true)
					.execute();

			if(response.statusCode() == 200) {
				validLink = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return validLink ;
	}




	/**
	 * @param link

	public void addLink(Element link) {
		if(link.absUrl("href").contains(domain)) {
			//Exclusiones mediante tags
			boolean exclude=params.getExclude();
			boolean include=params.getInclude();

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
		this.links = links.stream().distinct().collect(Collectors.toList());
	}
	 */

	/**
	 * @param url
	 * @return
	 * @throws URISyntaxException
	 */
	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) +"/" : domain +"/";
	}


	/**
	 * @param document
	 * @param attr
	 * @return
	 */
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
