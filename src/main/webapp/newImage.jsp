<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "java.util.*, java.sql.*, org.apache.commons.fileupload.*, org.apache.commons.fileupload.disk.*, org.apache.commons.fileupload.servlet.*,org.apache.commons.fileupload.util.*, java.io.*, com.nearinfinity.demo.*" %>
<%

//Get the specific parameters we want
String name = request.getParameter("name");
String subjectId = request.getParameter("subjectId");
String latitude = request.getParameter("latitude");
String longitude = request.getParameter("longitude");
String time_collected = request.getParameter("timestamp");

//First save the image info to the database
Throwable exception = null;
Connection connection = null;
PreparedStatement statement = null;
ResultSet results = null;
try {
   //Perform file upload here
   FileItemFactory factory = new DiskFileItemFactory();
   ServletFileUpload upload = new ServletFileUpload(factory);

   // Parse the request
   List items = upload.parseRequest(request);
   Iterator iterator = items.iterator();
   FileItem file = null;
   String fileName = null;
   while (iterator.hasNext()) {
      FileItem item = (FileItem)iterator.next();
      if (!item.isFormField()) {
         System.out.println("File detected");   
         file = item;
         fileName = item.getName();
      }
      else {
         if ( item.getFieldName().equalsIgnoreCase("name")) {
            name = item.getString();
         }
         else if ( item.getFieldName().equalsIgnoreCase("subjectId")) {
            subjectId = item.getString();
         }
         else if ( item.getFieldName().equalsIgnoreCase("latitude")) {
            latitude = item.getString();
         }
         else if ( item.getFieldName().equalsIgnoreCase("longitude")) {
            longitude = item.getString();
         }   
         else if ( item.getFieldName().equalsIgnoreCase("timestamp")) {
            time_collected = item.getString();
         }                  
      }
   } //end while  
   if (file == null) {
      throw new Exception("There was no file detected in the request.");
   } 
   else {
      System.out.println("Detected file " + fileName + " in the request");
   }
   
   System.out.println("name = " + name);
   System.out.println("subjectId = " + subjectId);
   System.out.println("latitude = " + latitude);
   System.out.println("longitude = " + longitude);  
   System.out.println("time_collected = " + time_collected);   
   
   Class.forName("com.mysql.jdbc.Driver").newInstance();
   connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/face_demo?user=root&password=root");
   String insertStatement = "insert into training_images (training_subject_id, latitude, longitude,time_collected) values (?,?,?,?)";    
   statement = connection.prepareStatement(insertStatement);
   statement.setLong(1, Long.parseLong(subjectId));
   statement.setDouble(2, Double.parseDouble(latitude));
   statement.setDouble(3, Double.parseDouble(longitude));
   statement.setTimestamp(4, new java.sql.Timestamp(Long.parseLong(time_collected)));
   statement.executeUpdate();
   System.out.println("inserted new image for " + subjectId);
 
   String lastAutoIncrement = "select last_insert_id()";
   statement = connection.prepareStatement(lastAutoIncrement);
   results = statement.executeQuery();
   results.next();
   long imageId = results.getLong(1);
   System.out.println("newly inserted id is " + imageId); 
   
   File dir = new File(".");
   System.out.println(dir.getCanonicalPath());   
   
   String extension = fileName.substring(fileName.lastIndexOf("."));
   String outputDirectory = "../webapps/facialRecognition/trainingImages/s"+subjectId+"/";
   File outputDir = new File(outputDirectory);
   if (!outputDir.exists() ) {
     outputDir.mkdirs();
   }
   String outputFileName = outputDirectory +imageId + extension;
   FileOutputStream output = new FileOutputStream(outputFileName);
   InputStream input = file.getInputStream();
   int inputValue = input.read();
   while (inputValue != -1) {
     output.write(inputValue);
     inputValue = input.read();
   }  
   input.close();
   output.close();
   System.out.println("Wrote " + fileName + " to " + outputFileName); 
   
   FaceDetection.extractImage("../webapps/facialRecognition/trainingImages", "../webapps/facialRecognition/trainingImages/scaled",Long.parseLong(subjectId), imageId, outputFileName, 100, 50);
   //throw new Exception("test problme");
}
catch (Throwable e) {
   exception = e;
   System.out.println(e);
}
finally {
  if (connection != null) 
     connection.close();
     
  if (statement != null) 
     statement.close();
  
  if (results != null) 
     results.close();
}

if (exception == null) {
   response.sendRedirect("index.jsp?name=" + name);
}
else {
   response.sendRedirect("index.jsp?name=" + name + "&error="+ exception.getMessage());
}
%>
