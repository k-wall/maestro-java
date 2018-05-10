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

import java.util.List;

public interface MultiPointProfile {
    final class EndPoint {
        private String topic;
        private String name;
        private String brokerURL;

        public EndPoint(String name, String topic, String brokerURL) {
            this.topic = topic;
            this.name = name;
            this.brokerURL = brokerURL;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Deprecated
        public String getBrokerURL() {
            return getSendReceiveURL();
        }

        @Deprecated
        public void setBrokerURL(String brokerURL) {
            setSendReceiverURL(brokerURL);
        }

        public String getSendReceiveURL() {
            return brokerURL;
        }

        public void setSendReceiverURL(final String brokerURL) {
            this.brokerURL = brokerURL;
        }
    }

    void addEndPoint(EndPoint endPoint);
    List<EndPoint> getEndPoints();
}
