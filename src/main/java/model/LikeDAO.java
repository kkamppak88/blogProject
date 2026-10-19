package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBUtil;

public class LikeDAO {
	
	public boolean isLiked(int bno, String memberId) {
		String sql = "select 1 from board_like where bno=? and member_id=?";
		
		try (Connection conn = DBUtil.getInstance();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, bno);
			pstmt.setString(2, memberId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//좋아요 토글 : 안 눌렀으면 추가, 이미 눌렀으면 취소
	// board_like 변경 + board.like_cnt 증감을 한 묶음(트랜잭션)으로 처리 
	public void toggle(int bno, String memberId) {
		String checkSql = "select 1 from board_like where bno=? and member_id=?";
		String insertSql = "insert into board_like (bno, member_id) values (?, ?)";
		String deleteSql = "delete from board_like where bno=? and member_id=?";
		String plusSql = "update board set like_cnt = like_cnt + 1 where bno=?";
		String minusSql = "update board set like_cnt = like_cnt - 1 where bno=?";
		
		try (Connection conn = DBUtil.getInstance()) {
			conn.setAutoCommit(false);
			try {
				//1)이미 눌렀는지 확인
				boolean exists;
				try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
					pstmt.setInt(1, bno);
					pstmt.setString(2, memberId);
					try (ResultSet rs = pstmt.executeQuery()) {
						exists = rs.next();
					}
				}
				
				//2)눌렀으면(delete + 개수-1), 안 눌렀으면(insert + 개수+1) 
				try (PreparedStatement pstmt = conn.prepareStatement(exists ? deleteSql : insertSql)) {
					pstmt.setInt(1, bno);
					pstmt.setString(2, memberId);
					pstmt.executeUpdate();
				}
				
				try (PreparedStatement pstmt = conn.prepareStatement(exists ? minusSql : plusSql)) {
					pstmt.setInt(1, bno);
					pstmt.executeUpdate();
				}
				
				conn.commit(); //두 작업이 모두 성공했을 때만 한꺼번에 확정
			} catch (Exception e) {
				conn.rollback(); //하나라도 실패하면 둘 다 없던 일로 되돌림
				throw e;
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//현재 좋아요 개수 조회
	public int selectLikeCnt(int bno) {
		String sql = "select like_cnt from board where bno=?";
		
		try (Connection conn = DBUtil.getInstance();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, bno);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("like_cnt");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}	
	
} //class end