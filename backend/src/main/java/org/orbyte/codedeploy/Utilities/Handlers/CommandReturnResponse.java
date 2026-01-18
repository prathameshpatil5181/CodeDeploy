package org.orbyte.codedeploy.Utilities.Handlers;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommandReturnResponse {

    private Status status;
    private String message;
    private String stdin;
    private String stdout;
    private String stderr;

}
