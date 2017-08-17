/**
 * Hub Common
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.api.item;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.model.HubView;
import com.blackducksoftware.integration.hub.model.enumeration.AllowEnum;
import com.blackducksoftware.integration.hub.model.view.components.LinkView;
import com.blackducksoftware.integration.hub.model.view.components.MetaView;
import com.blackducksoftware.integration.log.IntLogger;

public class MetaService {
    public static final String PROJECTS_LINK = "projects";

    public static final String PROJECT_LINK = "project";

    public static final String VERSIONS_LINK = "versions";

    public static final String VERSION_LINK = "versions";

    public static final String CANONICAL_VERSION_LINK = "canonicalVersion";

    public static final String VERSION_REPORT_LINK = "versionReport";

    public static final String COMPONENTS_LINK = "components";

    public static final String POLICY_RULES_LINK = "policy-rules";

    public static final String POLICY_RULE_LINK = "policy-rule";

    public static final String POLICY_STATUS_LINK = "policy-status";

    public static final String RISK_PROFILE_LINK = "riskProfile";

    public static final String VULNERABILITIES_LINK = "vulnerabilities";

    public static final String VULNERABLE_COMPONENTS_LINK = "vulnerable-components";

    public static final String MATCHED_FILES_LINK = "matched-files";

    public static final String CONTENT_LINK = "content";

    public static final String DOWNLOAD_LINK = "download";

    public static final String CODE_LOCATION_LINK = "codelocations";

    public static final String CODE_LOCATION_BOM_STATUS_LINK = "codelocation";

    public static final String SCANS_LINK = "scans";

    public static final String NOTIFICATIONS_LINK = "notifications";

    public static final String USERS_LINK = "users";

    public static final String GLOBAL_OPTIONS_LINK = "global-options";

    public static final String USER_OPTIONS_LINK = "user-options";

    public static final String TEXT_LINK = "text";
    
    public static final String ROLES_LINK = "roles";
    
    public static final String ROLE_USERS_LINK = "role-users";

    private final IntLogger logger;

    public MetaService(final IntLogger logger) {
        this.logger = logger;
    }

    public boolean hasLink(final HubView item, final String linkKey) throws HubIntegrationException {
        final MetaView meta = item.meta;
        if (meta == null) {
            return false;
        }
        final List<LinkView> links = meta.links;
        if (links == null) {
            return false;
        }
        for (final LinkView link : links) {
            if (link.rel.equals(linkKey)) {
                return true;
            }
        }
        return false;
    }

    public String getFirstLink(final HubView item, final String linkKey) throws HubIntegrationException {
        final List<LinkView> links = getLinkViews(item);
        final StringBuilder linksAvailable = new StringBuilder();
        linksAvailable.append("Could not find the link '" + linkKey + "', these are the available links : ");
        int i = 0;
        for (final LinkView link : links) {
            if (link.rel.equals(linkKey)) {
                return link.href;
            }
            if (i > 0) {
                linksAvailable.append(", ");
            }
            linksAvailable.append("'" + link.rel + "'");
            i++;
        }
        linksAvailable.append(". For Item : " + item.meta.href);
        throw new HubIntegrationException(linksAvailable.toString());
    }

    public String getFirstLinkSafely(final HubView item, final String linkKey) {
        try {
            final String link = getFirstLink(item, linkKey);
            return link;
        } catch (final HubIntegrationException e) {
            logger.debug("Link '" + linkKey + "' not found on item : " + item.json, e);
            return null;
        }
    }

    public List<String> getLinks(final HubView item, final String linkKey) throws HubIntegrationException {
        final List<LinkView> links = getLinkViews(item);
        final List<String> linkHrefs = new ArrayList<>();
        final StringBuilder linksAvailable = new StringBuilder();
        linksAvailable.append("Could not find the link '" + linkKey + "', these are the available links : ");
        int i = 0;
        for (final LinkView link : links) {
            if (link.rel.equals(linkKey)) {
                linkHrefs.add(link.href);
            }
            if (i > 0) {
                linksAvailable.append(", ");
            }
            linksAvailable.append("'" + link.rel + "'");
            i++;
        }
        if (!linkHrefs.isEmpty()) {
            return linkHrefs;
        }
        linksAvailable.append(". For Item : " + item.meta.href);
        throw new HubIntegrationException(linksAvailable.toString());
    }

    public MetaView getMetaView(final HubView item) throws HubIntegrationException {
        final MetaView meta = item.meta;
        if (meta == null) {
            throw new HubIntegrationException("Could not find meta information for this item : " + item.json);
        }
        return meta;
    }

    public List<LinkView> getLinkViews(final HubView item) throws HubIntegrationException {
        final MetaView meta = getMetaView(item);
        final List<LinkView> links = meta.links;
        if (links == null) {
            throw new HubIntegrationException("Could not find any links for this item : " + item.json);
        }
        return links;
    }

    public List<AllowEnum> getAllowedMethods(final HubView item) throws HubIntegrationException {
        final MetaView meta = getMetaView(item);
        return meta.allow;
    }

    public String getHref(final HubView item) throws HubIntegrationException {
        final MetaView meta = getMetaView(item);
        final String href = meta.href;
        if (href == null) {
            if (logger != null) {
                logger.error("Hub Item has no href : " + item.json);
            }
            throw new HubIntegrationException("This Hub item does not have any href information.");
        }
        return href;
    }

}
