#!/bin/bash

    END=30
    for ((i=1;i<=END;i++)); do
        app=mawiwpmar$i
        echo heroku apps:destroy $app --confirm $app
        heroku apps:destroy $app --confirm $app
    done

