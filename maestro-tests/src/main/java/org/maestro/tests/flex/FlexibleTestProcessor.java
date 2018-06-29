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

package org.maestro.tests.flex;

import org.maestro.client.notes.TestFailedNotification;
import org.maestro.reports.downloaders.ReportsDownloader;
import org.maestro.tests.AbstractTestProcessor;
import org.maestro.tests.AbstractTestProfile;

/**
 * A simple processor that doesn't do much, but is flexible enough to be used by 3rd party tools
 */
public class FlexibleTestProcessor extends AbstractTestProcessor {
    private boolean successful = true;


    public FlexibleTestProcessor(final AbstractTestProfile testProfile, final ReportsDownloader reportsDownloader) {
        super(testProfile, reportsDownloader);
    }

    @Override
    public boolean processNotifyFail(TestFailedNotification note) {
        super.processNotifyFail(note);
        successful = false;
        return true;
    }

    public boolean isSuccessful() {
        return successful;
    }

}
