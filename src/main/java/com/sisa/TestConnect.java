package com.sisa;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestConnect
 */
@WebServlet("/TestConnect")
public class TestConnect extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String database = "u710927107_Npmvs";
	private static String user = "u710927107_3TuS1";
	private static String pass = "4UGs9HrTk5";
	private Set<String> imagesVisited;
	
	Connection conn;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestConnect() {
        super();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://191.96.63.1:3306/" + database,user ,pass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String value = request.getParameter("database");
		String domain = request.getParameter("domain");
		Set<String> paginasPinged  = pagesPingedInDB(domain );
		response.getWriter().append("Served at: ").append(request.getContextPath()).append(value);
		
		
		System.out.println("Topos --> pagesPingedInDB.size(): " + paginasPinged.size());
		
		
		for( Iterator it2 = paginasPinged.iterator(); it2.hasNext(); ) { 
			String x = it2.next().toString();
			System.out.println("pingada:" + x);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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
		    //try { rs.close(); } catch (Exception e) { /* Ignored */ }
		    //try { conn.close(); } catch (Exception e) { /* Ignored */ }
		}
		
		return imagesVisited;
		
	}
	
	

}
