package ru.practicum.explorewithme.analyzer.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.message.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.message.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.message.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.message.UserPredictionsRequestProto;
import ru.practicum.ewm.stats.proto.service.RecommendationsControllerGrpc;
import ru.practicum.explorewithme.analyzer.service.grpc.AnalyzerGrpcService;

import java.util.List;
import java.util.function.Supplier;

@GrpcService
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AnalyzerGrpcController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final AnalyzerGrpcService analyzerGrpcService;

    @Override
    public void getRecommendationsForUser(
            UserPredictionsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        processServiceResult(
                () -> analyzerGrpcService.getRecommendationsForUser(request), responseObserver
        );
    }

    private void processServiceResult(
            Supplier<List<RecommendedEventProto>> serviceMethod,
            StreamObserver<RecommendedEventProto> responseObserver
    ) {
        try {
            List<RecommendedEventProto> recommendedEvents = serviceMethod.get();
            recommendedEvents.forEach(responseObserver::onNext);
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

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        processServiceResult(
                () -> analyzerGrpcService.getSimilarEvents(request), responseObserver
        );
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        processServiceResult(
                () -> analyzerGrpcService.getInteractionsCount(request), responseObserver
        );
    }
}
