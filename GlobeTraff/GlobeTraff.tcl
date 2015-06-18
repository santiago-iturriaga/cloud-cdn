
##########################################################
#                                                        #
#                 Web Workload Generator                 #
#                                                        #
#                 Version 1: November 2001               #
#                                                        #
##########################################################

# This program uses several procedures written in C.
# You can create an executable version of this script by amalgamating this
#  with the following C programs, using "xmktclapp" and compiling the output
#  as specified under usage.

# C procedures used by this script.
#   
#   MeanStd.c  // To determine the mean and STD of a time series.
#   buckets.c  // To create marginal distribution plot

# Requirements: webTraff requires the following executable program  
#                        in the working directory
#        ProWGen

# Usage: Specific for Skorpio machine
#   gcc -o webTraff MeanStd.c mult.c sprinkle.c aCorr.c rs.c variance.c buckets.c webTraff.c -L/sw/arch/lib -ltk8.0 -ltcl8.0 -lX11 -lm -ldl
#   gcc -static -o webTraff MeanStd.c mult.c sprinkle.c aCorr.c rs.c variance.c buckets.c webTraff.c -L/sw/arch/lib -ltk8.0 -ltcl8.0 -lX11 -lm -lsocket -lnsl -I/sw/ind/include
#   webTraff &

#--------------------------------------------------------------------------

# Set window title
wm title . "Web Proxy Workload Generation Kit"

. config -bg #cccccc

#--------------------------------------------------------------------------
#        *** globFNGeneration Module ***

frame .gn -relief groove -borderwidth 3 -bg #cccccc
pack .gn -side top -expand y -fill both


# Enter the name for the output file to store the Web workload


label .gn.inFlL -text "Output file name: " -bg #cccccc -fg black
grid .gn.inFlL -row 1 -column 1

entry .gn.inFl -width 20 -textvariable reqFl -xscrollcommand cpFlN \
	-bg #cccccc -fg black
grid .gn.inFl -row 1 -column 2 

set reqFl "default"

proc cpFlN {x1 x2} {
    global reqFl inFileName SimFn DisFn LruFn
    set inFileName "$reqFl"
    set SimFn "$reqFl"
    set DisFn "$reqFl"
    set LruFn stack.dat
} 

label .gn.lRUfile -text "LRU stack probabilities file: " -bg #cccccc -fg black
grid .gn.lRUfile -row 1 -column 3 

entry .gn.lrufn -width 20 -textvariable LruFn -bg #cccccc -fg black
grid .gn.lrufn -row 1 -column 4 

# Number of references to generate in Web workload

frame .gn.dSlFm -relief groove -borderwidth 3 -bg #cccccc
grid .gn.dSlFm -row 2 -column 1

scale .gn.dSlFm.rng -from 1 -to 68 -bigincrement 10 -command DscaleP \
      -orient horizontal -length 230 -variable DIscl -showvalue 0 \
      -bg #cccccc -fg black

.gn.dSlFm.rng set 20
set NumRefs 100

proc DscaleP {Ascl} {
    global NumRefs
    if {$Ascl <= 10} {
	set NumRefs [expr $Ascl - 1]
	.gn.dSlFm.rngVal configure -text "Num References:    $NumRefs"
    } elseif {$Ascl <= 20} {
	set NumRefs [expr ($Ascl - 10) * 10]
	.gn.dSlFm.rngVal configure -text "Num References:   $NumRefs"
    } elseif {$Ascl <= 29} {
	set NumRefs [expr ($Ascl - 19) * 100]
	.gn.dSlFm.rngVal configure -text "Num References:  $NumRefs"
    } elseif {$Ascl <= 39} {
	set NumRefs [expr ($Ascl - 29) * 1000]
	.gn.dSlFm.rngVal configure -text "Num References:  $NumRefs"
    } elseif {$Ascl <= 49} {
	set NumRefs [expr ($Ascl - 39) * 10000]
	.gn.dSlFm.rngVal configure -text "Num References:  $NumRefs"
    } elseif {$Ascl <= 59} {
	set NumRefs [expr ($Ascl - 49) * 100000]
	.gn.dSlFm.rngVal configure -text "Num References:  $NumRefs"
    } else {
	set NumRefs [expr ($Ascl - 58) * 1000000]
	.gn.dSlFm.rngVal configure -text "Num References: $NumRefs"
    }    
}

label .gn.dSlFm.rngVal -text "Num References:    100" -bg #cccccc -fg black

label .gn.dSlFm.xAxVal -text "1 10  100  1K  10K  100K  1M  10M" \
	-bg #cccccc -fg black

pack .gn.dSlFm.rngVal .gn.dSlFm.rng .gn.dSlFm.xAxVal -side top



# Number of documents in the workload, as percentage of references

frame .gn.docsFm -relief groove -borderwidth 3 -bg #cccccc 
grid .gn.docsFm -row 2 -column 2

scale .gn.docsFm.sl -from 0 -to 100 -bigincrement 10 -command DocsP \
      -orient horizontal -length 200 -variable Docs  \
      -resolution 1 -showvalue 0 -bg #cccccc -fg black

.gn.docsFm.sl set 30 
label .gn.docsFm.slVal -text "Documents Percentage: " -bg #cccccc -fg black

proc DocsP {Ascl} {
	.gn.docsFm.slVal configure -text "Documents (% references): $Ascl"
}

label .gn.docsFm.xAxVal -text "0 10 20 30 40 50 60 70 80 90 100" \
	-bg #cccccc -fg black

pack .gn.docsFm.slVal .gn.docsFm.sl .gn.docsFm.xAxVal -side top



# Percentage of "one-timers" in the workload

