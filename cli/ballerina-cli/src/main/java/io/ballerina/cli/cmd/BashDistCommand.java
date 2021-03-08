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

import com.google.gson.Gson;
import io.ballerina.cli.BLauncherCmd;
import io.ballerina.cli.launcher.LauncherUtils;
import io.ballerina.cli.launcher.util.BCompileUtil;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static io.ballerina.cli.cmd.Constants.BASH_DIST_COMMAND;

/**
 * This class represents the "bal completion bash-dist" command.
 *
 * @since 2.0.0
 */
@CommandLine.Command(name = BASH_DIST_COMMAND, description = "Generate bash completion script")
public class BashDistCommand implements BLauncherCmd {
    private PrintStream outStream;

    public BashDistCommand() {
        outStream = System.out;
    }

    public BashDistCommand(PrintStream outStream) {
        this.outStream = outStream;
    }

    @CommandLine.Parameters(description = "Command name")
    private List<String> bashDistCommands;

    @CommandLine.Option(names = {"--help", "-h", "?"}, hidden = true, description = "for more information")
    private boolean helpFlag;

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(BASH_DIST_COMMAND);
            outStream.println(commandUsageInfo);
            return;
        }

        if (bashDistCommands == null) {
            Gson gson = new Gson();
            Command[] commands = gson.fromJson(getCommandInfo(), Command[].class);

            for (Command command : commands) {
                outStream.println(command);
            }
            return;
        }

        if (bashDistCommands.size() > 0) {
            CommandUtil.printError(outStream, "too many arguments", "bal completion bash", true);
        }
    }

    @Override
    public String getName() {
        return BASH_DIST_COMMAND;
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

    /**
     * Retrieve command completion info.
     *
     * @return command completion info
     */
    private static String getCommandInfo() {
        String filePath = "bash-completion/command_completion.json";
        try {
            return BCompileUtil.readFileAsString(filePath);
        } catch (IOException e) {
            throw LauncherUtils.createLauncherException("completion file not found: " + filePath);
        }
    }
}
