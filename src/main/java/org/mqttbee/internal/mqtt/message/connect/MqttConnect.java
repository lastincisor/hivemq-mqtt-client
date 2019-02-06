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

package org.mqttbee.internal.mqtt.message.connect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.annotations.Immutable;
import org.mqttbee.internal.mqtt.datatypes.MqttClientIdentifierImpl;
import org.mqttbee.internal.mqtt.datatypes.MqttUserPropertiesImpl;
import org.mqttbee.internal.mqtt.message.MqttMessageWithUserProperties;
import org.mqttbee.internal.mqtt.message.auth.MqttEnhancedAuth;
import org.mqttbee.internal.mqtt.message.auth.MqttSimpleAuth;
import org.mqttbee.internal.mqtt.message.publish.MqttWillPublish;
import org.mqttbee.internal.util.StringUtil;
import org.mqttbee.mqtt.mqtt5.auth.Mqtt5EnhancedAuthMechanism;
import org.mqttbee.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;
import org.mqttbee.mqtt.mqtt5.message.connect.Mqtt5Connect;
import org.mqttbee.mqtt.mqtt5.message.publish.Mqtt5WillPublish;

import java.util.Optional;

/**
 * @author Silvio Giebl
 */
@Immutable
public class MqttConnect extends MqttMessageWithUserProperties implements Mqtt5Connect {

    public static final @NotNull MqttConnect DEFAULT =
            new MqttConnect(DEFAULT_KEEP_ALIVE, DEFAULT_CLEAN_START, DEFAULT_SESSION_EXPIRY_INTERVAL,
                    MqttConnectRestrictions.DEFAULT, null, null, null, MqttUserPropertiesImpl.NO_USER_PROPERTIES);

    private final int keepAlive;
    private final boolean cleanStart;
    private final long sessionExpiryInterval;
    private final @NotNull MqttConnectRestrictions restrictions;
    private final @Nullable MqttSimpleAuth simpleAuth;
    private final @Nullable Mqtt5EnhancedAuthMechanism enhancedAuthMechanism;
    private final @Nullable MqttWillPublish willPublish;

    public MqttConnect(
            final int keepAlive, final boolean cleanStart, final long sessionExpiryInterval,
            final @NotNull MqttConnectRestrictions restrictions, final @Nullable MqttSimpleAuth simpleAuth,
            final @Nullable Mqtt5EnhancedAuthMechanism enhancedAuthMechanism,
            final @Nullable MqttWillPublish willPublish, final @NotNull MqttUserPropertiesImpl userProperties) {

        super(userProperties);
        this.keepAlive = keepAlive;
        this.cleanStart = cleanStart;
        this.sessionExpiryInterval = sessionExpiryInterval;
        this.restrictions = restrictions;
        this.simpleAuth = simpleAuth;
        this.enhancedAuthMechanism = enhancedAuthMechanism;
        this.willPublish = willPublish;
    }

    @Override
    public int getKeepAlive() {
        return keepAlive;
    }

    @Override
    public boolean isCleanStart() {
        return cleanStart;
    }

    @Override
    public long getSessionExpiryInterval() {
        return sessionExpiryInterval;
    }

    @Override
    public @NotNull MqttConnectRestrictions getRestrictions() {
        return restrictions;
    }

    @Override
    public @NotNull Optional<Mqtt5SimpleAuth> getSimpleAuth() {
        return Optional.ofNullable(simpleAuth);
    }

    public @Nullable MqttSimpleAuth getRawSimpleAuth() {
        return simpleAuth;
    }

    @Override
    public @NotNull Optional<Mqtt5EnhancedAuthMechanism> getEnhancedAuthMechanism() {
        return Optional.ofNullable(enhancedAuthMechanism);
    }

    public @Nullable Mqtt5EnhancedAuthMechanism getRawEnhancedAuthMechanism() {
        return enhancedAuthMechanism;
    }

    @Override
    public @NotNull Optional<Mqtt5WillPublish> getWillPublish() {
        return Optional.ofNullable(willPublish);
    }

    public @Nullable MqttWillPublish getRawWillPublish() {
        return willPublish;
    }

    @Override
    public @NotNull MqttConnectBuilder.Default extend() {
        return new MqttConnectBuilder.Default(this);
    }

    public @NotNull MqttStatefulConnect createStateful(
            final @NotNull MqttClientIdentifierImpl clientIdentifier, final @Nullable MqttEnhancedAuth enhancedAuth) {

        return new MqttStatefulConnect(this, clientIdentifier, enhancedAuth);
    }

    @Override
    protected @NotNull String toAttributeString() {
        return "keepAlive=" + keepAlive + ", cleanStart=" + cleanStart + ", sessionExpiryInterval=" +
                sessionExpiryInterval +
                ((restrictions == MqttConnectRestrictions.DEFAULT) ? "" : ", restrictions=" + restrictions) +
                ((simpleAuth == null) ? "" : ", simpleAuth=" + simpleAuth) +
                ((enhancedAuthMechanism == null) ? "" : ", enhancedAuthMechanism=" + enhancedAuthMechanism) +
                ((willPublish == null) ? "" : ", willPublish=" + willPublish) +
                StringUtil.prepend(", ", super.toAttributeString());
    }

    @Override
    public @NotNull String toString() {
        return "MqttConnect{" + toAttributeString() + '}';
    }
}
