package com.razzzil.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageHandlerInvocation {
    private HandlerInvocation handlerInvocation;
    private InvocationParameters invocationParameters;
}
