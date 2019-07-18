<?php

function create_error($number, $message) {
	echo "<lgi>\n";
	echo "	<response>\n";
	echo "		<error>\n";
	echo "			<number>" . $number . "</number>\n";
	echo "			<message>" . $message . "</message>\n";
	echo "		</error>\n";
	echo "	</response>\n";
	echo "</lgi>\n";
	die();
}

function check_user_login($auth) {
	$sql = "SELECT * FROM user WHERE authenticator = '" . mysql_escape_string($auth) . "'";
	$query = mysql_query($sql);
	if (mysql_num_rows($query) == 1) {
		return mysql_fetch_array($query);
	}
	return false;
}

function status_string($workunit) {
	$status = "Unknown";
	if( $workunit['canonical_resultid']) {
		$status = "Finished";
	} else {
		if ($workunit['hr_class']) {
			$status = "Running";
		} else {
			$status = "Queued";
		}
	}
	if ($workunit['error_mask']) {
		$status = "Error";
	}
	if ($workunit['error_mask'] & 16) {
		$status = "Canceled";
	}
	return $status;
}

function jobname($name) {
	if (strpos($name, "_queue") === false) {
		return $name;
	} else {
		return substr($name, 0, strpos($name, "_queue"));
	}
}

function hexbin( $hex ) 
{
 $HEXDIGITS = "0123456789ABCDEF";

 $Length = strlen( $hex );
 $bin = "";

 $i = 0; 
 while( $hex[ $i ] == ' ' ) $i++;

 for( ; $i < $Length; $i += 2 )
 {
  $HighNibble = strpos( $HEXDIGITS, $hex[ $i ] );
  if( $HighNibble === FALSE ) return( "" );
  $LowNibble  = strpos( $HEXDIGITS, $hex[ $i + 1 ] );
  if( $LowNibble === FALSE ) return( "" );
  $Byte       = ( $HighNibble << 4 ) | $LowNibble;
  $bin       .= chr( $Byte );
 }

 return( $bin );
}

// --------------------------------------------------- 

function binhex( $bin )
{
 $HEXDIGITS = "0123456789ABCDEF";

 $Length = strlen( $bin );
 $hex = "";

 for( $i = 0; $i < $Length; $i++ )
 {
  $Byte       = ord( $bin[ $i ] ) & 0xFF;
  $HighNibble = $Byte >> 4;
  $LowNibble  = $Byte & 0x0F;
  $hex       .= $HEXDIGITS[ $HighNibble ].$HEXDIGITS[ $LowNibble ];
 }

 return( $hex );
}


?>