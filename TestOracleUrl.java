import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

public class TestOracleUrl {
    public static void main(String[] args) {
        OracleContainer oc = new OracleContainer(DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart").asCompatibleSubstituteFor("gvenzl/oracle-xe"))
            .withDatabaseName("FREEPDB1")
            .withEnv("ORACLE_DATABASE", "");
        
        System.out.println("JDBC URL: " + oc.getJdbcUrl());
        System.out.println("Env ORACLE_DATABASE: " + oc.getEnvMap().get("ORACLE_DATABASE"));
    }
}
