jenkins-jdeveloper-ojdeploy
===========================

Plugin for allow Jenkins execute JDeveloper ojdeploy utility.

With this you don't need add Ant/Maven scripts to build your ADF applications in Jenkins.

Let's use multiple versions of JDeveloper Ojdeploy utility (jenkins->configuration).

Easy job configuration, just read ojdeploy command line documentation.

Installation
===========================
git clone https://github.com/Emmerson-Miranda/jenkins-jdeveloper-ojdeploy.git
cd jenkins-jdeveloper-ojdeploy
mvn clean package
take the target\ojdeploy.hpi generated file and install it into jenkins.