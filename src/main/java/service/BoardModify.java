package service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDTO;

public class BoardModify implements Command {

	private static final String UPLOAD_DIR="C:/upload";
	
	@Override
	public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		HttpSession session =  request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		if(loginMember == null) {
			response.sendRedirect(request.getContextPath()+"/member/login");
			return false;
		}
		
		int bno = Integer.parseInt(request.getParameter("bno"));
		
		BoardDAO dao = new BoardDAO();
		BoardDTO board = dao.selectOne(bno);
		
		if(board == null) {
			response.sendRedirect(request.getContextPath()+"/blog/list");
			return false;
		}
		
		if(!board.getWriter().equals(loginMember.getId())) {
			request.setAttribute("board", board);
			request.setAttribute("errMsg", "본인이 작성한 글만 수정할 수 있습니다.");
			return true;
		}
		//get방식으로 요청이 들어오면 수정폼에 가존 값 채워서 보여주기
		if("GET".equalsIgnoreCase(request.getMethod())) {
			request.setAttribute("board", board);
			return true;
		}
		//post 방식으로 요청이 들어오면 실제 수정 처리
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String removeImg = request.getParameter("removeImg"); //true면 이미지 제거 요청
	
		String oldImgPath = board.getImgPath();
		String finalImgPath = oldImgPath; //기본값 : 기존 이미지 유지
		
		Part filePart = request.getPart("imgFile");
		if(filePart != null && filePart.getSize() > 0) { //새로운 이미지가 첨부
			deleteOldFile(oldImgPath); //예전 이미지 삭제
			finalImgPath = saveFile(filePart);
		}else if("true".equals(removeImg)){ //새 이미지 등록 없이 제거 버튼만 누른 경우 -> 
			deleteOldFile(oldImgPath);
			finalImgPath = null;
		}
		//둘 다 아니면 finalImgPath랑 finalImgPath 그대로 변경없음
		BoardDTO updateDTO = new BoardDTO();
		updateDTO.setBno(bno);
		updateDTO.setTitle(title);
		updateDTO.setContent(content);
		updateDTO.setWriter(loginMember.getId());
		updateDTO.setImgPath(finalImgPath);
		
		dao.update(updateDTO);
		response.sendRedirect(request.getContextPath()+"/blog/list");
		return false;
	}

	public String saveFile(Part filePart) throws IOException {
		String oriFileName = filePart.getSubmittedFileName(); //전송된 파일 이름을 가져와라
		String saveFileName = System.currentTimeMillis()+"-"+oriFileName;
		Path uploadDir = Paths.get(UPLOAD_DIR);
		if(!Files.exists(uploadDir)) {
			Files.createDirectories(uploadDir);
		}
		Path targetPath = uploadDir.resolve(saveFileName);
		try {
			InputStream input = filePart.getInputStream();
			Files.copy(input, targetPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saveFileName;
	}
	
	private void deleteOldFile(String fileName) {
		if(fileName == null) return;
		try {
			Files.deleteIfExists(Paths.get(UPLOAD_DIR, fileName));
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	
}
