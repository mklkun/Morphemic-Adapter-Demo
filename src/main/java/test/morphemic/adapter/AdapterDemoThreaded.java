package test.morphemic.adapter;

import org.activeeon.morphemic.PAGateway;
import org.activeeon.morphemic.model.Job;
import org.activeeon.morphemic.service.EntityManagerHelper;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;
import test.morphemic.adapter.common.PAConfiguration;
import test.morphemic.adapter.utils.ProtectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Hello world!
 */
public class AdapterDemoThreaded {

    public static void main(String[] args) {
        System.out.println("Begin");

        Configuration config = new BaseConfiguration();

        try {
            // load ProActive configuration
            config = PAConfiguration.loadPAConfiguration();
        } catch (ConfigurationException ce) {
            System.out.println("ERROR: " + ce.toString());
        }

        String paURL = config.getString(PAConfiguration.PA_URL);
        String paUsername = ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_LOGIN));
        String paPassword = ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_PASSWORD));

        // some dummy commit 2

        JSONObject jsonJob = null;
        JSONArray jsonClouds = null;
        JSONArray jsonNodesArray1 = null;
        JSONArray jsonNodesArray2 = null;
        try {
            jsonJob = new JSONObject(new String(Files.readAllBytes(Paths.get(AdapterDemoThreaded.class.getResource("/Example_Docker/JobTask_docker_input.json").getPath()))));
            jsonClouds = new JSONArray(new String(Files.readAllBytes(Paths.get(AdapterDemoThreaded.class.getResource("/Example_Docker/addClouds_input.json").getPath()))));
            jsonNodesArray1 = new JSONArray(new String(Files.readAllBytes(Paths.get(AdapterDemoThreaded.class.getResource("/Example_Docker/addNodes_input1.json").getPath()))));
            jsonNodesArray2 = new JSONArray(new String(Files.readAllBytes(Paths.get(AdapterDemoThreaded.class.getResource("/Example_Docker/addNodes_input2.json").getPath()))));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }

        PAGateway paGateway = new PAGateway(paURL);

        try {
            paGateway.connect(paUsername, paPassword);
            System.out.println("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        paGateway.addClouds(jsonClouds);
        System.out.println("Cloud added.");

        System.out.println("Creating job: " + jsonJob.toString());
        paGateway.createJob(jsonJob);

        try {
            System.out.println("Waiting ...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MultiThreadTests multiThreadTests1 = new MultiThreadTests(paGateway,
                jsonNodesArray1,
                jsonJob.optJSONObject("jobInformation").optString("id"));
        multiThreadTests1.start();
        System.out.println("Nodes 1 added.");

        MultiThreadTests multiThreadTests2 = new MultiThreadTests(paGateway,
                jsonNodesArray2,
                jsonJob.optJSONObject("jobInformation").optString("id"));
        multiThreadTests2.start();
        System.out.println("Nodes 2 added.");

        try {
            System.out.println("Waiting ...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Job jobToSubmit = EntityManagerHelper.find(Job.class, jsonJob.optJSONObject("jobInformation").optString("id"));
        System.out.println("Job found to submit: " + jobToSubmit.toString());

//        long submittedJobId = paGateway.submitJob(jsonJob.optJSONObject("jobInformation").optString("id"));
//        System.out.println("Submitted job id: " + submittedJobId);

        System.out.println("END");

        System.exit(0);
    }
}
