package service;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MemberDAO;
import model.MemberDTO;

public class LoginCommand implements Command {

	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if("GET".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		
		request.setCharacterEncoding("utf-8");
		
		String id = request.getParameter("id");
		String pwd = request.getParameter("pwd");
		
		MemberDAO dao = new MemberDAO();
		MemberDTO dto = dao.login(id);
		if(dto != null && BCrypt.checkpw(pwd, dto.getPwd())) {
			HttpSession session = request.getSession(); //세션 만들기
			session.setAttribute("loginMember", dto);
								//   기억장소   ,  값
			response.sendRedirect(request.getContextPath()+"/main");
			return false;
		}else {
			request.setAttribute("loginFail", "아이디 또는 비밀번호를 확인하세요");
			//처리된 값을 jsp로 출력을 하려면 request, session 속성에 저장해서 포워딩한다.
			//session : 한 번 로그인하면 다른 페이지로 이동해도 로그인 유지
			return true;
		}
	}
}