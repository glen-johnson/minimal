#!/usr/bin/perl

# run this on solr machine.
# reads /home/gimme/gimme.log every few seconds and prints stuff

use strict;
use warnings;

my $DELAY_TIME = 10;
my $GIMME_FILENAME = '/home/gimme/gimme.log';
if(! -e $GIMME_FILENAME) {
    print "FATAL: $GIMME_FILENAME does not exist\n";
    exit(0);
}

my $command = "wc -l $GIMME_FILENAME";
my $last_num_lines = 0;
my $num_lines = `$command`;
if($num_lines =~ m/\d+/) {
    $last_num_lines = $&;
}

if($last_num_lines > 0) {
   my $new_lines = 100;
   if($last_num_lines < $new_lines) {
      $new_lines = $last_num_lines;
   }
   my $tail_command = "tail -n $new_lines $GIMME_FILENAME";
   system($tail_command);
}

while(1) {
    sleep($DELAY_TIME);
    my $num_lines = `$command`;
    if($num_lines =~ m/\d+/) {
       $num_lines = $&;
       if($num_lines > $last_num_lines) {
	  my $new_lines = $num_lines - $last_num_lines;
          $last_num_lines = $num_lines;
	  my $tail_command = "tail -n $new_lines $GIMME_FILENAME";
          system($tail_command);
       }
   }
}
