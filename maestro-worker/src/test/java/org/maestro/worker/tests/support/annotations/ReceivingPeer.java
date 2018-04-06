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

package org.maestro.worker.tests.support.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This test annotation can be used to distinguish a receiving peer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ReceivingPeer {

    /**
     * The maestro URL
     * @return
     */
    String maestroUrl() default "mqtt://localhost:1883";

    /**
     * Peer role
     * @return
     */
    String role() default "receiver";

    /**
     * Test hostname
     * @return
     */
    String host() default "localhost";


    /**
     * The worker class
     * @return
     */
    String worker() default "org.maestro.worker.jms.JMSReceiverWorker";
}
