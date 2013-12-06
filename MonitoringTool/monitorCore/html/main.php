<?php
include_once(dirname(__FILE__).'/includes/utils.inc.php');

$this_version="3.3.1";

	// RSS reader
	define('MAGPIE_DIR', './includes/rss/');
	define('MAGPIE_CACHE_ON', 0);
	define('MAGPIE_CACHE_AGE', 0);
	define('MAGPIE_CACHE_DIR', '/tmp/magpie_cache');
	require_once(MAGPIE_DIR.'rss_fetch.inc');

?>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<HTML>

<HEAD>
<META NAME="ROBOTS" CONTENT="NOINDEX, NOFOLLOW">
<TITLE>mFino Monitoring Tool</TITLE>
<LINK REL='stylesheet' TYPE='text/css' HREF='stylesheets/common.css'>
</HEAD>

<BODY id="splashpage">
	<div class="updateavailable">
		<div class="updatemessage">mFino Monitoring System</div>
	</div>
</BODY>

</HTML>

