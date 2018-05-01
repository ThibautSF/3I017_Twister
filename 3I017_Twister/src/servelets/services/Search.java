package servelets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import services.classes.InvalidKeyException;

/**
 * @author Thibaut SIMON-FINE
 * 
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Search() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String query = request.getParameter("q");
		
		PrintWriter out = response.getWriter();
		
		try {
			JSONObject json = services.Services.search(key, query);
			
			response.setContentType("text/plain");
			out.print(json.toString());
		} catch (JSONException | SQLException e) {
			services.classes.ErrorPrint.printError(e, "Add Message");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			response.setContentType("text/plain");
			out.print(e.getMessage());
		} finally {
			out.close();
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
