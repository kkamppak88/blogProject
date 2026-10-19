package service;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MemberDAO;
import model.MemberDTO;

public class MemberInsert implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//get방식으로 요청이 들어오면 다음 코딩을 실행 안하게 한다
		if("get".equalsIgnoreCase(request.getMethod())) { //request.getMethod() 요청방식을 알고 싶을 때 사용
			return true;
		}
		
		request.setCharacterEncoding("utf-8");
		
		String id = request.getParameter("id");
		String pwd = request.getParameter("pwd"); 
		String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
		String nickname = request.getParameter("nickname");
		String email = request.getParameter("email");
		
		MemberDTO dto = new MemberDTO();
		dto.setEmail(email);
		dto.setId(id);
		dto.setNickname(nickname);
		dto.setPwd(hashedPwd);
		
		MemberDAO dao = new MemberDAO();
		dao.memberinsert(dto);
		
		response.sendRedirect(request.getContextPath()+ "/member/login");
		
		return false; //위에서 리다이렉트하면 false를 리턴하고 포워딩이 필요하면 true를 리턴한다
		
	}

}
