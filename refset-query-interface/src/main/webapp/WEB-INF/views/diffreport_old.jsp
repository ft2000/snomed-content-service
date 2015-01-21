<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1">
    <link rel='stylesheet' href='webjars/bootstrap/3.3.1/css/bootstrap.min.css'>
</head>
<body>
	<form method="POST" enctype="multipart/form-data"
		action="/refset/v1.0/refsets/generateDiffReport" >
		<h3>Please provide following files to  <span class="label label-info">generate refset diff report</span></h3>
  		<div class="form-group">
		    <label for="file_concept_snapshot">Concept snapshot file</label>
		    <input type="file" name="file_concept_snapshot" class="file">
		    <p class="help-block">e.g sct2_Concept_Snapshot_INT_20150131.txt </p>
		</div>
  		<div class="form-group">
		    <label for="file_concept_full">Concept full file</label>
			<input type="file" name="file_concept_full" class="file">
		    <p class="help-block">e.g sct2_Concept_Full_INT_20150131.txt </p>
		</div>
  		<div class="form-group">
		    <label for="file_description_snapshot">Description snapshot file</label>
		    <input type="file" name="file_description_snapshot" class="file">
		    <p class="help-block">e.g sct2_Description_Snapshot-en_INT_20150131.txt</p>
		</div>

		<div class="form-group">
		    <label for="file_refset_full">Refset GPFP simple snapshot file</label>
			<input type="file" name="file_refset_gpfp" class="file">
		    <p class="help-block">e.g xder2_Refset_GPFPSimpleSnapshot_INT_20140930.txt </p>
		</div> 
		<div class="form-group">
		    <label for="file_refset_full">Refset GPFP simple full file</label>
			<input type="file" name="file_refset_full" class="file">
		    <p class="help-block">e.g der2_Refset_SimpleFull_INT_20140731.txt </p>
		</div>
		
  		<div class="form-group">
		    <label for="file_refset_attribute_full">Refset attribute value full file</label>
			<input type="file" name="file_refset_attribute_full" class="file">
		    <p class="help-block">e.g der2_cRefset_AttributeValueFull_INT_20150131.txt</p>
		</div>

  		<div class="form-group">
		    <label for="file_refset_association_full">Refset Association reference snapshot file</label>
		    <input type="file" name="file_refset_association_snapshot" class="file">
		    <p class="help-block">e.g der2_cRefset_AssociationReferenceSnapshot_INT_20150131.txt </p>
		</div>
		
  		<div class="form-group">
		    <label for="file_refset_identifiers">Refset identifier file</label>
		    <input type="file" name="file_refset_identifiers" class="file">
		    <p class="help-block">e.g refset-identifiers.txt </p>
		</div>
				
		<input type="submit"
			value="Generate Refset Diff Report" class="btn btn-primary btn-lg">
	</form>
	<script type="text/javascript" src="webjars/bootstrap/3.3.1/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="webjars/jquery/2.1.3/jquery.min.js"></script>
</body>
</html>