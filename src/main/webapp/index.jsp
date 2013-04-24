<%
String userAgent = request.getHeader("User-Agent");
boolean isAndroid = false;
boolean isIPhone = false;
String message = "There are no mobile applications available for your device.  We know this is disappointing.  Are you sure you are on a mobile device?  You are probably on a regular computer.  I detected your user agent as " + userAgent + ". Please check back later or try again from a mobile device.";
if (userAgent.toLowerCase().contains("android")) {
   isAndroid = true;
}
else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipod")) {
   isIPhone = true;
   message = "iPhone App may be available in the future.  Yes, we know this is disappointing to Apple fans everywhere.  Please check back later.";
}

String alertMessage = "<h3>Welcome to the Facial Recognition Demo!</h3>";
String alertClass = "alert-success";
if (request.getParameter("name") != null && request.getParameter("error") == null) {
   alertMessage = "<h3>Successfully saved new image for " + request.getParameter("name") + "</h3>";
}
else if (request.getParameter("error") != null) {
  alertClass="alert-error";
  alertMessage = "<h3>There was a problem saving a new image for " + request.getParameter("name") + ".  Exception is " + request.getParameter("error") + "</h3>";
}
%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Facial Recognition Demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <style type="text/css">
      html { height: 100% }
      body { height: 100%; margin: 0; padding: 0 }
      #map_canvas { height: 100% }
    </style>

    <!-- Le styles -->
    <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <style>
      body {
        padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
      }
    </style>
    <link href="bootstrap/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="bootstrap/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="bootstrap/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="bootstrap/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="bootstrap/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="bootstrap/ico/apple-touch-icon-57-precomposed.png">
  </head>

  <body>
	<div class="modal hide fade" id="mobileDownload" >
	   <div class="modal-header">
	      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	      <h3 id="myModalLabel">Download Mobile App</h3>
	   </div>
	   <div class="modal-body">
	      <p><%= message %></p>
	   </div>
	   <div class="modal-footer">
	      <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	   </div>
	</div> <!--/mobileDownload -->
	
	<div class="modal hide fade" id="about" >
	   <div class="modal-header">
	      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	      <h3 id="myModalLabel">About</h3>
	   </div>
	   <div class="modal-body">
	      <p>This demo was created by Tom Neumark for Near Infinity.  By using this demo, you agree to only submit appropriate images.</p>
	   </div>
	   <div class="modal-footer">
	      <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	   </div>
      </div> <!--/about -->
      
	<div class="modal hide fade" id="newSubject" >
	   <div class="modal-header">
	      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	      <h3 id="myModalLabel">New Subject</h3>
	   </div>
	   <div class="modal-body">
	      <form id="newSubjectForm" class="form ajax" action="newSubject.jsp" method="POST" data-replace="#messages" >
	        <label>Subject Name</label>
	        <input id="name" name="name" type="text" />
	      </form>
	   </div>
	   <div class="modal-footer">
	      <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	      <button class="btn btn-primary" aria-hidden="true" href="#" id="addNewSubjectLink">Add New Subject</button>
	   </div>
      </div> <!--/new subject -->      

	<div class="modal hide fade" id="newImage" >
	   <div class="modal-header">
	      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	      <h3 id="newImageModalLabel">New Image</h3>
	   </div>
	   <div class="modal-body">
	      <form id="newImageForm" enctype="multipart/form-data" class="form" action="newImage.jsp" method="POST" data-replace="#messages" >
	        <input id="subjectId" name="subjectId" type="hidden" />
	        <input id="subjectName" name="name" type="hidden" />
	        <span class="btn btn-file"><input id="file" name="file" type="file"/></span>
	        <input id="latitude" name="latitude" type="hidden"/>
	        <input id="longitude" name="longitude" type="hidden" />
	        <input id="timestamp" name="timestamp" type="hidden" />
	      </form>
	   </div>
	   <div class="modal-footer">
	      <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	      <button class="btn btn-primary" aria-hidden="true" href="#" id="addNewImageLink">Add New Image</button>
	   </div>
      </div> <!--/new image -->    

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="#">Facial Recognition Demo</a>
          <div class="nav-collapse collapse">
            <ul class="nav">
              <li><a href="#about" data-toggle="modal">About</a></li>                              
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>  

    <div class="container">    
      <div id="messages" class="alert <%= alertClass %>" href="#"><%= alertMessage %></div>
      <div class="tabbable">
         <ul class="nav nav-tabs">
            <li class="active"><a href="#trainingFacesTabPane" data-toggle="tab">Haar Cascade Detection</a></li>
         </ul> <!-- /nav-tabs -->
       
         <div class="tab-content">
            <div class="tab-pane active" id="trainingFacesTabPane">
               <div style="float:right">
                  <a href="#newSubject" data-toggle="modal">
	             <i class="icon icon-user"></i>&nbspNew Subject
                  </a> 
                  &nbsp
                  <a href="trainingFaces.jsp" id="trainingFacesRefreshLink" class="ajax" data-replace=".trainingFacesResultsContent">
	             <i class="icon icon-refresh"></i>&nbspRefresh
                  </a>
               </div>
               <br>
               <div class="trainingFacesResultsContent">
               Click refresh to load.
               </div>
            </div>
            <div class="tab-pane" id="recognitionResultsTabPane">
               <div style="float:right">
                  <a href="recognitionResults.jsp" id="recognitionResultsRefreshLink" class="ajax" data-replace=".recognitionResultsContent">
	             <i class="icon icon-refresh"></i>&nbspRefresh
                  </a>
               </div>
               <div class="recognitionResultsContent">
               Click refresh to load.               
               </div>
            </div>
            <div class="tab-pane" id="mapTabPane">
               <p>Map goes here</p>
               <div id="map_canvas" style="width:100%; height:100%"></div>
            </div>            
         </div> <!-- /tab-content -->
       </div> <!-- /tabbable -->
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="http://code.jquery.com/jquery-latest.js"></script>
    <script src="bootstrap/js/bootstrap-transition.js"></script>
    <script src="bootstrap/js/bootstrap-alert.js"></script>
    <script src="bootstrap/js/bootstrap-modal.js"></script>
    <script src="bootstrap/js/bootstrap-dropdown.js"></script>
    <script src="bootstrap/js/bootstrap-scrollspy.js"></script>
    <script src="bootstrap/js/bootstrap-tab.js"></script>
    <script src="bootstrap/js/bootstrap-tooltip.js"></script>
    <script src="bootstrap/js/bootstrap-popover.js"></script>
    <script src="bootstrap/js/bootstrap-button.js"></script>
    <script src="bootstrap/js/bootstrap-collapse.js"></script>
    <script src="bootstrap/js/bootstrap-carousel.js"></script>
    <script src="bootstrap/js/bootstrap-typeahead.js"></script>
    <script src="bootstrap/js/bootstrap-ajax.js"></script>
    
    <script> 
       function getGPSAndSubmitNewImage(position) {          
          latitude = position.coords.latitude;
          longitude = position.coords.longitude;
          timestamp = position.timestamp;
          
          if (latitude != null) {
             $('#latitude').val(latitude);
          }

          if (longitude != null) {
             $('#longitude').val(longitude);
          }

          if (timestamp != null) {
             $('#timestamp').val(timestamp);
          }
          
          $('#newImageForm').submit();
	  $('#newImage').modal('hide');
          $("#trainingFacesRefreshLink").click();                              
       }
                 
       $(document).ready(function() {
         $("#trainingFacesRefreshLink").click();
         
         $("#recognitionResultsRefreshLink").click();
         
         $('#addNewSubjectLink').on('click', function(e){
            e.preventDefault();
            $('#newSubjectForm').submit();
	    $('#newSubject').modal('hide');
            $("#trainingFacesRefreshLink").click();
            $("#trainingFacesRefreshLink").click();
         });   
         
         $('#addNewImageLink').on('click', function(e){
            e.preventDefault();
            navigator.geolocation.getCurrentPosition(getGPSAndSubmitNewImage);
         });
         
         $('#newImage').on('show', function(e){
            id = $(this).data('modal').options.subject;
            subject_name = $(this).data('modal').options.subjectname;
            $('#subjectId').val(id);
            $('#subjectName').val(subject_name);
            $('#newImageModalLabel').html("New Image for Subject " + id + " (" + subject_name + ")");
         });                 
         
       });
    </script>

  </body>
</html>