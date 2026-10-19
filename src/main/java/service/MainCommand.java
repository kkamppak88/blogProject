package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BoardDTO;
import model.MainDAO;

public class MainCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		
		List<BoardDTO> newList = new MainDAO().MainNew();
		request.setAttribute("mainnew", newList);
		
		List<BoardDTO> popList = new MainDAO().MainPopular();
		request.setAttribute("mainpop", popList);
		
		return true;
	}

}
