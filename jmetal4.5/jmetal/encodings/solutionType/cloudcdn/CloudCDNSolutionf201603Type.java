package jmetal.encodings.solutionType.cloudcdn;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;
import jmetal.experiments.studies.CloudCDNSimpleStudy_f201603;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.util.JMException;

public class CloudCDNSolutionf201603Type extends SolutionType {

    private int NUM_BUCKETS = 100;
    
    CloudCDN_MP customProblem_;

    /**
     * Constructor
     *
     * @param problem Problem to solve
     */
    public CloudCDNSolutionf201603Type(Problem problem, int num_buckets) {
        super(problem);

        System.out.println("Number of buckets: " + num_buckets);
        
        customProblem_ = (CloudCDN_MP) problem;
        NUM_BUCKETS = num_buckets;
    } // Constructor

    /**
     * Creates the variables of the solution
     */
    @Override
    public Variable[] createVariables() {
        if (problem_.getClass().equals(CloudCDN_MP.class)) {
            //Variable[] variables = new Variable[problem_.getNumberOfVariables()];
            Variable[] variables = new Variable[2];
            variables[0] = new ArrayInt(problem_.getLength(0), customProblem_.GetRILowerLimits(), customProblem_.GetRIUpperLimits());
            variables[1] = new Binary(problem_.getLength(1));

            return variables;
        } else {
            Logger.getLogger(CloudCDNSimpleStudy_f201603.class.getName()).log(
                    Level.SEVERE, "Invalid problem type (not CloudCDN_SO problem). Skipping variable initialization.");
            return null;
        }
    } // createVariables
    
    public int GetDCDocIndex(int dcCount, int docCount, int dcId, int docId) {
        int bucketIdx = (int) Math.floor(((double) docId / (double) docCount) * NUM_BUCKETS);
        //return NUM_BUCKETS * dcId + bucketIdx;
        return dcCount * bucketIdx + dcId;
    }

    public ArrayInt GetRIVariables(Solution solution) {
        return (ArrayInt) solution.getDecisionVariables()[0];
    }

    public int GetRIDCCount(Solution solution, int dcId) throws JMException {
        return GetRIVariables(solution).getValue(dcId);
    }

    public static Binary GetDocStorageVariables(Solution solution) {
        return (Binary) solution.getDecisionVariables()[1];
    }

    public boolean IsDocStored(CloudCDN_MP customProblem, Solution solution, int dcId, int docId) {
        return GetDocStorageVariables(solution).getIth(
            GetDCDocIndex(
                    customProblem.getRegionesDatacenters().size(),
                    customProblem.getDocumentos().size(),
                    dcId,
                    docId));
    }
    
    public void SetDocStored(CloudCDN_MP customProblem, Solution solution, int dcId, int docId, boolean status) {
        GetDocStorageVariables(solution).setIth(
            GetDCDocIndex(
                    customProblem.getRegionesDatacenters().size(),
                    customProblem.getDocumentos().size(),
                    dcId,
                    docId),
            status);
    }
}
