/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package io.ballerina.plugins.idea.psi;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a named element.
 */
public interface BallerinaNamedElement extends BallerinaCompositeElement, BallerinaTypeOwner, PsiNameIdentifierOwner,
        NavigationItem {

    boolean isPublic();

    @Nullable
    PsiElement getIdentifier();

    @Nullable
    String getQualifiedName();

    @Override
    @NotNull
    BallerinaFile getContainingFile();

    @Nullable
    BallerinaTypeName findSiblingType();

    boolean isBlank();
}