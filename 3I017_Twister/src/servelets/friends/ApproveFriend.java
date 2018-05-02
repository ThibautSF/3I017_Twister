package servelets.friends;

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
 * Servlet implementation class ApproveFriend
 */
@WebServlet("/ApproveFriend")
public class ApproveFriend extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApproveFriend() {
		super();
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String action = request.getParameter("action");
		
		PrintWriter out = response.getWriter();
		
		try{
			int id_friend = Integer.parseInt(request.getParameter("friend"));
			
			JSONObject json = services.Friends.approveOrNotFriend(key, id_friend, action);
			
			response.setContentType("text/plain");
			out.print(json.toString());
			
		} catch (JSONException | SQLException e) {
			services.classes.ErrorPrint.printError(e, "Add Friend");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			response.setContentType("text/plain");
			out.print(e.getMessage());
		} catch (NumberFormatException e) {
			response.setContentType("text/plain");
			out.print("Id user doit être un nombre");
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
