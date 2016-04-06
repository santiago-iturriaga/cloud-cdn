write("", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex",append=FALSE)
resultDirectory<-"/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/data"
latexHeader <- function() {
  write("\\documentclass{article}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\title{StandardStudy}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\usepackage{amssymb}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\begin{document}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\maketitle", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\section{Tables}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\caption{", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write(problem, "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write(".HV.}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)

  write("\\label{Table:", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write(problem, "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write(".HV.}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)

  write("\\centering", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\begin{scriptsize}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\begin{tabular}{", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write(tabularString, "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write(latexTableFirstLine, "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\hline ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
}

latexTableTail <- function() { 
  write("\\hline", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\end{tabular}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\end{scriptsize}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  write("\\end{table}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
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
    write("--", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  }
  else if (i < j) {
    if (wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) >= median(data2)) {
        write("$\\blacktriangle$", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
      }
      else {
        write("$\\triangledown$", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE) 
      }
    }
    else {
      write("--", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE) 
    }
  }
  else {
    write(" ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
  }
}

### START OF SCRIPT 
# Constants
problemList <-c("CloudCDN_MO") 
algorithmList <-c("SMSEMOA", "NSGAII") 
tabularString <-c("lc") 
latexTableFirstLine <-c("\\hline  & NSGAII\\\\ ") 
indicator<-"HV"

 # Step 1.  Writes the latex header
latexHeader()
# Step 2. Problem loop 
for (problem in problemList) {
  latexTableHeader(problem,  tabularString, latexTableFirstLine)

  indx = 0
  for (i in algorithmList) {
    if (i != "NSGAII") {
      write(i , "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
      write(" & ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
      jndx = 0 
      for (j in algorithmList) {
        if (jndx != 0) {
          if (indx != jndx) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
          }
          if (j != "NSGAII") {
            write(" & ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
          }
          else {
            write(" \\\\ ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
          }
        }
        jndx = jndx + 1
      }
      indx = indx + 1
    }
  }

  latexTableTail()
} # for problem

tabularString <-c("| l | p{0.15cm}   | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} & \\multicolumn{1}{c|}{NSGAII} \\\\") 

# Step 3. Problem loop 
latexTableHeader("CloudCDN_MO ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "NSGAII") {
    write(i , "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
    write(" & ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
          } 
          if (problem == "CloudCDN_MO") {
            if (j == "NSGAII") {
              write(" \\\\ ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
            } 
            else {
              write(" & ", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
            }
          }
     else {
    write("&", "/home/siturria/github/cloud-cdn/jmetal4.5/results/CloudCDN_high_0_f201603/R/CloudCDN_high_0_f201603.HV.Wilcox.tex", append=TRUE)
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

