#! /usr/bin/perl -w

my $gpl_begin = '-GNU-GPL-BEGIN-';
my $gpl_end   = '-GNU-GPL-END-';

my @header;

sub proc {
    my $java = shift;
    print "$java\n";

    local *IN;
    if (!open(IN, "<$java")) {
	warn "can't read file: $java: $!\n";
	return;
    }
    my @contents;
    SCAN: while (<IN>) {

      # remove GNU GPL
	if (/$gpl_begin/) {
	  END: while (<IN>) {
	      if (/$gpl_end/) {

		# remove brancos apos GNU GPL
		while (<IN>) {
		  if (/./) {
		    push(@contents, $_);
		    next SCAN;
		  }
		}
	      }
	  }
	}
	else {
	  push(@contents, $_);
	}
    }
    close(IN);

    local *OUT;
    if (!open(OUT, ">$java")) {
	warn "can't write file: $java: $!\n";
	return;
    }

    print OUT "/*$gpl_begin*\n";
    print OUT @header;
    print OUT "*$gpl_end*/\n";
    print OUT "\n";
    print OUT @contents;

    close(OUT);
}

sub process {
    my $f;
    foreach $f (@_) {
	&proc($f);
    }
}

sub explore {
    my $dir;
    foreach $dir (@_) {

	if (! -d $dir) {
	    warn "skipping: '$dir' is not a directory\n";
	    return;
	}
	
	if (! -r $dir) {
	    warn "skipping: can't read dir: $dir\n";
	    return;
	}
	
	local *DIR;
	if (!opendir(DIR, $dir)) {
	    warn "can't open dir: $dir: $!\n";
	    return;
	}
	my @entries = readdir DIR;
	closedir DIR;

	my $i;
	for ($i = 0; $i <= $#entries; ++$i) {
	    my $e = $entries[$i];
	    $entries[$i] = "$dir/$e";
	}

	my @javas = grep { /\.java$/ && -f "$_" } @entries;
	my @dirs = grep { ($_ !~ /\/\./) && -d "$_" } @entries;

	&process(@javas);
	&explore(@dirs);
    }
}

@header = <STDIN>;

&explore(@ARGV);

