package co.bk.task.restapi.util;

import co.bk.task.restapi.service.dto.ChargeSessionDto;

import java.util.Comparator;

public class EndTimeComparator implements Comparator<ChargeSessionDto> {

    @Override
    public int compare(ChargeSessionDto dr1, ChargeSessionDto dr2) {
        if (dr1.getEndTime() == null && dr2.getEndTime() == null) {
            return 0;
        } else if (dr1.getEndTime() == null && dr2.getEndTime() != null) {
            return -1;
        } else if (dr1.getEndTime() != null && dr2.getEndTime() == null) {
            return 1;
        }
        return Long.compare(dr1.getEndTime(), dr2.getEndTime());
    }

}