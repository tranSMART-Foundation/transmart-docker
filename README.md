## A dockerfile to generate an eTRIKS-tranSMART ready to go container

## Purpose of this Dockerfile
Here is a Dockerfile with an emphasis on best practices and performance regarding to tranSMART ecosystem.
Pull requests are welcome.

## Deployment
This is an all-in-one tranSMART setup thus it should be used only for the test of eTRIKS platform.
In general the database, R, and Tomcat should be split in order to achieve a good througtput.

The amount of RAM is the critical part, at least 4GB should be assigned to the container.

### Pre-requisites

Minimum requirements :
- 2VCPU
- 4GB
- 10GB

Recommended requirements :
- 4VCPU
- 8GB
- 15GB

This is a **Dockerfile** which **needs** a special compute host with **Docker engine** installed on it. To proceed to, you can follow few steps described
on the official Docker website.

- For Debian follow : https://docs.docker.com/installation/debian/
- For Ubuntu follow : https://docs.docker.com/installation/ubuntulinux/
- For CentOS follow : https://docs.docker.com/installation/centos/

### How to start

#### Build and run on your own eTRIKS-tranSMART container

These commands will deploy an eTRIKS-tranSMART environnment with the latest tranSMART 1.2.2e

    git clone https://github.com/grumpycatt/transmart-docker.git
    cd transmart-docker
    docker build --rm -t myrepo/transmart .
    docker run -d -p 80:8080 5432:5432 8983:8983 --name transmart myrepo/transmart

#### Access

    Launch your favorite browser and go to http://transmartIP

#### Debug and core components

Look at running processes into your container with :

    docker ps
    docker exec transmart ps axuf

There must be at least 4 processes :
* PostgreSQL
* Java/Tomcat
* R
* SolR

Probe your network access :

    docker exec transmart netstat -tlup
    nc localhost 80 -v

Check the logs :

    docker logs transmart

#### Essential components used

* PostgreSQL 9.3
* Tomcat7
* Oracle JDK 1.8u51
* tranSMART 1.2.x
* Groovy 2.4.4
* RRO 3.2.0
* Rserve 1.8.x

## Other Docker deployments
There is few versions and perceptions of this setup, here is the list of what I know :
* [QuartzBio](https://github.com/quartzbio/transmart-docker)
* [io-informatics](https://github.com/io-informatics/transmart-docker)
