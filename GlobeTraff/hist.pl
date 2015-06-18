#!/usr/bin/perl  


my $fname = shift @ARGV; 
my @arr; 


open(IN, "<$fname"); 

$all = 0; 
while($line = <IN>) { 
	chomp $line; 
	$line =~ s/^\s+//; 
	($blah, $blah1, $size) = split(/\s+/, $line); 
	$all += $size; 
} 
close(IN); 

	
$binsize = $all / 40; 
print "$all $binsize\n"; 

open(IN, "<$fname"); 


while($line = <IN>) { 
	$i = 0; 
	$temp = $binsize; 
	chomp $line; 
	$line =~ s/^\s+//; 
	($blah, $blah1, $size) = split(/\s+/, $line); 
#	print "$size\n"; 
	while ($temp lt $size) { 
		$i++; 
		$temp += $binsize; 
	} 
#	printf($i); 
	$arr[$i] += 1; 
}

close(IN); 

print "${arr}\n";
foreach my $in (@arr) { 
	print "$in - $arr[$i]\n"; 
} 

exit(0); 

