#!/bin/bash

service postgresql start
screen -AdmS SolR make -C /tmp/transmart-data/solr/ start
sudo -u tomcat7 /usr/share/tomcat7/bin/catalina.sh run
sudo -u tomcat7 /opt/R/bin/R CMD Rserve --slave --vanilla --RS-conf /etc/Rserve.conf

# The container will run as long as the script is running, that's why
# we need something long-lived here
exec tail -f /var/log/tomcat7/catalina.out
