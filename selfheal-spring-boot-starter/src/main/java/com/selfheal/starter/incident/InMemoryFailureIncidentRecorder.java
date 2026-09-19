package com.selfheal.starter.incident;

import com.selfheal.starter.persistence.FailureIncidentPersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryFailureIncidentRecorder
        implements FailureIncidentRecorder,
        FailureIncidentPersistence {

    private final List<FailureContext> incidents =
            Collections.synchronizedList(
                    new ArrayList<>()
            );

    @Override
    public void record(FailureContext context) {

        if (context == null) {
            return;
        }

        if (isDuplicate(context)) {
            return;
        }

        incidents.add(context);

        System.out.println(
                "[SELFHEAL-INCIDENT] Failure captured"
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Application: "
                        + context.getApplicationName()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Component: "
                        + context.getComponentName()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Package: "
                        + context.getPackageName()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Class: "
                        + context.getClassName()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Method: "
                        + context.getMethodName()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] File: "
                        + context.getFileName()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Line: "
                        + context.getLineNumber()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Exception: "
                        + context.getExceptionType()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Failure Type: "
                        + context.getFailureType()
        );

        System.out.println(
                "[SELFHEAL-INCIDENT] Message: "
                        + context.getMessage()
        );
    }

    private boolean isDuplicate(
            FailureContext context) {

        synchronized (incidents) {

            for (FailureContext existing :
                    incidents) {

                if (!sameIncident(existing, context)) {
                    continue;
                }

                return true;
            }
        }

        return false;
    }

    private boolean sameIncident(
            FailureContext first,
            FailureContext second) {

        return equals(
                first.getApplicationName(),
                second.getApplicationName()
        )
                && equals(
                first.getPackageName(),
                second.getPackageName()
        )
                && equals(
                first.getClassName(),
                second.getClassName()
        )
                && equals(
                first.getMethodName(),
                second.getMethodName()
        )
                && equals(
                first.getFileName(),
                second.getFileName()
        )
                && first.getLineNumber()
                == second.getLineNumber()
                && equals(
                first.getExceptionType(),
                second.getExceptionType()
        )
                && equals(
                first.getMessage(),
                second.getMessage()
        );
    }

    private boolean equals(
            Object first,
            Object second) {

        if (first == null) {
            return second == null;
        }

        return first.equals(second);
    }

    public List<FailureContext> getIncidents() {

        synchronized (incidents) {
            return new ArrayList<>(incidents);
        }
    }

    public void clear() {

        synchronized (incidents) {
            incidents.clear();
        }
    }

    @Override
    public void save(FailureContext context) {
        record(context);
    }

    @Override
    public List<FailureContext> findAll() {
        return getIncidents();
    }

    @Override
    public void deleteAll() {
        clear();
    }
}