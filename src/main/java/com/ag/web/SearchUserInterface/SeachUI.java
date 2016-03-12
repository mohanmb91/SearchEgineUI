package com.ag.web.SearchUserInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ag.web.Model.keyValue;

/**
 * Servlet implementation class SeachUI
 */
public class SeachUI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SeachUI() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		ServletContext context = request.getSession().getServletContext();
		//context.setAttribute("Results", null);
		request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String searchString =  request.getParameter("SearchText").trim().toLowerCase();
	    List<keyValue> allURLS = new ArrayList<keyValue>();
		Search obj = new Search();
		allURLS = obj.performSearch(searchString);
		for (keyValue eachKeyValue : allURLS) {
			System.out.println(eachKeyValue.key + "=====>" + eachKeyValue.value);
		}
		ServletContext context = request.getSession().getServletContext();
		request.getSession().setAttribute("Results", allURLS);
		/*response.sendRedirect("SeachUI");*/
		request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);

}
}
