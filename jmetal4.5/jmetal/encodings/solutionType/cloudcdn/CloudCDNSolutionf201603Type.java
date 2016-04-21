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

    public int NUM_BUCKETS = 0;

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

    public Variable[] createOneMaxVariables() {
        Variable[] vars = createVariables();
        for (int i = 0; i < problem_.getLength(0); i++) {
            try {
                ((ArrayInt) vars[0]).setValue(i, 0);
            } catch (JMException ex) {
                Logger.getLogger(CloudCDNSolutionf201603Type.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (int i = 0; i < problem_.getLength(1); i++) {
            ((Binary) vars[1]).setIth(i, true);
        }

        return vars;
    }

    public Variable[] createZeroMaxVariables() {
        Variable[] vars = createVariables();
        for (int i = 0; i < problem_.getLength(0); i++) {
            try {
                ((ArrayInt) vars[0]).setValue(i, 0);
            } catch (JMException ex) {
                Logger.getLogger(CloudCDNSolutionf201603Type.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (int i = 0; i < problem_.getLength(1); i++) {
            ((Binary) vars[1]).setIth(i, false);
        }

        for (int i = 0; i < customProblem_.getDocumentos().size(); i++) {
            int dcIdx = GetDCDocIndex(
                    customProblem_.getRegionesDatacenters().size(),
                    customProblem_.getDocumentos().size(),
                    0,
                    i);

            ((Binary) vars[1]).setIth(dcIdx, true);
        }

        return vars;
    }

    public int GetDCDocIndex(int dcCount, int docCount, int dcId, int docId) {
        int bucketIdx = (docId * NUM_BUCKETS) / docCount;
        //return NUM_BUCKETS * dcId + bucketIdx;
        return dcCount * bucketIdx + dcId;
    }

    public ArrayInt GetRIVariables(Solution solution) {
        return (ArrayInt) solution.getDecisionVariables()[0];
    }

    public int GetRIDCCount(Solution solution, int dcId) throws JMException {
        return GetRIVariables(solution).getValue(dcId);
    }

    public void SetRIDCCount(Solution solution, int dcId, int value) throws JMException {
        GetRIVariables(solution).setValue(dcId, value);
    }

    public Binary GetDocStorageVariables(Solution solution) {
        return (Binary) solution.getDecisionVariables()[1];
    }

    public boolean IsDocStored(Solution solution, int dcId, int docId) {
        return GetDocStorageVariables(solution).getIth(
                GetDCDocIndex(
                        customProblem_.getRegionesDatacenters().size(),
                        customProblem_.getDocumentos().size(),
                        dcId,
                        docId));
    }

    public void SetDocStored(Solution solution, int dcId, int docId, boolean status) {
        int idx = GetDCDocIndex(
                customProblem_.getRegionesDatacenters().size(),
                customProblem_.getDocumentos().size(),
                dcId,
                docId);

        GetDocStorageVariables(solution).setIth(idx, status);
    }
}
