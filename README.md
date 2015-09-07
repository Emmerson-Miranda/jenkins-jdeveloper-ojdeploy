jenkins-jdeveloper-ojdeploy
===========================

Plugin for allow Jenkins execute JDeveloper ojdeploy utility.

With this you don't need add Ant/Maven scripts to build your ADF applications in Jenkins.

Let's use multiple versions of JDeveloper Ojdeploy utility (jenkins->configuration).

![alt tag](http://3.bp.blogspot.com/-xBXILx3KEMg/VTTHrkE44kI/AAAAAAAAA0c/Cw1J4kn_4HU/s1600/configure_ojdeploy_list.png)


Easy job configuration, just read ojdeploy command line documentation.

![alt tag](http://3.bp.blogspot.com/-nO1MhrIqMYY/VTTHrrr8fyI/AAAAAAAAA0Y/MQ5d9MMqKgE/s1600/task_configure.png)


Installation with command line
==============================
git clone https://github.com/Emmerson-Miranda/jenkins-jdeveloper-ojdeploy.git

cd jenkins-jdeveloper-ojdeploy

mvn clean package

take the target\ojdeploy.hpi generated file and install it into jenkins.
