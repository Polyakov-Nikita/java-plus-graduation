package ru.practicum.explorewithme.collector.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.message.UserActionProto;
import ru.practicum.ewm.stats.proto.service.UserActionControllerGrpc;
import ru.practicum.explorewithme.collector.service.CollectorGrpcService;

@GrpcService
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CollectorGrpcController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final CollectorGrpcService collectorGrpcService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            collectorGrpcService.collectUserAction(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    new StatusRuntimeException(
                            Status.INTERNAL
                                    .withDescription(e.getLocalizedMessage())
                                    .withCause(e)
                    )
            );
        }
    }
}
