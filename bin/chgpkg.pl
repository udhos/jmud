#!/usr/bin/perl -w

$oldpkg = shift @ARGV;
$newpkg = shift @ARGV;

for $i (@ARGV) {

    open INFILE,$i or die "Could not open $i for reading";
    @lines = <INFILE>;
    open OUTFILE,">$i" or die "Could not open $i for writing";

    for $line (@lines) {

	$line =~ s/\s+$//;

	if ($line =~ /^(package|import) $oldpkg(.*);$/o) {
	    print OUTFILE "$1 $newpkg$2;\n";
	    next;
	}

	print OUTFILE "$line\n";
    }
}
