# Use a suitable base image with JDK 17, Python, and other necessary dependencies
FROM python:3.11 AS base

# Force to use https for apt-get
RUN echo "deb https://deb.debian.org/debian/ stable main" > /etc/apt/sources.list

# Install JDK 17
RUN apt-get update && apt-get install -y openjdk-17-jdk

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Set the working directory in the container
WORKDIR /app

RUN apt-get install -y git vim less

# Copy and install Python dependencies
COPY . .

RUN pip3 install -r requirements.txt

RUN chmod +x /app/import_cert.sh
RUN tr -d '\r' < /app/import_cert.sh > /app/import_cert_unix.sh
RUN chmod +x /app/import_cert_unix.sh

# Streamlink - for audio recordings from Twitch
RUN apt-get install -y streamlink
RUN apt-get install -y ffmpeg

# Tesseract - for OCR on Twitch screenshots
RUN apt-get install -y tesseract-ocr
RUN apt-get install -y libtesseract-dev

# Install Google Chrome
RUN curl -sS -o - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list && \
    apt-get -yqq update && \
    apt-get -yqq install google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Maven build stage
FROM maven:3.8.4-openjdk-17 AS maven

# Set the working directory in the Maven build stage
WORKDIR /app

# Copy the Maven project file from the base stage
COPY --from=base /app .

# Build the project with Maven
RUN mvn clean package -DskipTests

# Final stage
FROM base AS final

# Copy the built artifacts from the Maven build stage
COPY --from=maven /app/target/rlcs-bot-1.0-SNAPSHOT-jar-with-dependencies.jar /app/target/

RUN mkdir /app/temp
RUN mkdir /app/temp/incoming
RUN mkdir /app/temp/complete
RUN mkdir /app/temp/audio
RUN mkdir /app/temp/processing
RUN mkdir /app/temp/logos

# Define the command to run your application
CMD ["bash", "-c", "./import_cert_unix.sh && java -jar target/rlcs-bot-1.0-SNAPSHOT-jar-with-dependencies.jar"]