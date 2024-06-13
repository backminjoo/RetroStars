<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>    
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>비밀번호 입력</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <img src="logo.png" alt="Logo" class="logo">
        <h1>비밀번호 입력</h1>
        <form id="password-form" action="/password.register" method="post">
            <div class="input-group">
                <label for="password">비밀번호:</label>
                <input type="password" id="password" name="password" placeholder="비밀번호" required>
            </div>
            <div class="input-group">
                <label for="confirm-password">비밀번호 확인:</label>
                <input type="password" id="confirm-password" name="confirm-password" placeholder="비밀번호 확인" required>
            </div>
            <button type="button" onclick="validatePassword()">다음</button>
        </form>
    </div>
    <script>
        function validatePassword() {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            if (password === "" || confirmPassword === "") {
                alert("비밀번호를 입력해주세요.");
            } else if (password !== confirmPassword) {
                alert("비밀번호가 일치하지 않습니다. 다시 확인해주세요.");
            } else {
                document.getElementById('password-form').submit();
            }
        }
    </script>
</body>
</html>