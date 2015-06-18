#!/usr/bin/perl

$line = <>; 
$line =~ s/^\s+//g; 
($lines, $dummy) = split(/\s+/, $line); 
$line =~ s/\s+//g; 

print $lines; 

