import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

public class TestOracle {
    public static void main(String[] args) {
        try {
            OracleContainer oc = new OracleContainer("gvenzl/oracle-free:23-slim");
            System.out.println("Success without asCompatibleSubstituteFor");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
