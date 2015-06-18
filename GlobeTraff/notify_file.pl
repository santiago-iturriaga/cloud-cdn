#!/usr/bin/perl 

$fname = shift @ARGV; 
$size = 0;

$tsize = `wc -l data/$fname`; 
chomp $tsize; 
$tsize =~ s/^\s+//; 
($tsize, $dummy)  = split(/\s+/, $tsize); 
sleep 2; 
while($tsize ne  $size) { 
	$size = $tsize; 
	sleep 2; 
	$tsize = `wc -l data/$fname`; 
	chomp $tsize; 
	$tsize =~ s/^\s+//; 
	($tsize, $dummy)  = split(/\s+/, $tsize); 
} 

`exec wish notify.tcl $fname`;
exit 0; 
	
