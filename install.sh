#!/bin/bash

        #heroku plugins:install java
        #mvn heroku:deploy -Dheroku.appName=$app
        #heroku restart -a $app

END=30
prefix="mawiwpmar"
main(){
    mvn clean package

        list=""
        destroy
        #deploy
}


destroy(){
    echo "--------------- DESTROY SLAVES ---------------"
    for ((i=1;i<=END;i++)); do
                app=$prefix$i
                heroku apps:destroy $app --confirm $app
    done
}

deploy(){
    echo "--------------- RESTART BUILDING SLAVES ---------------"
    for ((i=1;i<=END;i++)); do
            app=$prefix$i
            list+=" $app"
    #        heroku apps:create $app --no-remote
    #        heroku deploy:jar target/iwp-crawler-0.0.1-SNAPSHOT.jar --app $app
    done
    echo "--------------- STARTING MONITORING SLAVES ---------------"
    java -jar -Dspring.profiles.active=server ./target/iwp-crawler-0.0.1-SNAPSHOT.jar $list
    echo finished
}


main




