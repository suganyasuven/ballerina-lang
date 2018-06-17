package org.ballerinalang.net.grpc.listener;

import com.google.protobuf.Descriptors;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.net.grpc.GrpcCallableUnitCallBack;
import org.ballerinalang.net.grpc.GrpcConstants;
import org.ballerinalang.net.grpc.Message;
import org.ballerinalang.net.grpc.ServerCall;
import org.ballerinalang.net.grpc.Status;
import org.ballerinalang.net.grpc.StreamObserver;

import java.util.Map;

/**
 * Streaming service call handler.
 * This is registered in client and bidirectional streaming services.
 *
 * @param <ReqT> Request message type.
 * @param <RespT> Response message type.
 */
public class StreamingServerCallHandler<ReqT, RespT> extends ServerCallHandler<ReqT, RespT> {

    private final Map<String, Resource> resourceMap;

    public StreamingServerCallHandler(Descriptors.MethodDescriptor methodDescriptor, Map<String, Resource>
            resourceMap) {

        super(methodDescriptor);
        this.resourceMap = resourceMap;
    }

    @Override
    public ServerCall.Listener<ReqT> startCall(ServerCall<ReqT, RespT> call) {

        ServerCallStreamObserver<RespT> responseObserver = new ServerCallStreamObserver<>(call);
        StreamObserver<ReqT> requestObserver = invoke(responseObserver);
        return new StreamingServerCallListener(requestObserver, responseObserver, call);
    }

    public StreamObserver<ReqT> invoke(StreamObserver<RespT> responseObserver) {
        Resource onOpen = resourceMap.get(GrpcConstants.ON_OPEN_RESOURCE);
        CallableUnitCallback callback = new GrpcCallableUnitCallBack<>(null);
        Executor.submit(onOpen, callback, null, null, computeMessageParams
                (onOpen, null, responseObserver));

        return new StreamObserver<ReqT>() {
            @Override
            public void onNext(ReqT value) {
                Resource onMessage = resourceMap.get(GrpcConstants.ON_MESSAGE_RESOURCE);
                CallableUnitCallback callback = new GrpcCallableUnitCallBack<>(null);
                Executor.submit(onMessage, callback, null, null, computeMessageParams
                        (onMessage, value, responseObserver));
            }

            @Override
            public void onError(ReqT error) {
                Resource onError = resourceMap.get(GrpcConstants.ON_ERROR_RESOURCE);
                onErrorInvoke(onError, responseObserver, error);
            }

            @Override
            public void onCompleted() {
                Resource onCompleted = resourceMap.get(GrpcConstants.ON_COMPLETE_RESOURCE);
                if (onCompleted == null) {
                    String message = "Error in listener service definition. onError resource does not exists";
                    throw new RuntimeException(message);
                }

                CallableUnitCallback callback = new GrpcCallableUnitCallBack<>(responseObserver, Boolean.FALSE);
                Executor.submit(onCompleted, callback, null, null, computeMessageParams
                        (onCompleted, null, responseObserver));
            }
        };
    }

    private final class StreamingServerCallListener extends ServerCall.Listener<ReqT> {

        private final StreamObserver<ReqT> requestObserver;
        private final ServerCallStreamObserver<RespT> responseObserver;
        private boolean halfClosed = false;

        // Non private to avoid synthetic class
        StreamingServerCallListener(
                StreamObserver<ReqT> requestObserver,
                ServerCallStreamObserver<RespT> responseObserver,
                ServerCall<ReqT, RespT> call) {

            this.requestObserver = requestObserver;
            this.responseObserver = responseObserver;
        }

        @Override
        public void onMessage(ReqT request) {

            requestObserver.onNext(request);
        }

        @Override
        public void onHalfClose() {

            halfClosed = true;
            requestObserver.onCompleted();
        }

        @Override
        public void onCancel() {

            responseObserver.cancelled = true;
            if (!halfClosed) {
                Message message = new Message(Status.Code.CANCELLED.toStatus()
                        .withDescription("cancelled before receiving half close")
                        .asRuntimeException());
                requestObserver.onError((ReqT) message);
            }
        }

        @Override
        public void onReady() {
        }
    }
}
