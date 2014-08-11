/*
 *
 *  Copyright 2014 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.server.services.impl.jpa;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.netflix.genie.common.exceptions.GenieException;
import com.netflix.genie.common.model.Application;
import com.netflix.genie.common.model.Cluster;
import com.netflix.genie.common.model.Command;
import com.netflix.genie.common.model.CommandStatus;
import com.netflix.genie.server.services.ApplicationConfigService;
import com.netflix.genie.server.services.ClusterConfigService;
import com.netflix.genie.server.services.CommandConfigService;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.inject.Inject;

/**
 * Tests for the CommandConfigServiceJPAImpl.
 *
 * @author tgianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:genie-application-test.xml")
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class
})
//TODO: Test error codes in exceptions
public class TestCommandConfigServiceJPAImpl {

    private static final String APP_1_ID = "app1";
    private static final String CLUSTER_1_ID = "cluster1";

    private static final String COMMAND_1_ID = "command1";
    private static final String COMMAND_1_NAME = "pig_13_prod";
    private static final String COMMAND_1_USER = "tgianos";
    private static final String COMMAND_1_VERSION = "1.2.3";
    private static final String COMMAND_1_EXECUTABLE = "pig";
    private static final String COMMAND_1_JOB_TYPE = "yarn";
    private static final CommandStatus COMMAND_1_STATUS
            = CommandStatus.ACTIVE;

    private static final String COMMAND_2_ID = "command2";
    private static final String COMMAND_2_NAME = "hive_11_prod";
    private static final String COMMAND_2_USER = "amsharma";
    private static final String COMMAND_2_VERSION = "4.5.6";
    private static final String COMMAND_2_EXECUTABLE = "hive";
    private static final String COMMAND_2_JOB_TYPE = "yarn";
    private static final CommandStatus COMMAND_2_STATUS
            = CommandStatus.INACTIVE;

    private static final String COMMAND_3_ID = "command3";
    private static final String COMMAND_3_NAME = "pig_11_prod";
    private static final String COMMAND_3_USER = "tgianos";
    private static final String COMMAND_3_VERSION = "7.8.9";
    private static final String COMMAND_3_EXECUTABLE = "pig";
    private static final String COMMAND_3_JOB_TYPE = "yarn";
    private static final CommandStatus COMMAND_3_STATUS
            = CommandStatus.DEPRECATED;

    @Inject
    private CommandConfigService service;

    @Inject
    private ClusterConfigService cluster_service;

    @Inject
    private ApplicationConfigService app_service;

    /**
     * Test the get command method.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetCommand() throws GenieException {
        final Command command1 = this.service.getCommand(COMMAND_1_ID);
        Assert.assertEquals(COMMAND_1_ID, command1.getId());
        Assert.assertEquals(COMMAND_1_NAME, command1.getName());
        Assert.assertEquals(COMMAND_1_USER, command1.getUser());
        Assert.assertEquals(COMMAND_1_VERSION, command1.getVersion());
        Assert.assertEquals(COMMAND_1_STATUS, command1.getStatus());
        Assert.assertEquals(COMMAND_1_EXECUTABLE, command1.getExecutable());
        Assert.assertEquals(COMMAND_1_JOB_TYPE, command1.getJobType());
        Assert.assertNotNull(command1.getApplication());
        Assert.assertEquals(APP_1_ID, command1.getApplication().getId());
        Assert.assertEquals(5, command1.getTags().size());
        Assert.assertEquals(2, command1.getConfigs().size());

        final Command command2 = this.service.getCommand(COMMAND_2_ID);
        Assert.assertEquals(COMMAND_2_ID, command2.getId());
        Assert.assertEquals(COMMAND_2_NAME, command2.getName());
        Assert.assertEquals(COMMAND_2_USER, command2.getUser());
        Assert.assertEquals(COMMAND_2_VERSION, command2.getVersion());
        Assert.assertEquals(COMMAND_2_STATUS, command2.getStatus());
        Assert.assertEquals(COMMAND_2_EXECUTABLE, command2.getExecutable());
        Assert.assertEquals(COMMAND_2_JOB_TYPE, command2.getJobType());
        Assert.assertNull(command2.getApplication());
        Assert.assertEquals(4, command2.getTags().size());
        Assert.assertEquals(1, command2.getConfigs().size());

        final Command command3 = this.service.getCommand(COMMAND_3_ID);
        Assert.assertEquals(COMMAND_3_ID, command3.getId());
        Assert.assertEquals(COMMAND_3_NAME, command3.getName());
        Assert.assertEquals(COMMAND_3_USER, command3.getUser());
        Assert.assertEquals(COMMAND_3_VERSION, command3.getVersion());
        Assert.assertEquals(COMMAND_3_STATUS, command3.getStatus());
        Assert.assertEquals(COMMAND_3_EXECUTABLE, command3.getExecutable());
        Assert.assertEquals(COMMAND_3_JOB_TYPE, command3.getJobType());
        Assert.assertNull(command3.getApplication());
        Assert.assertEquals(5, command3.getTags().size());
        Assert.assertEquals(1, command3.getConfigs().size());
    }

    /**
     * Test the get command method.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetCommandNull() throws GenieException {
        this.service.getCommand(null);
    }

    /**
     * Test the get command method.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetCommandNotExists() throws GenieException {
        this.service.getCommand(UUID.randomUUID().toString());
    }

    /**
     * Test the get commands method.
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetCommandsByName() {
        final List<Command> commands = this.service.getCommands(
                COMMAND_2_NAME, null, null, 0, 10);
        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(COMMAND_2_ID, commands.get(0).getId());
    }

    /**
     * Test the get commands method.
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetCommandsByUserName() {
        final List<Command> apps = this.service.getCommands(
                null, COMMAND_1_USER, null, -1, -5000);
        Assert.assertEquals(2, apps.size());
        Assert.assertEquals(COMMAND_3_ID, apps.get(0).getId());
        Assert.assertEquals(COMMAND_1_ID, apps.get(1).getId());
    }

    /**
     * Test the get commands method.
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetCommandsByTags() {
        final Set<String> tags = new HashSet<String>();
        tags.add("prod");
        List<Command> commands = this.service.getCommands(
                null, null, tags, 0, 10);
        Assert.assertEquals(3, commands.size());
        Assert.assertEquals(COMMAND_2_ID, commands.get(0).getId());
        Assert.assertEquals(COMMAND_3_ID, commands.get(1).getId());
        Assert.assertEquals(COMMAND_1_ID, commands.get(2).getId());

        tags.add("pig");
        commands = this.service.getCommands(
                null, null, tags, 0, 10);
        Assert.assertEquals(2, commands.size());
        Assert.assertEquals(COMMAND_3_ID, commands.get(0).getId());
        Assert.assertEquals(COMMAND_1_ID, commands.get(1).getId());

        tags.clear();
        tags.add("hive");
        commands = this.service.getCommands(
                null, null, tags, 0, 10);
        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(COMMAND_2_ID, commands.get(0).getId());

        tags.add("somethingThatWouldNeverReallyExist");
        commands = this.service.getCommands(
                null, null, tags, 0, 10);
        Assert.assertTrue(commands.isEmpty());

        tags.clear();
        commands = this.service.getCommands(
                null, null, tags, 0, 10);
        Assert.assertEquals(3, commands.size());
        Assert.assertEquals(COMMAND_2_ID, commands.get(0).getId());
        Assert.assertEquals(COMMAND_3_ID, commands.get(1).getId());
        Assert.assertEquals(COMMAND_1_ID, commands.get(2).getId());
    }

    /**
     * Test the create method.
     *
     * @throws GenieException
     */
    @Test
    public void testCreateCommand() throws GenieException {
        try {
            this.service.getCommand(COMMAND_1_ID);
            Assert.fail("Should have thrown exception");
        } catch (final GenieException ge) {
            Assert.assertEquals(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    ge.getErrorCode()
            );
        }
        final Command command = new Command(
                COMMAND_1_NAME,
                COMMAND_1_USER,
                CommandStatus.ACTIVE,
                COMMAND_1_EXECUTABLE,
                COMMAND_1_VERSION
        );
        command.setId(COMMAND_1_ID);
        final Command created = this.service.createCommand(command);
        Assert.assertNotNull(this.service.getCommand(COMMAND_1_ID));
        Assert.assertEquals(COMMAND_1_ID, created.getId());
        Assert.assertEquals(COMMAND_1_NAME, created.getName());
        Assert.assertEquals(COMMAND_1_USER, created.getUser());
        Assert.assertEquals(CommandStatus.ACTIVE, created.getStatus());
        Assert.assertEquals(COMMAND_1_EXECUTABLE, created.getExecutable());
        this.service.deleteCommand(COMMAND_1_ID);
        try {
            this.service.getCommand(COMMAND_1_ID);
            Assert.fail("Should have thrown exception");
        } catch (final GenieException ge) {
            Assert.assertEquals(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    ge.getErrorCode()
            );
        }
    }

    /**
     * Test the create method when no id is entered.
     *
     * @throws GenieException
     */
    @Test
    public void testCreateCommandNoId() throws GenieException {
        Assert.assertTrue(
                this.service.getCommands(
                        null,
                        null,
                        null,
                        0,
                        Integer.MAX_VALUE
                ).isEmpty());
        final Command command = new Command(
                COMMAND_1_NAME,
                COMMAND_1_USER,
                CommandStatus.ACTIVE,
                COMMAND_1_EXECUTABLE,
                COMMAND_1_VERSION
        );
        final Command created = this.service.createCommand(command);
        Assert.assertNotNull(this.service.getCommand(created.getId()));
        Assert.assertEquals(COMMAND_1_NAME, created.getName());
        Assert.assertEquals(COMMAND_1_USER, created.getUser());
        Assert.assertEquals(CommandStatus.ACTIVE, created.getStatus());
        Assert.assertEquals(COMMAND_1_EXECUTABLE, created.getExecutable());
        this.service.deleteCommand(created.getId());
        try {
            this.service.getCommand(created.getId());
            Assert.fail("Should have thrown exception");
        } catch (final GenieException ge) {
            Assert.assertEquals(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    ge.getErrorCode()
            );
        }
    }

    /**
     * Test to make sure an exception is thrown when null is entered.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testCreateCommandNull() throws GenieException {
        this.service.createCommand(null);
    }

    /**
     * Test to make sure an exception is thrown when command already exists.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testCreateCommandAlreadyExists() throws GenieException {
        final Command command = new Command(
                COMMAND_1_NAME,
                COMMAND_1_USER,
                CommandStatus.ACTIVE,
                COMMAND_1_EXECUTABLE,
                COMMAND_1_VERSION
        );
        command.setId(COMMAND_1_ID);
        this.service.createCommand(command);
    }

    /**
     * Test to update an command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testUpdateCommandNoId() throws GenieException {
        final Command init = this.service.getCommand(COMMAND_1_ID);
        Assert.assertEquals(COMMAND_1_USER, init.getUser());
        Assert.assertEquals(CommandStatus.ACTIVE, init.getStatus());
        Assert.assertEquals(5, init.getTags().size());

        final Command updateCommand = new Command();
        updateCommand.setStatus(CommandStatus.INACTIVE);
        updateCommand.setUser(COMMAND_2_USER);
        final Set<String> tags = new HashSet<String>();
        tags.add("prod");
        tags.add("tez");
        tags.add("yarn");
        tags.add("hadoop");
        updateCommand.setTags(tags);
        this.service.updateCommand(COMMAND_1_ID, updateCommand);

        final Command updated = this.service.getCommand(COMMAND_1_ID);
        Assert.assertEquals(COMMAND_2_USER, updated.getUser());
        Assert.assertEquals(CommandStatus.INACTIVE, updated.getStatus());
        Assert.assertEquals(6, updated.getTags().size());
    }

    /**
     * Test to update an command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testUpdateCommandWithId() throws GenieException {
        final Command init = this.service.getCommand(COMMAND_1_ID);
        Assert.assertEquals(COMMAND_1_USER, init.getUser());
        Assert.assertEquals(CommandStatus.ACTIVE, init.getStatus());
        Assert.assertEquals(5, init.getTags().size());

        final Command updateApp = new Command();
        updateApp.setId(COMMAND_1_ID);
        updateApp.setStatus(CommandStatus.INACTIVE);
        updateApp.setUser(COMMAND_2_USER);
        final Set<String> tags = new HashSet<String>();
        tags.add("prod");
        tags.add("tez");
        tags.add("yarn");
        tags.add("hadoop");
        updateApp.setTags(tags);
        this.service.updateCommand(COMMAND_1_ID, updateApp);

        final Command updated = this.service.getCommand(COMMAND_1_ID);
        Assert.assertEquals(COMMAND_2_USER, updated.getUser());
        Assert.assertEquals(CommandStatus.INACTIVE, updated.getStatus());
        Assert.assertEquals(6, updated.getTags().size());
    }

    /**
     * Test to update an command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testUpdateCommandNullId() throws GenieException {
        this.service.updateCommand(null, new Command());
    }

    /**
     * Test to update an command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testUpdateCommandNullUpdateCommand() throws GenieException {
        this.service.updateCommand(COMMAND_1_ID, null);
    }

    /**
     * Test to update an command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testUpdateCommandNoAppExists() throws GenieException {
        this.service.updateCommand(
                UUID.randomUUID().toString(), new Command());
    }

    /**
     * Test to update an command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testUpdateCommandIdsDontMatch() throws GenieException {
        final Command updateApp = new Command();
        updateApp.setId(UUID.randomUUID().toString());
        this.service.updateCommand(COMMAND_1_ID, updateApp);
    }

    /**
     * Test delete all.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testDeleteAll() throws GenieException {
        Assert.assertEquals(3,
                this.service.getCommands(null, null, null, 0, 10).size());
        Assert.assertEquals(3, this.service.deleteAllCommands().size());
        Assert.assertTrue(
                this.service.getCommands(null, null, null, 0, 10)
                .isEmpty());
    }

    /**
     * Test delete.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testDelete() throws GenieException {
        List<Command> commands
                = this.cluster_service.getCommandsForCluster(CLUSTER_1_ID);
        Assert.assertEquals(3, commands.size());
        boolean found = false;
        for (final Command command : commands) {
            if (COMMAND_1_ID.equals(command.getId())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
        Set<Command> appCommands
                = this.app_service.getCommandsForApplication(APP_1_ID);
        Assert.assertEquals(1, appCommands.size());
        found = false;
        for (final Command command : appCommands) {
            if (COMMAND_1_ID.equals(command.getId())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);

        //Actually delete it
        Assert.assertEquals(COMMAND_1_ID,
                this.service.deleteCommand(COMMAND_1_ID).getId());

        commands = this.cluster_service.getCommandsForCluster(CLUSTER_1_ID);
        Assert.assertEquals(2, commands.size());
        found = false;
        for (final Command command : commands) {
            if (COMMAND_1_ID.equals(command.getId())) {
                found = true;
                break;
            }
        }
        Assert.assertFalse(found);
        appCommands = this.app_service.getCommandsForApplication(APP_1_ID);
        Assert.assertTrue(appCommands.isEmpty());

        //Test a case where the app has no commands to
        //make sure that also works.
        Assert.assertEquals(COMMAND_3_ID,
                this.service.deleteCommand(COMMAND_3_ID).getId());
    }

    /**
     * Test delete.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testDeleteNoId() throws GenieException {
        this.service.deleteCommand(null);
    }

    /**
     * Test delete.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testDeleteNoAppToDelete() throws GenieException {
        this.service.deleteCommand(UUID.randomUUID().toString());
    }

    /**
     * Test add configurations to command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testAddConfigsToCommand() throws GenieException {
        final String newConfig1 = UUID.randomUUID().toString();
        final String newConfig2 = UUID.randomUUID().toString();
        final String newConfig3 = UUID.randomUUID().toString();

        final Set<String> newConfigs = new HashSet<String>();
        newConfigs.add(newConfig1);
        newConfigs.add(newConfig2);
        newConfigs.add(newConfig3);

        Assert.assertEquals(2,
                this.service.getConfigsForCommand(COMMAND_1_ID).size());
        final Set<String> finalConfigs
                = this.service.addConfigsForCommand(COMMAND_1_ID, newConfigs);
        Assert.assertEquals(5, finalConfigs.size());
        Assert.assertTrue(finalConfigs.contains(newConfig1));
        Assert.assertTrue(finalConfigs.contains(newConfig2));
        Assert.assertTrue(finalConfigs.contains(newConfig3));
    }

    /**
     * Test add configurations to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testAddConfigsToCommandNoId() throws GenieException {
        this.service.addConfigsForCommand(null, new HashSet<String>());
    }

    /**
     * Test add configurations to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testAddConfigsToCommandNoConfigs() throws GenieException {
        this.service.addConfigsForCommand(COMMAND_1_ID, null);
    }

    /**
     * Test add configurations to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testAddConfigsToCommandNoCommand() throws GenieException {
        this.service.addConfigsForCommand(UUID.randomUUID().toString(),
                new HashSet<String>());
    }

    /**
     * Test update configurations for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testUpdateConfigsForCommand() throws GenieException {
        final String newConfig1 = UUID.randomUUID().toString();
        final String newConfig2 = UUID.randomUUID().toString();
        final String newConfig3 = UUID.randomUUID().toString();

        final Set<String> newConfigs = new HashSet<String>();
        newConfigs.add(newConfig1);
        newConfigs.add(newConfig2);
        newConfigs.add(newConfig3);

        Assert.assertEquals(2,
                this.service.getConfigsForCommand(COMMAND_1_ID).size());
        final Set<String> finalConfigs
                = this.service.updateConfigsForCommand(COMMAND_1_ID, newConfigs);
        Assert.assertEquals(3, finalConfigs.size());
        Assert.assertTrue(finalConfigs.contains(newConfig1));
        Assert.assertTrue(finalConfigs.contains(newConfig2));
        Assert.assertTrue(finalConfigs.contains(newConfig3));
    }

    /**
     * Test update configurations for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testUpdateConfigsForCommandNoId() throws GenieException {
        this.service.updateConfigsForCommand(null, new HashSet<String>());
    }

    /**
     * Test update configurations for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testUpdateConfigsForCommandNoApp() throws GenieException {
        this.service.updateConfigsForCommand(UUID.randomUUID().toString(),
                new HashSet<String>());
    }

    /**
     * Test get configurations for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetConfigsForCommand() throws GenieException {
        Assert.assertEquals(2,
                this.service.getConfigsForCommand(COMMAND_1_ID).size());
    }

    /**
     * Test get configurations to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetConfigsForCommandNoId() throws GenieException {
        this.service.getConfigsForCommand(null);
    }

    /**
     * Test get configurations to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetConfigsForCommandNoApp() throws GenieException {
        this.service.getConfigsForCommand(UUID.randomUUID().toString());
    }

    /**
     * Test remove all configurations for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveAllConfigsForCommand() throws GenieException {
        Assert.assertEquals(2,
                this.service.getConfigsForCommand(COMMAND_1_ID).size());
        Assert.assertEquals(0,
                this.service.removeAllConfigsForCommand(COMMAND_1_ID).size());
    }

    /**
     * Test remove all configurations for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveAllConfigsForCommandNoId() throws GenieException {
        this.service.removeAllConfigsForCommand(null);
    }

    /**
     * Test remove all configurations for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveAllConfigsForCommandNoApp() throws GenieException {
        this.service.removeAllConfigsForCommand(UUID.randomUUID().toString());
    }

    /**
     * Test remove configuration for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveConfigForCommand() throws GenieException {
        final Set<String> configs
                = this.service.getConfigsForCommand(COMMAND_1_ID);
        Assert.assertEquals(2, configs.size());
        Assert.assertEquals(1,
                this.service.removeConfigForCommand(
                        COMMAND_1_ID,
                        configs.iterator().next()).size());
    }

    /**
     * Test remove configuration for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveConfigForCommandNullConfig()
            throws GenieException {
        final Set<String> configs
                = this.service.getConfigsForCommand(COMMAND_1_ID);
        Assert.assertEquals(2, configs.size());
        Assert.assertEquals(2,
                this.service.removeConfigForCommand(
                        COMMAND_1_ID, null).size());
    }

    /**
     * Test remove configuration for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveConfigForCommandNoId() throws GenieException {
        this.service.removeConfigForCommand(null, "something");
    }

    /**
     * Test remove configuration for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveConfigForCommandNoApp() throws GenieException {
        this.service.removeConfigForCommand(
                UUID.randomUUID().toString(),
                "something");
    }

    /**
     * Test setting the application for a given command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testSetApplicationForCommand() throws GenieException {
        final Command command2 = this.service.getCommand(COMMAND_2_ID);
        Assert.assertNull(command2.getApplication());

        final Application app = this.app_service.getApplication(APP_1_ID);
        final Set<Command> preCommands
                = this.app_service.getCommandsForApplication(APP_1_ID);
        Assert.assertEquals(1, preCommands.size());
        Assert.assertEquals(COMMAND_1_ID, preCommands.iterator().next().getId());

        this.service.setApplicationForCommand(COMMAND_2_ID, app);

        final Set<Command> savedCommands
                = this.app_service.getCommandsForApplication(APP_1_ID);
        Assert.assertEquals(2, savedCommands.size());
        Assert.assertNotNull(this.service.getApplicationForCommand(COMMAND_2_ID));
    }

    /**
     * Test setting the application for a given command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testSetApplicationForCommandNoId() throws GenieException {
        this.service.setApplicationForCommand(null, new Application());
    }

    /**
     * Test setting the application for a given command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testSetApplicationForCommandNoApp() throws GenieException {
        this.service.setApplicationForCommand(COMMAND_2_ID, null);
    }

    /**
     * Test setting the application for a given command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testSetApplicationForCommandNoAppId() throws GenieException {
        this.service.setApplicationForCommand(COMMAND_2_ID, new Application());
    }

    /**
     * Test setting the application for a given command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testSetApplicationForCommandNoCommandExists() throws GenieException {
        final Application app = new Application();
        app.setId(APP_1_ID);
        this.service.setApplicationForCommand(
                UUID.randomUUID().toString(), app);
    }

    /**
     * Test setting the application for a given command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testSetApplicationForCommandNoAppExists() throws GenieException {
        final Application app = new Application();
        app.setId(UUID.randomUUID().toString());
        this.service.setApplicationForCommand(
                COMMAND_2_ID, app);
    }

    /**
     * Test get application for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetApplicationForCommand() throws GenieException {
        final Application app = this.service.getApplicationForCommand(COMMAND_1_ID);
        Assert.assertEquals(APP_1_ID, app.getId());
    }

    /**
     * Test get application for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetApplicationForCommandNoId() throws GenieException {
        this.service.getApplicationForCommand(null);
    }

    /**
     * Test get application for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetApplicationForCommandNoCommand() throws GenieException {
        this.service.getApplicationForCommand(UUID.randomUUID().toString());
    }

    /**
     * Test get application for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetApplicationForCommandNoApp() throws GenieException {
        this.service.getApplicationForCommand(COMMAND_2_ID);
    }

    /**
     * Test remove application for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveApplicationForCommand() throws GenieException {
        Assert.assertNotNull(this.service.getApplicationForCommand(COMMAND_1_ID));
        Assert.assertNotNull(this.service.removeApplicationForCommand(COMMAND_1_ID));
        try {
            this.service.getApplicationForCommand(COMMAND_1_ID);
            Assert.fail();
        } catch (final GenieException ge) {
            Assert.assertEquals(
                    HttpURLConnection.HTTP_NOT_FOUND, ge.getErrorCode());
        }
    }

    /**
     * Test remove application for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveApplicationForCommandNoId() throws GenieException {
        this.service.removeApplicationForCommand(null);
    }

    /**
     * Test remove application for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveApplicationForCommandNoCommandExists() throws GenieException {
        this.service.removeApplicationForCommand(UUID.randomUUID().toString());
    }

    /**
     * Test remove application for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveApplicationForCommandNoAppExists() throws GenieException {
        this.service.removeApplicationForCommand(COMMAND_2_ID);
    }

    /**
     * Test add tags to command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testAddTagsToCommand() throws GenieException {
        final String newTag1 = UUID.randomUUID().toString();
        final String newTag2 = UUID.randomUUID().toString();
        final String newTag3 = UUID.randomUUID().toString();

        final Set<String> newTags = new HashSet<String>();
        newTags.add(newTag1);
        newTags.add(newTag2);
        newTags.add(newTag3);

        Assert.assertEquals(5,
                this.service.getTagsForCommand(COMMAND_1_ID).size());
        final Set<String> finalTags
                = this.service.addTagsForCommand(COMMAND_1_ID, newTags);
        Assert.assertEquals(8, finalTags.size());
        Assert.assertTrue(finalTags.contains(newTag1));
        Assert.assertTrue(finalTags.contains(newTag2));
        Assert.assertTrue(finalTags.contains(newTag3));
    }

    /**
     * Test add tags to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testAddTagsToCommandNoId() throws GenieException {
        this.service.addTagsForCommand(null, new HashSet<String>());
    }

    /**
     * Test add tags to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testAddTagsToCommandNoTags() throws GenieException {
        this.service.addTagsForCommand(COMMAND_1_ID, null);
    }

    /**
     * Test add tags to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testAddTagsForCommandNoApp() throws GenieException {
        this.service.addTagsForCommand(UUID.randomUUID().toString(),
                new HashSet<String>());
    }

    /**
     * Test update tags for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testUpdateTagsForCommand() throws GenieException {
        final String newTag1 = UUID.randomUUID().toString();
        final String newTag2 = UUID.randomUUID().toString();
        final String newTag3 = UUID.randomUUID().toString();

        final Set<String> newTags = new HashSet<String>();
        newTags.add(newTag1);
        newTags.add(newTag2);
        newTags.add(newTag3);

        Assert.assertEquals(5,
                this.service.getTagsForCommand(COMMAND_1_ID).size());
        final Set<String> finalTags
                = this.service.updateTagsForCommand(COMMAND_1_ID, newTags);
        Assert.assertEquals(5, finalTags.size());
        Assert.assertTrue(finalTags.contains(newTag1));
        Assert.assertTrue(finalTags.contains(newTag2));
        Assert.assertTrue(finalTags.contains(newTag3));
        Assert.assertTrue(finalTags.contains(COMMAND_1_ID));
        Assert.assertTrue(finalTags.contains(COMMAND_1_NAME));
    }

    /**
     * Test update tags for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testUpdateTagsForCommandNoId() throws GenieException {
        this.service.updateTagsForCommand(null, new HashSet<String>());
    }

    /**
     * Test update tags for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testUpdateTagsForCommandNoApp() throws GenieException {
        this.service.updateTagsForCommand(UUID.randomUUID().toString(),
                new HashSet<String>());
    }

    /**
     * Test get tags for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetTagsForCommand() throws GenieException {
        Assert.assertEquals(5,
                this.service.getTagsForCommand(COMMAND_1_ID).size());
    }

    /**
     * Test get tags to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetTagsForCommandNoId() throws GenieException {
        this.service.getTagsForCommand(null);
    }

    /**
     * Test get tags to command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetTagsForCommandNoApp() throws GenieException {
        this.service.getTagsForCommand(UUID.randomUUID().toString());
    }

    /**
     * Test remove all tags for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveAllTagsForCommand() throws GenieException {
        Assert.assertEquals(5,
                this.service.getTagsForCommand(COMMAND_1_ID).size());
        final Set<String> finalTags
                = this.service.removeAllTagsForCommand(COMMAND_1_ID);
        Assert.assertEquals(2,
                finalTags.size());
        Assert.assertTrue(finalTags.contains(COMMAND_1_ID));
        Assert.assertTrue(finalTags.contains(COMMAND_1_NAME));
    }

    /**
     * Test remove all tags for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveAllTagsForCommandNoId() throws GenieException {
        this.service.removeAllTagsForCommand(null);
    }

    /**
     * Test remove all tags for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveAllTagsForCommandNoApp() throws GenieException {
        this.service.removeAllTagsForCommand(UUID.randomUUID().toString());
    }

    /**
     * Test remove tag for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveTagForCommand() throws GenieException {
        final Set<String> tags
                = this.service.getTagsForCommand(COMMAND_1_ID);
        Assert.assertEquals(5, tags.size());
        Assert.assertEquals(4,
                this.service.removeTagForCommand(
                        COMMAND_1_ID,
                        "tez").size()
        );
    }

    /**
     * Test remove tag for command.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveTagForCommandNullTag()
            throws GenieException {
        final Set<String> tags
                = this.service.getTagsForCommand(COMMAND_1_ID);
        Assert.assertEquals(5, tags.size());
        Assert.assertEquals(5,
                this.service.removeTagForCommand(
                        COMMAND_1_ID, null).size());
    }

    /**
     * Test remove configuration for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveTagForCommandNoId() throws GenieException {
        this.service.removeTagForCommand(null, "something");
    }

    /**
     * Test remove configuration for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testRemoveTagForCommandNoApp() throws GenieException {
        this.service.removeTagForCommand(
                UUID.randomUUID().toString(),
                "something"
        );
    }

    /**
     * Test remove configuration for command.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testRemoveTagForCommandId() throws GenieException {
        this.service.removeTagForCommand(
                COMMAND_1_ID,
                COMMAND_1_ID
        );
    }

    /**
     * Test the Get clusters for command function.
     *
     * @throws GenieException
     */
    @Test
    @DatabaseSetup("command/init.xml")
    @DatabaseTearDown(
            value = "command/init.xml",
            type = DatabaseOperation.DELETE_ALL)
    public void testGetCommandsForCommand() throws GenieException {
        final Set<Cluster> clusters
                = this.service.getClustersForCommand(COMMAND_1_ID);
        Assert.assertEquals(1, clusters.size());
        Assert.assertEquals(CLUSTER_1_ID, clusters.iterator().next().getId());
    }

    /**
     * Test the Get clusters for command function.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetClustersForCommandNoId() throws GenieException {
        this.service.getClustersForCommand("");
    }

    /**
     * Test the Get clusters for command function.
     *
     * @throws GenieException
     */
    @Test(expected = GenieException.class)
    public void testGetClustersForCommandNoApp() throws GenieException {
        this.service.getClustersForCommand(UUID.randomUUID().toString());
    }
}
