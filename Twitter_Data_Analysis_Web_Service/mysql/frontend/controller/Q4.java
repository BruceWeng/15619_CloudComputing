/*
 * 15619 team project
 * Team: sudoCloud
 * Member1: Huacong Cai - hcai
 * Member2: Qinyu Tong - qtong
 * Member3: Hansi Mou - hmou
 * 
 */

package sudoCloud.front.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@SuppressWarnings("serial")
@WebServlet("/q4")

/**
 * Handle q4 requests, return hashtags and their relevant tweet ids of a given data,
 * location and popularity range (m, n)
 */
public class Q4 extends HttpServlet {
	
	private String teamId = new String("sudoCloud");
    private String awsId1 = new String("7760-2621-3295");
    private String awsId2 = new String("2657-3003-6308");
    private String awsId3 = new String("2901-7055-0317");
    private String selectSql = "select resp from q4 where time=? and location=? and rank>=? and rank <=? collate utf8mb4_bin";
    
    /**
	 * Handle q4 requests, return hashtags and their relevant tweet ids of a given data,
	 * location and popularity range (m, n)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
    protected void doGet(HttpServletRequest request, 
    					HttpServletResponse response) 
    					throws ServletException, IOException{
    	
        response.setCharacterEncoding("utf-8"); //encoding
        
        String date = request.getParameter("date");
        String location = request.getParameter("location");
        String m = request.getParameter("m");
        String n = request.getParameter("n");
        
        PrintWriter writer = response.getWriter();
        writer.println(teamId + "," + awsId1 + ","
				+ awsId2 + "," + awsId3);
        
        //get result from database
    	Connection connection = null;
    	PreparedStatement pstmt = null;
    	ResultSet selectRes = null;
		try {
			//use connection pool
			connection = getConnection();
			
			//use prepare statement
			pstmt = connection.prepareStatement(selectSql);
			pstmt.setString(1, date);
			pstmt.setString(2, location);
			pstmt.setString(3, m);
			pstmt.setString(4, n);
			
			selectRes = pstmt.executeQuery();
			while (selectRes.next())
				writer.println(selectRes.getString(1));
			
		} catch (Exception e) {
			//do nothing
		} finally {
			try {
				if (selectRes != null) selectRes.close();
				if (pstmt != null) pstmt.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				//do nothing
			}
		}

        writer.close();
    }
    
    /**
     * Get a connection from connection pool provided by wildfly and return it
     * @return An available connection gotten from connection pool
     */
	private Connection getConnection() {
		Connection connection = null;
		try {
			InitialContext context = new InitialContext();
			//look up data source
			DataSource dataSource = (DataSource) context
				.lookup("java:jboss/datasources/TwitterDS");
			//get connection from the connection pool of wildfly
			connection = dataSource.getConnection();
		} catch (NamingException e) {
			//do nothing
		} 
		catch (SQLException e) {
			//do nothing
		}
		return connection;
	}
}