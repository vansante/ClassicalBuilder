<?php
/*
* Script that shows a users job in XML format, used by ClassicalBuilder.
*
*/

include_once( "../inc/db.inc" );
include_once( "../inc/util.inc" );
include_once( "../inc/prefs.inc" );
include_once( "../inc/queue.inc" );

include_once( "CB_utils.php" );

db_init();

$config = get_config();

$user = check_user_login($_REQUEST['auth']);
if ($user === false) {
	create_error(100, 'The supplied authentication was incorrect.');
}

$workunitid = intval($_REQUEST['job_id']);

if ($workunitid != 0) {

	$query = mysql_query("SELECT * FROM workunit WHERE id=" . $workunitid );
	if (mysql_num_rows($query) != 1) {
		create_error(200, 'This workunit does not exist.');
	}
	$workunit = mysql_fetch_array($query);

	$query = mysql_query("SELECT * FROM q_list WHERE workunit=" . $workunitid . " AND user = " . $user['id']);
	if (mysql_num_rows($query) == 0) {
		create_error(300, 'You do not have access to this workunit.');
	}

	$jobinputurl = parse_element($workunit['xml_doc'], '<file_info>');
	$jobinputurl = parse_element($jobinputurl, '<url>');
	$jobinput = @binhex(file_get_contents($jobinputurl));

	$query = mysql_query("SELECT * FROM result WHERE id=".$workunit['canonical_resultid']);
	$resultunit = mysql_fetch_array($query);

	$cursor = 0;
	$outputfiles = array();
	while ($tempfileinfo = parse_next_element($resultunit['xml_doc_out'], "<url>", &$cursor)) {
		$outputfiles[] = parse_element($tempfileinfo, "<name>");
	}
	$urls = array();
	if (count($outputfiles) >= 1) {
		$fanoutnr = parse_config($config, "<uldl_dir_fanout>" );

		for ($index = 0; $index < count($outputfiles); ++$index) {
			$filename = $outputfiles[$index];
			$urls[] = "upload/".fan_out_dir($filename, $fanoutnr)."/".$filename;
		}
	}
?>
<lgi>
	<ca_certificate></ca_certificate>
	<server_max_field_size>65355</server_max_field_size>
	<response>
		<project>project</project>
		<user><?=$user['name']?></user>
		<groups><?=$user['name']?></groups>
		<project_master_server>http://localhost</project_master_server>
		<this_project_server>http://localhost</this_project_server>
		<number_of_jobs>1</number_of_jobs>
		<job number=1> 
			<job_id><?=$workunit['id']?></job_id>
			<state><?=status_string($workunit)?></state>
			<application>classicaldynamics</application>
			<target_resources></target_resources>
			<owners><?=$user['name']?></owners>
			<read_access><?=$user['name']?></read_access>
			<state_time_stamp><?=$workunit['create_time']?></state_time_stamp>
			<job_specifics>
				<job_name><?=jobname($workunit['name'])?></job_name>
			</job_specifics>   
			<input><?=$jobinput?></input>
			<output><?
				$output = "<number_of_urls>" . count($urls) . "</number_of_urls>";
				$i = 0; foreach ($urls as $url) { $i++;
					$output .= "<file_url number=" . $i . ">" . $url . "</file_url>";
				}
				echo binhex($output);
				?></output>
		</job>
	</response>
</lgi>
<?
	
} else {

	$sql = "SELECT l.user, w.* FROM q_list l, workunit w WHERE l.workunit = w.id AND w.appid = 2 AND l.user = " . $user['id'];
	$query = mysql_query($sql) or die("Could not execute query: <hr>" . $sql);
?>
<lgi>
	<ca_certificate></ca_certificate>
	<server_max_field_size>65355</server_max_field_size>
	<response>
		<project>project</project>
		<user><?=$user['name']?></user>
		<groups><?=$user['name']?></groups>
		<project_master_server> http://localhost</project_master_server>
		<this_project_server> http://localhost</this_project_server>
		<number_of_jobs><?=mysql_num_rows($query)?></number_of_jobs>
		<? $i = 0; while ($result = mysql_fetch_array($query)) { $i++; ?>
		<job number=<?=$i?>> 
			<job_id><?=$result['id']?></job_id>
			<state><?=status_string($result)?></state>
			<application>classicaldynamics</application>
			<target_resources>any</target_resources>
			<owners><?=$user['name']?></owners>
			<read_access><?=$user['name']?></read_access>
			<state_time_stamp><?=$result['create_time']?></state_time_stamp>
			<job_specifics>
				<job_name><?=jobname($result['name'])?></job_name>
			</job_specifics>
		</job>
		<? } ?>
	</response>
</lgi>
<?

}

?>