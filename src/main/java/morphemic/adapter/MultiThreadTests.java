package morphemic.adapter;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.activeeon.morphemic.PAGateway;
import org.apache.log4j.Logger;
import org.json.JSONArray;

@AllArgsConstructor
public class MultiThreadTests extends Thread {

    private final PAGateway paGateway;

    private final JSONArray jsonNodesArray;

    private final String jsonJobId;

    private static final Logger LOGGER = Logger.getLogger(MultiThreadTests.class);

    @SneakyThrows
    @Override
    public void run() {
        LOGGER.info("Thread adding node " + jsonNodesArray.toString() + " started.");
        paGateway.addNodes(jsonNodesArray, jsonJobId);
        LOGGER.info("Thread added node " + jsonNodesArray.toString() + " properly.");
    }
}
