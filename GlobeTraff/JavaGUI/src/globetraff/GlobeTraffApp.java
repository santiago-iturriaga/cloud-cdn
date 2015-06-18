/*
 * GlobeTraffApp.java
 */

package globetraff;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class GlobeTraffApp extends SingleFrameApplication {

    public static final int VIDEO_WEIBULL = 0;
    public static final int VIDEO_GAMMA = 1;
    public static final int VIDEO_ZIPF = 2;

    public static final int STACK_INDEPENDENT = 3;
    public static final int STACK_STATIC = 4;
    public static final int STACK_DYNAMIC = 5;
    public static final int STACK_NEW = 6;
    private int web_ratio;
    private int p2p_ratio;
    private int video_ratio;
    private int other_ratio;
    private float web_zipf_slope;
    private float web_one_timers;
    private float web_pop_bias;
    private float web_pareto_tail;
    private float web_size_pop_corr;
    private int stack_depth;
    private int stack_model = STACK_STATIC;
    private float p2p_mz_slope;
    private int p2p_mz_plateau;
    private float p2p_ed_tau;
    private float p2p_ed_lamda;
    private int p2p_size;
    private int p2p_birth;
    private int pop_mode;
    private float video_weibull_k;
    private int video_weibull_lamda;
    private float video_gamma_k;
    private int video_gamma_theta;
    private float video_zipf_slope;
    private String filename;

    protected void initializeParameters()
    {
        
        setWeb_ratio(35);
        setP2p_ratio(16);
        setVideo_ratio(20);
        setOther_ratio(29);
        setWeb_zipf_slope((float)0.74);
        setWeb_one_timers((float)0.70);
        setWeb_pop_bias((float)0.20);
        setWeb_pareto_tail((float)1.20);
        setWeb_size_pop_corr(0);
        setStack_depth(1000);
        setStack_model(STACK_STATIC);
        setP2p_mz_slope((float)0.6);
        setP2p_mz_plateau(20);
        setP2p_ed_tau((float)87.74);
        setP2p_ed_lamda((float)1.1625);
        setP2p_size(650);
        setP2p_birth(3807);
        setPop_mode(VIDEO_WEIBULL);
        setVideo_weibull_k((float)0.513);
        setVideo_weibull_lamda(6010);
        setVideo_gamma_k((float)0.372);
        setVideo_gamma_theta(23910);
        setVideo_zipf_slope((float)0.668);
        //setFilename("workload.dat");

    }
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new GlobeTraffView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of GlobeTraffApp
     */
    public static GlobeTraffApp getApplication() {
        return Application.getInstance(GlobeTraffApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(GlobeTraffApp.class, args);
        getApplication().initializeParameters();
    }

    /**
     * @return the web_ratio
     */
    public int getWeb_ratio() {
        return web_ratio;
    }

    /**
     * @param web_ratio the web_ratio to set
     */
    public void setWeb_ratio(int web_ratio) {
        this.web_ratio = web_ratio;
    }

    /**
     * @return the p2p_ratio
     */
    public int getP2p_ratio() {
        return p2p_ratio;
    }

    /**
     * @param p2p_ratio the p2p_ratio to set
     */
    public void setP2p_ratio(int p2p_ratio) {
        this.p2p_ratio = p2p_ratio;
    }

    /**
     * @return the video_ratio
     */
    public int getVideo_ratio() {
        return video_ratio;
    }

    /**
     * @param video_ratio the video_ratio to set
     */
    public void setVideo_ratio(int video_ratio) {
        this.video_ratio = video_ratio;
    }

    /**
     * @return the other_ratio
     */
    public int getOther_ratio() {
        return other_ratio;
    }

    /**
     * @param other_ratio the other_ratio to set
     */
    public void setOther_ratio(int other_ratio) {
        this.other_ratio = other_ratio;
    }

    /**
     * @return the web_zipf_slope
     */
    public float getWeb_zipf_slope() {
        return web_zipf_slope;
    }

    /**
     * @param web_zipf_slope the web_zipf_slope to set
     */
    public void setWeb_zipf_slope(float web_zipf_slope) {
        this.web_zipf_slope = web_zipf_slope;
    }

    /**
     * @return the web_one_timers
     */
    public float getWeb_one_timers() {
        return web_one_timers;
    }

    /**
     * @param web_one_timers the web_one_timers to set
     */
    public void setWeb_one_timers(float web_one_timers) {
        this.web_one_timers = web_one_timers;
    }

    /**
     * @return the web_pop_bias
     */
    public float getWeb_pop_bias() {
        return web_pop_bias;
    }

    /**
     * @param web_pop_bias the web_pop_bias to set
     */
    public void setWeb_pop_bias(float web_pop_bias) {
        this.web_pop_bias = web_pop_bias;
    }

    /**
     * @return the web_pareto_tail
     */
    public float getWeb_pareto_tail() {
        return web_pareto_tail;
    }

    /**
     * @param web_pareto_tail the web_pareto_tail to set
     */
    public void setWeb_pareto_tail(float web_pareto_tail) {
        this.web_pareto_tail = web_pareto_tail;
    }

    /**
     * @return the web_size_pop_corr
     */
    public float getWeb_size_pop_corr() {
        return web_size_pop_corr;
    }

    /**
     * @param web_size_pop_corr the web_size_pop_corr to set
     */
    public void setWeb_size_pop_corr(float web_size_pop_corr) {
        this.web_size_pop_corr = web_size_pop_corr;
    }

    /**
     * @return the stack_depth
     */
    public int getStack_depth() {
        return stack_depth;
    }

    /**
     * @param stack_depth the stack_depth to set
     */
    public void setStack_depth(int stack_depth) {
        this.stack_depth = stack_depth;
    }

    /**
     * @return the stack_model
     */
    public int getStack_model() {
        return stack_model;
    }

    /**
     * @param stack_model the stack_model to set
     */
    public void setStack_model(int stack_model) {
        this.stack_model = stack_model;
    }

    /**
     * @return the p2p_mz_slope
     */
    public float getP2p_mz_slope() {
        return p2p_mz_slope;
    }

    /**
     * @param p2p_mz_slope the p2p_mz_slope to set
     */
    public void setP2p_mz_slope(float p2p_mz_slope) {
        this.p2p_mz_slope = p2p_mz_slope;
    }

    /**
     * @return the p2p_mz_plateau
     */
    public int getP2p_mz_plateau() {
        return p2p_mz_plateau;
    }

    /**
     * @param p2p_mz_plateau the p2p_mz_plateau to set
     */
    public void setP2p_mz_plateau(int p2p_mz_plateau) {
        this.p2p_mz_plateau = p2p_mz_plateau;
    }

    /**
     * @return the p2p_ed_tau
     */
    public float getP2p_ed_tau() {
        return p2p_ed_tau;
    }

    /**
     * @param p2p_ed_tau the p2p_ed_tau to set
     */
    public void setP2p_ed_tau(float p2p_ed_tau) {
        this.p2p_ed_tau = p2p_ed_tau;
    }

    /**
     * @return the p2p_ed_lamda
     */
    public float getP2p_ed_lamda() {
        return p2p_ed_lamda;
    }

    /**
     * @param p2p_ed_lamda the p2p_ed_lamda to set
     */
    public void setP2p_ed_lamda(float p2p_ed_lamda) {
        this.p2p_ed_lamda = p2p_ed_lamda;
    }

    /**
     * @return the p2p_size
     */
    public int getP2p_size() {
        return p2p_size;
    }

    /**
     * @param p2p_size the p2p_size to set
     */
    public void setP2p_size(int p2p_size) {
        this.p2p_size = p2p_size;
    }

    /**
     * @return the p2p_birth
     */
    public int getP2p_birth() {
        return p2p_birth;
    }

    /**
     * @param p2p_birth the p2p_birth to set
     */
    public void setP2p_birth(int p2p_birth) {
        this.p2p_birth = p2p_birth;
    }

    /**
     * @return the pop_mode
     */
    public int getPop_mode() {
        return pop_mode;
    }

    /**
     * @param pop_mode the pop_mode to set
     */
    public void setPop_mode(int pop_mode) {
        this.pop_mode = pop_mode;
    }

    /**
     * @return the video_weibull_k
     */
    public float getVideo_weibull_k() {
        return video_weibull_k;
    }

    /**
     * @param video_weibull_k the video_weibull_k to set
     */
    public void setVideo_weibull_k(float video_weibull_k) {
        this.video_weibull_k = video_weibull_k;
    }

    /**
     * @return the video_weibull_lamda
     */
    public int getVideo_weibull_lamda() {
        return video_weibull_lamda;
    }

    /**
     * @param video_weibull_lamda the video_weibull_lamda to set
     */
    public void setVideo_weibull_lamda(int video_weibull_lamda) {
        this.video_weibull_lamda = video_weibull_lamda;
    }

    /**
     * @return the video_gamma_k
     */
    public float getVideo_gamma_k() {
        return video_gamma_k;
    }

    /**
     * @param video_gamma_k the video_gamma_k to set
     */
    public void setVideo_gamma_k(float video_gamma_k) {
        this.video_gamma_k = video_gamma_k;
    }

    /**
     * @return the video_gamma_theta
     */
    public int getVideo_gamma_theta() {
        return video_gamma_theta;
    }

    /**
     * @param video_gamma_theta the video_gamma_theta to set
     */
    public void setVideo_gamma_theta(int video_gamma_theta) {
        this.video_gamma_theta = video_gamma_theta;
    }

    /**
     * @return the video_zipf_slope
     */
    public float getVideo_zipf_slope() {
        return video_zipf_slope;
    }

    /**
     * @param video_zipf_slope the video_zipf_slope to set
     */
    public void setVideo_zipf_slope(float video_zipf_slope) {
        this.video_zipf_slope = video_zipf_slope;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }


}
