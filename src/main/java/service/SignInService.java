package service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class SignInService {

	public String validate(String email, String password, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		PrintWriter out = response.getWriter();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query query = new Query("User").setFilter(new FilterPredicate("email", FilterOperator.EQUAL, email));
		PreparedQuery prepardQuery = datastore.prepare(query);
		Entity user = prepardQuery.asSingleEntity();

		if (email.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {

			return "admin";
		}
		

		if (user != null) {

			if (user.getProperty("password").equals(password)) {

				request.getSession().setAttribute("userId", user.getKey().getName().toString());
				if (user.getProperty("role").equals("faculty"))
					return "faculty";
				else {
					return "student";
				}
			} else {
				response.setStatus(401);
				out.println("<script type=\"text/javascript\">");
				out.println("alert('invalid credentials');");
				out.println("location='login.jsp';");
				out.println("</script>");
			}
		} else {
			response.setStatus(401);
			out.println("<script type=\"text/javascript\">");
			out.println("alert('invalid credentials');");
			out.println("window.location.replace(\"login.jsp\")");
			out.println("</script>");
		}
		
		if(email.length()==0 && password.length()==0) {
			response.setStatus(400);
			out.println("<script type=\"text/javascript\">");
			out.println("alert('Enter userName and Password');");
			out.println("window.location.replace(\"login.jsp\")");
			out.println("</script>");

		}

		return null;
	}

}
