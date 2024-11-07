
# Use litongjava/jdk:8u211 as the base image
FROM litongjava/jdk:8u211

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/tio-boot-table-to-json-1.0.jar /app/

# Command to run the jar file
CMD ["/usr/java/jdk1.8.0_211/bin/java", "-jar", "tio-boot-table-to-json-1.0.jar"]