frame .gn.onetimerFm -relief groove -borderwidth 3 -bg #cccccc 
grid .gn.onetimerFm -row 2 -column 3 

scale .gn.onetimerFm.sl -from 0 -to 100 -bigincrement 10 -command oneP \
      -orient horizontal -length 200 -variable OneTimers  \
      -resolution 1 -showvalue 0 -bg #cccccc -fg black

.gn.onetimerFm.sl set 70 
label .gn.onetimerFm.slVal -text "Percentage One-Timers: " -bg #cccccc -fg black

proc oneP {Ascl} {
	.gn.onetimerFm.slVal configure -text "One-Timers (% of documents): $Ascl"
}

label .gn.onetimerFm.xAxVal -text "0 10 20 30 40 50 60 70 80 90 100" \
	-bg #cccccc -fg black

pack .gn.onetimerFm.slVal .gn.onetimerFm.sl .gn.onetimerFm.xAxVal -side top



# Zipf slope

frame .gn.zipfFm -relief groove -borderwidth 3 -bg #cccccc
grid .gn.zipfFm -row 2 -column 4

scale .gn.zipfFm.sl -from 0.0 -to 1.0 -bigincrement 0.1 -command VslP \
      -orient horizontal -length 200 -variable Zipf  \
      -resolution .01 -showvalue 0 -bg #cccccc -fg black

.gn.zipfFm.sl set 0.75
label .gn.zipfFm.slVal -text "Zipf Slope: 0.75" -bg #cccccc -fg black

proc VslP {Ascl} {
	.gn.zipfFm.slVal configure -text "Zipf Slope: $Ascl"
}

label .gn.zipfFm.xAxVal -text "0.0  0.2  0.4  0.6  0.8  1.0" \
	-bg #cccccc -fg black

pack .gn.zipfFm.slVal .gn.zipfFm.sl .gn.zipfFm.xAxVal -side top


# Pareto tail index for heavy-tailed file size distribution

frame .gn.tailFm -relief groove -borderwidth 3 -bg #cccccc
grid .gn.tailFm -row 3 -column 1

scale .gn.tailFm.sl -from 1.0 -to 2.0 -bigincrement 0.1 -command TaillP \
      -orient horizontal -length 230 -variable Tail  \
      -resolution .01 -showvalue 0 -bg #cccccc -fg black

.gn.tailFm.sl set 1.2
label .gn.tailFm.slVal -text "Pareto Tail Index: 1.2" -bg #cccccc -fg black

proc TaillP {Ascl} {
	.gn.tailFm.slVal configure -text "Pareto Tail Index: $Ascl"
}

label .gn.tailFm.xAxVal -text "1.0  1.2  1.4  1.6  1.8  2.0" \
	-bg #cccccc -fg black

pack .gn.tailFm.slVal .gn.tailFm.sl .gn.tailFm.xAxVal -side top


# Correlation between size and popularity

frame .gn.corrFm -relief groove -borderwidth 3 -bg #cccccc
grid .gn.corrFm -row 3 -column 2

scale .gn.corrFm.sl -from -1.0 -to 1.0 -bigincrement 1.0 -command CorrlP \
      -orient horizontal -length 200 -variable Corr  \
      -resolution .01 -showvalue 0 -bg #cccccc -fg black

.gn.corrFm.sl set 0.0
label .gn.corrFm.slVal -text "Size-Popularity Correlation: 0.0" -bg #cccccc -fg black

proc CorrlP {Ascl} {
	.gn.corrFm.slVal configure -text "Size-Popularity Correlation: $Ascl"
}

label .gn.corrFm.xAxVal -text "-1.0     0.0     1.0" \
	-bg #cccccc -fg black

pack .gn.corrFm.slVal .gn.corrFm.sl .gn.corrFm.xAxVal -side top


#LRU Stack Depth

frame .gn.lrusd -relief groove -borderwidth 3 -bg #cccccc
grid .gn.lrusd -row 3  -column 3 

scale .gn.lrusd.sl -from 1 -to 29 -bigincrement 10 -command LruSD \
      -orient horizontal -length 200 -variable lruVar  -showvalue 0 \
      -bg #cccccc -fg black
.gn.lrusd.sl set 20
set LruSd 100

proc LruSD {Ascl} {
    global LruSd
    if {$Ascl <= 10} {
	set LruSd [expr $Ascl - 1]
	.gn.lrusd.slVal configure -text "LRU Stack Depth:  $LruSd"
    } elseif {$Ascl <= 20} {
	set LruSd [expr ($Ascl - 10) * 10]
	.gn.lrusd.slVal configure -text "LRU Stack Depth:  $LruSd"
    } else {
	set LruSd [expr ($Ascl - 19) * 100]
	.gn.lrusd.slVal configure -text "LRU Stack Depth: $LruSd"
    }    
}

label .gn.lrusd.slVal -text "LRU Stack Depth:  100" -bg #cccccc -fg black

label .gn.lrusd.xAxVal -text "1   10   100   1000" -bg #cccccc -fg black

pack .gn.lrusd.slVal .gn.lrusd.sl .gn.lrusd.xAxVal 


#Popularity Bias 
frame .gn.popBias -relief groove -borderwidth 3 -bg #cccccc
grid .gn.popBias -row 4 -column 1

scale .gn.popBias.sl -from 0.0 -to 1.0 -bigincrement 0.1 -command VslPb \
      -orient horizontal -length 230 -variable PopBias \
      -resolution .01 -showvalue 0 -bg #cccccc -fg black

.gn.popBias.sl set 0.20
label .gn.popBias.slVal -text "Popularity Bias: 0.20" -bg #cccccc -fg black

