/**
 * 팀 별빛, Software License, Version 1.0
 *
 * Copyright (c) 팀 별빛, All rights reserved.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import dto.MemberDTO;

/**
 * Description : 클래스에 대한 설명을 입력해주세요.
 * Date : 2024. 6. 12.
 * History :
 *  - 작성자 : Jin, 날짜 : 2024. 6. 12., 설명 : 최초작성
 *
 * @author : Jin 
 * @version 1.0 
 */
public class MemberDAO {
	public static MemberDAO instance;
	
	public synchronized static MemberDAO getInstance() {
		if (instance == null) {
			instance = new MemberDAO();
		}
		return instance;
	}
	
	public MemberDAO() {
		
	}
	
	private Connection getConnection() throws Exception{
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}
	
    /** 
     * @Method Name  : loginId 
     * @date : 2024. 6. 13. 
     * @author : Jin 
     * @version : 
     * @Method info : 로그인 기능.
     * @param id
     * @param pw
     * @return boolean
     * @throws Exception 
     */ 
    public boolean loginId(String id, String pw) throws Exception {
        String sql = "select * from jinhyeok.member where user_id = ? and user_pw = ?";
        try (Connection con = this.getConnection(); PreparedStatement pstat = con.prepareStatement(sql);) {
            pstat.setString(1, id);
            pstat.setString(2, pw);
            try (ResultSet rs = pstat.executeQuery();) {
                return rs.next();
            }
        }
    }

    /** 
     * @Method Name  : deleteMember 
     * @date : 2024. 6. 13. 
     * @author : Jin 
     * @version : 
     * @Method info : 회원탈퇴 기능.
     * @param id
     * @return int
     * @throws Exception 
     */ 
    public int deleteMember(String id) throws Exception {
    	String sql = "delete from jinhyeok.member where user_id = ?";
    	try (Connection con = this.getConnection(); PreparedStatement pstat = con.prepareStatement(sql);){
    		pstat.setString(1, id);
    		return pstat.executeUpdate();
    	}
    	
    }
    
    /** 
     * @Method Name  : addMember 
     * @date : 2024. 6. 13. 
     * @author : Jin 
     * @version : 
     * @Method info : 회원가입 기능.
     * @param MemberDTO dto
     * @return int
     * @throws Exception 
     */ 
    public int addMember(MemberDTO dto) throws Exception {
        String sql = "insert into jinhyeok.member (user_id, user_pw, user_name, user_nickname, user_no, user_phone, user_email, user_join_date) values(?, ?, ?, ?, ?, ?, ?, ?)";
        int result = 0;
        try (Connection con = this.getConnection(); PreparedStatement pstat = con.prepareStatement(sql);) {
        	pstat.setString(1, dto.getUserId());
        	pstat.setString(2, dto.getUserPw());
        	pstat.setString(3, dto.getUserName());
        	pstat.setString(4, dto.getUserNickname());
        	pstat.setString(5, dto.getUserNo());
        	pstat.setString(6, dto.getUserPhone());
        	pstat.setString(7, dto.getUserEmail());
        	pstat.setTimestamp(8, dto.getUserJoinDate());
//        	pstat.setString(9, dto.getUserProfileUrl());
//        	pstat.setString(10, dto.getUserLevel());
//        	pstat.setString(11, dto.getUserAdmin());
//        	pstat.setString(12, dto.getUserBlack());
//        	pstat.setString(13, dto.getUserActive());
            result = pstat.executeUpdate();
        }
        return result;
    }

}
