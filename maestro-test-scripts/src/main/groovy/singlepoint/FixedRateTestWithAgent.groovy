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

package singlepoint

import org.maestro.client.Maestro
import org.maestro.common.LogConfigurator
import org.maestro.common.Role
import org.maestro.common.client.notes.TestExecutionInfo
import org.maestro.common.client.notes.TestExecutionInfoBuilder
import org.maestro.common.duration.TestDurationBuilder
import org.maestro.tests.cluster.DistributionStrategyFactory
import org.maestro.tests.rate.FixedRateTestExecutor
import org.maestro.tests.rate.FixedRateTestProfile
import org.maestro.tests.support.DefaultTestEndpoint
import org.maestro.tests.support.TestEndpointResolver
import org.maestro.tests.support.TestEndpointResolverFactory
import org.maestro.tests.utils.ManagementInterface

maestroURL = System.getenv("MAESTRO_BROKER")
if (maestroURL == null) {
    println "Error: the maestro broker was not given"

    System.exit(1)
}

sendReceiveURL = System.getenv("SEND_RECEIVE_URL")
if (sendReceiveURL == null) {
    println "Error: the send/receive URL was not given"

    System.exit(1)
}

messageSize = System.getenv("MESSAGE_SIZE")
if (messageSize == null) {
    println "Error: the message size was not given"

    System.exit(1)
}

duration = System.getenv("TEST_DURATION")
if (duration == null) {
    println "Error: the test duration was not given"

    System.exit(1)
}

rate = System.getenv("RATE")
if (rate == null) {
    println "Error: the test rate was not given"

    System.exit(1)
}

parallelCount = System.getenv("PARALLEL_COUNT")
if (parallelCount == null) {
    println "Error: the test parallel count was not given"

    System.exit(1)
}

maxLatency = System.getenv("MAXIMUM_LATENCY")

extPointSource = System.getenv("EXT_POINT_SOURCE")
extPointBranch = System.getenv("EXT_POINT_BRANCH")
extPointCommand = System.getenv("EXT_POINT_COMMAND")

managementInterface = System.getenv("MANAGEMENT_INTERFACE")
inspectorName = System.getenv("INSPECTOR_NAME")
distributionStrategy = DistributionStrategyFactory.createStrategy(System.getenv("DISTRIBUTION_STRATEGY"), maestro)

logLevel = System.getenv("LOG_LEVEL")
LogConfigurator.configureLogLevel(logLevel)

println "Connecting to " + maestroURL
maestro = new Maestro(maestroURL)

FixedRateTestProfile testProfile = new FixedRateTestProfile()

TestEndpointResolver endpointResolver = TestEndpointResolverFactory.createTestEndpointResolver(System.getenv("ENDPOINT_RESOLVER_NAME"))

endpointResolver.register(Role.SENDER, new DefaultTestEndpoint(sendReceiveURL))
endpointResolver.register(Role.RECEIVER, new DefaultTestEndpoint(sendReceiveURL))

testProfile.setTestEndpointResolver(endpointResolver)

testProfile.setDuration(TestDurationBuilder.build(duration))
testProfile.setMessageSize(messageSize)

if (maxLatency != null) {
    testProfile.setMaximumLatency(Integer.parseInt(maxLatency))
}

testProfile.setRate(Integer.parseInt(rate))
testProfile.setParallelCount(Integer.parseInt(parallelCount))

testProfile.setExtPointSource(extPointSource)
testProfile.setExtPointBranch(extPointBranch)
testProfile.setExtPointCommand(extPointCommand)

ManagementInterface.setupInterface(managementInterface, inspectorName, testProfile)

FixedRateTestExecutor testExecutor = new FixedRateTestExecutor(maestro, testProfile, distributionStrategy)

description = System.getenv("TEST_DESCRIPTION")
comments = System.getenv("TEST_COMMENTS")

TestExecutionInfo testExecutionInfo = TestExecutionInfoBuilder.newBuilder()
        .withDescription(description)
        .withComment(comments)
        .withTestName("fixe-rate-with-agent")
        .withScriptName(this.class.getSimpleName())
        .build()

if (!testExecutor.run(testExecutionInfo)) {
    maestro.stop()

    System.exit(1)
}

maestro.stop()
System.exit(0)
