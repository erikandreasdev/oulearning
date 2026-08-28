#!/bin/bash
set -e
mkdir -p src/test/it

# Find all *IT.java files and the whole com/example/oulearning/it package contents
# Actually, let's just find everything that belongs to ITs.
# We will move all *IT.java
find src/test/java -name "*IT.java" | while read -r file; do
  rel_path=${file#src/test/java/}
  mkdir -p "src/test/it/$(dirname "$rel_path")"
  mv "$file" "src/test/it/$rel_path"
done

# We also need to move IntegrationTestBase, ApiEndpoints, etc. 
# So basically anything in src/test/java/com/example/oulearning/it that isn't a normal java file or we can just move the whole it package.
if [ -d src/test/java/com/example/oulearning/it ]; then
  find src/test/java/com/example/oulearning/it -type f -name "*.java" | while read -r file; do
    rel_path=${file#src/test/java/}
    mkdir -p "src/test/it/$(dirname "$rel_path")"
    mv "$file" "src/test/it/$rel_path"
  done
  # cleanup empty dirs
  find src/test/java -type d -empty -delete
fi
