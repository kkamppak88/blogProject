package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Command;
import service.MainCommand;

import java.io.IOException;

@WebServlet("/main")
public class MainController extends HttpServlet {
    private static final long serialVersionUID = 1L;
   
    public MainController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doAction(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doAction(request, response);
    }
    
    protected void doAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
        Command command = new MainCommand();
        boolean needForward = command.doCommand(request, response);
        
        if(needForward) {
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
        }
    }
}