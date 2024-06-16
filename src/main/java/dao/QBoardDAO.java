/**
 * 팀 별빛, Software License, Version 1.0
 *
 * Copyright (c) 팀 별빛, All rights reserved.
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import dto.QBoardDTO;

/**
 * Description : 클래스에 대한 설명을 입력해주세요.
 * Date : 2024. 6. 12.
 * History :
 *  - 작성자 : Sam, 날짜 : 2024. 6. 13., 설명 : 최초작성
 *
 * @author : Sam
 * @version 1.0 
 */
public class QBoardDAO {
	//singletone
	private QBoardDAO() {}
	public static QBoardDAO instance;

	public synchronized static QBoardDAO getInstance() {
		if(instance==null) {
			instance=new QBoardDAO();
		}
		return instance;
	}

	//JNDI
	private Connection getConnection() throws Exception{
		Context ctx=new InitialContext();
		DataSource ds=(DataSource)ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}

	//0.secret string(true,false) to string(y,n)
	public String getSecretYN(String secretString) {
		if(secretString.equals("true")) {
			return "Y";
		}else if(secretString.equals("false")) {
			return "N";
		}else {
			return null;
		}		
	}

	//0. 카테고리 이름을 검색하는 함수
	public int getCategory(String categoryName) throws Exception{
		String sql="select q_board_category from q_category where q_board_category_name=?";

		try(Connection con=this.getConnection();
				PreparedStatement ps=con.prepareStatement(sql);){	
			ps.setString(1, categoryName);
			try(ResultSet rs=ps.executeQuery();){
				while(rs.next()) {
					int QBoardCategory=rs.getInt("q_board_category");
					return QBoardCategory;
				}
				return 0;
			}
		}

	}

	//1. 글 추가하기 insert
	public int insert(QBoardDTO dto) throws Exception{
		String sql="insert into q_board values(q_board_sequence.nextval,?,?,?,?,sysdate,?,?)";
		try(Connection con=this.getConnection();
				PreparedStatement ps=con.prepareStatement(sql, new String[] {"q_board_seq"});){
			ps.setString(1, dto.getUserId());
			ps.setInt(2, dto.getqBoardCategory());
			ps.setString(3, dto.getqBoardTitle());
			ps.setString(4, dto.getqBoardContent());
			ps.setString(5, dto.getqBoardAnswer());
			ps.setString(6, dto.getqBoardSecret());
			ps.executeUpdate();
			try(ResultSet rs=ps.getGeneratedKeys();){
				rs.next();
				return rs.getInt(1);
			}
		}
	}


