package morphemic.adapter;

import org.activeeon.morphemic.PAGateway;
import org.activeeon.morphemic.model.Image;
import org.activeeon.morphemic.model.Job;
import org.activeeon.morphemic.service.EntityManagerHelper;
import org.apache.log4j.Logger;

import java.util.List;

public class DraftTests {

    private static final Logger LOGGER = Logger.getLogger(DraftTests.class);

    public static void main(String[] args) {

        LOGGER.info("Begin");

        String paURL = "https://trydev2.activeeon.com:8443";
        String paUsername = "labidi";
        String paPassword = "ProActive123";

//        String paURL = "http://localhost:8080";
//        String paUsername = "admin";
//        String paPassword = "admin";

        PAGateway paGateway = new PAGateway(paURL);

        try {
            paGateway.connect(paUsername, paPassword);
            LOGGER.info("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        paGateway.getAllCloudImages("dummy");


//        String EXAMPLES_FOLDER_PATH = "/Users/mkl/Desktop/Morphemic_client";
//        JSONObject jsonJob = null;
//        try {
//            jsonJob = new JSONObject(new String(Files.readAllBytes(Paths.get(EXAMPLES_FOLDER_PATH + "/Example_Commands/Job_commands_input.json"))));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String jobId = jsonJob.optJSONObject("jobInformation").optString("id");
//        LOGGER.info("Job " + jobId + " state: " + paGateway.getJobState(jobId).getStatus().toString());


//        List<Port> ports = EntityManagerHelper.createQuery("SELECT p FROM Port p", Port.class).getResultList();
//        System.out.println("ports: ");
//        ObjectMapper mapper = new ObjectMapper();
//        ports.forEach(port -> {
//            try {
//                System.out.println("   " + mapper.writeValueAsString(port));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });




//        List<PACloud> paClouds = EntityManagerHelper.createQuery("SELECT pac FROM PACloud pac", PACloud.class).getResultList();
//        System.out.println("paClouds: ");
//        paClouds.forEach(paCloud -> System.out.println("   " + paCloud.toString()));
////
//        List<Deployment> deployments = EntityManagerHelper.createQuery("SELECT d FROM Deployment d", Deployment.class).getResultList();
//        System.out.println("Deployments: ");
//        deployments.forEach(deployment -> System.out.println("   " + deployment.toString()));
//
//        List<Task> tasks = EntityManagerHelper.createQuery("SELECT t FROM Task t", Task.class).getResultList();
//        System.out.println("Tasks: ");
//        tasks.forEach(task -> System.out.println("   " + task.toString()));
//
        List<Job> jobs = EntityManagerHelper.createQuery("SELECT j FROM Job j", Job.class).getResultList();
        System.out.println("jobs: " + jobs.toString());
//
        List<Image> images = EntityManagerHelper.createQuery("SELECT im FROM Image im", Image.class).getResultList();
        System.out.println("images: " + images.toString());
//
//        List<Location> locations = EntityManagerHelper.createQuery("SELECT loc FROM Location loc", Location.class).getResultList();
//        System.out.println("locations: " + locations.toString());

//        List<NodeCandidate> allNodeCandidates = EntityManagerHelper.createQuery("SELECT nc FROM NodeCandidate nc", NodeCandidate.class).getResultList();
//        System.out.println("nodeCandidates: " + allNodeCandidates.toString());

        System.out.println("Done");

        System.exit(0);
    }
}

