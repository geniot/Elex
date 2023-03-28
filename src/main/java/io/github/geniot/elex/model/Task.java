package io.github.geniot.elex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
    private String fileName;
    private Action action;
    private TaskStatus status = TaskStatus.RUNNING;
    private int progress;
    private String ftIndexSize;
    private long finishedWhen;
}