proc VslPb {Ascl} {
	.gn.popBias.slVal configure -text "Popularity Bias: $Ascl"
}

label .gn.popBias.xAxVal -text "0.0  0.2  0.4  0.6  0.8  1.0" \
	-bg #cccccc -fg black

pack .gn.popBias.slVal .gn.popBias.sl .gn.popBias.xAxVal -side top


# Create LRU Stack modedl Radio buttons. 



radiobutton .gn.lrust -variable LruSt -command {set LruDyn 0; set LruCnm 0; set LruIm 0} -text "LRU Static Stack Model"
radiobutton .gn.lrudyn -variable LruDyn -command {set LruSt 0; set LruCnm 0; set LruIm 0 } -text "LRU Dynamic Stack  Model"
radiobutton .gn.lrucnm -variable LruCnm -command {set LruSt 0; set LruDyn 0; set LruIm 0} -text "New LRU Stcak Model"
radiobutton .gn.lruim -variable LruIm -command {set LruSt 0; set LruDyn 0; set LruCnm 0} -text "Independent Stack Model"

set LruDyn 0
set LruCnm 0
set LruIm 0
set LruSt 1

.gn.lrust select
.gn.lrudyn deselect
.gn.lrucnm deselect
.gn.lruim deselect

grid .gn.lrust -row 4 -column 3 
grid .gn.lrudyn -row 4 -column 2 
grid .gn.lrucnm -row 5 -column 2 
grid .gn.lruim -row 5 -column 3 


# Create Action Buttons

button .gn.gen -text "Generate" -command genP -bg #cccccc -fg black -borderwidth 7 -heigh 2 -width 20
grid .gn.gen -row 4 -column 4

proc genP {} {
    global reqFl NumRefs Docs OneTimers Zipf Tail Corr LruSd LruDyn LruSt LruCnm LruIm PopBias LruFn


	if {$LruIm != 0} { 
		set Model 0
	} elseif {$LruSt != 0} { 	
		set Model 1
	} elseif {$LruDyn != 0} { 
		set Model 2
	} else { 
		set Model 3
	} 
	
	puts " --- -- "
    #./ProWGen 0 "default" 100 30 100 0.71 1.20 0 100 0
    # First parameter for traffic type, Web: 1, P2P: 2, Video: 3, All (+other): 0 
    exec ./ProWGen 1 "default" $NumRefs $Docs $OneTimers $Zipf $Tail $Corr $LruSd 0 
	exec ./freqsize < data/$reqFl > data/docs.dat
	set ifv [exec ./lc.pl data/$reqFl]
	set dfv [exec ./lc.pl data/docs.dat]
	exec ./lrustack $LruFn $PopBias $LruSd $ifv $dfv $Model > data/$reqFl.tmp
	exec mv data/$reqFl.tmp data/$reqFl	
}

button .gn.rst -text "Reset" -command rstP -bg #cccccc -fg black -height 2 -width 20
grid .gn.rst -row 3 -column 4 


proc rstP {} {
     global reqFl inFileName SimFn DisFn LruFn LruDyn LruSt LruCnm LruIm PopBasis
     set reqFl "default"
     set inFileName $reqFl
     set SimFn  $reqFl
     set DisFn $reqFl
     set LruFn stack.dat

    .gn.lrusd.sl set 20
    .gn.dSlFm.rng set 20
    .gn.docsFm.sl set 30 
    .gn.popBias.sl set 0.2
    .gn.onetimerFm.sl set 70 
    .gn.zipfFm.sl set 0.75
    .gn.tailFm.sl set 1.2
    .gn.corrFm.sl set 0.0
    set LruDyn 0 
    set LruCnm 0 
    set LruIm 0 
    set LruSt 1
    .gn.lrust select
    .gn.lrudyn deselect
    .gn.lrucnm deselect
    .gn.lruim  deselect
}

##########################################################
#                                                        #
#                       Display Module                   #
#                                                        #
##########################################################


frame .main -relief groove -borderwidth 3 -bg #cccccc
frame .main.ser -relief groove -borderwidth 3 -bg #cccccc
frame .main.frm -relief groove -borderwidth 3 -bg #cccccc
frame .main.dis -relief groove -borderwidth 3 -bg #cccccc

pack .main.ser -side left -expand y -fill both 
pack .main -side top -expand y -fill both
pack .main.dis -side left -expand y -fill both 

# Entry for the filename

label .main.dis.fnL -text "Data file name: " -bg #cccccc -fg black
grid .main.dis.fnL -row 1 -column 1

entry .main.dis.fn -width 10 -textvariable DisFn -bg #cccccc -fg black
grid .main.dis.fn -row 1 -column 2 

# Entry for the Title

label .main.dis.tlL -text "Graph Title (optional):" -bg #cccccc -fg black
grid .main.dis.tlL -row 1 -column 3

entry .main.dis.tl -width 10 -textvariable DisTl  -bg #cccccc -fg black
grid .main.dis.tl -row 1 -column 4 

# Entry for Profile X limit

label .main.dis.pXlL -text "X Limit (optional): " -bg #cccccc -fg black
grid .main.dis.pXlL -row 2 -column 1

entry .main.dis.pXl -width 10 -textvariable PdisXl  -bg #cccccc -fg black
grid .main.dis.pXl -row 2 -column 2 

# Entry for Autocorrelation X limit

label .main.dis.aLgL -text "AC lags (optional): " -bg #cccccc -fg black
grid .main.dis.aLgL -row 2 -column 3

entry .main.dis.aLg -width 10 -textvariable Alag -bg #cccccc -fg black 
grid .main.dis.aLg -row 2 -column 4 

