postscript("CloudCDN_f201603_low_0_43200.HV.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data/"
qIndicator <- function(indicator, problem)
{
fileSMSEMOA<-paste(resultDirectory, "SMSEMOA", sep="/")
fileSMSEMOA<-paste(fileSMSEMOA, problem, sep="/")
fileSMSEMOA<-paste(fileSMSEMOA, indicator, sep="/")
SMSEMOA<-scan(fileSMSEMOA)

fileNSGAII<-paste(resultDirectory, "NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, problem, sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

fileMOCHC<-paste(resultDirectory, "MOCHC", sep="/")
fileMOCHC<-paste(fileMOCHC, problem, sep="/")
fileMOCHC<-paste(fileMOCHC, indicator, sep="/")
MOCHC<-scan(fileMOCHC)

algs<-c("SMSEMOA","NSGAII","MOCHC")
boxplot(SMSEMOA,NSGAII,MOCHC,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(2,2))
indicator<-"HV"
qIndicator(indicator, "CloudCDN_MO")
