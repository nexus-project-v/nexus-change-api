# sevenfood-product-api

docker-compose build
docker-compose build sonarqube
docker-compose up -d
docker-compose logs -f --tail=50000

docker stop $(docker ps -qa)
docker rm $(docker ps -qa)
docker rmi $(docker images -qa) -f

./mvnw clean install test jacoco:report
sudo sysctl -w vm.max_map_count=262144

docker build --network=host -t rogeriofontes/nexus-change-api:v0.0.2 .
docker login
docker push rogeriofontes/nexus-change-api:v0.0.2

docker pull rogeriofontes/nexus-change-api:v6
docker run -p 9934:9934 nexus-change-api:v6
docker run -p 9934:9934 --env-file .env -e SPRING_PROFILES_ACTIVE=dev rogeriofontes/nexus-change-api:v5

====
https://www.zaproxy.org/docs/docker/api-scan/
https://www.zaproxy.org/docs/docker/about/
====
docker run -u zap -p 8080:8080 -v "$(pwd)/docs/zap_workdir:/zap/wrk" -i ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://localhost:9914/api/v3/api-docs -f openapi -c zap-rules.conf -r zap_report_antes_op1.html
docker run -u zap -p 8080:8080 -v "$(pwd)/docs/zap_workdir:/zap/wrk" -i ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://localhost:9914/api/v3/api-docs -f openapi -c zap-rules.conf -r zap_report_antes_op1.html

http://localhost:9914/api/swagger-ui/index.html#/
http://localhost:9914/api/v3/api-docs

docker run -u zap -p 8080:8080 -v "$(pwd)/docs/zap_workdir:/zap/wrk" -i ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://192.168.100.31:9924/api/v3/api-docs -f openapi -c zap-rules.conf -r zap_report_antes_op1.html

#Criar ELK - logs
https://medium.com/@lahiruchandika/integrate-elk-stack-into-spring-boot-application-4991e7918c3a
