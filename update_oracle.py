import os
import glob

directory = "src/test/it/"
pattern = "**/*IT.java"

for filepath in glob.glob(os.path.join(directory, pattern), recursive=True):
    with open(filepath, "r") as f:
        content = f.read()
    
    # We want to replace the OracleContainer definition
    # This might be tricky with regex, let's just do a string replace since they are all identical.
    
    if "static OracleContainer oracle" in content:
        # Find the block
        lines = content.split('\n')
        new_lines = []
        skip = False
        for i, line in enumerate(lines):
            if "@ServiceConnection" in line and "OracleContainer" in lines[i+1]:
                # We skip @ServiceConnection
                continue
            if "static OracleContainer oracle = new OracleContainer" in line:
                # Add GenericContainer
                new_lines.append('    @org.testcontainers.junit.jupiter.Container')
                new_lines.append('    static org.testcontainers.containers.GenericContainer<?> oracle = new org.testcontainers.containers.GenericContainer<>(org.testcontainers.utility.DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart"))')
                new_lines.append('            .withEnv("APP_USER", "test")')
                new_lines.append('            .withEnv("APP_PASSWORD", "test")')
                new_lines.append('            .withExposedPorts(1521)')
                new_lines.append('            .waitingFor(new org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy().withRegEx(".*DATABASE IS READY TO USE!.*\\\\s"));')
                new_lines.append('')
                new_lines.append('    @org.springframework.test.context.DynamicPropertySource')
                new_lines.append('    static void oracleProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {')
                new_lines.append('        registry.add("spring.datasource.url", () -> "jdbc:oracle:thin:@localhost:" + oracle.getMappedPort(1521) + "/FREEPDB1");')
                new_lines.append('        registry.add("spring.datasource.username", () -> "test");')
                new_lines.append('        registry.add("spring.datasource.password", () -> "test");')
                new_lines.append('    }')
                skip = True
                continue
                
            if skip:
                if line.strip() == "":
                    skip = False
                    # don't append this blank line because we already added one
                elif ");" in line:
                    skip = False
                continue
            
            # Remove imports
            if "import org.testcontainers.containers.OracleContainer;" in line:
                continue
            if "import org.springframework.boot.testcontainers.service.connection.ServiceConnection;" in line:
                continue
            if "import org.testcontainers.utility.DockerImageName;" in line:
                continue
            if "import org.testcontainers.junit.jupiter.Container;" in line:
                continue
                
            new_lines.append(line)
            
        with open(filepath, "w") as f:
            f.write('\n'.join(new_lines))
        print(f"Updated {filepath}")
