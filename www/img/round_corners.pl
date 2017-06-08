#!/usr/bin/perl

# send a message to 19001 web1. He should fwd the message to web2 and web3

use strict;
use warnings;
use Cwd;

sub main;

my $RADIUS = '50';

push @ARGV, "crap.jpg";

if( @ARGV > 0) {
    my $input = $ARGV[0];
    my $output = q{};
    if(@ARGV > 1) {
        $output = $ARGV[1];
    }
    main($input, $output);
}
else {
    print "usage: round_corners.pl input.jpg [output.gif]\n";
}
exit(0);

sub main(@)
{
    my ($input, $output) = @_;
    if(! -e $input) {
        $input = cwd . '/' . $input;
        if(! -e $input) {
            print "FATAL: $input does not exist\n";
            exit(0);
        }
    }
    
    if($input !~ m/(gif|jpg|jpeg|png)$/) {
        print "FATAL: unknown format: $input\n";
        exit(0);
    }
    my $format = $1;
    
    # get the dimensions
    my $command = "identify $input";
    my $info = `$command`;
    if(not defined($info) or $info !~ m/\s(\d+)x(\d+)\s/) {
        print "FATAL: cannot \'identify\' this file: $input\n";
        exit(0);
    }
    my $width = $1;
    my $height = $2;
    print "WIDTH: $width HEIGHT: $height\n";
    my $dims = $width . 'x' . $height;
    
    
    # convert to png
    if($format ne 'png') {
        my $png = $input;
        $png =~ s/$format$/png/;
        my $command = "convert $input $png";
        system($command);
        $input = $png;
    }


    
    
    # create the transparent mask temp file
    my $mask_filename = cwd . '/mask.png';
    system("rm $mask_filename") if(-e $mask_filename);
    
    
    $command = "convert -size $dims xc:none -draw 'roundrectangle 0,0,$width,$height,$RADIUS,$RADIUS' $mask_filename";
    print "$command\n";
    system($command);
    
    if(! -e $mask_filename) {
        print "FATAL: cannot create mask file: $mask_filename\n";
        exit(0);
    }
    
    if($output !~ m/\w/) {
        $output = $input;
        $output =~ s/\.png$/.rounded_corners\.png/;
    }
    system("rm $output") if(-e $output);
    
    
    $command = "convert $input -matte $mask_filename -compose DstIn -composite $output";
    print "$command\n";
    system($command);
    
    if(! -e $output) {
        print "FATAL: cannot create mask file: $output\n";
        exit(0);
    }

    print "DONE.\n";
    system("eog $output");
    
    
    
    
}
 
