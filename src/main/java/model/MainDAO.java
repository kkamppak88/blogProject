package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class MainDAO {

	// 최신 글 목록 조회 (등록일 기준 내림차순 정렬)
	public List<BoardDTO> MainNew() {
		List<BoardDTO> list = new ArrayList<>();
		
		String sql = "select * from ("
				+ "    select b.bno, b.title, m.nickname as writer_nickname, to_char(b.reg_date, 'YYYY-MM-DD') as reg_date, "
				+ "        b.view_cnt, b.like_cnt, b.img_path "
				+ "    from board b "
				+ "    join member m on b.writer = m.id "
				+ "    order by b.reg_date desc, b.bno desc"
				+ ") where rownum <= 3";

		try (Connection conn = DBUtil.getInstance();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			
			while (rs.next()) {
				BoardDTO dto = new BoardDTO();
				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setWriterNickname(rs.getString("writer_nickname"));
				dto.setRegDate(rs.getString("reg_date"));
				dto.setViewCnt(rs.getInt("view_cnt"));
				dto.setLikeCnt(rs.getInt("like_cnt"));
				dto.setImgPath(rs.getString("img_path"));
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 인기 글 목록 조회 (좋아요, 조회수 기준 내림차순 정렬)
	public List<BoardDTO> MainPopular() {
		List<BoardDTO> list = new ArrayList<>();
		
		String sql = "select * from ("
				+ "    select b.bno, b.title, m.nickname as writer_nickname, to_char(b.reg_date, 'YYYY-MM-DD') as reg_date, "
				+ "        b.view_cnt, b.like_cnt, b.img_path "
				+ "    from board b "
				+ "    join member m on b.writer = m.id "
				+ "    order by b.like_cnt desc, b.view_cnt desc, b.bno desc"
				+ ") where rownum <= 3";
		
		try (Connection conn = DBUtil.getInstance();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			
			while (rs.next()) {
				BoardDTO dto = new BoardDTO();
				dto.setBno(rs.getInt("bno"));
				dto.setTitle(rs.getString("title"));
				dto.setWriterNickname(rs.getString("writer_nickname"));
				dto.setRegDate(rs.getString("reg_date"));
				dto.setViewCnt(rs.getInt("view_cnt"));
				dto.setLikeCnt(rs.getInt("like_cnt"));
				dto.setImgPath(rs.getString("img_path"));
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
}