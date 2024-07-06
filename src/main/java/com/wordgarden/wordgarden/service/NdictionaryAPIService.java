package com.wordgarden.wordgarden.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NdictionaryAPIService {
    public static void main(String[] args) {
        String clientId = "YOUR_CLIENT_ID"; // 애플리케이션 클라이언트 아이디
        String clientSecret = "YOUR_CLIENT_SECRET"; // 애플리케이션 클라이언트 시크릿

        // 데이터베이스 연결 정보
        String jdbcUrl = "jdbc:mysql://localhost:3306/your_database";
        String dbUser = "your_db_user";
        String dbPassword = "your_db_password";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // 데이터베이스 연결
            conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            // CSV 파일 읽기
            File csvFile = new File("path_to_your_csv_file.csv");
            Scanner scanner = new Scanner(csvFile);
            int wordCount = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(","); // CSV 파일의 구분자가 콤마일 경우
                String category = values[0];
                String word = values[1];

                // 단어id 생성
                String wordId = category + "_" + (++wordCount);

                // 단어를 데이터베이스에 삽입
                String insertQuery = "INSERT INTO " + category + "word_tb (word, word_id, likeword, category) VALUES (?, ?, 0, ?)";
                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setString(1, word);
                pstmt.setString(2, wordId);
                pstmt.setInt(3, 0); // 기본값으로 0 설정
                pstmt.setString(4, category);
                pstmt.executeUpdate();

                // 네이버 API 호출
                String text = URLEncoder.encode(word, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/search/encyc?query=" + text;

                Map<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("X-Naver-Client-Id", clientId);
                requestHeaders.put("X-Naver-Client-Secret", clientSecret);

                String responseBody = get(apiURL, requestHeaders);

                // API 응답을 데이터베이스에 저장
                saveResponseToDatabase(conn, category, word, responseBody);
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }

    private static void saveResponseToDatabase(Connection conn, String category, String word, String responseBody) {
        PreparedStatement pstmt = null;
        try {
            String insertQuery = "INSERT INTO YourResultTable (category, word, response) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, category);
            pstmt.setString(2, word);
            pstmt.setString(3, responseBody);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("응답을 데이터베이스에 저장하는 데 실패했습니다.", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
