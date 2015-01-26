<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1">
    <link rel='stylesheet' href='webjars/bootstrap/3.3.1/css/bootstrap.min.css'>
	<style type="text/css">
		
		#progressbox {
			border: 1px solid #0099CC;
			padding: 1px;
			position:relative;
			width:400px;
			border-radius: 3px;
			margin: 10px;
			display:none;
			text-align:left;
			}
		#progressbar {
			height:20px;
			border-radius: 3px;
			background-color: #003333;
			width:1%;
		}
		#statustxt {
			top:3px;
			left:50%;
			position:absolute;
			display:inline-block;
			color: #000000;
		}
	
	</style>
</head>
<body>

<div class="container">
	<form id="diffReportForm" method="POST" enctype="multipart/form-data"
		action="/refset/v1.0/refsets/generateDiffReport" class="navbar-form navbar-left">
		
		<h3><span class="label label-info"> Select desired release date and provide refset files to generate refset diff report</span></h3>

	 <div class="panel panel-info">
	 	<div class="panel-heading"><label for="releaseDate">SNOMED Release Date</label></div>
	  	<div class="panel-body">
			<div class="form-group col-sm-2">
				<select class="form-control col-sm-2" name="releaseDate">
	    			<option value="20150131">20150131</option>
	    			<option value="20140731">20140731</option>
	    			<option value="20140131">20140131</option>
				</select>
			</div> 
	  	</div>
	 </div>
				
	 <div class="panel panel-info">
	 	<div class="panel-heading"><label for="file_refset_gpfp">Refset simple snapshot file</label></div>
  	 	<div class="panel-body">
			<div class="form-group col-sm-7" >
				<input type="file" name="file_refset_gpfp" class="file">
			    <p class="help-block">e.g der2_Refset_SimpleSnapshot_INT_20140930.txt </p>
			</div>
		</div> 
	</div>

	 <div class="panel panel-info">
	 	<div class="panel-heading"><label for="file_refset_full">Refset simple full file</label></div>
  	 	<div class="panel-body">
			<div class="form-group col-sm-7">
				<input type="file" name="file_refset_full" class="file">
			    <p class="help-block">e.g der2_Refset_SimpleFull_INT_20140731.txt </p>
	    	 </div>
		</div>
	</div>

       <div id="progressbox">
         <div id="progressbar"></div>
         <div id="percent">0%</div>
		<div id="outcome"></div>
       </div>

	<div class="form-group col-sm-5">
		<input type="submit"
			value="Generate Refset Diff Report" class="btn btn-primary btn-lg" id="reportbt">
	</div>
	</form>
</div>

	<script type="text/javascript" src="webjars/jquery/2.1.3/jquery.min.js"></script>
	<script type="text/javascript" src="webjars/bootstrap/3.3.1/js/bootstrap.min.js"></script>
	<script src="//oss.maxcdn.com/jquery.form/3.50/jquery.form.min.js"></script>
	
	<script type="text/javascript">
	
	$(document).ready(function() {
		var options = {
		        beforeSend : function() {
	                
		        	$("#progressbox").show();
	                // clear everything
	                $("#progressbar").width('0%');
	                $("#outcome").empty();
	                $("#percent").html("0%");
		        },
		        uploadProgress : function(event, position, total, percentComplete) {
                	$("#outcome").html("<font color='red'>Uploading refset files</font>");
		        	$("#progressbar").width(percentComplete + '%');
	                $("#percent").html(percentComplete + '%');
	                if (percentComplete == 100) {
	                	$("#outcome").html("<font color='red'>Generating diff report..</font>");

	                }
		        },
		        success : function() {
		                
	        		$("#progressbar").width('100%');
	                $("#percent").html('100%');
	                $("#outcome").html("<font color='red'>Generating diff report..</font>");

		        },
		        complete : function(response) {
		            console.log(response);
		            if(response.status == '400') {
			      		
		            	$("#outcome").html("<font color='red'> Bad Request</font>");

		            }
		            else if (response.responseText.indexOf('Refset_Diff_Report_') > -1){
		            	
			      		$("#outcome").html("<font color='red'> Report generation completed </font>");
			      		var url=window.location.protocol + "//" + window.location.host + "/refset/v1.0/refsets/downloadDiffReport/" + response.responseText; 			            
			      		window.open(url,'Download');

		            } else {
		            	
			      		$("#outcome").html("<font color='red'> " + response.responseText + " </font>");

		            }

		        	setTimeout(function() { $("#progressbox").hide(); }, 5000);
           	        
		        },
		        error : function() {
		        	
		        	$("#outcome").html("<font color='red'> ERROR: unable to upload files</font>");
		        }
		};
		$("#diffReportForm").ajaxForm(options);
		});
		
		$("#reportbt").submit(); //Submit the form

		
	</script>
</body>
</html>