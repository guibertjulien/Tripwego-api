tripwego-api
=============================================

# remove oauth2 tokens

MacBook-Pro-de-Julien:~ JG$ rm .appcfg_oauth2_tokens_java

gcloud components update
gcloud config set project tripwego-api

# build

mvn clean package
mvn exec:java -DGetSwaggerDoc

gcloud service-management deploy openapi.json

# deploy to Google Cloud

mvn appengine:deploy

# updating index.yaml

gcloud datastore cleanup-indexes /Users/JG/Documents/DEV/WK_TRIPWEGO/tripwego-api/src/main/webapp/WEB-INF/index.yaml
gcloud datastore create-indexes /Users/JG/Documents/DEV/WK_TRIPWEGO/tripwego-api/src/main/webapp/WEB-INF/index.yaml

# updating cron task (update or remove)

gcloud app deploy /Users/JG/Documents/DEV/WK_TRIPWEGO/tripwego-api/src/main/webapp/WEB-INF/cron.yaml
