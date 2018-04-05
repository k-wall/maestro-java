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

package org.maestro.reports;

import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class InspectorReportResolverTest {
    private static final String BASE_URL = "http://localhost:5006/logs/tests";

    @Test
    public void testSuccessFiles() {
        ReportResolver reportResolver = new InspectorReportResolver(BASE_URL);

        List<String> successFiles = reportResolver.getSuccessFiles();
        assertEquals("List size does not match the expected size", 4, successFiles.size());

        assertEquals("The sender rate file does not match the expected sender rate file",
                BASE_URL + "/logs/tests/lastSuccessful/heap.csv.gz", successFiles.get(0));
        assertEquals("The test properties file does not match the expected file",
                BASE_URL + "/logs/tests/lastSuccessful/inspector.properties", successFiles.get(1));
        assertEquals("The memory area file does not match the expected file",
                BASE_URL + "/logs/tests/lastSuccessful/memory-areas.csv.gz", successFiles.get(2));
        assertEquals("The memory area file does not match the expected file",
                BASE_URL + "/logs/tests/lastSuccessful/queues.csv.gz", successFiles.get(3));
    }

    @Test
    public void testFailedFiles() {
        ReportResolver reportResolver = new InspectorReportResolver(BASE_URL);

        List<String> failedFiles = reportResolver.getFailedFiles();
        assertEquals("List size does not match the expected size", 4, failedFiles.size());

        assertEquals("The sender rate file does not match the expected sender rate file",
                BASE_URL + "/logs/tests/lastFailed/heap.csv.gz", failedFiles.get(0));
        assertEquals("The test properties file does not match the expected file",
                BASE_URL + "/logs/tests/lastFailed/inspector.properties", failedFiles.get(1));
        assertEquals("The memory area file does not match the expected file",
                BASE_URL + "/lastFailed/memory-areas.csv.gz", failedFiles.get(2));
        assertEquals("The memory area file does not match the expected file",
                BASE_URL + "/logs/tests/lastFailed/queues.csv.gz", failedFiles.get(3));
    }
}
