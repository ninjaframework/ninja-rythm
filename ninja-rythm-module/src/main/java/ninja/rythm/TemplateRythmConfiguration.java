/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.rythm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;

import org.rythmengine.Rythm;
import org.rythmengine.extension.ILoggerFactory;
import org.rythmengine.extension.ISourceCodeEnhancer;
import org.rythmengine.logger.ILogger;
import org.rythmengine.resource.ClasspathResourceLoader;
import org.rythmengine.template.ITemplate;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * To Configure Rythm in Ninja style.
 * 
 * @author sojin
 * 
 */
@Singleton
public class TemplateRythmConfiguration {

    private final Messages messages;

    private final NinjaProperties ninjaProperties;

    private final Logger logger;

    private final Properties properties = new Properties();

    @Inject
    public TemplateRythmConfiguration(Messages messages,
                                      Logger ninjaLogger,
                                      NinjaProperties ninjaProperties) {

        this.messages = messages;
        this.logger = ninjaLogger;
        this.ninjaProperties = ninjaProperties;

        init();
    }

    private void init() {

        final boolean isProd = ninjaProperties.isProd();

        properties.put("rythm.engine.mode", isProd ? Rythm.Mode.prod : Rythm.Mode.dev);
        properties.put("rythm.engine.file_write", !isProd);
        properties.put("rythm.engine.plugin.version", "");
        properties.put("rythm.resource.loader.impls", new ClasspathResourceLoader(null, "/"));
        if (!isProd) {
            File tmpDir = new File("target/rythm");
            tmpDir.mkdirs();
            properties.put("rythm.home.tmp.dir", tmpDir);
            logger.info("rythm tmp dir set to {}", tmpDir);
        }

        properties.put("rythm.log.factory", new ILoggerFactory() {
            @Override
            public ILogger getLogger(Class<?> clazz) {
                return new TemplateEngineRythmLogger(logger);
            }
        });

//        String classLocation = TemplateRythmConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//
//        Optional<String> templateDir, tmpDir = Optional.absent();
        // TODO revisit here once issue
        // https://github.com/greenlaw110/Rythm/issues/173 solved.
        
        ///
//        if (classLocation.contains("WEB-INF") && classLocation.contains("ninja-rythm-module") /* dirty hack until then */) {
//            String webInf = classLocation.substring(0, classLocation.indexOf("WEB-INF") + 7) + File.separator;
//            templateDir = Optional.of(webInf + "classes" + File.separator + "views");
//            properties.put("rythm.engine.file_write", false);
//            properties.put("rythm.engine.mode", Rythm.Mode.prod);
//            logger.warn("Rythm seems to be running in a servlet container, set the mode to prod.");
//        } else {
//            String tmp = System.getProperty("user.dir") + File.separator;
//            templateDir = Optional.of(tmp + "src" + File.separator + "main" + File.separator + "java");
//            tmpDir = Optional.of(tmp + "target" + File.separator + "tmp" + File.separator + "__rythm");
//        }

//        if (templateDir.isPresent()) {
//            properties.put("rythm.home.template", new File(templateDir.get()));
//            logger.info("rythm template root set to: {}", properties.get("rythm.home.template"));
//        } else {
//            logger.error("Unable to set rythm.home.template");
//        }
        
//        if (isProd || (ninjaProperties.getBooleanWithDefault("rythm.gae", false))){
//            properties.put("rythm.engine.file_write", false);
//            logger.info("In Prod/GAE mode, Rythm engine writing to file system disabled.");
//        } else if (tmpDir.isPresent()){
//            File temp = new File(tmpDir.get());
//            if (!temp.exists()) {
//                temp.mkdirs();
//            }
//            properties.put("rythm.home.tmp", temp);
//            logger.info("rythm tmp dir set to {}", properties.get("rythm.home.tmp"));
//        } else {
//            logger.info("Didn't set rythm.home.tmp.");
//        }
        
        properties.put("rythm.resource.autoScan", false);
        
        // Add all renderArgs specified in application.conf - rythm.renders.*
        final Map<String, Object> renderArgs = new HashMap<String, Object>();
        renderArgs.put("flash", "java.util.Map<String, String>");
        renderArgs.put("lang", "java.lang.String");
        renderArgs.put("contextPath", "java.lang.String");
        renderArgs.put("session", "java.util.Map<String, String>");
        for (String key : ninjaProperties.getAllCurrentNinjaProperties().stringPropertyNames()) {
            if (key.startsWith("rythm.renders.")) {
                String name = key.substring("rythm.renders.".length());
                String clazzName = ninjaProperties.get(key);
                renderArgs.put(name, clazzName);
            }
        }
        
        // Add all imports specified in application.conf - rythm.imports
        final List<String> imports = new ArrayList<>();
        String[] importsArray = ninjaProperties.getStringArray("rythm.imports");
        if (importsArray != null) {
            imports.addAll(Arrays.asList(importsArray));
        }
        

        properties.put("rythm.codegen.source_code_enhancer",
                new ISourceCodeEnhancer() {

                    @Override
                    public Map<String, ?> getRenderArgDescriptions() {
                        return renderArgs;
                    }

                    @Override
                    public void setRenderArgs(ITemplate template) {

                    }

                    @Override
                    public List<String> imports() {
                        return imports;
                    }

                    @Override
                    public String sourceCode() {
                        return null;
                    }
                });

        properties.put("rythm.i18n.message.resolver",
                new TemplateEngineRythmI18nMessageResolver(messages));

        // Set Rythm properties coming from Ninja application.conf
        // allows users to override any rythm properties.
        for (String key : ninjaProperties.getAllCurrentNinjaProperties().stringPropertyNames()) {
            if (key.startsWith("rythm.")) {
                properties.setProperty(key, ninjaProperties.get(key));
            }
        }
    }

    public Properties getConfiguration() {
        return properties;
    }
}
