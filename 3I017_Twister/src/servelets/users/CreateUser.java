package servelets.users;

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

import services.classes.NumberValueException;

/**
 * @author Thibaut SIMON-FINE
 * 
 * Servlet implementation class CreateUser
 */
@WebServlet("/CrateUser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateUser() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String pwd = request.getParameter("pwd");
		String nom = (request.getParameter("nom")!=null && request.getParameter("nom")!="") ? request.getParameter("nom") : "" ;
		String prenom = (request.getParameter("prenom")!=null && request.getParameter("prenom")!="") ? request.getParameter("prenom") : "" ;
		String ageStr = (request.getParameter("age")!=null && request.getParameter("age")!="") ? request.getParameter("age") : "-1" ;
		
		PrintWriter out = response.getWriter();
		
		try{
			int age = Integer.parseInt(ageStr);
			
			JSONObject json = services.Users.createUser(login, pwd, nom, prenom, age);
			
			response.setContentType("text/plain");
			out.print(json.toString());
		} catch (NumberFormatException e) {
			response.setContentType("text/plain");
			out.print("Vous devez entrer un entier positif !");
		} catch (JSONException | SQLException e) {
			services.classes.ErrorPrint.printError(e, "Create User");
			e.printStackTrace();
		} catch (NumberValueException e) {
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
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	
}