	//3.페이지별 게시글 select 하기
	public ArrayList<QBoardDTO> select(int startnum, int endnum, int category, String strsearchBy, String strsearchData) throws Exception {
		ArrayList<QBoardDTO> list = new ArrayList<>();
		//전체 카테고리일 때
		if(category==0) {
			//검색이 있을 때
			if(!(strsearchBy.equals("0"))) {
				String sql= "SELECT * FROM ( SELECT q_board.*, row_number() "+ 
						"OVER (ORDER BY q_board_seq DESC) AS rown FROM q_board where "+strsearchBy+" Like ?) subquery "+
						"WHERE rown BETWEEN ? AND ?";

				try (Connection con = this.getConnection();
						PreparedStatement ps = con.prepareStatement(sql);) {
					ps.setString(1, "%"+strsearchData+"%");
					ps.setInt(2, startnum);
					ps.setInt(3, endnum);

					try (ResultSet rs = ps.executeQuery();) {
						while (rs.next()) {
							int qBoardSeq = rs.getInt("q_board_seq");
							String userId = rs.getString("user_id");
							int qBoardCategory = rs.getInt("q_board_category");
							String qBoardTitle = rs.getString("q_board_title");
							String qBoardContent = rs.getString("q_board_content");
							Timestamp qBoardDate = rs.getTimestamp("q_board_date");
							String qBoardAnswer=rs.getString("q_board_answer");
							String qBoardSecret=rs.getString("q_board_secret");
							list.add(new QBoardDTO(qBoardSeq, userId, qBoardCategory, qBoardTitle, qBoardContent, qBoardDate, qBoardAnswer, qBoardSecret));
						}
						return list;
					}
				}


				//검색을 하지 않을 때
			}else {
				String sql = "SELECT * FROM ( SELECT q_board.*, row_number() "+
						"OVER (ORDER BY q_board_seq DESC) AS rown FROM q_board) subquery " +
						"WHERE rown BETWEEN ? AND ?";

				try (Connection con = this.getConnection();
						PreparedStatement ps = con.prepareStatement(sql);) {
					ps.setInt(1, startnum);
					ps.setInt(2, endnum);

					try (ResultSet rs = ps.executeQuery();) {
						while (rs.next()) {
							int qBoardSeq = rs.getInt("q_board_seq");
							String userId = rs.getString("user_id");
							int qBoardCategory = rs.getInt("q_board_category");
							String qBoardTitle = rs.getString("q_board_title");
							String qBoardContent = rs.getString("q_board_content");
							Timestamp qBoardDate = rs.getTimestamp("q_board_date");
							String qBoardAnswer=rs.getString("q_board_answer");
							String qBoardSecret=rs.getString("q_board_secret");
							list.add(new QBoardDTO(qBoardSeq, userId, qBoardCategory, qBoardTitle, qBoardContent, qBoardDate, qBoardAnswer, qBoardSecret));
						}
						return list;
					}
				}
			}

			//카테고리로 구분할 때
		}else {
			//검색이 있을 때
			if(!(strsearchBy.equals("0"))) {
				String sql = "SELECT * FROM ( SELECT q_board.*, row_number() "+
			             "OVER (ORDER BY q_board_seq DESC) AS rown "+
			             "FROM q_board WHERE q_board_category=? AND "+strsearchBy+" LIKE ?) subquery " +
			             "WHERE rown BETWEEN ? AND ?";

				try (Connection con = this.getConnection();
						PreparedStatement ps = con.prepareStatement(sql);) {
					ps.setInt(1, category);
					ps.setString(2, "%"+strsearchData+"%");
					ps.setInt(3, startnum);
					ps.setInt(4, endnum);

					try (ResultSet rs = ps.executeQuery();) {
						while (rs.next()) {
							int qBoardSeq = rs.getInt("q_board_seq");
							String userId = rs.getString("user_id");
							int qBoardCategory = rs.getInt("q_board_category");
							String qBoardTitle = rs.getString("q_board_title");
							String qBoardContent = rs.getString("q_board_content");
							Timestamp qBoardDate = rs.getTimestamp("q_board_date");
							String qBoardAnswer=rs.getString("q_board_answer");
							String qBoardSecret=rs.getString("q_board_secret");
							list.add(new QBoardDTO(qBoardSeq, userId, qBoardCategory, qBoardTitle, qBoardContent, qBoardDate, qBoardAnswer, qBoardSecret));
						}
						return list;
					}
				}

			}else {
				String sql = "SELECT * FROM ( " +
			             "SELECT q_board.*, row_number() OVER (ORDER BY q_board_seq DESC) AS rown " +
			             "FROM q_board WHERE q_board_category=?) subquery " +
			             "WHERE rown BETWEEN ? AND ?";

				try (Connection con = this.getConnection();
						PreparedStatement ps = con.prepareStatement(sql);) {
					ps.setInt(1, category);
					ps.setInt(2, startnum);
					ps.setInt(3, endnum);

					try (ResultSet rs = ps.executeQuery();) {
						while (rs.next()) {
							int qBoardSeq = rs.getInt("q_board_seq");
							String userId = rs.getString("user_id");
							int qBoardCategory = rs.getInt("q_board_category");
							String qBoardTitle = rs.getString("q_board_title");
							String qBoardContent = rs.getString("q_board_content");
							Timestamp qBoardDate = rs.getTimestamp("q_board_date");
							String qBoardAnswer=rs.getString("q_board_answer");
							String qBoardSecret=rs.getString("q_board_secret");
							list.add(new QBoardDTO(qBoardSeq, userId, qBoardCategory, qBoardTitle, qBoardContent, qBoardDate, qBoardAnswer, qBoardSecret));
						}
						return list;
					}
				}
			}

		}
	}


