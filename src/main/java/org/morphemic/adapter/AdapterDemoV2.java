package org.morphemic.adapter;

import org.activeeon.morphemic.PAGateway;
import org.activeeon.morphemic.model.Job;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.morphemic.adapter.common.PAConfiguration;
import org.morphemic.adapter.utils.ProtectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class AdapterDemoV2 {

    private static final Logger LOGGER = Logger.getLogger(AdapterDemoV2.class);



    public static void main(String[] args) {

        Configuration config = new BaseConfiguration();

        try {
            // load ProActive configuration
            config = PAConfiguration.loadPAConfiguration();
        } catch (ConfigurationException ce) {
            LOGGER.error("ERROR: " + ce.toString());
        }

        PAGateway paGateway = new PAGateway(config.getString(PAConfiguration.REST_URL));

        try {
            // Connecting to the server
            paGateway.connect(ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_LOGIN)),
                    ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_PASSWORD)));

            JSONObject jsonJob = createJSONJob();

            LOGGER.info("Creating job: " + jsonJob.toString());
            paGateway.createJob(jsonJob);



            EntityManagerFactory emf;
            EntityManager em = null;

            try {
                emf = Persistence.createEntityManagerFactory("model");
                em = emf.createEntityManager();
            } catch (Exception e) {
                System.exit(1);
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Job oldJob1 = em.find(Job.class, "AdapterJob@503ed762");
            LOGGER.info("Job old 1: " + oldJob1);



            // Other tasks
//            JSONArray jsonClouds=null;
//            paGateway.addClouds(jsonClouds);
//
//            JSONArray jsonNodes=null;
//            paGateway.addNodes(jsonNodes, "AdapterJob@503ed762");

//            JSONArray jsonMonitors;
//            paGateway.addMonitors(jsonMonitors, "AdapterJob@503ed762");



            long submittedJobId = paGateway.submitJob("AdapterJob@503ed762");
            LOGGER.info("Job submitted with id = " + submittedJobId);


        } catch (Exception e) {
            LOGGER.error(" ... Error: " + e.getMessage());
        } finally {
            paGateway.disconnect();
        }
        LOGGER.info("End.");
    }

    private static JSONObject createJSONJob() {
        JSONObject jsonJob = new JSONObject();
        JSONObject jsonTemp = new JSONObject();
        jsonTemp.put("id", "AdapterJob@503ed762");
        jsonTemp.put("name", "FCRDeployment_JOB");
        jsonJob.put("jobInformation", jsonTemp);

        jsonTemp = new JSONObject();
        JSONObject jsonTemp1 = new JSONObject();

        jsonTemp1.put("id", "AdapterTask@1585e21f");
        jsonTemp1.put("type", "bash");
        jsonTemp1.put("preInstall", "sudo service tomcat7 stop && rm -rf ~/load_balancer && mkdir ~/load_balancer && cd ~/load_balancer && wget https://s3-eu-west-1.amazonaws.com/melodic.testing.data/FCR/vms/mel1/load_balancer.sh && chmod +x ~/load_balancer/load_balancer.sh && printenv >> ~/load_balancer/lb_download_env.txt");
        jsonTemp1.put("install", "printenv >> ~/load_balancer/lb_install_env.txt");
        jsonTemp1.put("postInstall", "printenv >> ~/load_balancer/lb_config_env.txt && export PUBLIC_APP_IPS=($PUBLIC_ComponentPortAppReq) && sudo ~/load_balancer/load_balancer.sh install ${PUBLIC_APP_IPS[@]} && mkdir ~/test2");
        jsonTemp1.put("preStart", "");
        jsonTemp1.put("start", "printenv >> ~/load_balancer/lb_start_env.txt && sudo ~/load_balancer/load_balancer.sh start");
        jsonTemp1.put("postStart", "");
        jsonTemp1.put("preStop", "");
        jsonTemp1.put("stop", "");
        jsonTemp1.put("postStop", "");

        JSONObject jsonTemp2 = new JSONObject();
        jsonTemp2.put("id", "AdapterTask@72822c58");
        jsonTemp2.put("type", "bash");
        jsonTemp2.put("preInstall", "rm -rf ~/melodic && mkdir ~/melodic && cd ~/melodic && wget https://s3-eu-west-1.amazonaws.com/melodic.testing.data/FCR/FCRDB.sh && chmod +x ~/melodic/FCRDB.sh");
        jsonTemp2.put("install", "~/melodic/FCRDB.sh install");
        jsonTemp2.put("postInstall", "~/melodic/FCRDB.sh configure");
        jsonTemp2.put("preStart", "");
        jsonTemp2.put("start", "~/melodic/FCRDB.sh start");
        jsonTemp2.put("postStart", "");
        jsonTemp2.put("preStop", "");
        jsonTemp2.put("stop", "");
        jsonTemp2.put("postStop", "");

        jsonTemp.put("Component_LB", jsonTemp1);
        jsonTemp.put("Component_DB", jsonTemp2);
        jsonJob.put("tasks", jsonTemp);

        return (jsonJob);
    }
}
