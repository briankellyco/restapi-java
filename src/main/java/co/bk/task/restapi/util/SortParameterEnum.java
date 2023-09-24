package co.bk.task.restapi.util;

import java.util.Arrays;
import java.util.Optional;


public enum SortParameterEnum {

    START_TIME_ASC("startTime"),
    START_TIME_DESC("-startTime"),
    END_TIME_ASC("endTime"),
    END_TIME_DESC("-endTime");

    private String sortParam;

    SortParameterEnum(String sortParam) {
        this.sortParam = sortParam;
    }

    public static Optional<SortParameterEnum> identifySortParameter(String sortParamToIdentify) {
        return Arrays.stream(values()).filter(it -> it.sortParam.equalsIgnoreCase(sortParamToIdentify)).findAny();
    }
}
