package service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BoardDAO;
import model.BoardDTO;
import model.ReplyDTO;
import model.ReplyDAO;

public class BoardView implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		int bno = Integer.parseInt(request.getParameter("bno"));
		
		BoardDAO dao = new BoardDAO();
		dao.increViewCnt(bno);
		BoardDTO dto = dao.selectOne(bno);
		List<ReplyDTO> list = new ReplyDAO().selectList(bno);
		
		request.setAttribute("view", dto);	
		request.setAttribute("replylist", list);
		
		return true;
	}

}
