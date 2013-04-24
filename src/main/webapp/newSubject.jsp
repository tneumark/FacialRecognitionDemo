<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "java.util.Map, java.sql.*" %>
<%
Map<String, String[]> parameters = request.getParameterMap();
System.out.println();
for(String key : parameters.keySet()) {
   System.out.println(key + " = " + parameters.get(key)[0]);
}
String name = request.getParameter("name");
String latitude = request.getParameter("latitude");
String longitude = request.getParameter("longitude");
String time_collected = request.getParameter("timestamp");
Exception exception = null;
Connection connection = null;
PreparedStatement statement = null;
try {
   Class.forName("com.mysql.jdbc.Driver").newInstance();
  connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/face_demo?user=root&password=root");
  String insertStatement = "insert into training_subjects (name) values (?)";
  statement = connection.prepareStatement(insertStatement);
  statement.setString(1, name);
  statement.executeUpdate();
}
catch (SQLException e) {
   exception = e;
}
finally {
  if (connection != null) 
     connection.close();
     
  if (statement != null) 
     statement.close();
}

if (exception == null) {
%>
{ "html": "<div id=\"messages\" class=\"alert alert-success\"><h3>The subject named <%= name %> has been saved.</h3></div>" }
<%
}
else { %>
{ "html": "<div id=\"messages\" class=\"alert alert-error\"><h3>Could not save <%= name %></h3><%= exception.getMessage() %></div>" }
<%
}
%>
