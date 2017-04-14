/**********************************************************************
 * Copyright (c) 2016-2017 Contributors to the Eclipse Foundation 
 *
 * See the NOTICES file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * SPDX-License-Identifier: Apache-2.0
 **********************************************************************/
package org.eclipse.microprofile.config.tck;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Verify injection of {@code Optional<T>} fields.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class CdiOptionalInjectionTest extends Arquillian {

    private @Inject OptionalValuesBean optionalValuesBean;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
                .create(JavaArchive.class, "cdiOptionalInjectionTest.jar")
                .addClasses(CdiOptionalInjectionTest.class, OptionalValuesBean.class)
                .addAsManifestResource(new StringAsset("my.int.property=1234\nmy.string.property=hello"),
                        "microprofile-config.properties")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .as(JavaArchive.class);

        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "cdiOptionalInjectionTest.war")
                .addAsLibrary(testJar);
        return war;
    }


    @Test
    public void testOptionalInjection() {
        Assert.assertTrue(optionalValuesBean.getIntProperty().isPresent());
        Assert.assertEquals(optionalValuesBean.getIntProperty().get(), Integer.valueOf(1234));

        Assert.assertFalse(optionalValuesBean.getNotexistingProperty().isPresent());

        Assert.assertTrue(optionalValuesBean.getStringValue().isPresent());
        Assert.assertEquals(optionalValuesBean.getStringValue().get(), "hello");
    }



    @Dependent
    public static class OptionalValuesBean {
        @Inject
        @ConfigProperty(name="my.int.property")
        private Optional<Integer> intProperty;

        @Inject
        @ConfigProperty(name="my.notexisting.property")
        private Optional<Integer> notexistingProperty;

        private Optional<String> stringValue;

        @Inject
        public void setStringValue(@ConfigProperty(name="my.string.property") Optional<String> stringValue) {
            this.stringValue = stringValue;
        }

        public Optional<String> getStringValue() {
            return stringValue;
        }

        public Optional<Integer> getIntProperty() {
            return intProperty;
        }

        public Optional<Integer> getNotexistingProperty() {
            return notexistingProperty;
        }
    }
}