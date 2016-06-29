#!/usr/bin/perl -w

$op = shift @ARGV;

for $i (@ARGV) {

    open INFILE,$i or die "Could not open $i for reading";
    @lines = <INFILE>;
    open OUTFILE,">$i" or die "Could not open $i for writing";

    for (@lines) {

	s/\s+$//;

	eval $op;

	print OUTFILE "$_\n";

    }

}