	//3. 현재 전체 보드 카운티하는 함수
	public int getRecordCount(int category, String strsearchBy, String strsearchData) throws Exception {
		int result=0;

		//전체 카테고리일 때
		if(category==0) {
			//검색이 있을 때
			if(!(strsearchBy.equals("0"))) {
				String sql="select count(*) from q_board where "+ strsearchBy +" Like ?";

				try(Connection con=this.getConnection();
						PreparedStatement ps=con.prepareStatement(sql);){
					 ps.setString(1, "%"+strsearchData+"%");
					try(ResultSet rs=ps.executeQuery();){
						rs.next();
						result= rs.getInt(1);
						return result;
					}	
				}
				//검색이 없을 때
			}else {
				String sql="select count(*) from q_board";

				try(Connection con=this.getConnection();
						PreparedStatement ps=con.prepareStatement(sql);){	
					try(ResultSet rs=ps.executeQuery();){
						rs.next();
						result= rs.getInt(1);
						return result;
					}	
				}
			}

			//카테고리를 구분할 때
		}else {
			//검색이 있을 때
			if(!(strsearchBy.equals("0"))) {
				String sql="select count(*) from q_board where q_board_category=? and "+ strsearchBy +" Like ?";
				try(Connection con=this.getConnection();
						PreparedStatement ps=con.prepareStatement(sql);){
					ps.setInt(1, category);
				    ps.setString(2, "%"+strsearchData+"%");
					try(ResultSet rs=ps.executeQuery();){
						rs.next();
						result= rs.getInt(1);
						return result;
					}	
				}


				//검색이 없을 때
			}else {
				String sql="select count(*) from q_board where q_board_category=?";

				try(Connection con=this.getConnection();
						PreparedStatement ps=con.prepareStatement(sql);){
					ps.setInt(1, category);
					try(ResultSet rs=ps.executeQuery();){
						rs.next();
						result= rs.getInt(1);
						return result;
					}	
				}
			}
		}
	}
	
	//3. 내용출력하기
		public QBoardDTO selectcontent(int searchseq) throws Exception{
			String sql="select * from q_board  where q_board_seq=?";

			try(Connection con=this.getConnection();
					PreparedStatement ps=con.prepareStatement(sql);){	
				ps.setInt(1, searchseq);
				try(ResultSet rs=ps.executeQuery();){
					while(rs.next()) {
						int qBoardSeq = rs.getInt("q_board_seq");
						String userId = rs.getString("user_id");
						int qBoardCategory = rs.getInt("q_board_category");
						String qBoardTitle = rs.getString("q_board_title");
						String qBoardContent = rs.getString("q_board_content");
						Timestamp qBoardDate = rs.getTimestamp("q_board_date");
						String qBoardAnswer=rs.getString("q_board_answer");
						String qBoardSecret=rs.getString("q_board_secret");
						return new QBoardDTO(qBoardSeq, userId, qBoardCategory, qBoardTitle, qBoardContent, qBoardDate, qBoardAnswer, qBoardSecret);
					}
					return null;
				}
			}
		}


	/*//더미데이터만들기
	public static void main(String[] args) throws SQLException {
		String url="jdbc:oracle:thin:@localhost:1521:xe";
		String id="star";
		String pw="star";

		String sql="insert into q_board values(q_board_sequence.nextval,?,1,?,?,sysdate,'N','N')";

		try(Connection con= DriverManager.getConnection(url, id, pw);
				PreparedStatement pstat=con.prepareStatement(sql);){
			for(int i=1; i<30; i++) {
				pstat.setString(1, "test"+(i));
				pstat.setString(2, "test"+(i));
				pstat.setString(3, "test"+(i));
				pstat.addBatch();
			}
			pstat.executeBatch();
		}
	}*/






}