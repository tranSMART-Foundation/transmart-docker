#!/bin/bash

initctl start rserve
service postgresql start
screen -AdmS SolR make -C /tmp/transmart-data/solr/ start
service tomcat7 start

# The container will run as long as the script is running, that's why
# we need something long-lived here
exec tail -f /var/log/tomcat7/catalina.out
