mvn clean install

cp target/*.war ${JBOSS_HOME}/server/all/deploy
cp target/*.war ${JBOSS_HOME}/server/default/deploy



cp target/*.war ${JBOSS_HOME2}/server/all/deploy
cp target/*.war ${JBOSS_HOME2}/server/default/deploy
