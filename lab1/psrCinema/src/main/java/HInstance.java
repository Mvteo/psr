import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

import java.net.UnknownHostException;

public class HInstance {

    public static void main(String[] args) throws UnknownHostException {
        Config config = HConfig.getConfig();
        Hazelcast.newHazelcastInstance(config);
    }
}
