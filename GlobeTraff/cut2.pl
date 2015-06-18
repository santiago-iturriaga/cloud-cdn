#!/usr/bin/perl 

$all = 0; 
while ($line = <>) { 
	$line =~ s/^\s+//; 
	($lines, $dummy, $blah ) = split(/\s+/, $line); 
	$line =~ s/\s+//g; 
	$all += $line

}
$line = $all / 20;
($line, $dummy) = split(/\./, $line); 
print $line; 
