package org.bitbucket.privattaskmanager.domain;

public enum TaskStatus {

    NEW("NEW"), IN_PROGRESS("IN PROGRESS"), COMPLETED("COMPLETED"), CANCELED("CANCELED");

    private final String value;

    TaskStatus(String status) {
        this.value = status;
    }

    public static TaskStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (TaskStatus _status : TaskStatus.values()) {
            if (_status.value.equals(value)) {
                return _status;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
