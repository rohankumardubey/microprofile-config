/*
 * Copyright (c) 2016-2017 Mark Struberg and others
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
 *
 */
package org.eclipse.microprofile.config.tck;

import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.eclipse.microprofile.config.tck.base.AbstractTest;
import org.eclipse.microprofile.config.tck.configsources.CustomConfigSourceProvider;
import org.eclipse.microprofile.config.tck.configsources.CustomDbConfigSource;
import org.eclipse.microprofile.config.tck.converters.Duck;
import org.eclipse.microprofile.config.tck.converters.DuckConverter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class ConverterTest extends Arquillian {

    private @Inject Config config;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
                .create(JavaArchive.class, "converterTest.jar")
                .addClass(ConverterTest.class)
                .addPackage(CustomDbConfigSource.class.getPackage())
                .addClasses(DuckConverter.class, Duck.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsServiceProvider(ConfigSource.class, CustomDbConfigSource.class)
                .addAsServiceProvider(ConfigSourceProvider.class, CustomConfigSourceProvider.class)
                .as(JavaArchive.class);

        AbstractTest.addFile(testJar, "META-INF/microprofile-config.properties");
        AbstractTest.addFile(testJar, "sampleconfig.yaml");

        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "converterTest.war")
                .addAsLibrary(testJar);
        return war;
    }


    @Test
    public void testInteger() {
        Integer value = config.getValue("tck.config.test.javaconfig.converter.integervalue", Integer.class);
        Assert.assertEquals(value, Integer.valueOf(1234));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInteger_Broken() {
        Integer value = config.getValue("tck.config.test.javaconfig.converter.integervalue.broken", Integer.class);
    }

    @Test
    public void testLong() {
        Long value = config.getValue("tck.config.test.javaconfig.converter.longvalue", Long.class);
        Assert.assertEquals(value, Long.valueOf(1234567890));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLong_Broken() {
        Long value = config.getValue("tck.config.test.javaconfig.converter.longvalue.broken", Long.class);
    }

    @Test
    public void testFloat() {
        Float value = config.getValue("tck.config.test.javaconfig.converter.floatvalue", Float.class);
        Assert.assertEquals(value, Float.valueOf(12.34f));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFloat_Broken() {
        Float value = config.getValue("tck.config.test.javaconfig.converter.floatvalue.broken", Float.class);
    }

    @Test
    public void testDouble() {
        Double value = config.getValue("tck.config.test.javaconfig.converter.doublevalue", Double.class);
        Assert.assertEquals(value, Double.valueOf(12.34d));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDouble_Broken() {
        Double value = config.getValue("tck.config.test.javaconfig.converter.doublevalue.broken", Double.class);
    }

    @Test
    public void testBoolean() {
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.true", Boolean.class));
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.true_uppercase", Boolean.class));
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.true_mixedcase", Boolean.class));
        Assert.assertFalse(config.getValue("tck.config.test.javaconfig.configvalue.boolean.false", Boolean.class));

        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.one", Boolean.class));
        Assert.assertFalse(config.getValue("tck.config.test.javaconfig.configvalue.boolean.zero", Boolean.class));
        Assert.assertFalse(config.getValue("tck.config.test.javaconfig.configvalue.boolean.seventeen", Boolean.class));

        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.yes", Boolean.class));
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.yes_uppercase", Boolean.class));
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.yes_mixedcase", Boolean.class));
        Assert.assertFalse(config.getValue("tck.config.test.javaconfig.configvalue.boolean.no", Boolean.class));

        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.y", Boolean.class));
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.y_uppercase", Boolean.class));
        Assert.assertFalse(config.getValue("tck.config.test.javaconfig.configvalue.boolean.n", Boolean.class));

        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.on", Boolean.class));
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.on_uppercase", Boolean.class));
        Assert.assertTrue(config.getValue("tck.config.test.javaconfig.configvalue.boolean.on_mixedcase", Boolean.class));
        Assert.assertFalse(config.getValue("tck.config.test.javaconfig.configvalue.boolean.off", Boolean.class));
    }

}
