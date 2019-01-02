/*
 * Copyright 2018 The MQTT Bee project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mqttbee.internal.mqtt.message.publish.pubcomp.mqtt3;

import org.jetbrains.annotations.NotNull;
import org.mqttbee.annotations.Immutable;
import org.mqttbee.internal.mqtt.datatypes.MqttUserPropertiesImpl;
import org.mqttbee.internal.mqtt.message.publish.pubcomp.MqttPubComp;
import org.mqttbee.mqtt.mqtt3.message.publish.pubcomp.Mqtt3PubComp;
import org.mqttbee.mqtt.mqtt5.message.publish.pubcomp.Mqtt5PubCompReasonCode;

/**
 * @author Silvio Giebl
 */
@Immutable
public class Mqtt3PubCompView implements Mqtt3PubComp {

    public static final @NotNull Mqtt3PubCompView INSTANCE = new Mqtt3PubCompView();

    public static @NotNull MqttPubComp delegate(final int packetIdentifier) {
        return new MqttPubComp(packetIdentifier, Mqtt5PubCompReasonCode.SUCCESS, null,
                MqttUserPropertiesImpl.NO_USER_PROPERTIES);
    }

    private Mqtt3PubCompView() {}
}