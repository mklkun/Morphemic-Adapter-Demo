package org.morphemic.adapter;

import org.activeeon.morphemic.infrastructure.deployment.PAResourceManagerGateway;
import org.activeeon.morphemic.application.deployment.PASchedulerGateway;
import org.activeeon.morphemic.utils.PAFactory;
import org.apache.log4j.Logger;
import org.ow2.proactive.scheduler.common.exception.UserException;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive.scheduler.common.job.TaskFlowJob;
import org.ow2.proactive.scheduler.common.task.ScriptTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterDemo {

    private static final Logger LOGGER = Logger.getLogger(AdapterDemo.class);

    private static TaskFlowJob createApplicationWF(List<String> deployedNodes) throws UserException {
        TaskFlowJob myApplication = new TaskFlowJob();

        myApplication.setName("Morphemic_Example_1");
        Map<String, String> jobVariables = new HashMap<>();
        jobVariables.put("DB_USER", "wordpress");
        jobVariables.put("DB_PASSWORD", "pressword");
        jobVariables.put("DB_NAME", "wordpress");
        myApplication.setVariables(PAFactory.variablesToJobVariables(jobVariables));

        //Creating MySQL WF task
        ScriptTask mySQLTask = PAFactory.createBashScriptTask("Start_MySQL_Component", "Start_MySQL_Script.sh");
        mySQLTask.setPreScript(PAFactory.createSimpleScriptFromFIle("pre_script.sh", "bash"));
        mySQLTask.setPostScript(PAFactory.createSimpleScriptFromFIle("MySQL_post_script.groovy", "groovy"));
        mySQLTask.setSelectionScript(PAFactory.createGroovySelectionScript("check_node_name.groovy", new String[]{deployedNodes.get(0)}));
        Map<String, String> mySQLvariables = new HashMap<>();
        mySQLvariables.put("INSTANCE_NAME", "ComponentMySql");
        mySQLTask.setVariables(PAFactory.variablesToTaskVariables(mySQLvariables));

        //Creating Wordpress WF task
        ScriptTask myWordpressTask = PAFactory.createBashScriptTask("Start_Wordpress_Component", "Start_Wordpress_Script.sh");
        myWordpressTask.setPreScript(PAFactory.createSimpleScriptFromFIle("pre_script.sh", "bash"));
        myWordpressTask.setSelectionScript(PAFactory.createGroovySelectionScript("check_node_name.groovy", new String[]{deployedNodes.get(1)}));
        Map<String, String> taskVariables = new HashMap<>();
        taskVariables.put("INSTANCE_NAME", "ComponentMyWordpress");
        myWordpressTask.setVariables(PAFactory.variablesToTaskVariables(taskVariables));
        myWordpressTask.addDependence(mySQLTask);

        //Adding tasks to the workflow
        myApplication.addTask(mySQLTask);
        myApplication.addTask(myWordpressTask);

        return myApplication;
    }

    public static void main(String[] args) {

        PASchedulerGateway schedulerGateway = new PASchedulerGateway();
        PAResourceManagerGateway resourceManagerGateway = new PAResourceManagerGateway();

        String nodeSourceName = "AWSMORPHEMICUP";
        Integer numberVMs = 2;

        try {
            // Connecting to the RM
            resourceManagerGateway.connect();

            // Connecting to the Scheduler
            schedulerGateway.connect();

            LOGGER.info("Deploying VMs");
            resourceManagerGateway.deploySimpleAWSNodeSource(nodeSourceName, numberVMs);

            LOGGER.info("Preparation of deployed node names.");
            List<String> deployedNodes = resourceManagerGateway.getAsyncDeployedNodesInformation(nodeSourceName);

            LOGGER.info("deployedNodes = " + deployedNodes.toString());

            LOGGER.info("Creating application workflow.");
            TaskFlowJob myApplication = createApplicationWF(deployedNodes);

            JobId jobId = schedulerGateway.submit(myApplication);
            LOGGER.info("Job submitted with id = " + jobId);

        } catch (Exception e) {
            LOGGER.error(" ... Error: " + e.getMessage());
        } finally {
            resourceManagerGateway.disconnect();
            schedulerGateway.disconnect();
        }
        LOGGER.info("End.");
    }
}
