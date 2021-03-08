/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.cli.cmd;

import io.ballerina.cli.BLauncherCmd;
import picocli.CommandLine;

import java.io.PrintStream;
import java.util.List;

import static io.ballerina.cli.cmd.Constants.COMPLETION_COMMAND;

/**
 * This class represents the "bal completion" command.
 *
 * @since 2.0.0
 */
@CommandLine.Command(name = COMPLETION_COMMAND, description = "Generate bash completion script")
public class CompletionCommand implements BLauncherCmd {
    private PrintStream outStream;

    public CompletionCommand() {
        outStream = System.out;
    }

    public CompletionCommand(PrintStream outStream) {
        this.outStream = outStream;
    }

    @CommandLine.Parameters(description = "Command name")
    private List<String> completionCommands;

    @CommandLine.Option(names = { "--help", "-h", "?" }, hidden = true, description = "for more information")
    private boolean helpFlag;

    @Override
    public void execute() {
        if (helpFlag || completionCommands == null) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMPLETION_COMMAND);
            outStream.println(commandUsageInfo);
            return;
        }

        if (completionCommands.size() > 1) {
            CommandUtil.printError(outStream,
                    "too many arguments",
                    "bal completion bash",
                    true);
            return;
        }
    }

    @Override
    public String getName() {
        return COMPLETION_COMMAND;
    }

    @Override
    public void printLongDesc(StringBuilder out) {

    }

    @Override
    public void printUsage(StringBuilder out) {
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }
}
