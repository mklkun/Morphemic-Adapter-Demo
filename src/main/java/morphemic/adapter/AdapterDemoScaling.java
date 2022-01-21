package morphemic.adapter;

import morphemic.adapter.common.PAConfiguration;
import morphemic.adapter.utils.ProtectionUtils;
import org.activeeon.morphemic.PAGateway;
import org.activeeon.morphemic.service.Utils;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class AdapterDemoScaling {

    private static final Logger LOGGER = Logger.getLogger(AdapterDemoScaling.class);

    private static final String EXAMPLES_RELATIVE_PATH = "Example_Commands" + File.separator;

    public static void main(String[] args) {
        LOGGER.info("Begin");

        Configuration config = new BaseConfiguration();

        try {
            // load ProActive configuration
            config = PAConfiguration.loadPAConfiguration();
        } catch (ConfigurationException ce) {
            LOGGER.error("ERROR: ", ce);
        }

        String paURL = config.getString(PAConfiguration.PA_URL);
        String paUsername = ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_LOGIN));
        String paPassword = ProtectionUtils.decrypt(config.getString(PAConfiguration.REST_PASSWORD));

        PAGateway paGateway = new PAGateway(paURL);

        try {
            paGateway.connect(paUsername, paPassword);
            LOGGER.info("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        JSONObject jsonJob = new JSONObject(Utils.getContentWithFileName(EXAMPLES_RELATIVE_PATH + "Job_commands_input.json"));

        String taskName = "Component_App";
        List<String> nodeNames = new LinkedList<>();
        nodeNames.add("component-App-1-2");
        nodeNames.add("component-App-1-3");
        nodeNames.add("component-App-1-4");

//        paGateway.addScaleOutTask(nodeNames,
//                                  jsonJob.optJSONObject("jobInformation").optString("id"),
//                                  taskName);

        paGateway.addScaleInTask(nodeNames,
                                 jsonJob.optJSONObject("jobInformation").optString("id"),
                                 taskName);

//        paGateway.removeClouds(Collections.singletonList("awsbf6b9a0b5b22e1c4b08d7b508487b7e2"), true);

        try {
            LOGGER.info("Waiting ...");
            Thread.sleep(10000);
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
