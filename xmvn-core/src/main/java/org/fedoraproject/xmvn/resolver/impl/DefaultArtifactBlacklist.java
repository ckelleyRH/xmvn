/*-
 * Copyright (c) 2012-2014 Red Hat, Inc.
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
package org.fedoraproject.xmvn.resolver.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fedoraproject.xmvn.artifact.Artifact;
import org.fedoraproject.xmvn.artifact.DefaultArtifact;
import org.fedoraproject.xmvn.config.Configurator;
import org.fedoraproject.xmvn.resolver.ArtifactBlacklist;
import org.fedoraproject.xmvn.resolver.DependencyMap;
import org.fedoraproject.xmvn.utils.ArtifactUtils;

/**
 * Default implementation of {@code ArtifactBlacklist} container.
 * <p>
 * <strong>WARNING</strong>: This class is part of internal implementation of XMvn and it is marked as public only for
 * technical reasons. This class is not part of XMvn API. Client code using XMvn should <strong>not</strong> reference
 * it directly.
 * 
 * @author Mikolaj Izdebski
 */
@Named
@Singleton
@Deprecated
public class DefaultArtifactBlacklist
    implements ArtifactBlacklist
{
    private final Logger logger = LoggerFactory.getLogger( DefaultArtifactBlacklist.class );

    private final Configurator configurator;

    private final DependencyMap depmap;

    @Inject
    public DefaultArtifactBlacklist( Configurator configurator, DependencyMap depmap )
    {
        this.configurator = configurator;
        this.depmap = depmap;

        createInitialBlacklist();
        blacklistAliases();
    }

    private final Set<Artifact> blacklist = new LinkedHashSet<>();

    @Override
    public boolean contains( String groupId, String artifactId )
    {
        return contains( new DefaultArtifact( groupId, artifactId ) );
    }

    @Override
    public synchronized boolean contains( Artifact artifact )
    {
        return blacklist.contains( new DefaultArtifact( artifact.getGroupId(), artifact.getArtifactId() ) );
    }

    @Override
    public void add( String groupId, String artifactId )
    {
        add( new DefaultArtifact( groupId, artifactId ) );
    }

    @Override
    public synchronized void add( Artifact artifact )
    {
        blacklist.add( new DefaultArtifact( artifact.getGroupId(), artifact.getArtifactId() ) );
    }

    /**
     * Enumerate all blacklisted artifacts.
     * 
     * @return set view of artifact blacklist
     */
    public Set<Artifact> setView()
    {
        return Collections.unmodifiableSet( blacklist );
    }

    /**
     * Construct the initial artifact blacklist.
     */
    private void createInitialBlacklist()
    {
        add( ArtifactUtils.DUMMY );
        add( ArtifactUtils.DUMMY_JPP );

        for ( org.fedoraproject.xmvn.config.Artifact artifact : configurator.getConfiguration().getResolverSettings().getBlacklist() )
            add( artifact.getGroupId(), artifact.getArtifactId() );

        logger.debug( "Initial artifact blacklist is: {}", ArtifactUtils.collectionToString( blacklist, true ) );
    }

    /**
     * Blacklist all aliases of already blacklisted artifacts.
     */
    private void blacklistAliases()
    {
        Set<Artifact> aliasBlacklist = new LinkedHashSet<>();

        for ( Artifact artifact : blacklist )
        {
            Set<Artifact> relatives = depmap.relativesOf( artifact );
            aliasBlacklist.addAll( relatives );
            logger.debug( "Blacklisted relatives of {}: {}", artifact, ArtifactUtils.collectionToString( relatives ) );
        }

        blacklist.addAll( aliasBlacklist );
        logger.debug( "Final artifact blacklist is: {}", ArtifactUtils.collectionToString( blacklist, true ) );
    }
}