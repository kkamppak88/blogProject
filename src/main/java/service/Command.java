package service;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Command {
		/*
		 모든 기능(로그인 글쓰기 댓글쓰기...)이 공통으로 구현할 인터페이스
		 콘트롤러는 이 인터페이스만 다룬다
		  */
		boolean doCommand(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException;
	}

