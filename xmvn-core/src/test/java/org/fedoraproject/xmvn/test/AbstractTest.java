/*-
 * Copyright (c) 2016-2019 Red Hat, Inc.
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
package org.fedoraproject.xmvn.test;

import org.junit.Before;

import org.fedoraproject.xmvn.config.Configurator;
import org.fedoraproject.xmvn.locator.impl.DefaultServiceLocator;

/**
 * @author Mikolaj Izdebski
 */
public class AbstractTest
{
    private DefaultServiceLocator locator;

    @Before
    public void configureServiceLocator()
    {
        locator = new DefaultServiceLocator();
        locator.addService( Configurator.class, TestConfigurator.class );
    }

    public <T> T getService( Class<T> role )
    {
        return locator.getService( role );
    }
}
