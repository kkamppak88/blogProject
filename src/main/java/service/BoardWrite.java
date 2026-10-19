package service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.BoardDAO;
import model.BoardDTO;
import model.MemberDTO;

public class BoardWrite implements Command {

	private static final String UPLOAD_DIR="C:/upload";
	
   @Override
   public boolean doCommand(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

	   request.setCharacterEncoding("utf-8");
	   
      // 로그인한 사람만 글쓰기 가능
      HttpSession session = request.getSession();
      MemberDTO memberdto = (MemberDTO)session.getAttribute("loginMember");   // getAttribute: 세션 속성 가져오기
      if(memberdto == null) {
         response.sendRedirect(request.getContextPath()+"/member/login");
         return false;
      }
      
      
      // get 요청 시 글쓰기 화면 보여주기
      if("get".equalsIgnoreCase(request.getMethod())) {
         return true;
      }
      
      BoardDTO dto = new BoardDTO();
      
      dto.setTitle(request.getParameter("title"));
      dto.setContent(request.getParameter("content"));
      dto.setWriter(memberdto.getId());
      
      
      // 이미지 요청 - 첨부했을 때만 저장
      Part filePart = request.getPart("imgFile");
      // jsp/servlet에서 파일 업로드를 처리할 때 사용하는 servlet 표준 API
      // Part : 업로드 된 파일 하나를 표현하는 객체
      // request.getPart("imgFile") : http 요청으로 전송된 데이터 중에서 name="imgFile"인 파일을 가져와라
      if(filePart != null && filePart.getSize() > 0) {
         // getSize : 첨부된 이미지의 용량 -> 첨부된 이미지의 용량이 0보다 크면 saveFile 메소드로 보내라
         String saveFileName = saveFile(filePart);
         dto.setImgPath(saveFileName);
      }
      
      BoardDAO dao = new BoardDAO();
      dao.insert(dto);
      
     response.sendRedirect(request.getContextPath()+"/blog/list");
      
      return false;
   }   //doCommand
   
   public String saveFile(Part filePart) throws IOException {
	   //핵심 클래스
	   //path : 파일 생성이나 디렉토리의 경로 정보를 나타내는 객체
	   //Files : 파일 생성, 삭제, 읽기, 쓰기, 복사 등 실제 동작을 처리하는 정적 메서드 집합
	   String oriFileName = filePart.getSubmittedFileName();
	   //업로드한 원래 파일명
	   System.out.println("oriFileName : " + oriFileName);
	   //String saveFileName = System.currentTimeMillis()+"_"+oriFileName;
	   //currentTimeMillis() : 현재 시간을 밀리초 단위로 반환하는 메서드
	   String saveFileName = UUID.randomUUID()+"_"+oriFileName;
	   System.out.println("UUID : "+saveFileName);
	   
	   Path uploadDir = Paths.get(UPLOAD_DIR);
	   //문자열로 되어있는 경로를 Path 객체로 바꿔라
	   //Path : 경로를 표현하는 객체
	   //Paths : Path객체를 만들어주는 클래스
	   if(!Files.exists(uploadDir)) {
		   //uploadDir 즉 F:/upload 폴더가 존재하지 않으면
		   Files.createDirectories(uploadDir);
		   //상위폴더부터 하위폴더까지 자동으로 만들어 준다
	   }
	   Path targetPath = uploadDir.resolve(saveFileName);
	   //resolve() : 폴더 경로 + 파일 이름을 합쳐서 완전한 파일 경로를 만들어라.
	   try(InputStream input = filePart.getInputStream()) {
		   //getInputStream은 데이터를 읽어오는 통로
		   Files.copy(input, targetPath);
		   //input에서 파일 데이터를 읽어서 targetPath에 복사
	}
       return saveFileName;
   }

}
