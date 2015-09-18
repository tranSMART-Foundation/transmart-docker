# Docker build of transmartApp demo

Use Docker to build a single-machine version of transmart for demos of 
functionality and to provide a platform for tests. 

- Provenance: draws heavily from upstream fork - https://github.com/grumpycatt/transmart-docker
- Other sources, https://github.com/quartzbio/transmart-docker and
https://github.com/io-informatics/transmart-docker

## Deployment
This is an all-in-one tranSMART setup. All processes run on a container from
this single Docker Image. If should be used only for demos and testing of the platform.
In normal production the computational elements (SOLR, the database, R, and Tomcat)
would be split among two or more machines: supporting higher throughput. See: 
the 1.2.4/distributed subdirectory of https://github.com/io-informatics/transmart-docker
for a distributed example.

The amount of RAM is the critical part, at least 4GB should be assigned to the container.

### Pre-requisites

Minimum requirements :
- 2VCPU
- 4GB memory
- 10GB disk

Recommended requirements :
- 4VCPU
- 8GB memory
- 15GB disk

### How to start

There is a **Dockerfile** here, along with supporting material. 
One must use the **docker** command along with a special compute host,
a **docker machine**. To install these elements, follow the install steps described
on the official Docker website: https://docs.docker.com/

Docker must run in the context of a docker environment linked to a running docker-machine; 
once set up the, docker command will be available. In addition, you will need to know the
docker-machine's local IP address; use the command **docker-machine ip default** (where default
is the name of the docker-machine) to obtain the machine's local IP address, 
referred to as *transmartIP*, below.

#### Build and run on your own tranSMART container

These commands will deploy a docker container running tranSMART

    git clone https://github.com/tranSMART-Foundation/transmart-docker.git
    cd transmart-docker
    docker build --rm -t myrepo/transmart .
    docker run -d -p 80:8080 5432:5432 8983:8983 --name transmart myrepo/transmart

#### Access

    Launch your favorite browser and go to http://*transmartIP*

Note, in the above docker run command that port 80 on the docker-machines is mapped to
port 8080 in the container. Likewise, the common port for PostgreSQL, port 5432, on the
docker-machine is mapped to 5432 on *transmartIP*; so, psql can be used with that IP address
for the hostname. Finally, the SOLR port is also mapped; thus, http://*transmartIP*:8983/solr
will provide access to SOLR.

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

* Ubuntu 14.04.3 (trusty)
* PostgreSQL 9.3
* Tomcat7
* Oracle JDK 1.8u51
* tranSMART 1.2.x
* Groovy 2.4.4
* RRO 3.2.0
* Rserve 1.8.x

## Other Docker deployments
There are a few versions (alternatives) of this setup, some of which are:
* [QuartzBio](https://github.com/quartzbio/transmart-docker)
* [io-informatics](https://github.com/io-informatics/transmart-docker)
* [the eTRIKS project's Portal] (https://portal.etriks.org/Portal/transmartRelease/transmartDocker)
* And, of course: search GitHub for "transmart docker". 
