package morphemic.adapter;

import org.activeeon.morphemic.PAGateway;
import org.activeeon.morphemic.model.Job;
import org.activeeon.morphemic.service.EntityManagerHelper;
import org.activeeon.morphemic.service.Utils;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import morphemic.adapter.common.PAConfiguration;
import morphemic.adapter.utils.ProtectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;

import java.io.File;


public class AdapterDemo {

    private static final Logger LOGGER = Logger.getLogger(AdapterDemo.class);

    private static final String EXAMPLES_RELATIVE_PATH = "Example_Commands" + File.separator;

    public static void main(String[] args) {

        LOGGER.info("Begin");

        Configuration config = new BaseConfiguration();

        // Loading the configuration file
        try {
            // load ProActive configuration
            config = PAConfiguration.loadPAConfiguration();
        } catch (ConfigurationException ce) {
            LOGGER.error("ERROR: ", ce);
        }

        // Reading ProActive's URL, login and password from configuration file
        String paURL = config.getString(PAConfiguration.PA_URL);
        String paUsername = ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_LOGIN));
        String paPassword = ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_PASSWORD));

        // Instantiating SAL's gateway with the ProActive server URL
        PAGateway paGateway = new PAGateway(paURL);

        // Connecting SAL to ProActive
        try {
            paGateway.connect(paUsername, paPassword);
            LOGGER.info("Connected!");
        } catch (Exception e) {
            LOGGER.error("ERROR: ", e);
            System.exit(1);
        }

        // Reading the job, clouds and nodes JSON definitions
        JSONObject jsonJob = new JSONObject(Utils.getContentWithFileName(EXAMPLES_RELATIVE_PATH + "Job_commands_input.json"));
        JSONArray jsonClouds = new JSONArray(Utils.getContentWithFileName(EXAMPLES_RELATIVE_PATH + "addCloud_input.json"));
        JSONArray jsonNodesArray1 = new JSONArray(Utils.getContentWithFileName(EXAMPLES_RELATIVE_PATH + "addNodes_input1.json"));
        JSONArray jsonNodesArray2 = new JSONArray(Utils.getContentWithFileName(EXAMPLES_RELATIVE_PATH + "addNodes_input2.json"));

        // Adding cloud definitions
        paGateway.addClouds(jsonClouds);
        LOGGER.info("Cloud added.");

        // Defining an application job
        LOGGER.info("Creating job: " + jsonJob);
        paGateway.createJob(jsonJob);

        try {
            LOGGER.info("Waiting ...");
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
            LOGGER.warn("INTERRUPTION: ", ie);
        }

        String jobId = jsonJob.optJSONObject("jobInformation").optString("id");

        // Adding nodes 1 to components in a sequential fashion
        LOGGER.info("Adding nodes 1 " + jsonNodesArray1 + " started.");
        paGateway.addNodes(jsonNodesArray1, jobId);

        // Adding nodes 2 to components in a sequential fashion
        LOGGER.info("Adding nodes 2 " + jsonNodesArray2 + " started.");
        paGateway.addNodes(jsonNodesArray2, jobId);

        // Adding EMS monitoring to a component
//        List<String> nodeNames = new LinkedList<>();
//        nodeNames.add("component-DB-1-0");
//        paGateway.addEmsDeployment(nodeNames, "Dummy_bearer");

        // Verifying the job from the database
        Job jobToSubmit = EntityManagerHelper.find(Job.class, jobId);
        EntityManagerHelper.refresh(jobToSubmit);
        LOGGER.info("Job found to submit: " + jobToSubmit.toString());

        // Submitting the job to deployment
        long submittedJobId = paGateway.submitJob(jobId);
        LOGGER.info("Submitted job id: " + submittedJobId);

        try {
            LOGGER.info("Waiting ...");
            Thread.sleep(30000);
        } catch (InterruptedException ie) {
            LOGGER.warn("INTERRUPTION: ", ie);
        }

        try {
            paGateway.disconnect();
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: ", nce);
        }

        LOGGER.info("END");

        System.exit(0);
    }
}
