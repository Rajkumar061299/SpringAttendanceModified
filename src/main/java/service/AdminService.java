package service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Service
public class AdminService {

	@SuppressWarnings("deprecation")
	public void displayParticularAttendance(Date date, List<String> list, PrintWriter out, HttpServletResponse response) throws IOException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Filter propertyFilter = new FilterPredicate("email", FilterOperator.IN, list);
		Filter propertyFilter1 = new FilterPredicate("date", FilterOperator.EQUAL, date);

		Filter compositeFilter = CompositeFilterOperator.and(propertyFilter, propertyFilter1);
		Query query = new Query("AttendanceDetails").setFilter(compositeFilter);
		PreparedQuery prepardQuery = datastore.prepare(query);
		if(prepardQuery.countEntities()==list.size()) {
			response.setStatus(207);
					
		}
		boolean flag = false;
		for (Entity e : prepardQuery.asIterable()) {
			flag = true;
			out.println("<html><body style = \"background-color:#00ffff\">");
			out.print("<b>Name : </b>" + e.getProperty("name") + "     ");
			out.print("<b>Attendance Status : </b>" + e.getProperty("attendanceStatus"));
			out.print("<br>");
			out.println("</body></html>");

		}
		if(flag==false) {
			response.setStatus(404);
			out.println("<html><body style = \"background-color:#00ffff\">");
			out.println("<b>No data found</b>");
			out.println("</body></html>");
				}
		
		
	}

	public void addUser(String name, String password, String email, String role) {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		String user_id = UUID.randomUUID().toString();
		Entity e = new Entity("User", user_id);
		e.setProperty("name", name);
		e.setProperty("email", email);
		e.setProperty("password", password);
		e.setProperty("role", role);
		datastore.put(e);

	}

	public void updateTheAttendance(String email, Date date, String attendanceStatus, PrintWriter out) {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Filter propertyFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
		Query query = new Query("User").setFilter(propertyFilter);
		PreparedQuery prepardQuery = datastore.prepare(query);
		Entity user = prepardQuery.asSingleEntity();
		if (user != null) {
			String studentId = user.getKey().getName().toString();

			Filter propertyFilter1 = new FilterPredicate("date", FilterOperator.EQUAL, date);

			Query query1 = new Query("AttendanceDetails").setFilter(propertyFilter1).setAncestor(user.getKey());
			PreparedQuery prepardQuery1 = datastore.prepare(query1);

			Entity result = prepardQuery1.asSingleEntity();

			Key k = new KeyFactory.Builder("User", studentId).addChild("AttendanceDetails", result.getKey().getName())
					.getKey();

			Entity e;
			try {
				e = datastore.get(k);
				e.setProperty("name", user.getProperty("name"));
				e.setProperty("date", date);
				e.setProperty("attendanceStatus", attendanceStatus);
				datastore.put(e);

			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}

		}
	}

}
