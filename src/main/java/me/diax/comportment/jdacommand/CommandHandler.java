/*
 * Copyright 2017 Comportment | comportment@diax.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.diax.comportment.jdacommand;

import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Comportment at 17:43 on 10/05/17
 * https://github.com/Comportment | comportment@diax.me
 *
 * The command handler which deals with the registered commands.
 *
 * @author Comportment
 * @since 1.0.0
 */
public class CommandHandler {
    private final Log logger = LogFactory.getLog("JDA-Command");

    /**
     * A set of all of the commands that this CommandHandler can see.
     *
     * @see #getCommands()
     * @since 1.0.0
     */
    private Set<Command> commands = new HashSet<>();

    /**
     * A method to register commands with the command handler.
     *
     * @param commands The {@link Command}s to register.
     * @see #registerCommand(Command)
     * @since 1.0.0
     */
    public void registerCommands(Set<Command> commands) {
        this.commands.addAll(commands);
    }

    /**
     * A method to register commands with the command handler.
     *
     * @param commands The {@link Command}s to register.
     * @see #registerCommand(Command)
     * @see #registerCommands(Set)
     * @since 1.0.1
     */
    public void registerCommands(Command... commands) {
        Collections.addAll(this.commands, commands);
    }

    /**
     * A method to register a command with the command handler.
     *
     * @param command The {@link Command} to register.
     * @see #registerCommands(Set)
     * @since 1.0.1
     */
    public void registerCommand(Command command) {
        this.registerCommands(command);
    }

    /**
     * A method to unregister commands with the command handler.
     *
     * @param commands The commands to unregister.
     * @see #unregisterCommand(Command)
     * @see #unregisterCommands(Set)
     * @since 1.0.1
     */
    public void unregisterCommands(Set<Command> commands) {
        this.commands.removeAll(commands);
    }

    /**
     * A method to unregister commands with the command handler.
     *
     * @param commands The commands to unregister.
     * @see #unregisterCommand(Command)
     * @see #unregisterCommands(Set)
     * @since 1.0.1
     */
    public void unregisterCommands(Command... commands) {
        this.commands.removeAll(Arrays.asList(commands));
    }

    /**
     * A method to unregister a command with the command handler.
     *
     * @param command The command to unregister.
     * @see #unregisterCommands(Set)
     * @see #unregisterCommands(Command...)
     * @since 1.0.1
     */
    public void unregisterCommand(Command command) {
        this.unregisterCommands(command);
    }

    /**
     * @return All of the commands registered with this command handler.
     * @since 1.0.1
     */
    public Set<Command> getCommands() {
        return commands;
    }

    /**
     * Method which attempts to find a command from the given trigger
     *
     * @param trigger The trigger of the command to find.
     * @return The {@link Command} that was found, sometimes <code>null</code>
     * @since 1.0.0
     */
    public Command findCommand(String trigger) {
        return commands.stream().filter(cd -> Arrays.asList(cd.getDescription().triggers()).contains(trigger)).findFirst().orElse(null);
    }

    /**
     * Method which attempts to execute the given command.
     *
     * @param command The {@link Command} to execute.
     * @param message The {@link Message} which triggered the command.
     * @param args The arguments of the command.
     * @since 1.0.0
     */
    public void execute(Command command, Message message, String args) {
        CommandDescription cd = command.getDescription();
        if (cd == null) return;
        args = args.trim();
        if (cd.args() > args.split("\\s+").length) return;
        try {
            logger.info("Executing " + cd.name());
            command.execute(message, args);
        } catch (Exception e) {
            logger.error("Could not execute " + cd.name());
            throw new ExecutionException(e);
        }
    }

    /**
     * A method which calls {@link #findCommand(String)}, and then {@link #execute(Command, Message, String)} if the found command is not <code>null</code>
     *
     * @param trigger The trigger of the command.
     * @param message The {@link Message} which triggered the command.
     * @param args The args of the command.
     * @since 1.0.1
     * @see #findCommand(String)
     * @see #execute(Command, Message, String)
     */
    public void findAndExecute(String trigger, Message message, String args) {
        Command command = this.findCommand(trigger);
        if (command == null || command.getDescription() == null) return;
        this.execute(command, message, args);
    }
}