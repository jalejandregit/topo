package com.sisa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
//http://www.w3api.com/Java/DriverManager/getConnection/
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;


/* Campos BD
post_content_filtered	-->	longtext	--> https://4.bp.blogspot.com/-iNg-Cz-qjfg/VeV8hqq6xNI/AAAAAAAACTY/RgKDHKgNAqw/s1600/11845015_10153486550517332_7286356515993682321_o.jpg
guid 					-->	varchar(255)	--> parquesdeaventura.blogspot.com
pinged 					-->	mediumtext	--> https://parquesdeaventura.blogspot.com/2015/09/no-todo-es-trabajar-iii-escalada-de-la.html
 */

public class SqlRemote {
	private Set<String> imagesVisited;

	//private static String database = "vertikal_wp2";
	private static String database = "u710927107_Npmvs";
	//private static String user = "vertikal_wp2"; 
	private static String user = "u710927107_3TuS1";
	//private static String pass = "D.7yBaAXZ7VEPfcLl8159";
	//private static String pass = "D.7yBaAXZ7VEPfcLl8159";
	private static String pass = "4UGs9HrTk5";
	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
	private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");
	Connection conn;
	TreeMap<Integer,String>[] listaPosts;

	public SqlRemote() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// conn = DriverManager.getConnection("jdbc:mysql://91.134.184.222:3306/" + database,user ,pass);
			//conn = DriverManager.getConnection("jdbc:mysql://sql898.main-hosting.eu:3306/" + database,user ,pass);
			conn = DriverManager.getConnection("jdbc:mysql://191.96.63.1:3306/" + database,user ,pass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
/*
 * 
 */

	}
	
	
	public Set<String> pagesPingedInDB(String domain) {
		ResultSet rs = null;
		imagesVisited = new HashSet<String>();
		String query = "SELECT * FROM u710927107_Npmvs.wp_posts WHERE guid ='" + domain + "'";
		System.out.println("SqlRemote:" + query);
		try {
			Statement st = conn.createStatement();
			rs = st.executeQuery( query );
			while (rs.next()) {
				//System.out.println("SqlRemote urlVista:" + rs.getString("pinged"));
				imagesVisited.add(rs.getString("pinged"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
		    try { rs.close(); } catch (Exception e) { /* Ignored */ }
		    try { conn.close(); } catch (Exception e) { /* Ignored */ }
		}
		
		return imagesVisited;
		
	}

	/*post_content_filtered = urtohijodelia.blogspot.com*/ 

	public void select(String query) {
		ResultSet rs = null;
		try {
			Statement st = conn.createStatement();
			System.out.println("Buscando en la bd por:" + query);
			rs = st.executeQuery( query );
			while (rs.next()) {
				String ID = rs.getString("ID");
				String post_title = rs.getString("post_title");
				String post_name = rs.getString("post_name");
				String post_type = rs.getString("post_type");
				if(Objects.equals(post_type, "post")) {
					System.out.println("ID:" + ID + "-" +  post_title);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    try { rs.close(); } catch (Exception e) { /* Ignored */ }
		    try { conn.close(); } catch (Exception e) { /* Ignored */ }
		}
	}

	public void into(String domain, List<TreeMap<Integer,String>> listaPosts) {
		PreparedStatement preparedStatement_wp_posts = null;
		/*
ID
post_author
post_date
post_date_gmt
post_content
post_title
post_excerpt
post_status
comment_status
ping_status
post_password
post_name
to_ping
pinged
post_modified
post_modified_gmt
post_content_filtered
post_parent
guid
menu_order
post_type
post_mime_type
comment_count
ID	post_author	post_date	post_date_gmt	post_content	post_title	post_excerpt	post_status	comment_status	ping_status	post_password	post_name	to_ping	pinged	post_modified	post_modified_gmt	post_content_filtered	post_parent	guid	menu_order	post_type	post_mime_type	comment_count

		 */
		PreparedStatement preparedStatement_wp_postmeta = null; 


		try {
			
			int sizeLinkPost = listaPosts.get(0).size();
			Set<Map.Entry<Integer, String> > entryPost 			= listaPosts.get(0).entrySet();
			Set<Map.Entry<Integer, String> > entryTitlePost 	= listaPosts.get(1).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkContent	= listaPosts.get(2).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkImg 		= listaPosts.get(3).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkImgThumb = listaPosts.get(4).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkLatitud  = listaPosts.get(5).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkLongitud = listaPosts.get(6).entrySet();		
			Set<Map.Entry<Integer, String> > entrylinkAutor		 = listaPosts.get(7).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkAutorUrl	 = listaPosts.get(8).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkOrientacio = listaPosts.get(9).entrySet();
			Set<Map.Entry<Integer, String> > entrylinkLocation	 = listaPosts.get(10).entrySet();
			
			/*
			listaPosts.add(linkPost);
			listaPosts.add(linkTitlePost);
			listaPosts.add(linkContent);
			listaPosts.add(linksImg);
			listaPosts.add(linksImgThumb);
			listaPosts.add(linkLat);
			listaPosts.add(linkLong);			
			listaPosts.add(linkAutor);
			listaPosts.add(linkAutorUrl);
			listaPosts.add(linkOrientacio);
			listaPosts.add(linkLocation);
			 */
			
			// Convert entrySet to Array using toArray method
			Map.Entry<Integer, String>[] arrayPost 			= entryPost.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arrayTitlePost 	= entryTitlePost.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkContent 	= entrylinkContent.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkImg 		= entrylinkImg.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkImgThumb	= entrylinkImgThumb.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkLatitud	= entrylinkLatitud.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkLongitud	= entrylinkLongitud.toArray( new Map.Entry[entryPost.size()]);			
			Map.Entry<Integer, String>[] arraylinkAutor			= entrylinkAutor.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkAutorUrl		= entrylinkAutorUrl.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkOrientacio	= entrylinkOrientacio.toArray( new Map.Entry[entryPost.size()]);
			Map.Entry<Integer, String>[] arraylinkLocation		= entrylinkLocation.toArray( new Map.Entry[entryPost.size()]);
			
			preparedStatement_wp_posts = conn.prepareStatement("INSERT INTO wp_posts VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			preparedStatement_wp_postmeta = conn.prepareStatement("INSERT INTO wp_postmeta VALUES (?,?,?,?)");
			


			for (int i = 0; i < sizeLinkPost; i++)    {
				// Get Key using index and print
				String url 			= arrayPost[i].getValue();
				String contenido 	= arraylinkContent[i].getValue();
				String title 		= arrayTitlePost[i].getValue();
				String image		= arraylinkImg[i].getValue();
				String thumb		= arraylinkImgThumb[i].getValue();
				String lat			= arraylinkLatitud[i].getValue();
				String lon			= arraylinkLongitud[i].getValue();
				String autor 		= arraylinkAutor[i].getValue();
				String autorUrl		= arraylinkAutorUrl[i].getValue();
				String orientacio 	= arraylinkOrientacio[i].getValue();
				String location 	= arraylinkLocation[i].getValue();
				
				preparedStatement_wp_posts.setBigDecimal(1, null);
				preparedStatement_wp_posts.setInt(2, 1);
				preparedStatement_wp_posts.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
				preparedStatement_wp_posts.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
				preparedStatement_wp_posts.setString(5, contenido);
				preparedStatement_wp_posts.setString(6, title);
				preparedStatement_wp_posts.setString(7, "excerpt");
				preparedStatement_wp_posts.setString(8, "draft");
				preparedStatement_wp_posts.setString(9, "closed");
				preparedStatement_wp_posts.setString(10, "closed");
				preparedStatement_wp_posts.setString(11, "");
				preparedStatement_wp_posts.setString(12, convertToSlug(title));
				
				preparedStatement_wp_posts.setString(13, "");
				preparedStatement_wp_posts.setString(14, url); //pinged --> https://urtohijodelia.blogspot.com/2016/05/un-palo-dificil-al-forat-del-vent.html
				preparedStatement_wp_posts.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
				preparedStatement_wp_posts.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
				preparedStatement_wp_posts.setString(17, image);//post_content_filtered --> https://4.bp.blogspot.com/-1FlNoFrLzLo/VyXp1GGZvEI/AAAAAAAADvk/Zx3VIuQwbx4diGiAgsQHYh-jk8YSm1hpwCLcB/s1600/0014.jpg
				preparedStatement_wp_posts.setInt(18, 0);
				preparedStatement_wp_posts.setString(19, domain);//guid -->urtohijodelia.blogspot.com
				preparedStatement_wp_posts.setInt(20, 0);
				preparedStatement_wp_posts.setString(21, "post");
				preparedStatement_wp_posts.setString(22, "");
				preparedStatement_wp_posts.setInt(23, 0);
				
				preparedStatement_wp_posts.executeUpdate();
				long key = 0;
				ResultSet rs = preparedStatement_wp_posts.getGeneratedKeys();

				if (rs.next()) {
				    key = rs.getLong(1);
				}
				
				System.out.println("Sql return key=" + key);
				
				/**************** wp_post_meta **************/
			/*				
				_autor			field_6332e9bef19fb
				_link_post		field_630485e99c971
				_urlImagen		field_630367a79eff5
				__thumbnail_id	field_630367ca9eff6
				_orientacio		field_6331d0e326d66
				_latitud		field_6332ddd40bc69
				_longitud		field_6332de0d0bc6a
				_location		field_6332fc162b233
						 */
				/***** autor ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "autor");
				preparedStatement_wp_postmeta.setString(4, autor);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_autor");
				preparedStatement_wp_postmeta.setString(4, "field_6332e9bef19fb");
				preparedStatement_wp_postmeta.addBatch();
				
				
				/***** autorUrl	 ****/			
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "autor_url");
				preparedStatement_wp_postmeta.setString(4, autorUrl	);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_autor_url");
				preparedStatement_wp_postmeta.setString(4, "field_637b9f18752d4");
				preparedStatement_wp_postmeta.addBatch();
				
				/***** link_post ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "link_post");
				preparedStatement_wp_postmeta.setString(4, url);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_link_post");
				preparedStatement_wp_postmeta.setString(4, "field_630485e99c971");
				preparedStatement_wp_postmeta.addBatch();
				
				/******** url_imagen ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "urlImagen");
				preparedStatement_wp_postmeta.setString(4, image);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_urlImage");
				preparedStatement_wp_postmeta.setString(4, "field_630367a79eff5");
				preparedStatement_wp_postmeta.addBatch();
				
				/******** _thumbnail_id ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_thumbnail_id");
				preparedStatement_wp_postmeta.setString(4, thumb);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "__thumbnail_id");
				preparedStatement_wp_postmeta.setString(4, "field_630367ca9eff6");
				preparedStatement_wp_postmeta.addBatch();
				
				/***** orientacio ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "orientacio");
				preparedStatement_wp_postmeta.setString(4, orientacio);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_orientacio");
				preparedStatement_wp_postmeta.setString(4, "field_6331d0e326d66");
				preparedStatement_wp_postmeta.addBatch();
				
				/***** latitud ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "latitud");
				preparedStatement_wp_postmeta.setString(4, lat);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_latitud");
				preparedStatement_wp_postmeta.setString(4, "field_6332ddd40bc69");
				preparedStatement_wp_postmeta.addBatch();
				
				/***** longitud ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "longitud");
				preparedStatement_wp_postmeta.setString(4, lon);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_longitud");
				preparedStatement_wp_postmeta.setString(4, "field_6332de0d0bc6a");
				preparedStatement_wp_postmeta.addBatch();
				
				/***** location ****/
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "location");
				preparedStatement_wp_postmeta.setString(4, location);
				preparedStatement_wp_postmeta.addBatch();
				preparedStatement_wp_postmeta.setBigDecimal(1, null);
				preparedStatement_wp_postmeta.setLong(2, key);
				preparedStatement_wp_postmeta.setString(3, "_location");
				preparedStatement_wp_postmeta.setString(4, "field_6332fc162b233");
				preparedStatement_wp_postmeta.addBatch();
				
				
				 int[] updateCounts = preparedStatement_wp_postmeta.executeBatch();
				 
				//int[] updateCounts  = preparedStatement_wp_postmeta.executeUpdate();
				
				
				
				System.out.println("\n" + i + "-"  	+ arrayPost[i].getKey() + " ->" + arrayPost[i].getValue());
				System.out.println( i + "-"  	+ arrayTitlePost[i].getKey() + " ->" +  arrayTitlePost[i].getValue() );
				System.out.println(i + "-"  	+ arraylinkContent[i].getKey() + " ->" + arraylinkContent[i].getValue());
				System.out.println( i + "-"  	+ arraylinkImg[i].getKey() + " ->" + arraylinkImg[i].getValue());
				System.out.println( i + "-"  	+ arraylinkImgThumb[i].getKey() + " ->" + arraylinkImgThumb[i].getValue());
			}
			
			conn.setAutoCommit(false);
		    conn.close();
			
			//PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)		
			/*
	        stmt = conn.prepareStatement("INSERT INTO libros VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);			
			//stmt = conn.prepareStatement("INSERT INTO libros VALUES (?,?,?,?,?)");
			stmt.setString(1,"sISBN");
			stmt.setInt(2,22);
			stmt.setString(3,"sTitulo");
			stmt.setString(4,"sDescripcion");
			stmt.setString(5,"sCategoria");
			*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		   
		if (preparedStatement_wp_posts != null) {
			try {
				preparedStatement_wp_posts.close();
			} catch (SQLException e) { /* Ignored */}
		}
		if (preparedStatement_wp_postmeta != null) {
			try {
				preparedStatement_wp_postmeta.close();
			} catch (SQLException e) { /* Ignored */}
		}

	}
	

	
	public static String convertToSlug(String input)  {
		String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		slug = EDGESDHASHES.matcher(slug).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

}
