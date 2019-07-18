<?php

/*
* Script that submits a users job, used by ClassicalBuilder.
*
*/

include_once( "../inc/db.inc" );
include_once( "../inc/util.inc" );
include_once( "../inc/prefs.inc" );
include_once( "../inc/queue.inc" );

include_once( "CB_utils.php" );

db_init();

$jobapplication    = 2;
$jobname           = escapeshellcmd(addslashes($_REQUEST['name']));
if (!get_magic_quotes_gpc()) {
	$jobinput      = addslashes($_REQUEST['input']);
} else {
	$jobinput      = $_REQUEST['input'];
}
$jobfops           = 33211786946400;
$jobdisk           = 134217728;
$jobmem            = 536870912;

$user = check_user_login($_REQUEST['auth']);
if ($user === false) {
	create_error(100, 'The supplied authentication was incorrect.');
}

$config = get_config();
$name   = parse_config( $config, "<long_name>" );
$user   = get_logged_in_user();

$jobapplicationname = mysql_fetch_object( mysql_query( "SELECT * FROM app WHERE id=".$jobapplication ) );
$app = $jobapplicationname;
$jobapplicationfriendlyname = $jobapplicationname->user_friendly_name;
$jobapplicationname = $jobapplicationname->name;

if ($jobname == "" || strpos($jobname, "queue") || strpos($jobname, " ") || 
		strpos($jobname, '"') || strpos($jobname, "'") || strpos($jobname, "`") || strpos($jobname, "\\") ) {
	create_error(100, 'The supplied job name is invalid.');
}

if ($jobinput == "" ) {
	create_error(200, 'There was no input data.');
}
 
$appqmax = nr_of_jobs_for_user_for_app( $user, $app );
$appsubmitted = nr_of_submitted_jobs_for_user_for_app( $user, $app );

if ($appqmax <= $appsubmitted) {
	create_error(300, 'The supplied job name is invalid.');
}
 
$bin_dir = parse_config( $config, "<bin_dir>" );
$download_dir = parse_config( $config, "<download_dir>" );
$upload_dir = parse_config( $config, "<upload_dir>" );
$template_dir = parse_config( $config, "<template_dir>" );
$config_dir = parse_config( $config, "<project_dir>" );
$createworkprogram = parse_config( $config, "<create_work_program>" );

$extendedjobname = $jobname."_queue_".$jobapplication."_".time(0)."_".random_string();
$extendedjobname = escapeshellcmd( $extendedjobname );

$wu_template = $template_dir."/queue_".$jobapplicationname."_work_unit_template";
$result_template = $template_dir."/queue_".$jobapplicationname."_result_unit_template";
$temporaryinputfile = $extendedjobname;

$command_to_submit = $bin_dir."/".$createworkprogram;
$command_to_submit .= " -config_dir ".$config_dir;
$command_to_submit .= " -appname ".$jobapplicationname;
$command_to_submit .= " -wu_name ".$extendedjobname;
$command_to_submit .= " -wu_template ".$wu_template;
$command_to_submit .= " -result_template ".$result_template;
$command_to_submit .= " -rsc_fpops_est ".floor( ( float )( $jobfops ) );
$command_to_submit .= " -rsc_fpops_bound ".floor( 3.0 * ( float )( $jobfops ) );
$command_to_submit .= " -rsc_memory_bound ".floor( ( float )( $jobmem ) );
$command_to_submit .= " -rsc_disk_bound ".floor( ( float )( $jobdisk ) );
$command_to_submit .= " -priority 10 ";
$command_to_submit .= " ".$temporaryinputfile;
$command_to_submit = escapeshellcmd( $command_to_submit );
$command_to_submit = "cd ".$config_dir."; ".$command_to_submit;

$temporaryinputfile = $download_dir."/".$temporaryinputfile;

$filehandle = fopen($temporaryinputfile, "w");

if (!$filehandle) {
	create_error(400, 'Cannot create the temporary input file.');
}

if(!fwrite($filehandle, $jobinput)) {
	fclose( $filehandle );
	create_error(500, 'Cannot write to the temporary input file.');
}

fclose($filehandle);

if (strpos($jobapplicationname, "classical") !== false) {
	$testinputcommand = $bin_dir."/verify_classical_input ".$temporaryinputfile." /dev/null /dev/stdout /dev/stdout";
	$testinputcommand = escapeshellcmd($testinputcommand);
	$testinputcommand = "cd ".$config_dir."; ".$testinputcommand;
	$errorline = 0;
	exec( $testinputcommand, &$outputoftest, &$errorline );
	if ($errorline != 0) {
		$errorstring = "Your input data had an error on line ".$errorline.". The job was not submitted.";
		unlink( $temporaryinputfile );
		create_error(600, $errorstring ); 
	}
}

system($command_to_submit);

unlink($temporaryinputfile);

$workunit = mysql_fetch_object(mysql_query( "SELECT * FROM workunit WHERE name='".$extendedjobname."'" ));

if(!$workunit) {
	create_error(700, "Error during the submission of the workunit associated with your job." );
}

$qlistentry = mysql_query( "INSERT INTO q_list VALUES('','".$user->id."','".$workunit->id."')" );

if(!$qlistentry) {
	create_error(800, 'Error during submission of your job.');
}

$max_jobs = max_nr_of_jobs_of_user( $user );
$njobs = nr_of_jobs_of_user( $user );

?>
<lgi>
	<ca_certificate></ca_certificate>
	<server_max_field_size>65355</server_max_field_size>
	<response>
		<project>project</project>
		<project_master_server>http://localhost</project_master_server>
		<this_project_server> http://localhost</this_project_server>
		<user><?=$user['user']?></user>
		<groups><?=$user['user']?></groups>
		<job>
			<job_id><?=$workunit->id ?></job_id>
			<application>classicaldynamics</application>
			<state>queued</state>
			<target_resources>any</target_resources>
			<owners><?=$user['user']?></owners>
			<read_access><?=$user['user']?></read_access>
			<state_time_stamp><?=$workunit->create_time ?></state_time_stamp>
			<job_specifics>
				<job_name><?=jobname($workunit->name)?></job_name>
			</job_specifics>  
			<input></input>
		</job>
	</response>
</lgi>