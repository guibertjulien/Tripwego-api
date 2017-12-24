tripwego-api
=============================================

sample : https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/appengine-java8/endpoints-v2-backend

https://console.cloud.google.com/endpoints/api/tripwego-api.appspot.com/overview?project=tripwego-api

# App Engine Standard & Google Cloud Endpoints Frameworks & Java

Tripwego API with Google Cloud Endpoints Frameworks using
Java on App Engine Standard.

## Build with Maven

### Building the project

To build the project:

    mvn clean package

### Generating the openapi.json file

To generate the required configuration file `openapi.json`:

    mvn endpoints-framework:openApiDocs

### Deploying the API to App Engine

To deploy the API:

0. Invoke the `gcloud` command to deploy the API configuration file:

         gcloud endpoints services deploy target/openapi-docs/openapi.json

0. Deploy the API implementation code by invoking:

         mvn appengine:deploy

    The first time you upload a sample app, you may be prompted to authorize the
    deployment. Follow the prompts: when you are presented with a browser window
    containing a code, copy it to the terminal window.

0. Wait for the upload to finish.

### Updating index.yaml

         gcloud datastore cleanup-indexes /Users/JG/Documents/DEV/WK_TRIPWEGO/tripwego-api/src/main/webapp/WEB-INF/index.yaml
         gcloud datastore create-indexes /Users/JG/Documents/DEV/WK_TRIPWEGO/tripwego-api/src/main/webapp/WEB-INF/index.yaml

### Updating cron task (update or remove)

         gcloud app deploy /Users/JG/Documents/DEV/WK_TRIPWEGO/tripwego-api/src/main/webapp/WEB-INF/cron.yaml
