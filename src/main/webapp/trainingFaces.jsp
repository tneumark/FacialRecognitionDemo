<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "java.util.Map, java.sql.*, java.util.*, com.nearinfinity.demo.*" %>    
<%
Exception exception = null;
Connection connection = null;
PreparedStatement statement = null;
ResultSet subjectRS = null;
List<Subject> subjects = new ArrayList<Subject>();
try {
  Class.forName("com.mysql.jdbc.Driver").newInstance();
  connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/face_demo?user=root&password=root");
  String selectStatement = "select * from training_subjects order by id";
  statement = connection.prepareStatement(selectStatement);
  subjectRS = statement.executeQuery();

  while (subjectRS.next() ) {
    int id = subjectRS.getInt("id");
    //System.out.println("Subject:" + id);
    String name = subjectRS.getString("name");
    Subject subject = new Subject(id, name);
    subjects.add(subject);
    String findImagesStatement = "select * from training_images where training_subject_id = ?";
    PreparedStatement findImages = connection.prepareStatement(findImagesStatement);
    findImages.setInt(1, id);
    ResultSet imagesRS = findImages.executeQuery();
    while (imagesRS.next() ) {
       int imageId = imagesRS.getInt("id");
       //System.out.println("   Image:" + imageId);
       int trainingSubjectId = imagesRS.getInt("training_subject_id");
       Timestamp timeCollected = imagesRS.getTimestamp("time_collected");
       double longitude = imagesRS.getDouble("longitude");
       double latitude = imagesRS.getDouble("latitude");
       FaceImage image = new FaceImage(imageId, trainingSubjectId, timeCollected, latitude, longitude);
       subject.addFaceImage(image);
    }
    imagesRS.close();
  }
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



StringBuffer buffer = new StringBuffer();
buffer.append("{\"html\":");
buffer.append("\"<div class=\\\"trainingFacesResultsContent\\\">");
buffer.append("<div class=\\\"accordion\\\" id=\\\"trainingFacesAccordion\\\">");

for (Subject subjectToDisplay: subjects) {
    long subjectId = subjectToDisplay.getId();
   
   buffer.append("<div class=\\\"accordion-group\\\">");
   buffer.append("   <div class=\\\"accordion-heading\\\">");
   buffer.append("      <div class=\\\"row-fluid\\\"><div class=\\\"span5\\\"><a class=\\\"accordion-toggle\\\" data-toggle=\\\"collapse\\\" data-parent=\\\"trainingFacesAccordion\\\" href=\\\"#trainingFace" + subjectId + "\\\">" + subjectToDisplay.getName() +" (" + subjectToDisplay.getFaceImages().size() + " Images)" +" </a></div>");
   buffer.append("      <div class=\\\"span7\\\"><a href=\\\"#newImage\\\" data-subject=\\\"" + subjectId + "\\\" data-subjectname=\\\"" + subjectToDisplay.getName() + "\\\" data-toggle=\\\"modal\\\"><i class=\\\"icon icon-picture\\\"></i>&nbspNew Image</a></div></div>");
   buffer.append("   </div>");  //accordion-heading
   buffer.append("   <div id=\\\"trainingFace" + subjectId + "\\\" class=\\\"accordion-body collapse out\\\">");
   buffer.append("      <div class=\\\"accordion-inner\\\">");
   buffer.append("         <div id=\\\"face" + subjectId + "Carousel\\\" class=\\\"carousel slide\\\" data-interval=\\\"false\\\">");
   buffer.append("             <div class=\\\"carousel-inner\\\">");
   boolean firstImage = true;
   for (FaceImage image: subjectToDisplay.getFaceImages() ) {
      long imageId= image.getId();
      if (firstImage) {
         buffer.append("                <div class=\\\"active item\\\">");
         firstImage = false;
      }
      else {
         buffer.append("                <div class=\\\"item\\\">");
      }
      
      buffer.append("                   <img src=\\\"trainingImages/s"+subjectId+"/detected/"+imageId+".jpg\\\" style=\\\"width:100%\\\" />");
      buffer.append("                   <div class=\\\"carousel-caption\\\">");
      buffer.append("                      <h4>Subject id# " + subjectId + ", Image id# " + imageId + "</h4>");
      buffer.append("                      <p>This picture was taken on "+ image.getTimeCollected() + " at (lat, long) = (" + image.getLatitude() + "," +  image.getLongitude() + ").</p>");
      buffer.append("                   </div>"); //carousel-caption
      buffer.append("                </div>"); //active item
   }
      
   buffer.append("             </div>");  //carousel-inner
   buffer.append("             <a class=\\\"carousel-control left\\\" href=\\\"#face" + subjectId  + "Carousel\\\" data-slide=\\\"prev\\\">&lsaquo;</a>");
   buffer.append("             <a class=\\\"carousel-control right\\\" href=\\\"#face" + subjectId + "Carousel\\\" data-slide=\\\"next\\\">&rsaquo;</a>");
                        
   buffer.append("          </div>");  //face1Carousel
   buffer.append("       </div>");  //accordion-inner
   buffer.append("    </div>");  //trainingFace1
   buffer.append("</div>");  //accordion-group
} 

buffer.append("</div>"); //accordion div
buffer.append("</div>\"}"); //trainingFacesResultsContent
%>
<%= buffer.toString() %>