# Entry for Distribution limit

label .main.dis.mDisL -text "Size Limit (optional): " -bg #cccccc -fg black
grid .main.dis.mDisL -row 3 -column 1

entry .main.dis.mDis -width 10 -textvariable MdisL -bg #cccccc -fg black 
grid .main.dis.mDis -row 3 -column 2 

# Entry for Bytes per Interval

label .main.dis.mBpI -text "Time Interval BPI (optional): " -bg #cccccc -fg black
grid .main.dis.mBpI -row 3 -column 3 

entry .main.dis.mBpit -width 10 -textvariable MBpI -bg #cccccc -fg black 
grid .main.dis.mBpit -row 3 -column 4 

# Entry for Requests per Interval

label .main.dis.mRpI -text "Time Interval RPI (optional): " -bg #cccccc -fg black
grid .main.dis.mRpI -row 4 -column 1 

entry .main.dis.mRpit -width 10 -textvariable MRpI -bg #cccccc -fg black 
grid .main.dis.mRpit -row 4 -column 2 

# No.of distributions 

frame .main.dis.mdF -relief groove -borderwidth 3 -bg #cccccc
grid .main.dis.mdF -row 5 -column 4 -columnspan 1 

scale .main.dis.mdF.rng -from 1 -to 29 -bigincrement 10 -command mdSlP \
      -orient horizontal -length 200 -variable mdIscl -showvalue 0 \
      -bg #cccccc -fg black
.main.dis.mdF.rng set 20
set mdSl 100

proc mdSlP {Ascl} {
    global mdSl
    if {$Ascl <= 10} {
	set mdSl [expr $Ascl - 1]
	.main.dis.mdF.rngVal configure -text "Distributions:   $mdSl"
    } elseif {$Ascl <= 20} {
	set mdSl [expr ($Ascl - 10) * 10]
	.main.dis.mdF.rngVal configure -text "Distributions:  $mdSl"
    } else {
	set mdSl [expr ($Ascl - 19) * 100]
	.main.dis.mdF.rngVal configure -text "Distributions: $mdSl"
    }    
}

label .main.dis.mdF.rngVal -text "Distributions:  100" -bg #cccccc -fg black

label .main.dis.mdF.xAxVal -text "1	10	100	1000" \
	-bg #cccccc -fg black

pack .main.dis.mdF.rngVal .main.dis.mdF.rng .main.dis.mdF.xAxVal -side top
 
radiobutton .main.dis.mdPoi -variable MdPoi -command {set MdLin 0; set MdImp 0} \
	               -text "Points"
radiobutton .main.dis.mdImp -variable MdImp -command {set MdPoi 0; set MdLin 0} \
	               -text "Impulses"
radiobutton .main.dis.mdLin -variable MdLin -command {set MdPoi 0; set MdImp 0} \
	               -text "Lines"
.main.dis.mdPoi select
.main.dis.mdLin deselect
.main.dis.mdImp deselect
set MdLin 0
set MdImp 0
grid .main.dis.mdPoi -row 5 -column 1 
grid .main.dis.mdLin -row 5 -column 2
grid .main.dis.mdImp -row 5 -column 3

# Create Action Button


#Inter Arrival Time
button .main.ser.iat -text "Interarrival Time" -command serIat -bg #cccccc -fg black
pack .main.ser.iat -side top -expand y -fill both

proc serIat {} {
    global DisFn DisTl MdPoi MdLin MdImp

    exec cat data/$DisFn | ./iat > data/$DisFn.iat

    if {$DisTl == ""} {
	set IatTl "Interarrival time of $DisFn"
    } else {
	set IatTl $DisTl
    }
#    set Lags [exec cat data/$DisFn.rpi | ./get_time | tail -1]

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    mkIatGnu $DisFn $IatTl $sty
    exec gnuplot data/$DisFn.iat.gnu
    exec ghostview results/$DisFn.iat.ps &
}

proc mkIatGnu {file title sty} {
    set wFn "data/$file.iat.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"results/$file.iat.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "set xlabel \"Requests\" "
    puts $wfp "set ylabel \"Interarrival Time\" "
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[0:\] \[0:\] 'data/$file.iat' with $sty"
    close $wfp
}


# Request per Interval button
button .main.ser.rPi -text "Requests Per Interval" -command serRpI -bg #cccccc -fg black
pack .main.ser.rPi -side top -expand y -fill both

proc serRpI {} {
    global DisFn DisTl MdPoi MdLin MdImp MRpI

    exec cat data/$DisFn | ./strip_col3 > data/$DisFn.tmp
    
    if {$MRpI != ""} { 
    	exec cat data/$DisFn.tmp | ./reqsperinterval $MRpI > data/$DisFn.rpi
    } else { 
	exec cat data/$DisFn.tmp | ./reqsperinterval > data/$DisFn.rpi
    }

    exec cat data/$DisFn | ./iat2 > data/$DisFn.iat2
    exec ./histogram -b 1 data/$DisFn.iat2 data/$DisFn.rpiht
    exec ./hist  data/$DisFn.rpiht > data/$DisFn.rpih


    if {$DisTl == ""} {
	set RpiTl "Requests Per Interval of $DisFn"
    } else {
	set RpiTl $DisTl
    }
    set Lags [exec cat data/$DisFn.rpi | ./get_time | tail -1]

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    mkRpiGnu $DisFn $RpiTl $Lags $sty
    mkRpi2Gnu $DisFn $RpiTl $sty
    exec gnuplot data/$DisFn.rpi.gnu
    exec gnuplot data/$DisFn.rpih.gnu
    exec ghostview results/$DisFn.rpi.ps &
    exec ghostview results/$DisFn.rpih.ps &


}

