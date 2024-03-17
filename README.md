# Installation

Dockerising this is on my to-do list, so good luck.

1. Clone the project.  Carefully review `src/main/java/com/wfbfm/rlcsbot/app/RuntimeConstants.java`
2. Create a python venv in the project directory and do a `pip install -r requirements.txt`.
If not running on Windows, update the PYTHON_VENV_PATH in RuntimeConstants.java
3. Install [ffmpeg](https://ffmpeg.org/) if not already available on your machine
4. Install [streamlink](https://streamlink.github.io/install.html) if not already available on your machine
5. Create a folder in project directory `/appconfig/` - this will hold the Elastic API key
6. Follow the [Elastic Docker instructions](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html) to
configure a single-node 8.12.2 Elasticsearch container:
   *  `docker network create elastic`
   *  `docker pull docker.elastic.co/elasticsearch/elasticsearch:8.12.2`
   *  `docker run --name es01 --net elastic -p 9200:9200 -it -m 1GB docker.elastic.co/elasticsearch/elasticsearch:8.12.2`
   *  Take note of the Elastic password that is printed to the terminal after running the container for the first time
   *  Copy the generated http_ca.crt to your machine `docker cp es01:/usr/share/elasticsearch/config/certs/http_ca.crt .`
   *  Add the generated http_ca.crt to your JDK trusted certs - similar to `keytool -importcert -alias elasticSearch -file "http_ca.crt" -keystore "%JAVA_HOME%\lib\security\cacerts"`
7. Continue following the [Elastic Docker instructions](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)
to stand up a Kibana container:
   * `docker pull docker.elastic.co/kibana/kibana:8.12.2`
   * `docker run --name kib01 --net elastic -p 5601:5601 docker.elastic.co/kibana/kibana:8.12.2`
   * Navigate to the link printed to the terminal - typically `localhost:5601?code=XXX` with a trailing unique code
   * Log in with user `elastic` and the password generated in 6)
   * Generate an API key via your [local Kibana config](http://localhost:5601/app/management/security/api_keys) page
   * Copy your API key to a new file `/appconfig/elastic_api_key.txt`

