Rythm template module for Ninja framework.
=====================
Rythm (http://rythmengine.org/) is a feature rich, very high performing and easy to use static template engine written in Java.

This is an easly plugable module for Ninja web framework to write templates using Rythm engine.

Getting started
---------------

Setup
-----

1) Add the ninja-rythm dependency to your pom.xml:

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-rythm-module</artifactId>
        <version>0.0.1</version>
    </dependency>

2) Install the module in your conf.Module:

    @Override
    protected void configure() {
        install(new NinjaRythmModule());
    }
    
3) All set. Start writing template in 'views' folder of your application.


4) Or check out <code>ninja-rythm-demo</code>. Run any one of the below commands under demo:

    mvn jetty:run
    OR 
    mvn tomcat7:run


Modify code/template -- Save -- Refresh browser. Enjoy!


***TODO - Usage in detail.***