proc mkRpiGnu {file title lag sty} {
    set wFn "data/$file.rpi.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"results/$file.rpi.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "set ylabel \"Requests\" "
    puts $wfp "set xlabel \"Intervals\" "
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[0:$lag] \[0:\] 'data/$file.rpi' with $sty"
    close $wfp
}

proc mkRpi2Gnu {file title sty} {
    set wFn "data/$file.rpih.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"results/$file.rpih.ps\" "
    puts $wfp "set title \"File Size Distribution\" "
    puts $wfp "set ylabel \"Frequency (%)\" "
    puts $wfp "set xlabel \"Time Interval (seconds)\" "
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[0:\] \[0:\] 'data/$file.rpih' with steps"
    close $wfp
}


# Bytes per Interval button. 
button .main.ser.bPi -text "Bytes Per Interval" -command serBpI -bg #cccccc -fg black
pack .main.ser.bPi -side top -expand y -fill both


proc serBpI {} {
    global DisFn DisTl MdPoi MdLin MdImp MBpI
	
    set numel [exec cat data/$DisFn | ./cut2.pl]
    exec cat data/$DisFn | ./strip_col2 > data/$DisFn.tmp
    exec cat data/$DisFn | ./col3 > data/$DisFn.bpit
    exec ./histogram -b 5000  data/$DisFn.bpit data/$DisFn.bpiht 
    exec ./hist data/$DisFn.bpiht > data/$DisFn.bpih
    if {$MBpI != ""} { 
	    exec cat data/$DisFn.tmp | ./bytesperinterval $MBpI > data/$DisFn.bpi
    } else { 
	    exec cat data/$DisFn.tmp | ./bytesperinterval > data/$DisFn.bpi
    }

    if {$DisTl == ""} {
	set BpiTl "Bytes Per Interval of $DisFn"
    } else {
	set BpiTl $DisTl
    }
    set Lags [exec cat data/$DisFn.bpi | ./get_time | tail -1]

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    mkBpiGnu $DisFn $BpiTl $Lags $sty
    mkBpi2Gnu $DisFn $BpiTl $sty
    exec gnuplot data/$DisFn.bpi.gnu
    exec gnuplot data/$DisFn.bpih.gnu
    exec ghostview results/$DisFn.bpi.ps &
    exec ghostview results/$DisFn.bpih.ps &
}

proc mkBpiGnu {file title lag sty} {
    set wFn "data/$file.bpi.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"results/$file.bpi.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "set ylabel \"Bytes\" "
    puts $wfp "set xlabel \"Intervals\" "
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[0:$lag] \[0:\] 'data/$file.bpi' with $sty"
    close $wfp
}


proc mkBpi2Gnu {file title sty} {
    set wFn "data/$file.bpih.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"results/$file.bpih.ps\" "
    puts $wfp "set title \"File Size Distribution\" "
    puts $wfp "set ylabel \"Frequency (%)\" "
    puts $wfp "set xlabel \"File Size (bytes)\" "
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[0:\] \[0:\] 'data/$file.bpih' with steps"
    close $wfp
}



#Variance Time Button
button .main.ser.vt -text "Variance Time" -command serVt -bg #cccccc -fg black
pack .main.ser.vt -side top -expand y -fill both

proc serVt {} {
    global DisFn DisTl MdPoi MdLin MdImp

    exec ./variance data/$DisFn data/$DisFn.var

    if {$DisTl == ""} {
	set VarTl "Variance of $DisFn"
    } else {
	set VarTl $DisTl
    }

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    mkVarGnu $DisFn $VarTl $sty
    exec gnuplot data/$DisFn.var.gnu
    exec ghostview results/$DisFn.var.ps &
}

proc mkVarGnu {file title sty} {
    set wFn "data/$file.var.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"results/$file.var.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "set ylabel \"Variance of Sample Mean\" "
    puts $wfp "set xlabel \"Aggregation Level m\" "
    puts $wfp "set logscale x"
    puts $wfp "set logscale y"
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot 'data/$file.var' with $sty"
    close $wfp
}


#R/S analysis button. 
button .main.ser.rs -text "R/S Statistics" -command serRs -bg #cccccc -fg black 
pack .main.ser.rs -side top -expand y -fill both

proc serRs {} {
    global DisFn DisTl MdPoi MdLin MdImp
   
    set lines [exec wc -l data/$DisFn | ./cut.pl ]
    exec cat data/$DisFn | ./rs > data/$DisFn.rs
    if {$DisTl == ""} {
	set RsTl "Rescale Adjust Range Statistics of $DisFn"
    } else {
	set RsTl $DisTl
    }

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    mkRsGnu $DisFn $RsTl $sty
    exec gnuplot data/$DisFn.rs.gnu
    exec ghostview results/$DisFn.rs.ps &
}

proc mkRsGnu {file title sty} {
    set x [exec ./rs_x < data/$file.rs]
    set y [exec ./rs_y < data/$file.rs]
    set wFn "data/$file.rs.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"results/$file.rs.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "set ylabel \"R/S Statistic\" "
    puts $wfp "set xlabel \"Sample Size n\" "
    puts $wfp "set logscale x"
    puts $wfp "set logscale y"
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[1:$x] \[1:$y] 'data/$file.rs' with $sty,'rs1.dat' with lines, 'rs5.dat' with lines"
    close $wfp
}


#Autocorrelation Button. 
button .main.ser.aB -text "Autocorrelation" -command serAcP \
                   -bg #cccccc -fg black
pack .main.ser.aB -side top -expand y -fill both

