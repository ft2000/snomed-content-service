<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1">
    <link rel='stylesheet' href='webjars/bootstrap/3.3.1/css/bootstrap.min.css'>
</head>
<body>

<div class="container">
	<form method="POST" enctype="multipart/form-data"
		action="/refset/v1.0/refsets/generateDiffReport" class="form-verticle">
		
		<h3><span class="label label-info">Please select desired release date and provide following files to generate refset diff report</span></h3>
		<br />
		<br />
		<div class="form-group col-sm-2">
		    <label for="releaseDate">SNOMED Release Date</label>
			<select class="form-control col-sm-2" name="releaseDate">
    			<option value="20150131">20150131</option>
			</select>
		</div> 
		
		<div class="form-group col-sm-3" >
		    <label for="file_refset_gpfp">Refset GPFP simple snapshot file</label>
			<input type="file" name="file_refset_gpfp" class="file">
		    <p class="help-block">e.g xder2 Refset GPFPSimpleSnapshot INT 20140930.txt </p>
		</div> 
		<div class="form-group col-sm-3">
		    <label for="file_refset_full">Refset GPFP simple full file</label>
			<input type="file" name="file_refset_full" class="file">
		    <p class="help-block">e.g der2 Refset SimpleFull INT 20140731.txt </p>
		</div>

  		<div class="form-group col-sm-3">
		    <label for="file_refset_identifiers">Refset identifier file</label>
		    <input type="file" name="file_refset_identifiers" class="file">
		    <p class="help-block">e.g refset-identifiers.txt </p>
		</div>
		<br />
		<br />
		<br /> 		
 		<div class="form-group col-sm-5">
				
		<input type="submit"
			value="Generate Refset Diff Report" class="btn btn-primary btn-lg">
		</div>
	</form>
</div>
	<script type="text/javascript" src="webjars/bootstrap/3.3.1/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="webjars/jquery/2.1.3/jquery.min.js"></script>
</body>
</html>