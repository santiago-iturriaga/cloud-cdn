write("", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex",append=FALSE)
resultDirectory<-"results/CloudCDN_f201603_low_0_43200/data"
latexHeader <- function() {
  write("\\documentclass{article}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\title{StandardStudy}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\usepackage{amssymb}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\begin{document}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\maketitle", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\section{Tables}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\caption{", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write(problem, "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write(".EPSILON.}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)

  write("\\label{Table:", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write(problem, "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write(".EPSILON.}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)

  write("\\centering", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\begin{scriptsize}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\begin{tabular}{", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write(tabularString, "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write(latexTableFirstLine, "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\hline ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
}

latexTableTail <- function() { 
  write("\\hline", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\end{tabular}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\end{scriptsize}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  write("\\end{table}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
}

printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { 
  file1<-paste(resultDirectory, algorithm1, sep="/")
  file1<-paste(file1, problem, sep="/")
  file1<-paste(file1, indicator, sep="/")
  data1<-scan(file1)
  file2<-paste(resultDirectory, algorithm2, sep="/")
  file2<-paste(file2, problem, sep="/")
  file2<-paste(file2, indicator, sep="/")
  data2<-scan(file2)
  if (i == j) {
    write("-- ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  }
  else if (i < j) {
    if (wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) <= median(data2)) {
        write("$\\blacktriangle$", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
      }
      else {
        write("$\\triangledown$", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE) 
      }
    }
    else {
      write("--", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE) 
    }
  }
  else {
    write(" ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
  }
}

### START OF SCRIPT 
# Constants
problemList <-c("CloudCDN_MO") 
algorithmList <-c("SMSEMOA", "NSGAII", "MOCHC") 
tabularString <-c("lcc") 
latexTableFirstLine <-c("\\hline  & NSGAII & MOCHC\\\\ ") 
indicator<-"EPSILON"

 # Step 1.  Writes the latex header
latexHeader()
# Step 2. Problem loop 
for (problem in problemList) {
  latexTableHeader(problem,  tabularString, latexTableFirstLine)

  indx = 0
  for (i in algorithmList) {
    if (i != "MOCHC") {
      write(i , "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
      write(" & ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
      jndx = 0 
      for (j in algorithmList) {
        if (jndx != 0) {
          if (indx != jndx) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
          }
          if (j != "MOCHC") {
            write(" & ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
          }
          else {
            write(" \\\\ ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
          }
        }
        jndx = jndx + 1
      }
      indx = indx + 1
    }
  }

  latexTableTail()
} # for problem

tabularString <-c("| l | p{0.15cm}   | p{0.15cm}   | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} & \\multicolumn{1}{c|}{NSGAII} & \\multicolumn{1}{c|}{MOCHC} \\\\") 

# Step 3. Problem loop 
latexTableHeader("CloudCDN_MO ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "MOCHC") {
    write(i , "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
    write(" & ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
          } 
          if (problem == "CloudCDN_MO") {
            if (j == "MOCHC") {
              write(" \\\\ ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
            } 
            else {
              write(" & ", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
            }
          }
     else {
    write("&", "results/CloudCDN_f201603_low_0_43200/R/CloudCDN_f201603_low_0_43200.EPSILON.Wilcox.tex", append=TRUE)
     }
        }
      }
      jndx = jndx + 1
    }
    indx = indx + 1
  }
} # for algorithm

  latexTableTail()

#Step 3. Writes the end of latex file 
latexTail()

