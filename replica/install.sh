#!/bin/bash

        #heroku plugins:install java
        #mvn heroku:deploy -Dheroku.appName=$app
        #heroku restart -a $app

mvn clean package

while true; do

    END=50
    list=""
    echo "--------------- RESTART BUILDING SLAVES ---------------"
    for ((i=1;i<=END;i++)); do
        app=iwp$i
        list+=" $app"
        heroku apps:destroy $app --confirm $app
        heroku apps:create $app --no-remote
        heroku deploy:jar target/iwp-crawler-0.0.1-SNAPSHOT.jar --app $app
    done
    echo "--------------- STARTING MONITORING SLAVES ---------------"
    nohup java -jar -Dspring.profiles.active=server ./target/iwp-crawler-0.0.1-SNAPSHOT.jar $list > /dev/null &

done





