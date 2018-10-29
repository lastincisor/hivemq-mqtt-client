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

package org.mqttbee.mqtt.mqtt3;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.MqttGlobalPublishFilter;
import org.mqttbee.api.mqtt.mqtt3.Mqtt3RxClient;
import org.mqttbee.api.mqtt.mqtt3.message.connect.Mqtt3Connect;
import org.mqttbee.api.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import org.mqttbee.api.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.mqttbee.api.mqtt.mqtt3.message.publish.Mqtt3PublishResult;
import org.mqttbee.api.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;
import org.mqttbee.api.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import org.mqttbee.api.mqtt.mqtt3.message.unsubscribe.Mqtt3Unsubscribe;
import org.mqttbee.api.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import org.mqttbee.mqtt.MqttRxClient;
import org.mqttbee.mqtt.message.connect.connack.mqtt3.Mqtt3ConnAckView;
import org.mqttbee.mqtt.message.connect.mqtt3.Mqtt3ConnectView;
import org.mqttbee.mqtt.message.disconnect.mqtt3.Mqtt3DisconnectView;
import org.mqttbee.mqtt.message.publish.mqtt3.Mqtt3PublishResultView;
import org.mqttbee.mqtt.message.publish.mqtt3.Mqtt3PublishView;
import org.mqttbee.mqtt.message.subscribe.mqtt3.Mqtt3SubscribeView;
import org.mqttbee.mqtt.message.subscribe.suback.mqtt3.Mqtt3SubAckView;
import org.mqttbee.mqtt.message.unsubscribe.mqtt3.Mqtt3UnsubscribeView;
import org.mqttbee.mqtt.mqtt3.exceptions.Mqtt3ExceptionFactory;
import org.mqttbee.rx.FlowableWithSingle;

/**
 * @author Silvio Giebl
 * @author David Katz
 */
public class Mqtt3RxClientView implements Mqtt3RxClient {

    private static final @NotNull Function<Throwable, Completable> EXCEPTION_MAPPER_COMPLETABLE =
            e -> Completable.error(Mqtt3ExceptionFactory.map(e));

    private static final @NotNull Function<Throwable, Single<Mqtt5ConnAck>> EXCEPTION_MAPPER_SINGLE_CONNACK =
            e -> Single.error(Mqtt3ExceptionFactory.map(e));

    private static final @NotNull Function<Throwable, Single<Mqtt5SubAck>> EXCEPTION_MAPPER_SINGLE_SUBACK =
            e -> Single.error(Mqtt3ExceptionFactory.map(e));

    private static final @NotNull Function<Throwable, Flowable<Mqtt5Publish>> EXCEPTION_MAPPER_FLOWABLE_PUBLISH =
            e -> Flowable.error(Mqtt3ExceptionFactory.map(e));

    private static final @NotNull Function<Throwable, Flowable<Mqtt5PublishResult>>
            EXCEPTION_MAPPER_FLOWABLE_PUBLISH_RESULT = e -> Flowable.error(Mqtt3ExceptionFactory.map(e));

    private final @NotNull MqttRxClient delegate;
    private final @NotNull Mqtt3ClientDataView clientData;

    public Mqtt3RxClientView(final @NotNull MqttRxClient delegate) {
        this.delegate = delegate;
        clientData = new Mqtt3ClientDataView(delegate.getClientData());
    }

    @Override
    public @NotNull Single<Mqtt3ConnAck> connect(final @NotNull Mqtt3Connect connect) {
        return delegate.connect(Mqtt3ConnectView.delegate(connect))
                .onErrorResumeNext(EXCEPTION_MAPPER_SINGLE_CONNACK)
                .map(Mqtt3ConnAckView.MAPPER);
    }

    @Override
    public @NotNull Single<Mqtt3SubAck> subscribe(final @NotNull Mqtt3Subscribe subscribe) {
        return delegate.subscribe(Mqtt3SubscribeView.delegate(subscribe))
                .onErrorResumeNext(EXCEPTION_MAPPER_SINGLE_SUBACK)
                .map(Mqtt3SubAckView.MAPPER);
    }

    @Override
    public @NotNull FlowableWithSingle<Mqtt3Publish, Mqtt3SubAck> subscribeStream(
            final @NotNull Mqtt3Subscribe subscribe) {

        return delegate.subscribeStream(Mqtt3SubscribeView.delegate(subscribe))
                .mapError(Mqtt3ExceptionFactory.MAPPER)
                .mapBoth(Mqtt3PublishView.MAPPER, Mqtt3SubAckView.MAPPER);
    }

    @Override
    public @NotNull Flowable<Mqtt3Publish> publishes(final @NotNull MqttGlobalPublishFilter filter) {
        return delegate.publishes(filter)
                .onErrorResumeNext(EXCEPTION_MAPPER_FLOWABLE_PUBLISH)
                .map(Mqtt3PublishView.MAPPER);
    }

    @Override
    public @NotNull Completable unsubscribe(final @NotNull Mqtt3Unsubscribe unsubscribe) {
        return delegate.unsubscribe(Mqtt3UnsubscribeView.delegate(unsubscribe))
                .toCompletable()
                .onErrorResumeNext(EXCEPTION_MAPPER_COMPLETABLE);
    }

    @Override
    public @NotNull Flowable<Mqtt3PublishResult> publish(final @NotNull Flowable<Mqtt3Publish> publishFlowable) {
        return delegate.publish(publishFlowable.map(Mqtt3PublishView.DELEGATE_MAPPER))
                .onErrorResumeNext(EXCEPTION_MAPPER_FLOWABLE_PUBLISH_RESULT)
                .map(Mqtt3PublishResultView.MAPPER);
    }

    @Override
    public @NotNull Completable disconnect() {
        return delegate.disconnect(Mqtt3DisconnectView.delegate()).onErrorResumeNext(EXCEPTION_MAPPER_COMPLETABLE);
    }

    @Override
    public @NotNull Mqtt3ClientDataView getClientData() {
        return clientData;
    }

    @Override
    public @NotNull Mqtt3AsyncClientView toAsync() {
        return new Mqtt3AsyncClientView(delegate.toAsync());
    }

    @Override
    public @NotNull Mqtt3BlockingClientView toBlocking() {
        return new Mqtt3BlockingClientView(delegate.toBlocking());
    }
}