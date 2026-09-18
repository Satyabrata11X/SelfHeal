package com.selfheal.starter.incident;

public class StackTraceLocationExtractor {

    public StackTraceLocation extract(
            Throwable throwable) {

        if (throwable == null) {
            return StackTraceLocation.empty();
        }

        StackTraceElement[] stackTrace =
                throwable.getStackTrace();

        if (stackTrace == null
                || stackTrace.length == 0) {

            return StackTraceLocation.empty();
        }

        for (StackTraceElement element : stackTrace) {

            if (element == null) {
                continue;
            }

            String className =
                    element.getClassName();

            if (className.startsWith(
                    "com.selfheal.starter")) {
                continue;
            }

            return new StackTraceLocation(
                    extractPackage(className),
                    extractClass(className),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber()
            );
        }

        StackTraceElement element =
                stackTrace[0];

        String className =
                element.getClassName();

        return new StackTraceLocation(
                extractPackage(className),
                extractClass(className),
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber()
        );
    }

    private String extractPackage(
            String className) {

        int lastDot =
                className.lastIndexOf('.');

        if (lastDot <= 0) {
            return "";
        }

        return className.substring(
                0,
                lastDot
        );
    }

    private String extractClass(
            String className) {

        int lastDot =
                className.lastIndexOf('.');

        if (lastDot < 0) {
            return className;
        }

        return className.substring(
                lastDot + 1
        );
    }

    public static class StackTraceLocation {

        private final String packageName;

        private final String className;

        private final String methodName;

        private final String fileName;

        private final int lineNumber;

        public StackTraceLocation(
                String packageName,
                String className,
                String methodName,
                String fileName,
                int lineNumber) {

            this.packageName = packageName;
            this.className = className;
            this.methodName = methodName;
            this.fileName = fileName;
            this.lineNumber = lineNumber;
        }

        public static StackTraceLocation empty() {

            return new StackTraceLocation(
                    "",
                    "",
                    "",
                    null,
                    -1
            );
        }

        public String getPackageName() {
            return packageName;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getFileName() {
            return fileName;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }
}