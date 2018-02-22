package org.mqttbee.api.mqtt.mqtt5.message.connect;

import org.mqttbee.annotations.DoNotImplement;
import org.mqttbee.annotations.NotNull;
import org.mqttbee.api.mqtt.datatypes.MqttUTF8String;
import org.mqttbee.api.mqtt.mqtt5.auth.Mqtt5EnhancedAuthProvider;
import org.mqttbee.api.mqtt.mqtt5.datatypes.Mqtt5UserProperties;
import org.mqttbee.api.mqtt.mqtt5.message.Mqtt5Message;
import org.mqttbee.api.mqtt.mqtt5.message.Mqtt5MessageType;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5WillPublish;
import org.mqttbee.mqtt.datatypes.MqttVariableByteInteger;
import org.mqttbee.util.UnsignedDataTypes;

import java.nio.ByteBuffer;
import java.util.Optional;

import static org.mqttbee.api.mqtt.mqtt5.message.connect.Mqtt5ConnectBuilder.RestrictionsBuilder;
import static org.mqttbee.api.mqtt.mqtt5.message.connect.Mqtt5ConnectBuilder.SimpleAuthBuilder;

/**
 * MQTT 5 CONNECT packet.
 *
 * @author Silvio Giebl
 */
@DoNotImplement
public interface Mqtt5Connect extends Mqtt5Message {

    int NO_KEEP_ALIVE = 0;
    int DEFAULT_KEEP_ALIVE = 60;
    boolean DEFAULT_CLEAN_START = true;
    long DEFAULT_SESSION_EXPIRY_INTERVAL = 0;
    long NO_SESSION_EXPIRY = UnsignedDataTypes.UNSIGNED_INT_MAX_VALUE;
    boolean DEFAULT_RESPONSE_INFORMATION_REQUESTED = false;
    boolean DEFAULT_PROBLEM_INFORMATION_REQUESTED = true;

    @NotNull
    static Mqtt5ConnectBuilder builder() {
        return new Mqtt5ConnectBuilder();
    }

    @NotNull
    static Mqtt5ConnectBuilder extend(@NotNull final Mqtt5Connect connect) {
        return new Mqtt5ConnectBuilder(connect);
    }

    /**
     * @return the keep alive the client wants to use.
     */
    int getKeepAlive();

    /**
     * @return whether the client has no session present or wants to clear a present session.
     */
    boolean isCleanStart();

    /**
     * @return the session expiry interval the client wants to use. The default is {@link
     * #DEFAULT_SESSION_EXPIRY_INTERVAL}. If it is {@link #NO_SESSION_EXPIRY} the session does not expire.
     */
    long getSessionExpiryInterval();

    /**
     * @return whether the client requests response information from the server. The default is {@link
     * #DEFAULT_RESPONSE_INFORMATION_REQUESTED}.
     */
    boolean isResponseInformationRequested();

    /**
     * @return whether the client requests problem information from the server. The default is {@link
     * #DEFAULT_PROBLEM_INFORMATION_REQUESTED}.
     */
    boolean isProblemInformationRequested();

    /**
     * @return the restrictions set from the client.
     */
    @NotNull
    Restrictions getRestrictions();

    /**
     * @return the optional simple authentication and/or authorization related data of this CONNECT packet.
     */
    @NotNull
    Optional<SimpleAuth> getSimpleAuth();

    /**
     * @return the optional enhanced authentication and/or authorization provider of this CONNECT packet.
     */
    @NotNull
    Optional<Mqtt5EnhancedAuthProvider> getEnhancedAuthProvider();

    /**
     * @return the optional Will Publish of this CONNECT packet.
     */
    @NotNull
    Optional<Mqtt5WillPublish> getWillPublish();

    /**
     * @return the optional user properties of this CONNECT packet.
     */
    @NotNull
    Mqtt5UserProperties getUserProperties();

    @NotNull
    @Override
    default Mqtt5MessageType getType() {
        return Mqtt5MessageType.CONNECT;
    }


    /**
     * Simple authentication and/or authorization related data in the CONNECT packet.
     */
    @DoNotImplement
    interface SimpleAuth {

        @NotNull
        static SimpleAuthBuilder builder() {
            return new SimpleAuthBuilder();
        }

        /**
         * @return the optional username.
         */
        @NotNull
        Optional<MqttUTF8String> getUsername();

        /**
         * @return the optional password.
         */
        @NotNull
        Optional<ByteBuffer> getPassword();

    }


    /**
     * Restrictions from the the server in the CONNECT packet.
     */
    @DoNotImplement
    interface Restrictions {

        /**
         * The default maximum amount of not acknowledged publishes with QoS 1 or 2 the client accepts concurrently.
         */
        int DEFAULT_RECEIVE_MAXIMUM = 65_535;
        /**
         * The default maximum amount of topic aliases the client accepts from the server.
         */
        int DEFAULT_TOPIC_ALIAS_MAXIMUM = 0;
        /**
         * The default maximum packet size the client accepts from the server which indicates that the packet size is
         * not limited beyond the restrictions of the encoding.
         */
        int DEFAULT_MAXIMUM_PACKET_SIZE_NO_LIMIT = MqttVariableByteInteger.MAXIMUM_PACKET_SIZE_LIMIT;

        @NotNull
        static RestrictionsBuilder builder() {
            return new RestrictionsBuilder();
        }

        /**
         * @return the maximum amount of not acknowledged publishes with QoS 1 or 2 the client accepts concurrently. The
         * default is {@link #DEFAULT_RECEIVE_MAXIMUM}.
         */
        int getReceiveMaximum();

        /**
         * @return the maximum amount of topic aliases the client accepts from the server. The default is {@link
         * #DEFAULT_TOPIC_ALIAS_MAXIMUM}.
         */
        int getTopicAliasMaximum();

        /**
         * @return the maximum packet size the client accepts from the server. The default is {@link
         * #DEFAULT_MAXIMUM_PACKET_SIZE_NO_LIMIT}.
         */
        int getMaximumPacketSize();

    }

}
