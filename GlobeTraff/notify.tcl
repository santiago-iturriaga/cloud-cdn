set arg1 [lindex $argv 0]
button .hello -text "File $arg1 created!" -command {exit 0}
pack .hello -padx 20 -pady 10

