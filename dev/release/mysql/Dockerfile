FROM mariadb:10.5.8

ENV MYSQL_DATABASE=openspg \
    MYSQL_ROOT_PASSWORD=openspg

ADD ./initdb.sql /docker-entrypoint-initdb.d

EXPOSE 3306


