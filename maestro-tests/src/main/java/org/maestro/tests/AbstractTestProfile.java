/*
 * Copyright 2018 Otavio R. Piske <angusyoung@gmail.com>
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

package org.maestro.tests;

public abstract class AbstractTestProfile implements TestProfile {
    private int testExecutionNumber;
    private String managementInterface;
    private String inspectorName;

    public int getTestExecutionNumber() {
        return testExecutionNumber;
    }

    public void incrementTestExecutionNumber() {
        testExecutionNumber++;
    }

    public String getManagementInterface() {
        return managementInterface;
    }

    public void setManagementInterface(final String managementInterface) {
        this.managementInterface = managementInterface;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(final String inspectorName) {
        this.inspectorName = inspectorName;
    }

    @Override
    public String toString() {
        return "AbstractTestProfile{" +
                "testExecutionNumber=" + testExecutionNumber +
                '}';
    }
}
