package servelets.messages;

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
 * Servlet implementation class AddMessage
 */
@WebServlet("/AddMessage")
public class AddMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddMessage() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String content = request.getParameter("content");
		String parent = (request.getParameter("parent")!=null && request.getParameter("parent")!="") ? request.getParameter("parent") : "" ;
		
		PrintWriter out = response.getWriter();
		
		try {
			JSONObject json;
			if(parent!=""){
				json = services.Messages.addComment(key, content, parent);
			} else
				json = services.Messages.addMessage(key, content);
			
			response.setContentType("text/plain");
			out.print(json.toString());
		} catch (JSONException | SQLException e) {
			services.classes.ErrorPrint.printError(e, "Add Message");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			response.setContentType("text/plain");
			out.print(e.getMessage());
			//TODO déconnexion !
		} finally {
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
