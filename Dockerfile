# Use a suitable base image with JDK 17, Python, and other necessary dependencies
FROM python:3.11 AS base

# Install JDK 17
RUN apt-get update && apt-get install -y openjdk-17-jdk

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Set the working directory in the container
WORKDIR /app

# Python - for audio transcription
RUN apt-get install -y python3.11 python3-pip git

# Copy and install Python dependencies
COPY . .
RUN pip3 install -r requirements.txt

# TODO: Chrome web driver?

# Streamlink - for audio recordings from Twitch
RUN apt-get install -y streamlink

# Tesseract - for OCR on Twitch screenshots
RUN apt-get install -y tesseract-ocr
RUN apt-get install -y libtesseract-dev


# Maven build stage
FROM maven:3.8.4-openjdk-17 AS maven

# Set the working directory in the Maven build stage
WORKDIR /app
RUN mkdir /temp
RUN mkdir /temp/incoming
RUN mkdir /temp/complete
RUN mkdir /temp/audio
RUN mkdir /temp/processing
RUN mkdir /temp/logos

# Copy the Maven project file from the base stage
COPY --from=base /app .

# Build the project with Maven
RUN mvn clean package -DskipTests

# Final stage
FROM base AS final

# Copy the built artifacts from the Maven build stage
COPY --from=maven /app/target/rlcs-bot-1.0-SNAPSHOT-jar-with-dependencies.jar /app/target/

# Define the command to run your application
# TODO: replace with actual variables
CMD ["java", "-jar", "-DELASTICSEARCH_HOST=https://elasticsearch:9200", "-DELASTICSEARCH_USERNAME=elastic", "-DELASTICSEARCH_PASSWORD=password", "-DAPP_PORT=8080", "-DSECRET_ADMIN_APP_PORT=1", "target/rlcs-bot-1.0-SNAPSHOT-jar-with-dependencies.jar"]