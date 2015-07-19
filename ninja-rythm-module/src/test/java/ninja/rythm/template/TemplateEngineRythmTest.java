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

package ninja.rythm.template;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

import ninja.Context;
import ninja.Result;
import ninja.Route;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.rythm.TemplateEngineRythm;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.template.TemplateEngineHelper;
import ninja.template.TemplateEngineManager;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.rythmengine.RythmEngine;
import org.rythmengine.template.ITemplate;
import org.slf4j.Logger;

import com.google.common.base.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineRythmTest {

    @Mock
    Context contextRenderable;

    @Mock
    ResponseStreams responseStreams;

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Messages messages;

    @Mock
    ITemplate template;

    @Mock
    Lang lang;

    @Mock
    Logger ninjaLogger;

    @Mock
    TemplateEngineManager templateEngineManager;

    @Mock
    TemplateEngineHelper rythmHelper;

    @Mock
    Result result;

    @Mock
    Route route;

    @Mock
    RythmEngine engine;

    @Mock
    Session cookie;

    @Mock
    FlashScope flash;

    @Test
    public void testInvoke() throws Exception {
        Properties p = new Properties();
        p.setProperty("key", "value");
        when(ninjaProperties.getAllCurrentNinjaProperties()).thenReturn(p);

        TemplateEngineRythm rythm = new TemplateEngineRythm(messages, lang,
                ninjaLogger, rythmHelper,
                ninjaProperties, engine);

        when(contextRenderable.finalizeHeaders(Mockito.eq(result))).thenReturn(
                responseStreams);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(responseStreams.getOutputStream()).thenReturn(
                byteArrayOutputStream);

        when(responseStreams.getWriter()).thenReturn(
                new PrintWriter(byteArrayOutputStream));

        when(cookie.isEmpty()).thenReturn(true);
        when(contextRenderable.getSession()).thenReturn(cookie);

        when(flash.getCurrentFlashCookieData()).thenReturn(
                new HashMap<String, String>());
        when(contextRenderable.getFlashScope()).thenReturn(flash);
        when(contextRenderable.getRoute()).thenReturn(route);

        when(
                rythmHelper.getTemplateForResult(
                        Mockito.eq(route), Mockito.eq(result),
                        Mockito.eq(".rtm.html"))).thenReturn("TemplateName");

        Optional<String> language = Optional.absent();
        when(
                lang.getLanguage(Mockito.eq(contextRenderable),
                        Mockito.eq(Optional.of(result)))).thenReturn(language);

        when(engine.getTemplate(Mockito.eq("TemplateName"), anyObject()))
                .thenReturn(template);

        when(
                template.__setRenderArgs(Mockito
                        .eq(new HashMap<String, Object>()))).thenReturn(
                template);
        when(template.render()).thenReturn("Hellow world from Rythm");

        rythm.invoke(contextRenderable, result);

        assertEquals("Hellow world from Rythm" + SystemUtils.LINE_SEPARATOR,
                byteArrayOutputStream.toString());
    }
}
