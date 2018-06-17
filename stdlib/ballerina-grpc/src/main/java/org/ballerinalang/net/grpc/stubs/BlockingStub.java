/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.net.grpc.stubs;

import com.google.protobuf.Descriptors;
import io.netty.handler.codec.http.HttpHeaders;
import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.BTupleType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.values.BRefType;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.net.grpc.CallOptions;
import org.ballerinalang.net.grpc.ClientCall;
import org.ballerinalang.net.grpc.ClientCallImpl;
import org.ballerinalang.net.grpc.Message;
import org.ballerinalang.net.grpc.MessageUtils;
import org.ballerinalang.net.grpc.MethodDescriptor;
import org.ballerinalang.net.grpc.Status;
import org.ballerinalang.net.grpc.StreamObserver;
import org.ballerinalang.net.http.DataContext;
import org.wso2.transport.http.netty.contract.HttpClientConnector;

import java.util.Arrays;

import static org.ballerinalang.net.grpc.GrpcConstants.MESSAGE_HEADERS;
import static org.ballerinalang.net.grpc.GrpcConstants.PROTOCOL_STRUCT_PACKAGE_GRPC;
import static org.ballerinalang.net.http.HttpConstants.PACKAGE_BALLERINA_BUILTIN;
import static org.ballerinalang.net.http.HttpConstants.STRUCT_GENERIC_ERROR;

/**
 * This class handles Blocking client connection.
 *
 * @since 1.0.0
 */
public class BlockingStub extends AbstractStub<BlockingStub> {

    private static final BTupleType RESP_TUPLE_TYPE = new BTupleType(Arrays.asList(BTypes.typeAny, BTypes.typeAny));

    public BlockingStub(HttpClientConnector clientConnector, Struct endpointConfig) {

        super(clientConnector, endpointConfig);
    }

    private BlockingStub(HttpClientConnector connector, Struct endpointConfig, CallOptions callOptions) {

        super(connector, endpointConfig, callOptions);
    }

    @Override
    protected BlockingStub build(HttpClientConnector connector, Struct endpointConfig, CallOptions callOptions) {

        return new BlockingStub(connector, endpointConfig, callOptions);
    }

    /**
     * Executes a unary call and blocks on the response.
     *
     * @param request          request message.
     * @param methodDescriptor method descriptor
     */
    public <ReqT, RespT> void executeUnary(ReqT request, MethodDescriptor<ReqT, RespT> methodDescriptor,
                                           DataContext dataContext) {

        ClientCall<ReqT, RespT> call = new ClientCallImpl<>(getConnector(), createOutboundRequest(((Message) request)
                .getHeaders()), methodDescriptor, getCallOptions());

        call.start(new CallBlockingListener<>(dataContext, methodDescriptor.getSchemaDescriptor()
                .getOutputType()));
        try {
            call.sendMessage(request);
            call.halfClose();
        } catch (RuntimeException | Error e) {
            throw cancelThrow(call, e);
        }
    }

    /**
     * Complete a GrpcFuture using {@link StreamObserver} events.
     */
    private static final class CallBlockingListener<RespT> extends ClientCall.Listener<RespT> {

        private final DataContext dataContext;
        private final Descriptors.Descriptor outputDescriptor;
        private RespT value;

        // Non private to avoid synthetic class
        private CallBlockingListener(DataContext dataContext, Descriptors.Descriptor outputDescriptor) {

            this.dataContext = dataContext;
            this.outputDescriptor = outputDescriptor;
        }

        @Override
        public void onHeaders(HttpHeaders headers) {

        }

        @Override
        public void onMessage(RespT value) {

            if (this.value != null) {
                throw Status.Code.INTERNAL.toStatus().withDescription("More than one value received for unary call")
                        .asRuntimeException();
            }
            this.value = value;
        }

        @Override
        public void onClose(Status status, HttpHeaders trailers) {

            BStruct httpConnectorError = null;
            BRefValueArray inboundResponse = null;

            if (status.isOk()) {
                if (value == null) {
                    // No value received so mark the future as an error
                    httpConnectorError = MessageUtils.getConnectorError(dataContext.context,
                            Status.Code.INTERNAL.toStatus()
                                    .withDescription("No value received for unary call").asRuntimeException());

                } else {
                    BValue responseBValue = MessageUtils.generateRequestStruct((Message) value, dataContext.context
                            .getProgramFile(), outputDescriptor.getName(), getBalType(outputDescriptor.getName(),
                            dataContext.context));
                    // Set response headers, when response headers exists in the message context.
                    BStruct headerStruct = BLangConnectorSPIUtil.createBStruct(dataContext.context.getProgramFile(),
                            PROTOCOL_STRUCT_PACKAGE_GRPC, "Headers");
                    headerStruct.addNativeData(MESSAGE_HEADERS, ((Message) value).getHeaders());
                    BRefValueArray contentTuple = new BRefValueArray(RESP_TUPLE_TYPE);
                    contentTuple.add(0, (BRefType) responseBValue);
                    contentTuple.add(1, headerStruct);
                    inboundResponse = contentTuple;
                }
            } else {
                httpConnectorError = MessageUtils.getConnectorError(dataContext.context, status.asRuntimeException());
            }
            if (inboundResponse != null) {
                dataContext.context.setReturnValues(inboundResponse);
            } else if (httpConnectorError != null) {
                dataContext.context.setReturnValues(httpConnectorError);
            } else {
                BStruct err = BLangConnectorSPIUtil.createBStruct(dataContext.context, PACKAGE_BALLERINA_BUILTIN,
                        STRUCT_GENERIC_ERROR, "HttpClient failed");
                dataContext.context.setReturnValues(err);
            }
            dataContext.callback.notifySuccess();
        }
    }

    /**
     * Returns corresponding Ballerina type for the proto buffer type.
     *
     * @param protoType Protocol buffer type
     * @param context   Ballerina Context
     * @return .
     */
    private static BType getBalType(String protoType, Context context) {

        if (protoType.equalsIgnoreCase("DoubleValue") || protoType
                .equalsIgnoreCase("FloatValue")) {
            return BTypes.typeFloat;
        } else if (protoType.equalsIgnoreCase("Int32Value") || protoType
                .equalsIgnoreCase("Int64Value") || protoType
                .equalsIgnoreCase("UInt32Value") || protoType
                .equalsIgnoreCase("UInt64Value")) {
            return BTypes.typeInt;
        } else if (protoType.equalsIgnoreCase("BoolValue")) {
            return BTypes.typeBoolean;
        } else if (protoType.equalsIgnoreCase("StringValue")) {
            return BTypes.typeString;
        } else if (protoType.equalsIgnoreCase("BytesValue")) {
            return BTypes.typeBlob;
        } else {
            return context.getProgramFile().getEntryPackage().getStructInfo(protoType).getType();
        }
    }
}
