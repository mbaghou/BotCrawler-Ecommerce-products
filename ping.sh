#!/bin/bash


echo "--------------- START BUILDING SLAVES ---------------"

END=31
list=""
for ((i=2;i<=END;i++)); do
    echo curl https://iwp$i.herokuapp.com
    curl https://iwp$i.herokuapp.com
done