proc serAcP {} {
    global DisFn DisTl Alag MdPoi MdLin MdImp
    if {$DisTl == ""} {
	set AcTl "Autocorrelation of $DisFn"
    } else {
	set AcTl $DisTl
    }

    if {$Alag == ""} {
	set TLags [exec cat data/$DisFn | wc -l]
	if {$TLags < 100} {
	    set Lags $TLags
	} else {
	    set Lags 100
	}
    } else {
	set Lags $Alag
    }

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    exec cat data/$DisFn | ./col2 > data/$DisFn.tmp
    exec ./autocorr data/$DisFn.tmp 1 0 $Lags > data/$DisFn.ac
    mkAcGnu $DisFn $AcTl $Lags $sty
    exec gnuplot data/$DisFn.ac.gnu
    exec ghostview results/$DisFn.ac.ps &
}

proc mkAcGnu {file title lag sty} {
    set wFn "data/$file.ac.gnu"
    set wfp [open $wFn w]
    puts $wfp "set ylabel \"Autocorrelation\" "
    puts $wfp "set xlabel \"Lag\" "
    puts $wfp "set nokey"
    puts $wfp "set terminal postscript"
    puts $wfp ""
    puts $wfp "set output \"results/$file.ac.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "plot \[0:$lag\] \[-1:1\] 'data/$file.ac' with $sty"
    close $wfp
}


button .main.frm.lrusdB -text "LRU Stack Depth Analysis" -command disLrusd -bg #cccccc -fg black
pack .main.frm.lrusdB -side top -expand y -fill both

proc disLrusd {} { 
    global DisFn DisTl mdSl MdisL MdLin MdPoi MdImp
    if {$DisTl == ""} {
	set MdTl "LRU Stack Depth Analysis"
    } else {
	set MdTl $DisTl
    }
    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    exec ./lrusd < data/$DisFn > results/lrusd.dat
    mkLRUsdGnu results/lrusd.dat $MdTl $sty 
    exec gnuplot results/lrusd.dat.gnu
    exec ghostview results/lrusd.dat.ps &
    mkLRUsdGnu2 results/lrusdts.dat $MdTl $sty 
    exec gnuplot results/lrusdts.dat.gnu
    exec ghostview results/lrusdts.dat.ps &
}


proc mkLRUsdGnu {file title sty} {
    exec ./reverse $file
    set wFn "$file.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"$file.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "set xlabel \"Stack Level\" "
    puts $wfp "set ylabel \"Frequency (in percent)\" "
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[0:\] \[0:\] '$file' with $sty"
    close $wfp
}


proc mkLRUsdGnu2 {file title sty} {
    set wFn "$file.gnu"
    set wfp [open $wFn w]
    puts $wfp "set terminal postscript"
    puts $wfp "set output \"$file.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "set ylabel \"Stack Level\" "
    puts $wfp "set xlabel \"Elapsed time (sec)\" "
    puts $wfp "set nokey"
    puts $wfp ""
    puts $wfp "plot \[0:\] \[0:\] '$file' with $sty"
    close $wfp
}

#Popularity Button
pack .main.frm -side left -expand y -fill both

button .main.frm.pB -text "Popularity" -command disProP -bg #cccccc -fg black
pack .main.frm.pB -side top -expand y -fill both

proc disProP {} {
    global  DisTl PdisXl MdPoi MdLin MdImp DisFn genP reqFl

    exec ./col3 < data/$DisFn | ./popularity | sort -nr > data/docs.dat
    set fl "data/docs.dat"

    if {$DisTl == ""} {
	set ProTl "Document Popularity Profile of $DisFn"
    } else {
	set ProTl $DisTl
    }

    if {$PdisXl == ""} {
	set ProXl [exec cat $fl | wc -l]
    } else {
	set ProXl $PdisXl
    }

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    mkProGnu $fl $ProTl $ProXl $sty
    exec gnuplot $fl.gnu
    exec ghostview results/$DisFn.pop.ps &
}

proc mkProGnu {file title xLmt sty} {
    global DisFn
    set wFn "$file.gnu"
    set wfp [open $wFn w]
    puts $wfp "set output \"results/$DisFn.pop.ps\" "
    puts $wfp "set terminal postscript"
    puts $wfp "set title \"$title\" "
    puts $wfp "set nokey"
    puts $wfp "set xlabel \"Document Rank\" "
    puts $wfp "set logscale x"
    puts $wfp "set ylabel \"Number of References\" "
    puts $wfp "set logscale y"
    puts $wfp ""
    puts $wfp "set xrange \[1:\] "
    puts $wfp "set yrange \[1:\] "
    puts $wfp "plot '$file' using 1 with $sty"
    close $wfp
}

button .main.frm.hisB -text "Size Distribution" -command disMDP \
                     -bg #cccccc -fg black
pack .main.frm.hisB -side top -expand y -fill both

proc disMDP {} {
    global DisFn DisTl mdSl MdisL MdLin MdPoi MdImp
    if {$DisTl == ""} {
	set MdTl "Size Distribution of $DisFn"
    } else {
	set MdTl $DisTl
    }

    if {$MdisL == ""} {
	set MdisL [exec cat data/$DisFn | ./col3 | sort -n | tail -1]
    } 

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    exec cat data/$DisFn | ./col3 | ./buckets $mdSl 0 $MdisL data/$DisFn.md
    mkMDGnu $DisFn $MdTl $sty 
    exec gnuplot data/$DisFn.md.gnu
    exec ghostview results/$DisFn.md.ps &
}

