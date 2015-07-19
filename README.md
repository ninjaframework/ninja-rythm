Rythm template module for Ninja framework.
=====================
Rythm (http://rythmengine.org/) is a feature rich, very high performing and easy to use static template engine written in Java.

This is an easly plugable module for Ninja web framework to write templates using Rythm engine.

Getting started
---------------

To be done:
- Update all templates in the integration test to rythm, it's just a bare copy of ninja-servlet-integration-test for the moment
- How to push all this to maven-central ?

Setup
-----

1) Add the ninja-rythm dependency to your pom.xml:

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-rythm-module</artifactId>
        <version>1.0.5-SNAPSHOT</version>
    </dependency>

2) Install the module in your conf.Module, and choose the template engines you want to use:

    @Override
    protected void configure() {
        install(new NinjaRythmModule());
        install(new NinjaTemplateSelectorModule(
                getProvider(TemplateEngineRythm.class),
                getProvider(TemplateEngineFreemarker.class)
                ));
    }
    
3) All set. Start writing template in 'views' folder of your application.

Templates must have ".rtm.html" extension, but due to internals in rythm engine, includes must have ".html" extension.

4) Or check out <code>ninja-rythm-demo</code>. Run any one of the below commands under demo:

    mvn ninja:run

Modify code/template -- Save -- Refresh browser. Enjoy!

Hot-deploy
----------

To keep hot-deploy feature of updated templates, you must make some changes to your pom.xml:

- change the jetty maven plugin configuration to exclude **/*.html instead of only **/*.ftl.html
- change the ninja maven plugin configuration:

	<plugin>
		<groupId>org.ninjaframework</groupId>
		<artifactId>ninja-maven-plugin</artifactId>
		<version>${ninja.version}</version>
		<configuration>
			<excludes>
				<exclude>(.*)\\views\\(.*)\.html</exclude>
				<exclude>(.*)/views/(.*)\.html</exclude>
				<exclude>(.*)\\assets\\(.*)</exclude>
				<exclude>(.*)/assets/(.*)</exclude>
			</excludes>
		</configuration>
	</plugin>

Modify template -- Save -- Refresh browser and the application does not restart any more. Enjoy!

Usage
-----

In addition to rythm engine templating features :
- allow to add singleton objects to all templates, using rythm.engine.<name>=<class_name>
	The class name must extend RythmRenderUtility, see RythmUtils in demo
- allow to add imports to all templates, using rythm.imports=<import>,<import>,...
	See application.conf in demo

In dev mode, java and rythm temporary files are written in target/rythm folder.
In production mode, there are no temporary files.

