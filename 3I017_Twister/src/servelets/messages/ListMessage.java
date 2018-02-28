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
 * Servlet implementation class ListMessage
 */
@WebServlet("/ListMessage")
public class ListMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListMessage() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String type = (request.getParameter("for")!=null && request.getParameter("for")!="") ? request.getParameter("for") : "all" ;
		int nb_message;
		int id_user;
		
		if(request.getParameter("nb")!=null && request.getParameter("nb")!=""){
			try{
				nb_message = Integer.parseInt(request.getParameter("nb"));
			} catch (NumberFormatException e){
				nb_message = (type.equals("all")) ? services.Messages.DEFAULT_MAX_MESSAGE : 0;
			}
		} else {
			nb_message = (type.equals("all")) ? services.Messages.DEFAULT_MAX_MESSAGE : 0;
		}
		
		if(request.getParameter("user")!=null && request.getParameter("user")!="")
			id_user = Integer.parseInt(request.getParameter("user"));
		else
			id_user = -1;
		
		PrintWriter out = response.getWriter();
		
		try {
			JSONObject json;
			
			if (id_user!=-1)
				json = services.Messages.listMessage(type, key, nb_message, id_user);
			else
				json = services.Messages.listMessage(type, key, nb_message);
			
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