proc mkMDGnu {file title sty} {
    set wFn "data/$file.md.gnu"
    set wfp [open $wFn w]
    puts $wfp "set ylabel \"Probability\" "
    puts $wfp "set xlabel \"Observed Value\" "
    puts $wfp "set nokey"
    puts $wfp "set terminal postscript"
    puts $wfp ""
    puts $wfp "set output \"results/$file.md.ps\" "
    puts $wfp "set title \"$title\" "
    puts $wfp "plot 'data/$file.md' using 2:4 with $sty"
    close $wfp
}


button .main.frm.llcdB -text "LLCD Plot" -command disLLCDP \
                     -bg #cccccc -fg black
pack .main.frm.llcdB -side top -expand y -fill both

proc disLLCDP {} {
    global DisFn DisTl mdSl MdisL MdPoi MdLin MdImp
    if {$DisTl == ""} {
	set MdTl "LLCD Plot of $DisFn"
    } else {
	set MdTl $DisTl
    }

    if {$MdisL == ""} {
	set MdisL [exec cat data/$DisFn | ./col3 | sort -n | tail -1]
    } 

    if {$MdPoi == 0} {
	if {$MdLin == 0} {
	    set sty "impulses"
	} else {
	    set sty "lines"
	}
    } else {
	set sty "points"
    }

    exec cat data/$DisFn | ./col3 | ./buckets $mdSl 0 $MdisL data/$DisFn.md
    mkLLCDGnu $DisFn $MdTl $sty 
    exec gnuplot data/$DisFn.llcd.gnu
    exec ghostview results/$DisFn.llcd.ps &
}

proc mkLLCDGnu {file title sty} {
    set wFn "data/$file.llcd.gnu"
    set wfp [open $wFn w]
    puts $wfp "set output \"results/$file.llcd.ps\" "
    puts $wfp "set terminal postscript"
    puts $wfp "set nokey"
    puts $wfp "set title \"$title\" "
    puts $wfp "set xlabel \"Observed Value\" "
    puts $wfp "set logscale x"
    puts $wfp "set ylabel \"Probability\" "
    puts $wfp "set logscale y"
    puts $wfp ""
    puts $wfp "plot 'data/$file.md' using 2:6 with $sty"
    close $wfp
}

button .main.frm.defB -text "Defaults" -command defP \
             -bg #cccccc -fg black 
pack .main.frm.defB -side top -expand y -fill both

proc defP {} {
    global PdisXl Alag DisFn DisL DisTl MdisL MBpI MRpI
    set tmp [exec cat data/$DisFn | wc -l]
    set inter [split $tmp]
    set idx 0
    while {[lindex $inter $idx] == ""} {
        incr idx
    }
    set DisXl [lindex $inter $idx]
    if {$PdisXl < 100} {
      set Alag $PdisXl
    } else {
      set Alag 100
    }
    .main.dis.mdF.rng set 20
    set MdisL ""
    set PdisXl "" 
    set DisTl ""  
    set MDisL ""  
    set MBpI ""  
    set MRpI ""  
}


##########################################################
#                                                        #
#              Cache Simulation Module                   #
#                                                        #
##########################################################


frame .sim -relief groove -borderwidth 3 -bg #cccccc
pack .sim -side left

# Entry for the filename

label .sim.fnL -text "Input trace file name: " -bg #cccccc -fg black
grid .sim.fnL -row 1 -column 1

entry .sim.fn -width 20 -textvariable SimFn -bg #cccccc -fg black
grid .sim.fn -row 1 -column 2 



# Cache size parameter

frame .sim.dSlFm -relief groove -borderwidth 3 -bg #cccccc
grid .sim.dSlFm -row 2 -column 1

scale .sim.dSlFm.rng -from 1 -to 68 -bigincrement 10 -command CscaleP \
      -orient horizontal -length 230 -variable CIscl -showvalue 0 \
      -bg #cccccc -fg black

.sim.dSlFm.rng set 40
set CacheSize 1000000

proc CscaleP {Ascl} {
    global CacheSize
    if {$Ascl <= 3} {
	set CacheSize [expr $Ascl - 1]
	.sim.dSlFm.rngVal configure -text "Cache Size:    $CacheSize"
    } elseif {$Ascl <= 6} {
	set CacheSize [expr ($Ascl - 3) * 10]
	.sim.dSlFm.rngVal configure -text "Cache Size:   $CacheSize"
    } elseif {$Ascl <= 10} {
	set CacheSize [expr ($Ascl - 6) * 100]
	.sim.dSlFm.rngVal configure -text "Cache Size:   $CacheSize"
    } elseif {$Ascl <= 20} {
	set CacheSize [expr ($Ascl - 10) * 1000]
	.sim.dSlFm.rngVal configure -text "Cache Size:   $CacheSize"
    } elseif {$Ascl <= 29} {
	set CacheSize [expr ($Ascl - 19) * 10000]
	.sim.dSlFm.rngVal configure -text "Cache Size:  $CacheSize"
    } elseif {$Ascl <= 39} {
	set CacheSize [expr ($Ascl - 29) * 100000]
	.sim.dSlFm.rngVal configure -text "Cache Size:  $CacheSize"
    } elseif {$Ascl <= 49} {
	set CacheSize [expr ($Ascl - 39) * 1000000]
	.sim.dSlFm.rngVal configure -text "Cache Size:  $CacheSize"
    } elseif {$Ascl <= 59} {
	set CacheSize [expr ($Ascl - 49) * 10000000]
	.sim.dSlFm.rngVal configure -text "Cache Size:  $CacheSize"
    } else {
	set CacheSize [expr ($Ascl - 58) * 100000000]
	.sim.dSlFm.rngVal configure -text "Cache Size: $CacheSize"
    }    
}

