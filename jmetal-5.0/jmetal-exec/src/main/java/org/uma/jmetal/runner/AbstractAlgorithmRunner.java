package org.uma.jmetal.runner;

import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Abstract class for Runner classes
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public abstract class AbstractAlgorithmRunner {
  /**
   * Write the population into two files and prints some data on screen
   * @param population
   */
  public static void printFinalSolutionSet(List<? extends Solution<?>> population) {

    new SolutionSetOutput.Printer(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }

  /**
   * Print all the available quality indicators
   * @param population
   * @param paretoFrontFile
   * @throws FileNotFoundException
   */
  public static void printQualityIndicators(List<? extends Solution<?>> population, String paretoFrontFile)
      throws FileNotFoundException {
    Front referenceFront = new ArrayFront(paretoFrontFile);
    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;

    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population)) ;
    List<DoubleSolution> normalizedPopulation = FrontUtils
        .convertFrontToSolutionList(normalizedFront) ;

    String outputString = "\n" ;
    outputString += "Hypervolume (N) : " +
        new Hypervolume<List<? extends Solution<?>>>(normalizedReferenceFront).evaluate(
            normalizedPopulation) + "\n";
    outputString += "Hypervolume     : " +
        new Hypervolume<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n";
    outputString += "Epsilon (N)     : " +
        new Epsilon<List<? extends Solution<?>>>(normalizedReferenceFront).evaluate(normalizedPopulation) +
        "\n" ;
    outputString += "Epsilon         : " +
        new Epsilon<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n" ;
    outputString += "GD (N)          : " +
        new GenerationalDistance<List<? extends Solution<?>>>(normalizedReferenceFront).evaluate(normalizedPopulation) + "\n";
    outputString += "GD              : " +
        new GenerationalDistance<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n";
    outputString += "IGD (N)         : " +
        new InvertedGenerationalDistance<List<? extends Solution<?>>>(normalizedReferenceFront).evaluate(normalizedPopulation) + "\n";
    outputString +="IGD             : " +
        new InvertedGenerationalDistance<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n";
    outputString += "IGD+ (N)        : " +
        new InvertedGenerationalDistancePlus<List<? extends Solution<?>>>(normalizedReferenceFront).evaluate(normalizedPopulation) + "\n";
    outputString += "IGD+            : " +
        new InvertedGenerationalDistancePlus<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n";
    outputString += "Spread (N)      : " +
        new Spread<List<? extends Solution<?>>>(normalizedReferenceFront).evaluate(normalizedPopulation) + "\n";
    outputString += "Spread          : " +
        new Spread<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n";
    outputString += "R2 (N)          : " +
        new R2<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation) + "\n";
    outputString += "R2              : " +
        new R2<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n";
    outputString += "Error ratio     : " +
        new ErrorRatio<List<? extends Solution<?>>>(referenceFront).evaluate(population) + "\n";

    JMetalLogger.logger.info(outputString);
  }
}
