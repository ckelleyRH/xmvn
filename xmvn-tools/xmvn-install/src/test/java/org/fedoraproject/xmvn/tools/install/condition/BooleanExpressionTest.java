/*-
 * Copyright (c) 2014 Red Hat, Inc.
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
package org.fedoraproject.xmvn.tools.install.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.fedoraproject.xmvn.repository.ArtifactContext;

import org.junit.Test;

/**
 * @author Mikolaj Izdebski
 */
public class BooleanExpressionTest
{
    @Test
    public void testBasicExpressions()
    {
        ArtifactContext context = new ArtifactContext( Collections.<String, String> emptyMap() );

        BooleanExpression trueExpression = new BooleanLiteral( true );
        assertTrue( trueExpression.getValue( context ) );

        BooleanExpression falseExpression = new BooleanLiteral( false );
        assertFalse( falseExpression.getValue( context ) );

        BooleanExpression andExpression = new And( trueExpression, falseExpression );
        assertFalse( andExpression.getValue( context ) );

        BooleanExpression orExpression = new Or( trueExpression, falseExpression );
        assertTrue( orExpression.getValue( context ) );

        BooleanExpression xorExpression = new Xor( trueExpression, falseExpression );
        assertTrue( xorExpression.getValue( context ) );
    }

    @Test
    public void testProperties()
    {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put( "foo", "bar" );
        properties.put( "baz", "" );
        ArtifactContext context = new ArtifactContext( properties );

        StringExpression fooProperty = new Property( "foo" );
        assertEquals( "bar", fooProperty.getValue( context ) );

        StringExpression bazProperty = new Property( "baz" );
        assertEquals( "", bazProperty.getValue( context ) );

        StringExpression xyzzyProperty = new Property( "xyzzy" );
        assertEquals( null, xyzzyProperty.getValue( context ) );

        BooleanExpression fooDefined = new Defined( "foo" );
        assertTrue( fooDefined.getValue( context ) );

        BooleanExpression bazDefined = new Defined( "baz" );
        assertTrue( bazDefined.getValue( context ) );

        BooleanExpression xyzzyDefined = new Defined( "xyzzy" );
        assertFalse( xyzzyDefined.getValue( context ) );
    }
}
