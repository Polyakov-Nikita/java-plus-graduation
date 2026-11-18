package ru.practicum.test.utils;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
public class TestBase {
    protected <M> void assertMethodCall(M mock, Consumer<M> methodCall) {
        methodCall.accept(Mockito.verify(
                mock,
                Mockito.times(1))
        );
    }

    protected <M> void assertMethodNotCall(M mock, Consumer<M> methodCall) {
        methodCall.accept(Mockito.verify(
                mock,
                Mockito.never())
        );
    }
}
