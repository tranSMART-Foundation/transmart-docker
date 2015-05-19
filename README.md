# A dockerfile in order to generate a tranSMART ready to go container

## Author
There is few versions and perceptions of this setup, here is the list of what I know :
* [QuartzBio](https://github.com/quartzbio/transmart-docker)
* [io-informatics](https://github.com/io-informatics/transmart-docker)

Here is another Dockerfile with an emphasis on best practices and performance regarding to tranSMART ecosystem.

Pull requests are welcome.

## Deployment
This is an all-in-one tranSMART setup thus it should not be used for production. The database, R, and Tomcat should be splitted in order to achieve a good througtput.

The amount of RAM is the critical part, at least 4GB should be assigned to the JVM for tranSMART to run properly.
10GB of disk free are recommended, the built image taking 6GB at its creation and 2VCPU are enough for the rest.

### How to start

#### Run the container from Docker Registry (easy)


#### Build and run on your own (advanced)
    git clone https://github.com/grumpycatt/transmart-docker.git
    cd transmart-docker
    docker build --rm -t myrepo/transmart .
    docker run -d -p 80:8080 5432:5432 8983:8983 --name transmart myrepo/transmart

#### Access

    Launch your favorite browser and go to http://transmartIP/transmart

#### Debug and core components
Look at running processes into your container with :

    docker ps
    docker exec transmart ps axuf

There must be at least 4 processes :
- PostgreSQL
- Java/Tomcat
- R
- SolR

Probe your network access :

    docker exec transmart netstat -tlup
    nc localhost 80 -v

Check the logs :

    docker logs transmart

* PostgreSQL 9.3
* Tomcat7
* Oracle JDK 1.8u45
* tranSMART 1.2.x
* Groovy 2.4.3