label .sim.dSlFm.rngVal -text "Cache Size:    100" -bg #cccccc -fg black

label .sim.dSlFm.xAxVal -text "1   1KB 10K 100K 1MB 10M 100M 1GB" \
	-bg #cccccc -fg black

pack .sim.dSlFm.rngVal .sim.dSlFm.rng .sim.dSlFm.xAxVal -side top


radiobutton .sim.lruPol -variable lruPol -command {set lfuPol 0; set gdPol 0; set randPol 0; set fifoPol 0} \
	               -text "LRU"
radiobutton .sim.lfuPol -variable lfuPol -command {set lruPol 0; set gdPol 0; set randPol 0; set fifoPol 0} \
	               -text "LFU"
radiobutton .sim.gdPol -variable gdPol -command {set lruPol 0; set lfuPol 0; set randPol 0; set fifoPol 0} \
	               -text "GD-Size"
radiobutton .sim.randPol -variable randPol -command {set lruPol 0; set lfuPol 0; set gdPol 0; set fifoPol 0} \
	               -text "RAND"
radiobutton .sim.fifoPol -variable fifoPol -command {set lruPol 0 ; set lfuPol 0; set gdPol 0; set randPol 0} \
	               -text "FIFO"
.sim.lruPol select
.sim.lfuPol deselect
.sim.gdPol deselect
.sim.randPol deselect
.sim.fifoPol deselect

set lfuPol 0
set gdPol 0
set randPol 0
set fifoPol 0

grid .sim.lruPol -row 3 -column 1 
grid .sim.lfuPol -row 3 -column 2 
grid .sim.gdPol -row 3 -column 3 
grid .sim.randPol -row 3 -column 4 
grid .sim.fifoPol -row 3 -column 5 

# Create Action Buttons

button .sim.gen -text "Simulate" -command simP -bg #cccccc -fg black
grid .sim.gen -row 1 -column 6

proc simP {} {
    global SimFn lfuPol lruPol gdPol randPol fifoPol  CacheSize
    if {$lfuPol != 0} { 
	set pol 2
    } elseif {$gdPol != 0} {  
	set pol 3
    } elseif {$randPol != 0 } { 
  	set pol 4
    } elseif { $fifoPol != 0} { 
	set pol 5
    } else {
   	set pol 1
    }
##   run LRU policy for now (1)
    exec ./CacheDriver $SimFn $SimFn.hr.dat $SimFn.bhr.dat 0 $CacheSize $CacheSize pol cachesim.dat -1
}

button .sim.runrvt -text "Ratio vs. Time" -command runRvsT -bg #cccccc -fg black 
grid .sim.runrvt -row 2 -column 6 

proc runRvsT {} { 
    global SimFn lfuPol lruPol gdPol randPol fifoPol  CacheSize
    if {$lfuPol != 0} { 
	set pol 2
    } elseif {$gdPol != 0} {  
	set pol 3
    } elseif {$randPol != 0 } { 
  	set pol 4
    } elseif { $fifoPol != 0} { 
	set pol 5
    } else {
   	set pol 1
    }
    exec ./runsize $SimFn $pol  $CacheSize
	exec ./div_htr < data/hit_time_ratio.dat
	exec ./create_htr_plt $CacheSize > data/htr.plt
	exec gnuplot data/htr.plt
	exec ghostview results/htr.eps &
}



button .sim.runsize -text "Run Size" -command runSizeP -bg #cccccc -fg black
grid .sim.runsize -row 2 -column 3

proc runSizeP {} {
    global SimFn lfuPol gdPol randPol fifoPol  CacheSize
    if {$lfuPol != 0} { 
	set pol 2
    } elseif {$gdPol != 0} {  
	set pol 3
    } elseif {$randPol != 0 } { 
  	set pol 4
    } elseif { $fifoPol != 0} { 
	set pol 5
    } else {
   	set pol 1
    }

     exec ./runsize $SimFn $pol $CacheSize
     exec ghostview results/sizehr.eps &
}

button .sim.runpolicy -text "Run Policies" -command runPolicyP -bg #cccccc -fg black
grid .sim.runpolicy -row 3 -column 6

proc runPolicyP {} {
    global SimFn CacheSize 
    exec ./runpolicy $SimFn $CacheSize
    exec ghostview results/pol1.eps &
    exec ghostview results/pol.eps &
}



##########################################################
#                                                        #
#                       KitAction Module                 #
#                                                        #
##########################################################


frame .kit -relief groove -borderwidth 3 -bg #cccccc
pack .kit -side left -expand y -fill both


button .kit.abtB -text "About" -command aboutP -bg #cccccc -fg black
pack .kit.abtB -expand y -fill both

proc aboutP {} {
    toplevel .kit.about -bg #cccccc
    wm title .kit.about "About webTraff"
    label .kit.about.msg -wraplength 650 -justify center -bg #cccccc -fg black
    putInfo
    pack .kit.about.msg 
    frame .kit.about.spc -height 30 -bg #cccccc
    pack .kit.about.spc -fill x -expand y
    button .kit.about.ok -text "OK" -command wCloseP -bg #cccccc -fg black 
    pack .kit.about.ok 
}

proc wCloseP {} {
    destroy .kit.about
}

proc putInfo {} {
    .kit.about.msg configure -text \
"webTraff  (Version 3)  is  an  interactive  kit,\n\
to generate and analyse Web workloads.\n\n\n\
\
Author:\n\n\ 
Carey Williamson\n\
Department of Computer Science\n\
University of Calgary\n\
Calgary, AB, Canada\n\n\n\""
}


button .kit.extB -text "Exit" -command exit -bg #cccccc -fg black
pack .kit.extB -expand y -fill both


