/*
 * Copyright 2017, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.manager.server.apps;

import org.openremote.container.Container;
import org.openremote.container.ContainerService;
import org.openremote.container.web.WebService;
import org.openremote.manager.server.security.ManagerIdentityService;
import org.openremote.manager.server.web.ManagerWebService;
import org.openremote.manager.shared.apps.ConsoleApp;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.openremote.model.Constants.MASTER_REALM_DISPLAY_NAME;

public class ConsoleAppService implements ContainerService {

    protected ManagerWebService managerWebService;
    protected ManagerIdentityService identityService;

    @Override
    public void init(Container container) throws Exception {

        this.managerWebService = container.getService(ManagerWebService.class);
        this.identityService = container.getService(ManagerIdentityService.class);

        container.getService(WebService.class).getApiSingletons().add(
            new ConsoleAppResourceImpl(this)
        );
    }

    @Override
    public void start(Container container) throws Exception {
    }

    @Override
    public void stop(Container container) throws Exception {
    }

    public ConsoleApp[] getInstalled() throws Exception {
        List<ConsoleApp> result = new ArrayList<>();
        Files.list(managerWebService.getConsoleDocRoot()).forEach(path -> {
            String directoryName = path.getFileName().toString();
            String tenantRealm = identityService.getActiveTenantRealmId(directoryName);
            String tenantDisplayName = identityService.getActiveTenantDisplayName(tenantRealm);
            if (tenantDisplayName != null) {
                String appUrl = managerWebService.getConsoleUrl(
                    identityService.getExternalServerUri(),
                    directoryName
                );
                ConsoleApp consoleApp = new ConsoleApp(tenantDisplayName, appUrl);
                result.add(consoleApp);
            }
        });

        result.sort((o1, o2) -> {
            if (o1.getName().equals(MASTER_REALM_DISPLAY_NAME))
                return -1;
            if (o2.getName().equals(MASTER_REALM_DISPLAY_NAME))
                return 1;
            return o1.getName().compareTo(o2.getName());
        });

        return result.toArray(new ConsoleApp[result.size()]);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            '}';
    }
}