package ru.handh.doctor.event;

import android.support.v4.util.Pair;

import java.util.List;

import ru.handh.doctor.model.Reference;

/**
 * Created by hugochaves on 08.08.2016.
 */
public class RedirectionInfoEvent {
    private List<Pair<Reference, Reference>> resultList;

    public RedirectionInfoEvent(List<Pair<Reference, Reference>> resultList) {
        this.resultList = resultList;
    }

    public List<Pair<Reference, Reference>> getResultList() {
        return resultList;
    }

    public void setResultList(List<Pair<Reference, Reference>> resultList) {
        this.resultList = resultList;
    }
}
