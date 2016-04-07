postscript("CloudCDN_low_high_0_f201603.EPSILON.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
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

algs<-c("SMSEMOA","NSGAII")
boxplot(SMSEMOA,NSGAII,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(2,2))
indicator<-"EPSILON"
qIndicator(indicator, "CloudCDN_MO")
