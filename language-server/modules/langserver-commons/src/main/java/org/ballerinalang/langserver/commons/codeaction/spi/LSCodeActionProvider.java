/*
 * Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.langserver.commons.codeaction.spi;

import org.ballerinalang.langserver.commons.CodeActionContext;
import org.ballerinalang.langserver.commons.codeaction.CodeActionNodeType;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;

import java.util.List;

/**
 * Represents the SPI interface for the Ballerina Code Action Provider.
 *
 * @since 1.2.0
 */
public interface LSCodeActionProvider {

    /**
     * Returns True if code action is enabled.
     *
     * @return True if code action is enabled, False otherwise
     */
    boolean isEnabled();

    /**
     * Returns the list of code actions based on node type or diagnostics.
     *
     * @param nodeType          code action node type
     * @param codeActionContext code action operation context
     * @return list of Code Actions
     */
    List<CodeAction> getNodeBasedCodeActions(CodeActionNodeType nodeType, CodeActionContext codeActionContext);

    /**
     * Returns the list of code actions based on node type or diagnostics.
     *
     * @param nodeType           code action node type
     * @param codeActionContext  code action operation context
     * @param diagnosticsOfRange diagnostics list of the cursor range
     * @return list of Code Actions
     */
    List<CodeAction> getDiagBasedCodeActions(CodeActionNodeType nodeType, CodeActionContext codeActionContext,
                                             List<Diagnostic> diagnosticsOfRange);

    /**
     * Returns True of node type based code actions are supported.
     *
     * @return True of node type based code actions are supported, False otherwise
     */
    boolean isNodeBasedSupported();

    /**
     * Returns True of code diagnostics type based code actions are supported.
     *
     * @return True of code diagnostics based code actions are supported, False otherwise
     */
    boolean isDiagBasedSupported();

    /**
     * returns the list of node types that the code action belongs to.
     *
     * @return list of code action node type
     */
    List<CodeActionNodeType> getCodeActionNodeTypes();
}
