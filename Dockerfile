# eTRIKS tranSMART 1.2.x All-In-One deployment

FROM ubuntu:latest
MAINTAINER Leslie-A DENIS <leslie-alexandre.denis@cc.in2p3.fr>
LABEL Description="tranSMART 1.2.x eTRIKS All-In-One instance in order to test the product deployment" Vendor="eTRIKS" Version="1.0"

# Core vars
ENV deps_pkgs="libcairo2-dev php5-cli php5-json gfortran g++ libreadline-dev \
               libxt-dev libpango1.0-dev texlive-fonts-recommended tex-gyre fonts-dejavu-core"
ENV war_url="https://owncloud.etriks.org/index.php/s/E6RplJ8Hie3p2kU/download"
ENV jdk_heap=4G
ENV jdk_url="http://download.oracle.com/otn-pub/java/jdk/8u51-b16/jdk-8u51-linux-x64.tar.gz"
ENV r_debpackage_url="https://owncloud.etriks.org/index.php/s/mwGXYd3wopFVFza/download"
ENV r_revomath_url="https://owncloud.etriks.org/index.php/s/lAUQz00IQICns9m/download"
ENV r_rserve_url="http://www.rforge.net/src/contrib/Rserve_1.8-4.tar.gz"

# tranSMART vars
ENV PGDATABASE=transmart
ENV TABLESPACES=/var/lib/postgresql/tablespaces/
ENV PGHOST=localhost
ENV PGPORT=5432
ENV PGUSER=postgres

# Debconf
ENV DEBIAN_FRONTEND=noninteractive

# User
USER root

# --------------

# Core
RUN apt-get update && apt-get install --no-install-recommends -y \
  build-essential   \
  curl   \
  git    \
  tar    \
  wget  \
  unzip \
  sudo \
  rsync \
  postgresql \
  openjdk-7-jdk \
  screen \
  php5-cli \
  php5-json \
  libssl-dev \
  locales \
  $deps_pkgs

RUN apt-get build-dep --no-install-recommends -y \
  r-cran-rserve

# --------------

# Locale setup
RUN locale-gen en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8

# --------------

# Fetching last stable tranSMART data repo
RUN cd /tmp && \
    git clone --progress https://github.com/transmart/transmart-data.git && \
    cd transmart-data && \
    git checkout tags/v1.2.4

WORKDIR /tmp/transmart-data

RUN bash -c "make -C env /var/lib/postgresql/tablespaces && \
    make -C env update_etl data-integration ../vars"

# Groovy installation
RUN bash -c "curl -s get.gvmtool.net | bash && \
    source "$HOME/.gvm/bin/gvm-init.sh" && \
    gvm install groovy && \
    gvm default groovy 2.4.4"

# Public data loading
RUN service postgresql start && \
    bash -lc "source vars;make -j4 postgres && \
    make update_datasets && \
    make -C samples/postgres load_clinical_GSE8581 && \
    make -C samples/postgres load_ref_annotation_GSE8581 && \
    make -C samples/postgres load_expression_GSE8581"

# PostgreSQL config
RUN echo "host    all    postgres    0.0.0.0/0    md5" >> /etc/postgresql/9.3/main/pg_hba.conf
RUN sed -i 's/'localhost'/'*'/' /etc/postgresql/9.3/main/postgresql.conf

# --------------

# R
WORKDIR /tmp
ADD includes/R/Rserve.conf /etc/Rserve.conf
ADD includes/R/R_pkgs* /tmp/
RUN wget -q "$r_debpackage_url" -O RRO.deb && \
    wget -q "$r_revomath_url" -O RevoMath.tar.gz && \
    wget -q "$r_rserve_url" -O Rserve.tar.gz && \
    dpkg -i RRO.deb && \
    tar xzf RevoMath.tar.gz && \
    R --no-save --no-restore --no-readline --verbose -f R_pkgs_cran.R && \
    R --no-save --no-restore --no-readline --verbose R_pkgs_bioconductor.R && \
    R --no-save --no-restore --no-readline --verbose R_pkgs_update.R && \
    R CMD INSTALL Rserve.tar.gz && \
    cd RevoMath && bash RevoMath.sh

# tomcat7
RUN apt-get install --no-install-recommends -y tomcat7 tomcat7-common && \
    mkdir -p /usr/share/tomcat7 && chown tomcat7:tomcat7 /usr/share/tomcat7
WORKDIR /tmp/transmart-data
RUN bash -c "source vars;TSUSER_HOME=/usr/share/tomcat7/ make -C config/ install && make -C solr/ solr_home"

# --------------

# Java
ADD includes/setenv.sh /usr/share/tomcat7/bin/setenv.sh
RUN echo JAVA_OPTS=\"-server -d64 -XX:+AggressiveOpts -XX:+UseAES -XX:+UseAESIntrinsics -XX:MaxHeapSize=$jdk_heap\" >> /usr/share/tomcat7/bin/setenv.sh && \
    chmod +x /usr/share/tomcat7/bin/setenv.sh

# Oracle JDK
WORKDIR /tmp
RUN wget -q --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" $jdk_url && \
    tar xzf jdk-8u51-linux-x64.tar.gz -C /usr/lib/jvm

# --------------

# tranSMART Config
ADD includes/Config-eTRIKS.groovy /usr/share/tomcat7/.grails/transmartConfig/Config.groovy
ADD includes/tomcat7 /etc/default/tomcat7

# tranSMART WAR
RUN rm -r /var/lib/tomcat7/webapps/ROOT && \
    wget -q -O /var/lib/tomcat7/webapps/ROOT.war "$war_url"

# --------------

ADD includes/run.sh /root/run.sh
RUN chmod +x /root/run.sh

# --------------

# Open ports
EXPOSE 8080 8983 5432

ENTRYPOINT ["/root/run.sh"]
