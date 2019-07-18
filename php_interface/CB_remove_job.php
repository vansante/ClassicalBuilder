<?php

/*
* Script that removes a users job, used by ClassicalBuilder.
*
*/

include_once( "../inc/db.inc" );
include_once( "../inc/util.inc" );
include_once( "../inc/prefs.inc" );
include_once( "../inc/queue.inc" );

include_once( "CB_utils.php" );

db_init();

$user = check_user_login($_REQUEST['auth']);
if ($user === false) {
	create_error(100, 'The supplied authentication was incorrect.');
}

$workunitid  = intval($_REQUEST['job_id']);
$workunit    = mysql_fetch_object(mysql_query( "SELECT * FROM workunit WHERE id=".$workunitid));
$job         = mysql_fetch_object(mysql_query( "SELECT * FROM q_list WHERE workunit=".$workunitid));

$jobname     = workunit_name($workunit);

if ($user['id'] != $job->user) {
	create_error(100, 'You do not have the rights to delete this job.');
}

$config = get_config();

$allresults = mysql_query( "SELECT * FROM result WHERE workunitid=".$workunitid );
$nrofresults = mysql_num_rows($allresults);

for ($resultindex = 0; $resultindex < $nrofresults; ++$resultindex) {
	$result = mysql_fetch_object($allresults);
	$result->xml_doc_in = remove_tags($result->xml_doc_in, "<queue_tag/>");
	$query = "UPDATE result SET xml_doc_in='".$result->xml_doc_in."' WHERE id=".$result->id;
	mysql_query($query);
}

$query = "UPDATE result SET server_state=5,outcome=5 WHERE server_state=2 AND workunitid=".$workunit->id;
mysql_query($query);

$workunit->xml_doc = remove_tags($workunit->xml_doc, "<queue_tag/>");
$query = "UPDATE workunit SET xml_doc='".$workunit->xml_doc."' WHERE id=".$workunit->id;
mysql_query($query);

$query = "UPDATE workunit SET error_mask=error_mask|16,transition_time=".time(0)." WHERE id=".$workunit->id;
mysql_query($query);

$query = "DELETE FROM q_list WHERE id=".$job->id;
mysql_query($query);

?>
<lgi>
	<ca_certificate></ca_certificate>
	<server_max_field_size>65355</server_max_field_size>
	<response>
		<user><?=$user['name']?></user>
		<groups><?=$user['name']?></groups>
		<project>project</project>
		<project_master_server>http://localhost</project_master_server>
		<this_project_server>http://localhost</this_project_server>
        <job>
			<job_id><?=$workunit->id?></job_id>
			<state>Aborting</state>
			<application>classicaldynamics</application>
			<target_resources>any</target_resources>
			<owners><?=$user['name']?></owners>
			<read_access><?=$user['name']?></read_access>
			<state_time_stamp><?=$workunit->create_time?></state_time_stamp>
			<job_specifics>
				<job_name><?=jobname($workunit->name)?></job_name>
			</job_specifics>   
        </job>
	</response>
</lgi>
